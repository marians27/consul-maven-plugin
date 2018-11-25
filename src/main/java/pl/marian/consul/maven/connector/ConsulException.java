package pl.marian.consul.maven.connector;

public class ConsulException extends RuntimeException {

    public ConsulException(String message) {
        super(message);
    }

    public ConsulException(String message, Throwable cause) {
        super(message, cause);
    }
}
