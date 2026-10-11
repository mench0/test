package com.tms.profilemanager.app.integration.humo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@XmlRootElement(name = "MERCHANT")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class MerchantHumo {

    @XmlElement(name = "NAME")
    String name;
    @XmlElement(name = "MID")
    String mid;
    @XmlElement(name = "TID")
    String tid;
    @XmlElement(name = "MCC")
    String mcc;
    @XmlElement(name = "CITY")
    String city;
    @XmlElement(name = "STREET")
    String street;
    @XmlElement(name = "TTYPE")
    OperationType type;

    @XmlAccessorType(XmlAccessType.FIELD)
    public enum OperationType {
        I, U, D
    }
}
