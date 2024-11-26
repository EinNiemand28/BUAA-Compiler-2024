package llvm.value.instruction.base;

import llvm.value.Value;
import llvm.value.Function;

import java.util.List;
import java.util.ArrayList;


public class CallInstruction extends Instruction {
    private final Function function;
    private final List<Value> arguments;
    
    public CallInstruction(Function function, List<Value> arguments) {
        super(function.getReturnType(), "", arguments.size());
        this.function = function;
        this.arguments = new ArrayList<>(arguments);
        setOperand(0, function);
        for (int i = 0; i < arguments.size(); i++) {
            setOperand(i + 1, arguments.get(i));
        }
    }

    public Function getFunction() { return function; }
    public List<Value> getArguments() { return arguments; }

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
        append(getType()).append(" @").append(function.getName()).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) { sb.append(", "); }
            Value arg = arguments.get(i);
            sb.append(arg.getType()).append(" ").append(arg.getName());
        }
        return sb.append(")").toString();
    }
}
