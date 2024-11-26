package llvm.value.constant;

public class ConstantString extends Constant{
    private final String value;
    private final int size;

    private ConstantString(String value, int size) {
        super(llvm.ir.IRType.ArrayIRType.get(llvm.ir.IRType.IntegerIRType.get(8), size));
        this.value = value;
        this.size = size;
    }

    public static ConstantString get(String value, int size) {
        return new ConstantString(value, size);
    }

    public static ConstantString get(String value) {
        return new ConstantString(value, value.length() + 1);
    }

    public String getValue() { return value; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("c\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\n') { sb.append("\\0A"); }
            else if (c == '\t') { sb.append("\\09"); }
            else if (c == '\0') { sb.append("\\00"); }
            else { sb.append(c); }
        }
        sb.append("\\00");
        for (int i = value.length() + 1; i < size; i++) { sb.append("\\00"); }
        sb.append("\"");
        return sb.toString();
    }
}
