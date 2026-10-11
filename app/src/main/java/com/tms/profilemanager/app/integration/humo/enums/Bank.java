package com.tms.profilemanager.app.integration.humo.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Bank {

    IPOTEKA("Ипотека-банк", "9033", "01"),
    SANOAT_QURILISH("Узбекский промышленно-строительный банк", "9003", "02"),
    AGRO("Агробанк", "9004", "03"),
    ASAKA("Асака", "9013", "04"),
    BIZNESNI_RIVOJLANTIRISH("Кишлок курилиш банк", "9009", "06"),
    XALQ("Народный банк Республики Узбекистан", "9006", "08"),
    ASIA_ALLIANCE("Asia Alliance Bank", "1057", "09"),
    KAPITAL("Капиталбанк", "9049", "10"),
    NBU("НБУ Республики Узбекистан", "9002", "12"),
    MIKROKREDIT("Микрокредитбанк", "9005", "13"),
    GARANT("Савдогар", "9008", "14"),
    TURON("Туронбанк", "9011", "15"),
    HAMKOR("Hamkorbank", "9012", "16"),
    IPAK_YOLI("Ипак Йули", "9014", "17"),
    TRAST("Трастбанк", "9030", "18"),
    ALOQA("Алокабанк", "9031", "19"),
    KDB("КДБ Банк Узбекистан", "9034", "20"),
    TURKISTON("Туркистон", "9038", "21"),
    UNIVERSAL("Универсал банк", "9048", "23"),
    OCTOBANK("Ravnaq-bank", "0980", "24"),
    DAVR("Давр-банк", "9051", "25"),
    INVEST_FINANCE("Invest Finance Bank", "9053", "26"),
    ORIENT_FINANS("Ориент Финанс", "1071", "27"),
    HI_TECH("Hi-Tech Bank", "1066", "28"),
    ZIRAAT_BANK_UZBEKISTAN("Ziraat Bank", "0395", "29"),
    SADERAT("Дочерний банк банка Садерат Ирана в городе Ташкенте", "0950", "30"),
    AVO("UzAgroExportBank", "9059", "31"),
    MADAD_INVEST("MadadInvestBank", "9058", "32"),
    POYTAXT("Poytaxt Bank", "9060", "33"),
    TENGE("Tenge Bank", "9061", "34"),
    TBC("TBC", "9062", "35"),
    UZUM("Apelsin Bank", "9064", "36"),
    SMART("Hayot Bank", "9066", "37"),
    HAYOT("Yangi Bank", "9068", "38"),
    APEX("SMART BANK", "9065", "39"),
    YANGI("Apex Bank", "9067", "40"),
    ANOR("ANOR Bank", "9063", "60");

    String name;
    String mfo;
    String bankId;
}
