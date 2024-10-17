package utils;

import enums.ErrorType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Recorder {
    private static final List<Error> errors = new ArrayList<>();

    public static void addErrorMessage(ErrorType error, int lineno) {
        errors.add(new Error(error, lineno));
    }

    public static List<Error> getErrors() {
        return errors;
    }

    public static void printErrorMessages() throws IOException {
        for (Error error : errors) {
            Printer.printErrorMessage(error);
        }
    }
}
