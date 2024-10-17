import frontend.lexer.Lexer;
import frontend.lexer.TokenStream;
import frontend.parser.Parser;
import frontend.parser.node.CompUnitNode;
import utils.Printer;
import utils.Recorder;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class Compiler {
    // TODO
    public static void main(String[] args) throws Exception {
        PushbackInputStream input = new PushbackInputStream(new FileInputStream("testfile.txt"));
        Printer.init();

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(new TokenStream(lexer.getTokenList()));
        parser.parse();
        CompUnitNode compUnit = parser.getCompUnit();

        Recorder.printErrorMessages();

        input.close();
        Printer.close();
    }
}
