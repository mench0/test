package com.tms.profilemanager.app.integration.humo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@XmlRootElement(name = "Terminals")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class Terminals {

    @XmlAttribute(name = "Count")
    String count;
    @XmlElement(name = "Terminal")
    List<TerminalHumo> terminal;

}