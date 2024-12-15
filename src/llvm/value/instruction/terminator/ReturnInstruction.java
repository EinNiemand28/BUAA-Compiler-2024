package llvm.value.instruction.terminator;

import llvm.value.Value;
import llvm.value.BasicBlock;
import llvm.value.instruction.base.TerminatorInstruction;
import llvm.ir.IRType;
import java.util.List;
import java.util.ArrayList;


public class ReturnInstruction extends TerminatorInstruction {
    public ReturnInstruction() {
        super(IRType.VoidIRType.getInstance(), 0);
    }

    public ReturnInstruction(Value value) {
        super(value.getType(), 1);
        setOperand(0, value);
    }

    public boolean hasReturnValue() {
        return getNumOperands() > 0;
    }

    public Value getReturnValue() {
        return hasReturnValue() ? getOperand(0) : null;
    }

    public IRType getReturnType() {
        return hasReturnValue() ? getReturnValue().getType() : IRType.VoidIRType.getInstance();
    }

    @Override
    public void buildCFG() {
        BasicBlock currentBB = getParent();
        if (currentBB != null && currentBB.getParent() != null) {
            currentBB.getParent().setExitBlock(currentBB);
        }
    }

    @Override
    public List<BasicBlock> getSuccessors() {
        return new ArrayList<>();
    }

    @Override
    public String getInstructionName() {
        return "ret";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getInstructionName());
        if (hasReturnValue()) {
            sb.append(" ").append(getReturnValue().getType())
            .append(" ").append(getReturnValue().getName());
        } else {
            sb.append(" void");
        }
        return sb.toString();
    }
}
