package com.mrivanplays.server;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Component
public class ApplicationConfiguration {

    private String bcryptPassword;
    private File favicon;

    public ApplicationConfiguration() {
        File file = new File(".", "config.json");
        if (!file.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.json")) {
                Files.copy(in, file.getAbsoluteFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JsonNode object;
        try {
            object = ServerConstants.JSON_MAPPER.readTree(file);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot initialize config");
        }
        bcryptPassword = object.get("encodedPassword").asText();
        favicon = new File(object.get("faviconPath").asText());
    }

    public String getEncodedPassword() {
        return bcryptPassword;
    }

    public File getFavicon() {
        return favicon;
    }
}
