package enums;

public enum ErrorType {
    illegalSymbol("a"),
    nameRedefinition("b"),
    missingSEMICN("i"),
    missingRPARENT("j"),
    missingRBRACK("k"),
    ;

    private final String value;
    ErrorType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
