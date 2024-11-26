package llvm.value.instruction.memory;

import llvm.ir.IRType;
import llvm.value.Value;
import llvm.value.instruction.base.MemoryInstruction;

import java.util.List;

public class GetElementPtrInstruction extends MemoryInstruction {
    private IRType elementIRType;

    private static IRType getElementType(Value pointer, List<Value> indices) {
        if (!pointer.getType().isPointerTy()) {
            throw new IllegalArgumentException("Pointer must be a pointer type");
        }
        IRType IRType = ((llvm.ir.IRType.PointerIRType) pointer.getType()).getElementType();
        for (int i = 1; i < indices.size(); i++) {
            if (!IRType.isArrayTy()) {
                throw new IllegalArgumentException("Type must be an array type");
            }
            IRType = ((llvm.ir.IRType.ArrayIRType) IRType).getElementType();
        }
        return IRType;
    }

    public GetElementPtrInstruction(Value pointer, List<Value> indices) {
        super(getElementType(pointer, indices), indices.size() + 1);
        if (!pointer.getType().isPointerTy()) {
            throw new IllegalArgumentException("Pointer must be a pointer type");
        }
        this.elementIRType = ((llvm.ir.IRType.PointerIRType) pointer.getType()).getElementType();
        setOperand(0, pointer);
        for (int i = 0; i < indices.size(); i++) {
            setOperand(i + 1, indices.get(i));
        }
    }

    public Value getPointer() { return getOperand(0); }
    
    @Override
    public String getInstructionName() {
        return "getelementptr";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" = ").
        append(getInstructionName()).append(" ");
        sb.append(elementIRType).append(", ptr ");
        sb.append(getPointer().getName()).append(" ");
        int size = getNumOperands();
        for (int i = 1; i < size; i++) {
            sb.append(", ").append(getOperand(i).getType()).
            append(" ").append(getOperand(i).getName());
        }
        return sb.toString();
    }
}
