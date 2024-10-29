package qiwi.test_task.exception;

public class InvalidWalletRequestException extends RuntimeException {
    public InvalidWalletRequestException(String message) {
        super(message);
    }
}
