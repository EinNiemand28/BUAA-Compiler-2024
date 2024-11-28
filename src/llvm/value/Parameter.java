package llvm.value;

import llvm.ir.IRType;

public class Parameter extends Value {
    private final Function function;
    private final int num;

    public Parameter(IRType type, String name, int num, Function function) {
        super(type, name);
        this.function = function;
        this.num = num;
    }

    public Function getFunction() { return function; }
    public int getNum() { return num; }

    @Override
    public String toString() {
        return getType() + " " + getName();
    }

    @Override
    public String getName() {
        return "%" + super.getName();
    }
}
