package edu.bbte.idde.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.bbte.idde.data.exception.ConfigLoadException;

import java.io.IOException;
import java.io.InputStream;

public final class ConfigLoader {

    private static final ApplicationConfig CONFIG = loadConfigInternalImpl();

    private ConfigLoader() {
    }

    public static ApplicationConfig load() {
        return CONFIG;
    }

    private static ApplicationConfig loadConfigInternalImpl() {
        String profile = System.getenv("APP_PROFILE");
        if (profile == null) {
            profile = System.getProperty("app.profile", "inmemory");
        }

        String fileName = switch (profile) {
            case "jdbc" -> "config-jdbc.xml";
            case "inmemory" -> "config-inmemory.xml";
            default -> "config-inmemory.xml";
        };

        XmlMapper mapper = new XmlMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = loader.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new ConfigLoadException("Configuration file not found: " + fileName);
            }
            return mapper.readValue(is, ApplicationConfig.class);
        } catch (IOException e) {
            throw new ConfigLoadException("Error reading configuration: " + e.getMessage(), e);
        }
    }
}