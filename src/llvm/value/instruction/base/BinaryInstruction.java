package llvm.value.instruction.base;

import llvm.value.Value;

public class BinaryInstruction extends Instruction {
    public enum Operator {
        Add("add"),
        Sub("sub"),
        Mul("mul"),
        SDiv("sdiv"),
        SRem("srem"),
        // And("and"),
        // Or("or"),
        ;

        private final String name;

        Operator (String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }

    private final Operator operator;

    public BinaryInstruction(Operator op, Value lhs, Value rhs) {
        super(lhs.getType(), "", 2);
        this.operator = op;
        setOperand(0, lhs);
        setOperand(1, rhs);
    }
    
    public Value getLhs() { return getOperand(0); }
    public Value getRhs() { return getOperand(1); }
    public Operator getOperator() { return operator; }

    @Override
    public String getInstructionName() {
        return operator.getName();
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s, %s", 
        getName(), getInstructionName(), getType(), 
        getLhs().getName(), getRhs().getName());
    }
}
