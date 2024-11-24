package utils;

import frontend.enums.ErrorType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Recorder {
    private static final List<Error> errors = new ArrayList<>();

    public static void addErrorMessage(ErrorType error, int lineno) {
        errors.add(new Error(error, lineno));
    }

    public static List<Error> getErrors() {
        Collections.sort(errors);
        return errors;
    }

    public static void printErrorMessages() throws IOException {
        Collections.sort(errors);
        for (Error error : errors) {
            Printer.printErrorMessage(error);
        }
    }
}
