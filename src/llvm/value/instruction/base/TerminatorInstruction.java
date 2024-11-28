package llvm.value.instruction.base;

import llvm.ir.IRType;

public abstract class TerminatorInstruction extends Instruction {
    protected TerminatorInstruction(IRType type, int num) {
        super(type, "", num);
    }
}
