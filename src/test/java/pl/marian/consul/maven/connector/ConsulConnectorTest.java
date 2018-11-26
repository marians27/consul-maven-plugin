package pl.marian.consul.maven.connector;

import com.pszymczyk.consul.junit.ConsulResource;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsulConnectorTest {

    @ClassRule
    public static final ConsulResource consul = new ConsulResource();

    private HttpClient client = HttpClientBuilder.create().build();

    @Test
    public void consulIsRunning() throws Exception {
        HttpGet request = new HttpGet(createUrl("/v1/agent/self"));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    public void connectorShouldStoreBooleanValue() throws Exception {
        ConsulConnector consulConnector = createConnector("config/test");

        consulConnector.putKeyValue("feature.enabled", true);

        HttpGet request = new HttpGet(createUrl("/v1/kv/config/test/feature.enabled"));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(extractStoredValue(response)).isEqualTo("true");
    }

    @Test
    public void connectorShouldStoreIntegerValue() throws Exception {
        ConsulConnector consulConnector = createConnector("config");

        consulConnector.putKeyValue("count", 111);

        HttpGet request = new HttpGet(createUrl("/v1/kv/config/count"));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(extractStoredValue(response)).isEqualTo("111");
    }

    @Test
    public void connectorShouldStoreStringValue() throws Exception {
        ConsulConnector consulConnector = createConnector("config");

        consulConnector.putKeyValue("name", "someName");

        HttpGet request = new HttpGet(createUrl("/v1/kv/config/name"));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(extractStoredValue(response)).isEqualTo("someName");
    }

    @Test
    public void connectorShouldStoreValueWithoutDirectory() throws Exception {
        ConsulConnector consulConnector = createConnector("");

        consulConnector.putKeyValue("name", "someName");

        HttpGet request = new HttpGet(createUrl("/v1/kv/name"));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(extractStoredValue(response)).isEqualTo("someName");
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

    private String extractStoredValue(HttpResponse response) throws IOException {
        String responseBody = EntityUtils.toString(response.getEntity());
        String value = new JSONArray(responseBody).getJSONObject(0).getString("Value");
        return new String(Base64.getDecoder().decode(value.getBytes()));
    }

    private ConsulConnector createConnector(String kvDirectory) {
        return new ConsulConnector("localhost", consul.getHttpPort(), kvDirectory);
    }

    private String createUrl(String apiPath) {
        return "http://localhost:" + consul.getHttpPort() + apiPath;
    }
}