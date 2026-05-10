package Exceptions;
import Logic.UnoCard;

class InvalidColorSubmissionException extends Exception {
    private UnoCard.Color expected;
    private UnoCard.Color actual;

    public InvalidColorSubmissionException(String var1, UnoCard.Color var2, UnoCard.Color var3) {
        this.actual = var2;
        this.expected = var3;
    }
}

