package pl.marian.consul.maven.connector;

import com.pszymczyk.consul.junit.ConsulResource;
import org.junit.ClassRule;
import org.junit.Test;
import pl.marian.consul.maven.test.ConsulTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsulConnectorTest {

    @ClassRule
    public static final ConsulResource consul = new ConsulResource();

    private ConsulTestUtil consulTestUtil = new ConsulTestUtil(consul.getHttpPort());

    @Test
    public void consulIsRunning() throws Exception {
        assertThat(consulTestUtil.isConsulRunning()).isTrue();
    }

    @Test
    public void connectorShouldStoreBooleanValue() throws Exception {
        ConsulConnector consulConnector = createConnector("config/test");

        consulConnector.putKeyValue("feature.enabled", true);

        assertThat(consulTestUtil.consulContainsProperty("config/test", "feature.enabled")).isTrue();
        assertThat(consulTestUtil.loadPropertyValueFromConsul("config/test", "feature.enabled")).isEqualTo("true");
    }

    @Test
    public void connectorShouldStoreIntegerValue() throws Exception {
        ConsulConnector consulConnector = createConnector("config");

        consulConnector.putKeyValue("count", 111);

        assertThat(consulTestUtil.consulContainsProperty("config", "count")).isTrue();
        assertThat(consulTestUtil.loadPropertyValueFromConsul("config", "count")).isEqualTo("111");
    }

    @Test
    public void connectorShouldStoreStringValue() throws Exception {
        ConsulConnector consulConnector = createConnector("config");

        consulConnector.putKeyValue("name", "someName");

        assertThat(consulTestUtil.consulContainsProperty("config", "name")).isTrue();
        assertThat(consulTestUtil.loadPropertyValueFromConsul("config", "name")).isEqualTo("someName");
    }

    @Test
    public void connectorShouldStoreValueWithoutDirectory() throws Exception {
        ConsulConnector consulConnector = createConnector("");

        consulConnector.putKeyValue("name", "someName");

        assertThat(consulTestUtil.consulContainsProperty("", "name")).isTrue();
        assertThat(consulTestUtil.loadPropertyValueFromConsul("", "name")).isEqualTo("someName");
    }

    @Test(expected = ConsulException.class)
    public void connectorShouldThrowExceptionIfConsulWronglyConfigured() throws Exception {
        ConsulConnector consulConnector = new ConsulConnector("unknown", 1111, "config");

        consulConnector.putKeyValue("name", "someName");
    }

    @Test(expected = ConsulException.class)
    public void connectorShouldThrowExceptionIfPropertyNameIsInvalid() throws Exception {
        ConsulConnector consulConnector = createConnector("config");

        consulConnector.putKeyValue("../../test/dd/", "someName");
    }

    private ConsulConnector createConnector(String kvDirectory) {
        return new ConsulConnector("localhost", consul.getHttpPort(), kvDirectory);
    }
}