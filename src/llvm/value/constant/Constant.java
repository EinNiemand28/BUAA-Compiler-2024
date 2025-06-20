package llvm.value.constant;

import llvm.ir.IRType;
import llvm.value.Value;

public abstract class Constant extends Value {
    public Constant(IRType type) {
        super(type, "");
    }

    public abstract boolean isZero();
    public abstract int getSize();
}
