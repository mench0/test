package com.tms.profilemanager.app.utils;

import freemarker.template.Configuration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailSender {

    JavaMailSender mailSender;
    Configuration freemarkerConfiguration;


    @Value("${spring.mail.from}")
    String emailFrom;

    public static final String PASSWORD_GENERATED_TEMPLATE = "password-generated-email";
    public static final String RESET_PASSWORD_TEMPLATE = "reset-password-email";

    @SneakyThrows
    public void send(String to, String subject, String text, Map<String, Resource> inlineAttachments) {
        MimeMessage message = mailSender.createMimeMessage();
        message.setSubject(subject);
        MimeMessageHelper helper;
        helper = new MimeMessageHelper(message, true);
        helper.setFrom(new InternetAddress(emailFrom));
        helper.setTo(to);
        helper.setText(text, true);
        for (Map.Entry<String, Resource> entry: inlineAttachments.entrySet()) {
            helper.addInline(entry.getKey(), entry.getValue());
        }
        mailSender.send(message);
    }

    @SneakyThrows
    public String getContent(String templateName, Map<String, Object> args) {
        StringWriter stringWriter = new StringWriter();
        freemarkerConfiguration.getTemplate(templateName + ".ftlh").process(args, stringWriter);
        return stringWriter.getBuffer().toString();
    }

    public Map<String, Resource> getInlineAttachments() {
        return Map.of(
                "logo", new ClassPathResource("templates/img/logo.jpg")
        );
    }
}
