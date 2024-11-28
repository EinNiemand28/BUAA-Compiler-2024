package llvm.value.constant;

import llvm.ir.IRType;

import java.util.ArrayList;
import java.util.List;

public class ConstantArray extends Constant {
    private final List<Constant> elements;
    private final int size;

    private static Constant getZeroValue(IRType type) {
        if (type instanceof IRType.IntegerIRType) {
            int bitWidth = ((IRType.IntegerIRType) type).getBitWidth();
            return ConstantInt.get(bitWidth, 0);
        } else if (type instanceof IRType.ArrayIRType arrayType) {
            return ConstantArray.get(arrayType.getElementType(),
            arrayType.getSize(), new ArrayList<>());
        }
        throw new IllegalArgumentException("Invalid type for zero value: " + type);
    }

    private ConstantArray(IRType type, int size, List<Constant> initializers) {
        super(IRType.ArrayIRType.get(type, size));
        this.size = size;
        this.elements = new ArrayList<>(size);
        this.elements.addAll(initializers);

        for (int i = initializers.size(); i < size; i++) {
            this.elements.add(getZeroValue(type));
        }
    }

    public static ConstantArray get(IRType type, int size, List<Constant> initializers) {
        return new ConstantArray(type, size, initializers);
    }

    public boolean isZero() {
        for (Constant c : elements) {
            if (!c.isZero()) {
                return false;
            }
        }
        return true;
    }

    public List<Constant> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // sb.append(getType()).append(" ");
        if (size > 0) {
            sb.append("[ ");
            for (int i = 0; i < size; i++) {
                if (i > 0) { sb.append(", "); }
                sb.append(elements.get(i).getType()).append(" ").append(elements.get(i));
            }
            sb.append(" ]");
        }
        return sb.toString();
    }
}