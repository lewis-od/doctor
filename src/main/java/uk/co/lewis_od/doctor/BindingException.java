package uk.co.lewis_od.doctor;

public class BindingException extends RuntimeException {

    public BindingException(final String message) {
        super(message);
    }

    public BindingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
