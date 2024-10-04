package enums;

public enum ErrorType {
    illegalSymbol("a"),
    nameRedefinition("b"),
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
