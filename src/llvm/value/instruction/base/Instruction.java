package llvm.value.instruction.base;

import llvm.ir.IRType;
import llvm.value.User;
import llvm.value.BasicBlock;

public abstract class Instruction extends User {
    private BasicBlock parent;
    private Instruction prevInst;
    private Instruction nextInst;
    
    protected Instruction(IRType IRType, String name, int num) {
        super(IRType, name, num);
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
            parent.removeInstruction(this);
        }
    }

    public abstract String getInstructionName();

    public BasicBlock getParent() { return parent; }
    public Instruction getPrevInst() { return prevInst; }
    public Instruction getNextInst() { return nextInst; }

    public void setParent(BasicBlock block) { this.parent = block; }

    @Override
    public String getName() {
        return "%" + super.getName();
    }
}
/*backend/llvm/value/inst/
├── base/                           # 基础抽象类
│   ├── Instruction.java           # 所有指令的基类
│   ├── BinaryInstruction.java     # 二元指令基类
│   ├── UnaryInstruction.java      # 一元指令基类
│   ├── MemoryInstruction.java     # 内存操作基类
│   ├── TerminatorInstruction.java # 终止指令基类
│   └── CompareInstruction.java    # 比较指令基类
│
├── binary/                         # 二元运算指令
│   ├── AddInstruction.java
│   ├── SubInstruction.java
│   ├── MulInstruction.java
│   ├── SDivInstruction.java
│   └── SRemInstruction.java
│
├── memory/                         # 内存操作指令
│   ├── AllocaInstruction.java
│   ├── LoadInstruction.java
│   ├── StoreInstruction.java
│   └── GetElementPtrInstruction.java
│
├── terminator/                     # 终止指令
│   ├── BranchInstruction.java     # 条件/无条件跳转
│   └── ReturnInstruction.java     # 返回指令
│
├── compare/                        # 比较指令
│   └── ICmpInstruction.java       # 整数比较
│
├── other/                         # 其他指令
│   ├── CallInstruction.java      # 函数调用
│   ├── PhiInstruction.java       # PHI节点
│   ├── ZExtInstruction.java      # 零扩展
│   └── TruncInstruction.java     # 截断
│
└── enums/                         # 枚举定义
    ├── BinaryOperator.java       # 二元运算类型
    ├── CompareOperator.java      # 比较运算符
    └── UnaryOperator.java        # 一元运算类型 */