package llvm.value.instruction.base;

import llvm.value.Value;
import llvm.value.Function;

import java.util.ArrayList;
import java.util.List;


public class CallInstruction extends Instruction {
    private final Function function;
    private List<Value> args;
    
    public CallInstruction(Function function, List<Value> arguments) {
        super(function.getReturnType(), "", arguments.size() + 1);
        this.function = function;
        args = new ArrayList<>(arguments);
        setOperand(0, function);
        for (int i = 0; i < arguments.size(); i++) {
            setOperand(i + 1, arguments.get(i));
        }
    }

    public Function getFunction() { return function; }

    public List<Value> getArguments() { return args; }

    public Function getCallee() {
        return function;
    }

    @Override
    public String getInstructionName() {
        return "call";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!getType().isVoidTy()) {
            sb.append(getName()).append(" = ");
        }
        sb.append(getInstructionName()).append(" ").
        append(getType()).append(" ").append(function.getName()).append("(");
        for (int i = 1; i < getNumOperands(); i++) {
            if (i > 1) { sb.append(", "); }
            sb.append(getOperand(i).getType()).append(" ").append(getOperand(i).getName());
        }
        return sb.append(")").toString();
    }
}
