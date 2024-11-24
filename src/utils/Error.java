package utils;

import frontend.enums.ErrorType;

public class Error implements Comparable<Error> {
    private final ErrorType type;
    private final int lineno;

    public Error(ErrorType type, int lineno) {
        this.type = type;
        this.lineno = lineno;
    }

    public String toString() {
        return lineno + " " + type.toString() + "\n";
    }

    @Override
    public int compareTo(Error o) {
        return Integer.compare(this.lineno, o.lineno);
    }
}
