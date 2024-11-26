package llvm.value.constant;

import llvm.ir.IRType;
import llvm.value.Value;

public abstract class Constant extends Value {
    public Constant(IRType IRType) {
        super(IRType, "");
    }
}
