package llvm.value.instruction.terminator;

import llvm.value.Value;
import llvm.value.instruction.base.TerminatorInstruction;
import llvm.ir.IRType;

public class ReturnInstruction extends TerminatorInstruction {
    public ReturnInstruction() {
        super(IRType.VoidIRType.getInstance(), 0);
    }

    public ReturnInstruction(Value returnValue) {
        super(returnValue.getType(), 1);
        setOperand(0, returnValue);
    }

    public boolean hasReturnValue() {
        return getNumOperands() > 0;
    }

    public Value getReturnValue() {
        return getOperand(0);
    }

    @Override
    public String getInstructionName() {
        return "ret";
    }

    @Override
    public String toString() {
        if (hasReturnValue()) {
            return String.format("%s %s %s", 
            getInstructionName(), getReturnValue().getType(), 
            getReturnValue().getName());
        } else {
            return String.format("%s %s", 
            getInstructionName(), getType());
        }
    }
}
