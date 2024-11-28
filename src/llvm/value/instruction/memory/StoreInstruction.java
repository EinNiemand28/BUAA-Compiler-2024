package llvm.value.instruction.memory;

import llvm.value.Value;
import llvm.value.instruction.base.MemoryInstruction;
import llvm.ir.IRType;

public class StoreInstruction extends MemoryInstruction {
    public StoreInstruction(Value value, Value pointer) {
        super(IRType.VoidIRType.getInstance(), 2);
        setOperand(0, value);
        setOperand(1, pointer);
    }

    public Value getValue() { return getOperand(0); }
    public Value getPointer() { return getOperand(1); }

    @Override
    public String getInstructionName() {
        return "store";
    }

    @Override
    public String toString() {
        return String.format("%s %s %s, %s %s",
        getInstructionName(), getValue().getType(), getValue().getName(), 
        getPointer().getType(), getPointer().getName());
    }
}
