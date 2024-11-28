package llvm.value.instruction.terminator;

import llvm.value.Value;
import llvm.value.instruction.base.TerminatorInstruction;
import llvm.ir.IRType;

public class BranchInstruction extends TerminatorInstruction {
    private final boolean isConditional;

    public BranchInstruction(Value target) {
        super(IRType.VoidIRType.getInstance(), 1);
        this.isConditional = false;
        setOperand(0, target);
    }
    
    public BranchInstruction(Value condition, Value trueTarget, Value falseTarget) {
        super(IRType.VoidIRType.getInstance(), 3);
        this.isConditional = true;
        setOperand(0, condition);
        setOperand(1, trueTarget);
        setOperand(2, falseTarget);
    }

    public boolean isConditional() { return isConditional; }

    public Value getTarget() { return getOperand(0); }

    public Value getCondition() { return getOperand(0); }

    public Value getTrueTarget() { return getOperand(1); }

    public Value getFalseTarget() { return getOperand(2); }

    @Override
    public String getInstructionName() {
        return "br";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isConditional) {
            sb.append(getInstructionName()).append(" ");
            sb.append(getCondition().getType()).append(" ").
            append(getCondition().getName());
            sb.append(", label ").append(getTrueTarget().getName());
            sb.append(", label ").append(getFalseTarget().getName());
        } else {
            sb.append(getInstructionName()).append(" label ").
            append(getTarget().getName());
        }
        return sb.toString();
    }
}
