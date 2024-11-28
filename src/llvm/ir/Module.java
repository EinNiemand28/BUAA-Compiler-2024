package llvm.ir;

import llvm.value.Function;
import llvm.value.GlobalValue;

import java.util.List;
import java.util.ArrayList;
/*
 * 本次作业是为了让同学们尽快实现一个完整的编译器，
 * 测试程序中仅涉及常量声明、变量声明、读语句、写语句、赋值语句，
 * 加减乘除模除等运算语句、函数定义及调用语句
 */
/*
 * src/
└── backend/
    └── llvm/
        ├── asm/
        │   ├── AsmWriter.java     // IR字符串生成
        │   └── AsmPrinter.java    // IR打印输出
        ├── ir/
        │   ├── Module.java        // 编译单元
        │   ├── LlvmContext.java   // 上下文
        │   ├── Type.java         // 类型系统
        │   └── SlotTracker.java   // 寄存器编号管理
        └── value/
            ├── Value.java         // 基类
            ├── Use.java          // 使用关系
            ├── User.java         // 使用者
            ├── BasicBlock.java    // 基本块
            ├── Constant.java      // 常量
            ├── Function.java      // 函数
            └── inst/
                ├── Instruction.java       // 指令基类
                └── Instructions.java      // 具体指令实现
 */
public class Module {
    List<GlobalValue> globalsValues;
    List<Function> functions;

    public Module() {
        this.globalsValues = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void addGlobalValue(GlobalValue globalValue) {
        globalsValues.add(globalValue);
    }

    public void addFunction(Function function) {
        SlotTracker.getInstance().reset();
        functions.add(function);
    }

    public List<GlobalValue> getGlobalsValues() { return globalsValues; }
    public List<Function> getFunctions() { return functions; }
}