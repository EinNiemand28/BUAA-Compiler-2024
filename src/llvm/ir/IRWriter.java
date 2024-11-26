package llvm.ir;

import llvm.value.Function;
import llvm.value.GlobalValue;
import llvm.value.Parameter;
import llvm.value.instruction.base.Instruction;
import llvm.value.BasicBlock;

import java.util.List;

public class IRWriter {
    private final Module module;
    private final StringBuilder ir;
    private final SlotTracker slotTracker;

    public IRWriter(Module module) {
        this.module = module;
        this.ir = new StringBuilder();
        this.slotTracker = new SlotTracker();
    }

    public String generateIr() {
        generateBuildInFunctions();
        generateGlobalValues();
        generateFunctions();

        return ir.toString();
    }

    private void generateBuildInFunctions() {
        ir.append("declare i32 @getint()\n");
        ir.append("declare i32 @getchar()\n");
        ir.append("declare void @putint(i32)\n");
        ir.append("declare void @putchar(i32)\n");
        ir.append("declare void @putstr(i8*)\n");
    }

    private void generateGlobalValues() {
        for (GlobalValue global : module.getGlobalsValues()) {
            ir.append(global).append("\n");
        }
        ir.append("\n");
    }

    private void generateFunctions() {
        for (Function func : module.getFunctions()) {
            generateFunction(func);
            ir.append("\n");
        }
    }
    
    private void generateFunction(Function func) {
        slotTracker.reset();
        ir.append("define dso_local ").append(func.getReturnType()).
        append(" @").append(func.getName()).append("(");

        List<Parameter> params = func.getParameters();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) { ir.append(", "); }
            ir.append(params.get(i)).append(" ");
        }
        ir.append(") {\n");

        for (BasicBlock bb : func.getBasicBlocks()) {
            generateBasicBlock(bb);
        }
        ir.append("}\n");
    }

    private void generateBasicBlock(BasicBlock bb) {
        // only entry block now
        ir.append(bb.getNameForLabel()).append(":\n");
        for (Instruction inst : bb.getInstructions()) {
            ir.append("    ").append(inst).append("\n");
        }
        ir.append("\n");
    }
}
