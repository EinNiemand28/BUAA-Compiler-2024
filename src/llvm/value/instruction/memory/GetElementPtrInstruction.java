package llvm.value.instruction.memory;

import llvm.ir.IRType;
import llvm.value.Value;
import llvm.value.instruction.base.MemoryInstruction;

import java.util.List;

public class GetElementPtrInstruction extends MemoryInstruction {
    private final IRType elementIRType;

    private static IRType getElementType(Value pointer, List<Value> indices) {
        IRType baseType = pointer.getType();
        IRType elementType = ((IRType.PointerIRType) baseType).getElementType();
        for (int i = 1; i < indices.size(); i++) {
            if (elementType.isArrayTy()) {
                elementType = ((IRType.ArrayIRType) elementType).getElementType();
            } else if (elementType.isPointerTy()) {
                elementType = ((IRType.PointerIRType) elementType).getElementType();
            } else {
                throw new IllegalArgumentException("Invalid index");
            }
        }
        return IRType.PointerIRType.get(elementType);
    }

    public GetElementPtrInstruction(Value pointer, List<Value> indices) {
        super(getElementType(pointer, indices), indices.size() + 1);
        elementIRType = ((IRType.PointerIRType) pointer.getType()).getElementType();
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
        sb.append(getName()).append(" = ").append(getInstructionName()).append(" inbounds ");
        sb.append(elementIRType);
        
        for (int i = 0; i < getNumOperands(); i++) {
            sb.append(", ").append(getOperand(i).getType()).
                    append(" ").append(getOperand(i).getName());
        }
        return sb.toString();
    }
}
