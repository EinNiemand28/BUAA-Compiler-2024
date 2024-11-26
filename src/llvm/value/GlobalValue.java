package llvm.value;

import llvm.ir.IRType;
import llvm.value.constant.Constant;

public class GlobalValue extends Value {
    private boolean isUnnamedAddr;
    private final boolean isConst;
    private Constant initializer;
    private static int count = 0;

    public GlobalValue(IRType IRType, String name, boolean isConst) {
        super(IRType, name);
        this.isConst = isConst;
        this.isUnnamedAddr = false;
    }

    public GlobalValue(IRType IRType, boolean isConst) {
        this(IRType, ".str" + (count++ > 0 ? count : ""), isConst);
    }

    public void setInitializer(Constant initializer) { this.initializer = initializer; }
    public void setUnnamedAddr(boolean isUnnamedAddr) { this.isUnnamedAddr = isUnnamedAddr; }

    public Constant getInitializer() { return initializer; }
    public boolean isConst() { return isConst; }
    public boolean isUnnamedAddr() { return isUnnamedAddr; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(getName()).append(" = ");
        if (isUnnamedAddr) { sb.append("private unnamed_addr "); }
        else { sb.append("dso_local"); }
        if (isConst) { sb.append("constant "); }
        else { sb.append("global "); }

        sb.append(getType()).append(" ");

        if (initializer != null) { sb.append(initializer); }
        else { sb.append("zeroinitializer"); }
        
        return sb.toString();
    }

    @Override
    public String getName() {
        return "@" + super.getName();
    }
}
