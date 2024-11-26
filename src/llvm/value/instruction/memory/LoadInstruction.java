package llvm.value.instruction.memory;

import llvm.value.instruction.base.MemoryInstruction;
import llvm.value.Value;

public class LoadInstruction extends MemoryInstruction {
    public LoadInstruction(Value pointer) {
        super(((llvm.ir.IRType.PointerIRType)pointer.getType()).getElementType(), 2);
        setOperand(0, pointer);
    }

    public Value getPointer() { return getOperand(0); }
    
    @Override
    public String getInstructionName() {
        return "load";
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s, %s %s", 
        getName(), getInstructionName(), getType(), 
        getPointer().getType(), getPointer().getName());
    }
}
