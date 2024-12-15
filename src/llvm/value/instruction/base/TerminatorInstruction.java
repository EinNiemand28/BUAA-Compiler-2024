package llvm.value.instruction.base;

import llvm.ir.IRType;
import llvm.value.BasicBlock;
import java.util.List;

public abstract class TerminatorInstruction extends Instruction {
    protected TerminatorInstruction(IRType type, int num) {
        super(type, "", num);
    }

    public abstract void buildCFG();

    public abstract List<BasicBlock> getSuccessors();
}
