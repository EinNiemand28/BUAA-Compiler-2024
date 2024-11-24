package llvm.value.inst.base;

import llvm.value.User;
import llvm.value.BasicBlock;

public abstract class Instruction extends User {
    private BasicBlock parent;
    private Instruction prevInst;
    private Instruction nextInst;
    
    protected Instruction(llvm.ir.Type type, String name, int num) {
        super(type, name, num);
        this.parent = null;
        this.prevInst = null;
        this.nextInst = null;
    }

    public void insertBefore(Instruction inst) {
        this.prevInst = inst.prevInst;
        this.nextInst = inst;
        if (inst.prevInst != null) {
            inst.prevInst.nextInst = this;
        }
        inst.prevInst = this;
        this.parent = inst.parent;
    }

    public void insertAfter(Instruction inst) {
        this.prevInst = inst;
        this.nextInst = inst.nextInst;
        if (inst.nextInst != null) {
            inst.nextInst.prevInst = this;
        }
        inst.nextInst = this;
        this.parent = inst.parent;
    }

    public void removeFromParent() {
        if (prevInst != null) {
            prevInst.nextInst = nextInst;
        }
        if (nextInst != null) {
            nextInst.prevInst = prevInst;
        }
        if (parent != null) {
            // TODO
        }
    }

    public abstract String getName();

    public BasicBlock getParent() { return parent; }
    public Instruction getPrevInst() { return prevInst; }
    public Instruction getNextInst() { return nextInst; }
}
