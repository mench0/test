package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.config.PaymentScreenLogoProperties;
import com.tms.profilemanager.app.mapper.entity.PaymentScreenLogoMapper;
import com.tms.profilemanager.app.service.PaymentScreenLogoService;
import com.tms.profilemanager.dto.PaymentScreenLogoDto;
import com.tms.profilemanager.dto.create.PaymentScreenLogoCreateDto;
import com.tms.profilemanager.dto.patch.PaymentScreenLogoPatchDto;
import com.tms.profilemanager.dto.util.PaymentScreenLogoImage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payment-screen-logos")
@Tag(name = "Payment screen logos", description = "Логотипы экрана оплаты")
@RequiredArgsConstructor
public class PaymentScreenLogoController {

    private final PaymentScreenLogoService paymentScreenLogoService;
    private final PaymentScreenLogoMapper paymentScreenLogoMapper;
    private final PaymentScreenLogoProperties paymentScreenLogoProperties;

    @GetMapping
    @Operation(summary = "Загрузка всех логотипов")
    public ResponseEntity<List<PaymentScreenLogoDto>> getAllLogos() {
        return ResponseEntity.ok(paymentScreenLogoMapper.toDtoList(paymentScreenLogoService.findAllLogos()));
    }

    @GetMapping("/{logoId}")
    @Operation(summary = "Загрузка логотипа по id")
    public ResponseEntity<PaymentScreenLogoDto> getLogoById(@PathVariable("logoId") Long id) {
        return ResponseEntity.ok(paymentScreenLogoMapper.toDto(paymentScreenLogoService.findLogoById(id)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузка логотипа")
    public void upload(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart(value = "createDto") PaymentScreenLogoCreateDto inputDto
    ) {
        paymentScreenLogoService.upload(file, inputDto);
    }

    @PutMapping(value = "/{logoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Изменение логотипа по id")
    public void update(
            @PathVariable("logoId") Long id,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @Valid @RequestPart PaymentScreenLogoPatchDto patchDto
    ) {
        paymentScreenLogoService.update(id, patchDto, file);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Удаление логотипа по id")
    public void delete(@PathVariable("id") Long id) {
        paymentScreenLogoService.delete(id);
    }

    @GetMapping(value = "/images/{imageId}", produces = MediaType.ALL_VALUE)
    @Operation(summary = "Получить картинку по imageId")
    public ResponseEntity<byte[]> getPaymentScreenLogoImage(@PathVariable("imageId") String imageId) {
        PaymentScreenLogoImage image = paymentScreenLogoService.downloadByImageId(imageId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=" + paymentScreenLogoProperties.getImageCacheTtl().toSeconds())
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(image.getContent());
    }
}
