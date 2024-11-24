package utils;

import frontend.lexer.Token;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Printer {

    private static FileOutputStream lexerFile = null;
    private static FileOutputStream parserFile = null;
    private static FileOutputStream errorFile = null;
    private static FileOutputStream symbolFile = null;
    private static FileOutputStream llvmIrFile = null;

    public static void init() throws FileNotFoundException {
        lexerFile = new FileOutputStream("lexer.txt");
        parserFile = new FileOutputStream("parser.txt");
        errorFile = new FileOutputStream("error.txt");
        symbolFile = new FileOutputStream("symbol.txt");
        llvmIrFile = new FileOutputStream("llvm_ir.txt");
        try {
            llvmIrFile.write(("declare i32 @getint()          ; 读取一个整数\n" +
                            "declare i32 @getchar()     ; 读取一个字符\n" +
                            "declare void @putint(i32)      ; 输出一个整数\n" +
                            "declare void @putch(i32)       ; 输出一个字符\n" +
                            "declare void @putstr(i8*)      ; 输出字符串\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printTokens(List<Token> tokenList) throws IOException {
        for (Token token : tokenList) {
            printToken(token);
        }
    }

    public static void printToken(Token token) throws IOException {
        lexerFile.write(token.toString().getBytes());
    }

    public static void printSyntaxComp(String syntaxComp) throws IOException {
        parserFile.write(syntaxComp.getBytes());
    }

    public static void printSymbol(String symbolText) throws IOException {
        symbolFile.write(symbolText.getBytes());
    }

    public static void printErrorMessage(Error error) throws IOException {
        errorFile.write(error.toString().getBytes());
    }

    public static void close() throws IOException {
        lexerFile.close();
        parserFile.close();
        errorFile.close();
        symbolFile.close();
        llvmIrFile.close();
    }
}
