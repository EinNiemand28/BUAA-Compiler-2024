package llvm.value.constant;

public class ConstantInt extends Constant {
    private final int value;

    private ConstantInt(int width, int value) {
        super(llvm.ir.IRType.IntegerIRType.get(width));
        this.value = value;
    }

    public static ConstantInt get(int width, int value) {
        return new ConstantInt(width, value);
    }

    public int getValue() { return value; }

    @Override
    public String toString() {
        return getType() + " " + value;
    }

    @Override
    public String getName() {
        return value + "";
    }
}
