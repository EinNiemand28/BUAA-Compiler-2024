package llvm.value.constant;

import llvm.ir.IRType;

import java.util.ArrayList;
import java.util.List;

public class ConstantArray extends Constant {
    private final List<Constant> elements;
    private final int size;

    private static Constant getZeroValue(IRType IRType) {
        if (IRType instanceof llvm.ir.IRType.IntegerIRType) {
            int bitWidth = ((llvm.ir.IRType.IntegerIRType) IRType).getBitWidth();
            return ConstantInt.get(bitWidth, 0);
        } else if (IRType instanceof llvm.ir.IRType.ArrayIRType) {
            llvm.ir.IRType.ArrayIRType arrayType = (llvm.ir.IRType.ArrayIRType) IRType;
            return ConstantArray.get(arrayType.getElementType(),
            arrayType.getSize(), new ArrayList<>());
        }
        throw new IllegalArgumentException("Invalid type for zero value: " + IRType);
    }

    private ConstantArray(IRType IRType, int size, List<Constant> initializers) {
        super(llvm.ir.IRType.ArrayIRType.get(IRType, size));
        this.size = size;
        this.elements = new ArrayList<>(size);
        this.elements.addAll(initializers);

        for (int i = initializers.size(); i < size; i++) {
            this.elements.add(getZeroValue(IRType));
        }
    }

    public static ConstantArray get(IRType IRType, int size, List<Constant> initializers) {
        return new ConstantArray(IRType, size, initializers);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType()).append(" ");
        if (size > 0) {
            sb.append("[ ");
            for (int i = 0; i < size; i++) {
                if (i > 0) { sb.append(", "); }
                sb.append(elements.get(i));
            }
            sb.append(" ]");
        }
        return sb.toString();
    }
}