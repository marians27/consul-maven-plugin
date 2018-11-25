package pl.marian.consul.maven.connector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

import static java.lang.String.format;

public class ConsulConnector {

    private static final String KV_PUT_URL_TEMPLATE = "http://%s:%d/v1/kv/%s/%s";

    private final String consulHost;

    private final int consulPort;

    private final String kvFolder;

    public ConsulConnector(String consulHost, int consulPort, String kvFolder) {
        this.consulHost = consulHost;
        this.consulPort = consulPort;
        this.kvFolder = kvFolder;
    }

    public void putKeyValue(Object key, Object value) {
        HttpResponse response = sendRequest(key, value);
        handleResponse(key, value, response);
    }

    private void handleResponse(Object key, Object value, HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (isSuccessful(statusCode)) {
            throw new ConsulException(format("Response code %d after sending %s, %s to consul", statusCode, key, value));
        }
    }

    private HttpResponse sendRequest(Object key, Object value) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut(createUrl(key));
        try {
            request.setEntity(new StringEntity(value.toString()));
            return client.execute(request);
        } catch (IOException e) {
            throw new ConsulException(format("Error during sending %s, %s to consul", key, value), e);
        }
    }

    private String createUrl(Object key) {
        return format(KV_PUT_URL_TEMPLATE, consulHost, consulPort, kvFolder, key);
    }

    private boolean isSuccessful(int statusCode) {
        return statusCode >= 300 || statusCode < 200;
    }
}
