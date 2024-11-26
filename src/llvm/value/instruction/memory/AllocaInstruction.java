package llvm.value.instruction.memory;

import llvm.ir.IRType;
import llvm.value.instruction.base.MemoryInstruction;

public class AllocaInstruction extends MemoryInstruction {
    private final IRType allocatedIRType;

    public AllocaInstruction(IRType IRType) {
        super(llvm.ir.IRType.PointerIRType.get(IRType), 0);
        this.allocatedIRType = IRType;
    }

    public IRType getAllocatedType() { return allocatedIRType; }

    @Override
    public String getInstructionName() {
        return "alloca";
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s", 
        getName(), getInstructionName(), getAllocatedType());
    }
}
