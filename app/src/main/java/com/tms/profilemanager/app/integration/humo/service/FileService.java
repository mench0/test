package com.tms.profilemanager.app.integration.humo.service;

import com.tms.profilemanager.app.config.MinioProperties;
import com.tms.profilemanager.app.entity.HumoSetting;
import com.tms.profilemanager.app.integration.humo.dto.TerminalHumo;
import com.tms.profilemanager.app.integration.humo.dto.Terminals;
import com.tms.profilemanager.app.service.HumoSettingService;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {

    MinioProperties minioProperties;
    MinioClient humoMinioClient;

    HumoSettingService humoSettingService;

    Map<String, String> serialNumberToFileName = new HashMap<>();

    public Set<Map<String, TerminalHumo>> download() {
        String bucket = minioProperties.getHumo().getBucket();
        List<String> humoSettings = humoSettingService.getAllHumoSettingsUsingCacheWithoutAccessControl().stream()
                .map(HumoSetting::getHumoId)
                .toList();
        Set<Map<String, TerminalHumo>> terminalsSet = new HashSet<>();
        try {
            List<String> itemsNameOrderedByDate = getFilesNameOrderedByDate();
            for (String name : itemsNameOrderedByDate) {
                GetObjectResponse stream = humoMinioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(name).build());
                Map<String, TerminalHumo> terminalMap = readStream(stream, name, humoSettings);
                terminalsSet.add(terminalMap);
            }
        } catch (Exception e) {
            log.error("Ошибка при получении данных из minio", e);
        }
        return terminalsSet;
    }

    private Map<String, TerminalHumo> readStream(InputStream inputStream, String fileName, List<String> humoSettings) {
        JAXBContext jaxbContext;
        Map<String, TerminalHumo> terminalMap = new HashMap<>();
        Terminals terminals = null;
        String xmlString = null;
        try (inputStream) {
            xmlString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            jaxbContext = JAXBContext.newInstance(Terminals.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema humoSchema = sf.newSchema(getClass().getClassLoader().getResource("schema/humo.xsd"));
            jaxbUnmarshaller.setSchema(humoSchema);
            terminals = (Terminals) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            for (TerminalHumo terminal : terminals.getTerminal()) {
                String sn = terminal.getSerialNr();
                if (!humoSettings.contains(sn.substring(0, 5))) {
                    replaceToReject(fileName, xmlString, sn);
                    continue;
                }
                if (!terminalMap.containsKey(sn) && !serialNumberToFileName.containsKey(sn)) {
                    terminalMap.put(sn, terminal);
                    serialNumberToFileName.put(sn, fileName);
                }
            }
        } catch (JAXBException | SAXException e) {
            log.error("Файл {} не прошел валидацию. Отправляем в ошибочную очередь.", fileName, e);
            replaceToError(fileName, xmlString, e);
        } catch (IOException e) {
            log.error("Неизвестная ошибка при валидации файла.", e);
        }
        return terminalMap;
    }

    public void removeFileFromMinio(String serialNumber) {
        try {
            String fileNameFromMinio = serialNumberToFileName.get(serialNumber);
            humoMinioClient.removeObject(RemoveObjectArgs.builder().bucket(minioProperties.getHumo().getBucket()).object(fileNameFromMinio).build());
            log.info("Из minio удалили файл с названием {} и serialNumber={}", fileNameFromMinio, serialNumber.substring(5));
            serialNumberToFileName.remove(serialNumber);
        } catch (Exception e) {
            log.error("Не смогли удалить файл из minio", e);
        }
    }

    private void replaceToError(String fileName, String content, Exception exception) {
        Map<String, String> metadata = new HashMap<>();
        if (exception.getCause() != null) {
            metadata.put("error-description", exception.getCause().getMessage());
        }
        replaceToBucket(fileName, minioProperties.getHumo().getErrorBucket(), metadata, content);
        log.info("Невалидный файл был отправлен в {} и удален из {}", minioProperties.getHumo().getErrorBucket(), minioProperties.getHumo().getBucket());
    }

    private void replaceToReject(String fileName,String content, String serialNumber) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("serial-number", serialNumber);
        replaceToBucket(fileName, minioProperties.getHumo().getRejectBucket(), metadata, content);
        log.info("Файл c незарегистрированным humoId был отправлен в {} и удален из {}", minioProperties.getHumo().getRejectBucket(), minioProperties.getHumo().getBucket());
    }

    private void replaceToBucket(String fileName, String bucketName, Map<String, String> metadata, String content) {
        try {
            if (!humoMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                humoMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            uploadToBucket(fileName, bucketName, metadata, content);
            humoMinioClient.removeObject(RemoveObjectArgs.builder().bucket(minioProperties.getHumo().getBucket()).object(fileName).build());
        } catch (Exception e) {
            log.error("Ошибка при перемещении файла '{}' в '{}': {}", fileName, bucketName, e.getMessage());
        }
    }

    private void uploadToBucket(String fileName, String bucketName, Map<String, String> metadata, String content) {
        try {
            log.info("Записываем файл '{}' в '{}'", fileName, bucketName);
            InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);
            humoMinioClient.putObject(PutObjectArgs.builder()
                    .stream(inputStream, inputStream.available(), -1)
                    .bucket(bucketName)
                    .object(fileName)
                    .userMetadata(metadata)
                    .build());
            log.info("Файл '{}' был перемещен в '{}'", fileName, bucketName);
        } catch (Exception e) {
            log.error("Ошибка при записи файла '{}' в '{}': {}", fileName, bucketName, e.getMessage());
        }
    }

    private List<String> getFilesNameOrderedByDate() {
        Iterable<Result<Item>> filesInfo = humoMinioClient.listObjects(ListObjectsArgs.builder()
                .bucket(minioProperties.getHumo().getBucket())
                .maxKeys(minioProperties.getHumo().getMaxKeys()).build());
        Map<String, Long> fileNameToDate = new HashMap<>();
        Iterator<Result<Item>> iterator = filesInfo.iterator();
        while (iterator.hasNext()) {
            try {
                Item item = iterator.next().get();
                fileNameToDate.put(item.objectName(), item.lastModified().toEpochSecond());
            } catch (Exception e) {
                log.error("Ошибка при получении информации о файлах", e);
            }
        }
        return fileNameToDate.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList();
    }
}
