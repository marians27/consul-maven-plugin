package pl.marian.consul.maven.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class PropertyLoader {

    private final String baseDirectory;

    public PropertyLoader(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public Properties loadProperties(String fileName) {
        try {
            Properties prop = new Properties();
            try (FileInputStream input = new FileInputStream(new File(baseDirectory, fileName))) {
                prop.load(input);
            }
            return prop;
        } catch (IOException e) {
            //TODO: Better exception handling!
            throw new RuntimeException(e);
        }
    }

    public List<String> listPropertyFiles() {
        File propertyDirectory = new File(baseDirectory);
        String[] files = propertyDirectory.list((dir, name) -> name.endsWith(".properties"));
        if (files != null) {
            return Arrays.asList(files);
        }
        return Collections.emptyList();
    }

}
