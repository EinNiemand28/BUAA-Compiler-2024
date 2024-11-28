package llvm.value.instruction.memory;

import llvm.ir.IRType;
import llvm.value.instruction.base.MemoryInstruction;

public class AllocaInstruction extends MemoryInstruction {
    private final IRType allocatedIRType;

    public AllocaInstruction(IRType type) {
        super(IRType.PointerIRType.get(type), 0);
        this.allocatedIRType = type;
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
