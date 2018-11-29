package pl.marian.consul.maven.io;

import org.junit.Test;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyLoaderTest {

    @Test
    public void loadPropertiesShouldLoadAllPropertiesFromExistingFile() throws Exception {
        PropertyLoader propertyLoader = new PropertyLoader(baseDirectory("test-01"));

        Properties properties = propertyLoader.loadProperties("test1.properties");

        assertThat(properties).hasSize(3);
        assertThat(properties.getProperty("test1.feature1.enabled")).isEqualTo("true");
        assertThat(properties.getProperty("test1.feature1.name")).isEqualTo("Custom");
        assertThat(properties.getProperty("application")).isEqualTo("Test");
    }

    @Test(expected = LoaderException.class)
    public void loadPropertiesShouldThrowExceptionIfFileToBeLoadedDoesNotExists() throws Exception {
        PropertyLoader propertyLoader = new PropertyLoader(baseDirectory("test-01"));

        propertyLoader.loadProperties("unknown.properties");
    }

    @Test
    public void listPropertyFilesShouldListOnlyPropertiesFilesFromExistingDirectory() throws Exception {
        PropertyLoader propertyLoader = new PropertyLoader(baseDirectory("test-01"));

        List<String> propertyFiles = propertyLoader.listPropertyFiles();

        assertThat(propertyFiles).hasSize(2);
        assertThat(propertyFiles.get(0)).isEqualTo("test1.properties");
        assertThat(propertyFiles.get(1)).isEqualTo("test2.properties");
    }

    @Test(expected = LoaderException.class)
    public void listPropertyFilesShouldThrowExceptionIfBaseDirectoryDoesNotExists() throws Exception {
        PropertyLoader propertyLoader = new PropertyLoader("./unknown");

        List<String> propertyFiles = propertyLoader.listPropertyFiles();

        assertThat(propertyFiles).isEmpty();
    }

    @Test
    public void listPropertyFilesShouldReturnEmptyListIfBaseDirectoryDoesNotContainPropertiesFiles() throws Exception {
        PropertyLoader propertyLoader = new PropertyLoader(baseDirectory("test-02"));

        List<String> propertyFiles = propertyLoader.listPropertyFiles();

        assertThat(propertyFiles).isEmpty();
    }

    private String baseDirectory(String baseDirectoryName) throws Exception {
        return Paths
                .get(loadClassPathResource(baseDirectoryName).toURI())
                .toAbsolutePath()
                .toString();
    }

    private URL loadClassPathResource(String resourceName) {
        return getClass().getClassLoader().getResource(resourceName);
    }
}