package llvm.value;

import llvm.value.instruction.base.Instruction;
import llvm.value.instruction.memory.AllocaInstruction;
import llvm.ir.IRType;

import java.util.List;
import java.util.ArrayList;

public class BasicBlock extends Value {
    private final List<Instruction> instructions;
    private final Function parent;
    private BasicBlock prevBlock;
    private BasicBlock nextBlock;
    private int allocNum;

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
        instructions.add(instruction);
        instruction.setParent(this);
        if (instruction instanceof AllocaInstruction) { allocNum++; }
    }

    public void insertInstruction(Instruction instruction, int index) {
        instructions.add(index, instruction);
        instruction.setParent(this);
        if (instruction instanceof AllocaInstruction) { allocNum++; }
    }

    public void removeInstruction(Instruction instruction) {
        instructions.remove(instruction);
        instruction.setParent(null);
    }

    public List<Instruction> getInstructions() { return instructions; }
    public Function getParent() { return parent; }
    public BasicBlock getPrevBlock() { return prevBlock; }
    public BasicBlock getNextBlock() { return nextBlock; }
    public int getAllocNum() { return allocNum; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(":").append("\n");
        for (Instruction inst : instructions) {
            sb.append("  ").append(inst).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getName() {
        return "%" + super.getName();
    }
}
