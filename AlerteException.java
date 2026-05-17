package TP;

public class AlerteException extends Exception {
    private Gravite gravite;

    public AlerteException(String message, Gravite gravite) {
        super(message);
        this.gravite = gravite;
    }

    public Gravite getGravite() {
        return gravite;
    }
}
