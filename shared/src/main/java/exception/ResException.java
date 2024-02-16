package exception;

public class ResException extends Exception {

    final private int status;
    public ResException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}
