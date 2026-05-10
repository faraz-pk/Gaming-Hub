package Exceptions;

class InvalidPlayerTurnException extends Exception {
    String playerId;

    public InvalidPlayerTurnException(String var1, String var2) {
        super(var1);
        this.playerId = var2;
    }

    public String getpid() {
        return this.playerId;
    }
}
