package llvm.value.constant;

import llvm.ir.IRType;

public class ConstantInt extends Constant {
    private int value;

    private ConstantInt(int width, int value) {
        super(IRType.IntegerIRType.get(width));
        this.value = value;
    }

    public static ConstantInt get(int width, int value) {
        return new ConstantInt(width, value);
    }

    public int getValue() { return value; }

    public boolean isZero() {
        return value == 0;
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public String getName() {
        return value + "";
    }
}
