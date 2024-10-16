package utils;

import frontend.lexer.Token;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Printer {

    private static FileOutputStream lexerFile = null;
    private static FileOutputStream parserFile = null;
    private static FileOutputStream errorFile = null;

    public static void init() throws FileNotFoundException {
        lexerFile = new FileOutputStream("lexer.txt");
        parserFile = new FileOutputStream("parser.txt");
        errorFile = new FileOutputStream("error.txt");
    }

    public static void printToken(Token token) throws IOException {
        lexerFile.write(token.toString().getBytes());
    }

    public static void printSyntaxComp(String syntaxComp) throws IOException {
        parserFile.write(syntaxComp.getBytes());
    }

    public static void printErrorMessage(Error error) throws IOException {
        errorFile.write(error.toString().getBytes());
    }

    public static void close() throws IOException {
        lexerFile.close();
        parserFile.close();
        errorFile.close();
    }
}
