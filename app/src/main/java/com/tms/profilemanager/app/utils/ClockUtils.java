package com.tms.profilemanager.app.utils;

import lombok.Setter;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class ClockUtils {

    @Setter
    private static Clock clock = Clock.tickMillis(ZoneOffset.UTC);

    public static OffsetDateTime now() {
        return OffsetDateTime.now(clock);
    }

    public static ZoneId getZone() {
        return clock.getZone();
    }
}
