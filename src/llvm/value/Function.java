package llvm.value;

import llvm.ir.IRType;

import java.util.List;
import java.util.ArrayList;

public class Function extends Value {
    private final List<BasicBlock> basicBlocks;
    private final List<Parameter> parameters;
    private BasicBlock entryBlock;

    public Function(IRType IRType, String name) {
        super(IRType, name);
        this.basicBlocks = new ArrayList<>();
        this.parameters = new ArrayList<>();

        if (!IRType.isFunctionTy()) {
            throw new IllegalArgumentException("not a function type");
        }

        List<IRType> paramIRTypes = ((llvm.ir.IRType.FunctionIRType) IRType).getParamTypes();
        for (int i = 0; i < parameters.size(); i++) {
            this.parameters.add(new Parameter(paramIRTypes.get(i), "", i, this));
        }
        this.entryBlock = new BasicBlock("", this);
        basicBlocks.add(entryBlock);
    }

    public void addBasicBlock(BasicBlock bb) {
        if (!basicBlocks.isEmpty()) {
            bb.insertAfter(basicBlocks.get(basicBlocks.size() - 1));
        }
        basicBlocks.add(bb);
    }

    public List<BasicBlock> getBasicBlocks() { return basicBlocks; }
    public List<Parameter> getParameters() { return parameters; }
    public BasicBlock getEntryBlock() { return entryBlock; }
    public IRType getReturnType() { return ((llvm.ir.IRType.FunctionIRType) getType()).getReturnType(); }

    @Override
    public String getName() {
        return "@" + super.getName();
    }
}
