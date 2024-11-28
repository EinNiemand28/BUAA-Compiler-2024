package llvm.value.constant;

import llvm.ir.IRType;

public class ConstantString extends Constant{
    private final String value;
    private final int size;

    private ConstantString(String value, int size) {
        super(IRType.ArrayIRType.get(IRType.IntegerIRType.get(8), size));
        this.value = value;
        this.size = size;
    }

    public static ConstantString get(String str, int size) {
        StringBuilder sb = new StringBuilder();
        int len = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\\' && i + 1 < str.length()) {
                char ch2 = str.charAt(i + 1);
                switch (ch2) {
                    case 'n' -> sb.append("\\0A");
                    case 't' -> sb.append("\\09");
                    case 'r' -> sb.append("\\0D");
                    case '\\' -> sb.append("\\5C");
                    case '\"' -> sb.append("\\22");
                    default -> {}
                }
                i++;
            } else { sb.append(ch); }
            len++;
        }
        if (size == -1) {
            sb.append("\\00");
            size = len + 1;
        } else {
            for (int i = len; i < size; i++) { sb.append("\\00"); }
        }
        return new ConstantString(sb.toString(), size);
    }

    public boolean isZero() {
        return size == 1;
    }

    public String getValue() { return value; }

    public int getSize() { return size; }

    @Override
    public String toString() {
        return "c\"" + value + "\"";
    }
}
