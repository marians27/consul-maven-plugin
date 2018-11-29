package pl.marian.consul.maven.io;

public class LoaderException extends RuntimeException {

    public LoaderException(String message) {
        super(message);
    }

    public LoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
