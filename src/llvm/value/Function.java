package llvm.value;

import llvm.ir.IRType;
import llvm.value.instruction.terminator.ReturnInstruction;

import java.util.List;
import java.util.ArrayList;

public class Function extends Value {
    private final List<BasicBlock> basicBlocks;
    private final List<Parameter> parameters;
    private final BasicBlock entryBlock;
    private BasicBlock exitBlock;
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
        this.hasReturn = false;

        List<IRType> paramIRTypes = ((IRType.FunctionIRType) type).getParamTypes();
        for (int i = 0; i < paramIRTypes.size(); i++) {
            this.parameters.add(new Parameter(paramIRTypes.get(i), "", i, this));
        }

        this.entryBlock = new BasicBlock("entry", this);
        this.exitBlock = null;
    }

    public BasicBlock createBasicBlock() {
        BasicBlock bb = new BasicBlock("", this);
        return bb;
    }

    public void removeBasicBlock(BasicBlock bb) {
        for (BasicBlock pred : new ArrayList<>(bb.getPredecessors())) {
            bb.removePredecessor(pred);
        }
        for (BasicBlock succ : new ArrayList<>(bb.getSuccessors())) {
            bb.removeSuccessor(succ);
        }
        
        basicBlocks.remove(bb);
        
        if (bb == exitBlock) {
            exitBlock = null;
        }
    }

    public void addBasicBlock(BasicBlock bb) {
        if (!basicBlocks.isEmpty()) {
            bb.insertAfter(basicBlocks.get(basicBlocks.size() - 1));
        }
        basicBlocks.add(bb);
    }

    public void buildCFG() {
        for (BasicBlock bb : basicBlocks) {
            bb.getPredecessors().clear();
            bb.getSuccessors().clear();
        }

        for (BasicBlock bb : basicBlocks) {
            if (bb.hasTerminator()) {
                bb.getTerminator().buildCFG();
            }
        }
    }

    public BasicBlock getExitBlock() {
        if (exitBlock == null) {
            for (BasicBlock bb : basicBlocks) {
                if (bb.hasTerminator() && bb.getTerminator() instanceof ReturnInstruction) {
                    exitBlock = bb;
                    break;
                }
            }
        }
        return exitBlock;
    }

    public void setExitBlock(BasicBlock bb) {
        this.exitBlock = bb;
    }

    public void setHasReturn(boolean hasReturn) { 
        this.hasReturn = hasReturn; 
    }

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
