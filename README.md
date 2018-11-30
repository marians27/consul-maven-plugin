# Consul Maven Plugin [![Build Status](https://travis-ci.com/marians27/consul-maven-plugin.svg?branch=master)](https://travis-ci.com/marians27/consul-maven-plugin)

Maven plugin for exporting properties files to the Consul key-value store

## Getting Started
### Configuration
In pom.xml add following section
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>pl.marian.consul</groupId>
                <artifactId>consul-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <consulHost>127.0.0.1</consulHost>
                    <consulPort>8500</consulPort>
                    <consulKvFolder>config/my-app</consulKvFolder>
                    <propertyDir>src/main/resources/test</propertyDir>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
### Running plugin
```bash
mvn consul:propertiesExport
```
