package com.tms.profilemanager.app.integration.humo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class TerminalHumo {

    @XmlAttribute(name = "CopyFromID")
    String copyFromID;
    @XmlElement(name = "SERIAL_NR")
    String serialNr;
    @XmlElement(name = "MERCHANT")
    MerchantHumo merchant;
    @XmlElement(name = "BANK_COD")
    String bankId;

}
