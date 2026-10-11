package com.tms.profilemanager.app.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptTemplate extends BaseEntity {

    @Column(unique = true)
    Long externalId;

    Long merchantId;

    String name;

    @Embedded
    Header header;

    @Embedded
    Footer footer;

    String logoFileId;

    boolean active;

    @Column(nullable = false)
    OffsetDateTime validFrom;

    @Column(nullable = false)
    OffsetDateTime validTo;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Header implements Serializable {
        @Column(name = "header_text")
        String text;

        @Column(name = "header_font_size")
        String fontSize;

        @Column(name = "header_italic")
        boolean italic;

        @Column(name = "header_bold")
        boolean bold;

        @Column(name = "header_underline")
        boolean underline;

        @Column(name = "header_text_align")
        String textAlign;

    }

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Footer implements Serializable {

        @Column(name = "footer_text")
        String text;

        @Column(name = "footer_font_size")
        String fontSize;

        @Column(name = "footer_italic")
        boolean italic;

        @Column(name = "footer_bold")
        boolean bold;

        @Column(name = "footer_underline")
        boolean underline;

        @Column(name = "footer_text_align")
        String textAlign;
    }
}
