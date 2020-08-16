package uk.co.lewisod.doctor;

class ProviderCreationException extends RuntimeException {
    public ProviderCreationException(String message) {
        super(message);
    }

    public ProviderCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
