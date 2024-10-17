import frontend.lexer.Lexer;
import frontend.lexer.TokenStream;
import frontend.parser.Parser;
import frontend.parser.node.Node;
import utils.Printer;
import utils.Recorder;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class Compiler {
    public static void main(String[] args) throws Exception {
        String path = "testfile.txt";
        PushbackInputStream input = new PushbackInputStream(new FileInputStream(path));
        Printer.init();

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(new TokenStream(lexer.getTokenList()));
        Printer.printTokens(lexer.getTokenList());
        parser.parse();

        Node compUnit = parser.getCompUnit();
        compUnit.print();
        Recorder.printErrorMessages();

        input.close();
        Printer.close();
    }
}
