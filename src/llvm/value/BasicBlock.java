package llvm.value;

import llvm.value.instruction.base.Instruction;
import llvm.value.instruction.memory.AllocaInstruction;
import llvm.value.instruction.base.TerminatorInstruction;
import llvm.value.instruction.terminator.BranchInstruction;
import llvm.value.instruction.terminator.ReturnInstruction;
import llvm.ir.IRType;

import java.util.List;
import java.util.ArrayList;

public class BasicBlock extends Value {
    private final List<Instruction> instructions;
    private final Function parent;
    private BasicBlock prevBlock;
    private BasicBlock nextBlock;
    private int allocNum;
    private final List<BasicBlock> predecessors;
    private final List<BasicBlock> successors;
    private TerminatorInstruction terminator;
    private boolean sealed;

    public BasicBlock(String name, Function function) {
        super(IRType.LabelIRType.getInstance(), name);
        this.instructions = new ArrayList<>();
        this.parent = function;
        if (function != null) {
            function.addBasicBlock(this);
        }
        this.prevBlock = null;
        this.nextBlock = null;
        this.allocNum = 0;
        this.predecessors = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.terminator = null;
        this.sealed = false;
    }

    public void insertBefore(BasicBlock bb) {
        this.prevBlock = bb.prevBlock;
        this.nextBlock = bb;
        if (bb.prevBlock != null) {
            bb.prevBlock.nextBlock = this;
        }
        bb.prevBlock = this;
    }
    
    public void insertAfter(BasicBlock bb) {
        this.prevBlock = bb;
        this.nextBlock = bb.nextBlock;
        if (bb.nextBlock != null) {
            bb.nextBlock.prevBlock = this;
        }
        bb.nextBlock = this;
    }

    public void remove() {
        if (prevBlock != null) {
            prevBlock.nextBlock = nextBlock;
        }
        if (nextBlock != null) {
            nextBlock.prevBlock = prevBlock;
        }
        prevBlock = null;
        nextBlock = null;
    }

    public void addInstruction(Instruction instruction) {
        if (sealed) {
            return;
        }
        if (instruction instanceof BranchInstruction || instruction instanceof ReturnInstruction) {
            this.terminator = (TerminatorInstruction) instruction;
        }
        instructions.add(instruction);
        instruction.setParent(this);
    }

    public void insertInstruction(Instruction instruction, int index) {
        if (instruction instanceof BranchInstruction || instruction instanceof ReturnInstruction) {
            this.terminator = (TerminatorInstruction) instruction;
        }
        instructions.add(index, instruction);
        instruction.setParent(this);
        if (instruction instanceof AllocaInstruction) { allocNum++; }
    }

    public void removeInstruction(Instruction instruction) {
        if (instruction instanceof TerminatorInstruction && instruction == terminator) {
            terminator = null;
        }
        instructions.remove(instruction);
        instruction.setParent(null);
    }

    public void addPredecessor(BasicBlock pred) {
        if (!predecessors.contains(pred)) {
            predecessors.add(pred);
            pred.addSuccessor(this);
        }
    }

    public void addSuccessor(BasicBlock succ) {
        if (!successors.contains(succ)) {
            successors.add(succ);
            succ.addPredecessor(this);
        }
    }

    public void removePredecessor(BasicBlock pred) {
        predecessors.remove(pred);
        pred.getSuccessors().remove(this);
    }

    public void removeSuccessor(BasicBlock succ) {
        successors.remove(succ);
        succ.getPredecessors().remove(this);
    }

    public boolean hasTerminator() {
        return terminator != null;
    }

    public TerminatorInstruction getTerminator() {
        return terminator;
    }

    public void setTerminator(TerminatorInstruction terminator) {
        if (sealed) {
            return;
        }
        if (hasTerminator()) {
            removeInstruction(this.terminator);
        }
        this.terminator = terminator;
        addInstruction(terminator);
    }

    public List<BasicBlock> getPredecessors() { return predecessors; }
    public List<BasicBlock> getSuccessors() { return successors; }
    public List<Instruction> getInstructions() { return instructions; }
    public Function getParent() { return parent; }
    public BasicBlock getPrevBlock() { return prevBlock; }
    public BasicBlock getNextBlock() { return nextBlock; }
    public int getAllocNum() { return allocNum; }
    public boolean isSealed() { return sealed; }
    public void seal() { sealed = true; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getName()).append(":").append("\n");
        for (Instruction inst : instructions) {
            sb.append("    ").append(inst).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getName() {
        return "%" + super.getName();
    }
}
