import frontend.lexer.Lexer;
import frontend.lexer.TokenStream;
import frontend.parser.Parser;
import frontend.parser.node.CompUnitNode;
import frontend.parser.node.Node;
import frontend.symbol.SymbolTable;
import frontend.visitor.Visitor;
import llvm.ir.IRGenerator;
import llvm.ir.Module;
import utils.Printer;
import utils.Recorder;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class Compiler {
    public static void main(String[] args) throws Exception {
        // System.out.println(System.getProperty("user.dir"));
        String path = "testfile.txt";
        // String path = "./Compiler/testfile.txt";
        PushbackInputStream input = new PushbackInputStream(new FileInputStream(path));
        Printer.init();

        Lexer lexer = new Lexer(input);
        Printer.printTokens(lexer.getTokenList());

        Parser parser = new Parser(new TokenStream(lexer.getTokenList()));
        parser.parse();
        Node compUnit = parser.getCompUnit();
        compUnit.print();

        Visitor.getInstance().visit((CompUnitNode) compUnit);
        SymbolTable symbolTable = Visitor.getInstance().getCurTable();
        symbolTable.print();

        if (Recorder.getErrors().isEmpty()) {
            IRGenerator.getInstance().setSymbolTable(symbolTable);
            IRGenerator.getInstance().visit((CompUnitNode) compUnit);
            Module module = IRGenerator.getInstance().getIrModule();
            Printer.printIr(module);

            // MIPSGenerator.getInstance().setIrModule(module);
            // MIPSGenerator.getInstance().generate();
            // Target target = MIPSGenerator.getInstance().getTarget();
            // Printer.printMips(target);
        }
        Recorder.printErrorMessages();

        input.close();
        Printer.close();
    }
}
