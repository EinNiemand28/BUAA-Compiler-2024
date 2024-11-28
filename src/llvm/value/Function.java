package llvm.value;

import llvm.ir.IRType;

import java.util.List;
import java.util.ArrayList;

public class Function extends Value {
    private final List<BasicBlock> basicBlocks;
    private final List<Parameter> parameters;
    private final BasicBlock entryBlock;
    private boolean hasReturn;

    public static Function GETINT;
    public static Function GETCHAR;
    public static Function PUTINT;
    public static Function PUTCH;
    public static Function PUTSTR;

    static {
        IRType getintType = IRType.FunctionIRType.get(IRType.IntegerIRType.get(32), new ArrayList<>());
        GETINT = new Function(getintType, "getint");

        IRType getcharType = IRType.FunctionIRType.get(IRType.IntegerIRType.get(32), new ArrayList<>());
        GETCHAR = new Function(getcharType, "getchar");

        List<IRType> putinParams = new ArrayList<>();
        putinParams.add(IRType.IntegerIRType.get(32));
        IRType putinType = IRType.FunctionIRType.get(IRType.VoidIRType.getInstance(), putinParams);
        PUTINT = new Function(putinType, "putint");

        List<IRType> putchParams = new ArrayList<>();
        putchParams.add(IRType.IntegerIRType.get(32));
        IRType putchType = IRType.FunctionIRType.get(IRType.VoidIRType.getInstance(), putchParams);
        PUTCH = new Function(putchType, "putch");

        List<IRType> putstrParams = new ArrayList<>();
        putstrParams.add(IRType.PointerIRType.get(IRType.IntegerIRType.get(8)));
        IRType pustrType = IRType.FunctionIRType.get(IRType.VoidIRType.getInstance(), putstrParams);
        PUTSTR = new Function(pustrType, "putstr");
    }

    public Function(IRType type, String name) {
        super(type, name);
        this.basicBlocks = new ArrayList<>();
        this.parameters = new ArrayList<>();

        if (!type.isFunctionTy()) {
            throw new IllegalArgumentException("not a function type");
        }

        List<IRType> paramIRTypes = ((IRType.FunctionIRType) type).getParamTypes();
        for (int i = 0; i < paramIRTypes.size(); i++) {
            this.parameters.add(new Parameter(paramIRTypes.get(i), "", i, this));
        }
        this.entryBlock = new BasicBlock("", this);
        this.hasReturn = false;
    }

    public void addBasicBlock(BasicBlock bb) {
        if (!basicBlocks.isEmpty()) {
            bb.insertAfter(basicBlocks.get(basicBlocks.size() - 1));
        }
        basicBlocks.add(bb);
    }

    public void setHasReturn(boolean hasReturn) { this.hasReturn = hasReturn; }

    public boolean hasReturn() { return hasReturn; }

    public List<BasicBlock> getBasicBlocks() { return basicBlocks; }
    public List<Parameter> getParameters() { return parameters; }
    public BasicBlock getEntryBlock() { return entryBlock; }
    public IRType getReturnType() { return ((IRType.FunctionIRType) getType()).getReturnType(); }

    @Override
    public String getName() {
        return "@" + super.getName();
    }
}
