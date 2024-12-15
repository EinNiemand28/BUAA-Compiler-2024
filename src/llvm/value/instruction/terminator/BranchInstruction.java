package llvm.value.instruction.terminator;

import llvm.value.Value;
import llvm.value.BasicBlock;
import llvm.value.instruction.base.TerminatorInstruction;
import llvm.ir.IRType;
import java.util.List;
import java.util.ArrayList;

public class BranchInstruction extends TerminatorInstruction {
    private final boolean isConditional;

    public BranchInstruction(BasicBlock target) {
        super(IRType.VoidIRType.getInstance(), 1);
        this.isConditional = false;
        setOperand(0, target);
    }
    
    public BranchInstruction(Value condition, BasicBlock trueTarget, BasicBlock falseTarget) {
        super(IRType.VoidIRType.getInstance(), 3);
        this.isConditional = true;
        setOperand(0, condition);
        setOperand(1, trueTarget);
        setOperand(2, falseTarget);
    }

    public void setTarget(BasicBlock target) {
        setOperand(0, target);
    }

    public void setTrueTarget(BasicBlock trueTarget) {
        setOperand(1, trueTarget);
    }

    public void setFalseTarget(BasicBlock falseTarget) {
        setOperand(2, falseTarget);
    }

    public boolean isConditional() { return isConditional; }
    
    public BasicBlock getTarget() { return (BasicBlock) getOperand(0); }

    public Value getCondition() { return getOperand(0); }

    public BasicBlock getTrueTarget() { return (BasicBlock) getOperand(1); }

    public BasicBlock getFalseTarget() { return (BasicBlock) getOperand(2); }

    @Override
    public void buildCFG() {
        BasicBlock currentBB = getParent();
        if (isConditional) {
            currentBB.addSuccessor(getTrueTarget());
            currentBB.addSuccessor(getFalseTarget());
        } else {
            currentBB.addSuccessor(getTarget());
        }
    }

    @Override
    public List<BasicBlock> getSuccessors() {
        if (isConditional) {
            return new ArrayList<>(List.of(getTrueTarget(), getFalseTarget()));
        } else {
            return new ArrayList<>(List.of(getTarget()));
        }
    }

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
