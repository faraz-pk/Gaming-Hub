package Exceptions;
import Logic.UnoCard;

class InvalidValueSubmissionException extends Exception {
    private UnoCard.Value expected;
    private UnoCard.Value actual;

    public InvalidValueSubmissionException(String var1, UnoCard.Value var2, UnoCard.Value var3) {
        this.actual = var2;
        this.expected = var3;
    }
}