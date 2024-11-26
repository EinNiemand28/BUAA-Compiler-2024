package llvm.value.instruction.base;

import llvm.ir.IRType;
import llvm.value.Value;

public class UnaryInstruction extends Instruction {
    public enum Operator {
        ZExt("zext"),
        Trunc("trunc"),
        ;

        private final String name;
        Operator(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }

    public Operator operator;
    public IRType targetIRType;

    public UnaryInstruction(Operator op, Value operand, IRType targetIRType) {
        super(targetIRType, "", 1);
        this.operator = op;
        this.targetIRType = targetIRType;
        setOperand(0, operand);
    }

    public Operator getOperator() { return operator; }
    public IRType getTargetType() { return targetIRType; }
    public Value getOperand() { return getOperand(0); }
    
    @Override
    public String getInstructionName() {
        return operator.getName();
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s to %s", 
        getName(), getInstructionName(), 
        getOperand().getType(), getOperand().getName(), targetIRType);
    }
}