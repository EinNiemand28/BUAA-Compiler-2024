package enums;

public enum ErrorType {
    IllegalSymbol("a"),
    RedefinedName("b"),
    UndefinedName("c"),
    MismatchedParamNum("d"),
    MismatchedParamType("e"),
    MismatchedReturnStmt("f"),
    MissingReturnStmt("g"),
    AssignToConst("h"),
    MissingSEMICN("i"),
    MissingRPARENT("j"),
    MissingRBRACK("k"),
    MismatchedPrintfArgs("l"),
    BreakOrContinueOutsideBlock("m"),
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
