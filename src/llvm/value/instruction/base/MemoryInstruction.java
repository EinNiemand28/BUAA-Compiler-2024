package llvm.value.instruction.base;

import llvm.ir.IRType;

public abstract class MemoryInstruction extends Instruction {
    protected MemoryInstruction(IRType type, int num) {
        super(type, "", num);
    }
}
