package llvm.value;

import llvm.ir.IRGenerator;
import llvm.ir.IRType;
import llvm.value.constant.Constant;
import llvm.value.constant.ConstantString;

import java.util.HashMap;
import java.util.Map;

public class GlobalValue extends Value {
    private boolean isUnnamedAddr;
    private final boolean isConst;
    private Constant initializer;
    private static int count = 0;
    public static Map<String, GlobalValue> stringPool = new HashMap<>();

    public GlobalValue(IRType type, String name, boolean isConst) {
        super(IRType.PointerIRType.get(type), name);
        this.isConst = isConst;
        this.isUnnamedAddr = false;
        this.initializer = null;
    }

    private GlobalValue(IRType type, boolean isConst) {
        this(type, ".str" + (count > 0 ? "." + count : ""), isConst);
        count++;
    }

    public static GlobalValue createConstantString(String str, int size) {
        ConstantString initializer = ConstantString.get(str, size);
        if (stringPool.containsKey(initializer.getValue())) {
            return stringPool.get(initializer.getValue());
        }
        IRType arrType = IRType.ArrayIRType.get(IRType.IntegerIRType.get(8), initializer.getSize());
        GlobalValue strCon = new GlobalValue(arrType, true);

        strCon.isUnnamedAddr = true;
        strCon.initializer = initializer;
        IRGenerator.getInstance().getIrModule().addGlobalValue(strCon);
        stringPool.put(initializer.getValue(), strCon);
        return strCon;
    }

    public void setInitializer(Constant initializer) { this.initializer = initializer; }
    public void setUnnamedAddr(boolean isUnnamedAddr) { this.isUnnamedAddr = isUnnamedAddr; }

    public Constant getInitializer() { return initializer; }
    public boolean isConst() { return isConst; }
    public boolean isUnnamedAddr() { return isUnnamedAddr; }

    public int getSize() {
        if (initializer != null) {
            return initializer.getSize();
        }
        IRType type = ((IRType.PointerIRType) getType()).getElementType();
        if (type instanceof IRType.ArrayIRType) {
            return ((IRType.ArrayIRType) type).getSize();
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" = ");
        if (isUnnamedAddr) { sb.append("private unnamed_addr "); }
        else { sb.append("dso_local "); }
        if (isConst) { sb.append("constant "); }
        else { sb.append("global "); }

        sb.append(((IRType.PointerIRType) getType()).getElementType()).append(" ");

        if (initializer != null) { sb.append(initializer); }
        else { sb.append("zeroinitializer"); }
        
        return sb.toString();
    }

    @Override
    public String getName() {
        return "@" + super.getName();
    }
}
