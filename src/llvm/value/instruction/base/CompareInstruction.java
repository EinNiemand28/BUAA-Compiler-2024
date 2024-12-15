package llvm.value.instruction.base;

import llvm.value.Value;
import llvm.ir.IRType;

public class CompareInstruction extends Instruction {
    public enum Operator {
        EQ("eq"),
        NE("ne"),
        SLT("slt"),
        SLE("sle"),
        SGT("sgt"),
        SGE("sge"),
        ;
        
        private final String name;
        
        Operator(String name) {
            this.name = name;
        }
        
        public String getName() { return name; }
    }

    public final Operator operator;

    public CompareInstruction(Operator op, Value lhs, Value rhs) {
        super(IRType.IntegerIRType.get(1), "", 2);
        this.operator = op;
        setOperand(0, lhs);
        setOperand(1, rhs);
        if (lhs.getType() != rhs.getType()) {
            throw new IllegalArgumentException("not same type");
        }
    }

    public Operator getOperator() { return operator; }
    public Value getLhs() { return getOperand(0); }
    public Value getRhs() { return getOperand(1); }

    @Override
    public String getInstructionName() {
        return "icmp " + operator.getName();
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s, %s",
        getName(), getInstructionName(), getLhs().getType(),
        getLhs().getName(), getRhs().getName());
    }
}
