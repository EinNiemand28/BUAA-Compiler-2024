import frontend.Lexer.Lexer;
import frontend.Lexer.Token;
import frontend.Lexer.TokenStream;
import util.Printer;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class Compiler {

    public static void main(String[] args) throws Exception {
        PushbackInputStream input = new PushbackInputStream(new FileInputStream("testfile.txt"));
        Printer.init();

        Lexer lexer = new Lexer(input);
        TokenStream tokenStream = new TokenStream(lexer.getTokenList());
        for (Token token = tokenStream.read(); token != null; token = tokenStream.read()) {
            Printer.printToken(token);
        }

        input.close();
        Printer.close();
    }
}
