package pl.marian.consul.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import pl.marian.consul.maven.connector.ConsulConnector;
import pl.marian.consul.maven.io.PropertyLoader;

import java.util.Properties;

@Mojo(name = "propertiesExport")
public class PropertiesExportMojo extends AbstractMojo {

    @Parameter(property = "consulHost", defaultValue = "localhost")
    private String consulHost;

    @Parameter(property = "consulPort", defaultValue = "8500")
    private Integer consulPort;

    @Parameter(property = "consulKvFolder", defaultValue = "")
    private String consulKvFolder;

    @Parameter(property = "propertyDir", required = true)
    private String propertyDir;

    public void execute() throws MojoExecutionException {
        getLog().info("Exporting properties to Consul KV Store");
        getLog().info("Consul Host: " + consulHost);
        getLog().info("Consul Port: " + consulPort);
        getLog().info("Exporting properties from: " + propertyDir);

        PropertyLoader propertyLoader = new PropertyLoader(propertyDir);
        ConsulConnector consulConnector = new ConsulConnector(consulHost, consulPort, consulKvFolder);

        propertyLoader.listPropertyFiles()
                .forEach(fileName -> processFile(propertyLoader, consulConnector, fileName));
    }

    private void processFile(PropertyLoader propertyLoader, ConsulConnector consulConnector, String fileName) {
        getLog().info("Processing file " + fileName);
        Properties prop = propertyLoader.loadProperties(fileName);
        prop.forEach(consulConnector::putKeyValue);
    }
}
