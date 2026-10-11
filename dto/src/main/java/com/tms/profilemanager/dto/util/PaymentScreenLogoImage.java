package com.tms.profilemanager.dto.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
public class PaymentScreenLogoImage implements Serializable {

    String id;

    String contentType;

    byte[] content;

}
