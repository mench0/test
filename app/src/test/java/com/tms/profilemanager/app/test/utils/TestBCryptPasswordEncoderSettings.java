package com.tms.profilemanager.app.test.utils;

import com.tms.profilemanager.app.config.security.SecurityConfiguration.BCryptPasswordEncoderSettings;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class TestBCryptPasswordEncoderSettings extends BCryptPasswordEncoderSettings {

    @Override
    public int getStrength() {
        return 4; // optimization for tests
    }
}
