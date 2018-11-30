package pl.marian.consul.maven.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

public class ConsulTestUtil {

    private final HttpClient client = HttpClientBuilder.create().build();

    private final int consulPort;

    public ConsulTestUtil(int consulPort) {
        this.consulPort = consulPort;
    }

    public boolean isConsulRunning() throws Exception {
        HttpGet request = new HttpGet(createUrl("/v1/agent/self"));
        HttpResponse response = client.execute(request);

        return response.getStatusLine().getStatusCode() == 200;
    }

    public String loadPropertyValueFromConsul(String kvDirectory, String propertyName) throws IOException {
        HttpGet request = new HttpGet(createUrl("/v1/kv/" + kvDirectory + "/" + propertyName));
        HttpResponse response = client.execute(request);

        return extractStoredValue(response);
    }

    public boolean consulContainsProperty(String kvDirectory, String propertyName) throws IOException {
        HttpGet request = new HttpGet(createUrl("/v1/kv/" + kvDirectory + "/" + propertyName));
        HttpResponse response = client.execute(request);

        return response.getStatusLine().getStatusCode() == 200;
    }

    public List<String> findAllProperties(String kvDirectory) throws IOException {
        HttpGet request = new HttpGet(createUrl("/v1/kv/" + kvDirectory + "?keys"));
        HttpResponse response = client.execute(request);

        return extractListFromResponse(response);
    }

    private String extractStoredValue(HttpResponse response) throws IOException {
        String responseBody = EntityUtils.toString(response.getEntity());
        String value = new JSONArray(responseBody).getJSONObject(0).getString("Value");
        return new String(Base64.getDecoder().decode(value.getBytes()));
    }

    private List<String> extractListFromResponse(HttpResponse response) throws IOException {
        String responseBody = EntityUtils.toString(response.getEntity());
        Iterator<Object> iterator = new JSONArray(responseBody).iterator();
        List<String> list = new ArrayList<>();
        iterator.forEachRemaining(obj -> list.add(obj.toString()));
        return list;
    }

    private String createUrl(String apiPath) {
        return "http://localhost:" + consulPort + apiPath;
    }
}
