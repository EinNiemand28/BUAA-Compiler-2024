package llvm.ir;

import java.util.List;

public abstract class Type {
    public enum TypeID {
        VoidTyID,
        LabelTyID,
        IntegerTyID,
        ArrayTyID,
        PointerTyID,
        FunctionTyID,
    }

    protected TypeID typeID;

    protected Type(TypeID typeID) {
        this.typeID = typeID;
    }

    public TypeID getTypeID() { return typeID; }

    public boolean isVoidTy() { return typeID == TypeID.VoidTyID; }
    public boolean isLabelTy() { return typeID == TypeID.LabelTyID; }
    public boolean isIntegerTy() { return typeID == TypeID.IntegerTyID; }
    public boolean isArrayTy() { return typeID == TypeID.ArrayTyID; }
    public boolean isPointerTy() { return typeID == TypeID.PointerTyID; }
    public boolean isFunctionTy() { return typeID == TypeID.FunctionTyID; }

    public static class VoidType extends Type {
        private static VoidType instance = new VoidType();

        public VoidType() { super(TypeID.VoidTyID); }
        public static VoidType getInstance() { return instance; }
        @Override
        public String toString() { return "void"; }
    }

    public static class LabelType extends Type {
        private static LabelType instance = new LabelType();

        public LabelType() { super(TypeID.LabelTyID); }
        public static LabelType getInstance() { return instance; }
        @Override
        public String toString() { return "label"; }
    }

    public static class IntegerType extends Type {
        private final int bitWidth;
        private static final IntegerType INT1 = new IntegerType(1);
        private static final IntegerType INT8 = new IntegerType(8);
        private static final IntegerType INT32 = new IntegerType(32);

        private IntegerType(int bitWidth) {
            super(TypeID.IntegerTyID);
            this.bitWidth = bitWidth;
        }

        public static IntegerType get(int bitWidth) {
            switch (bitWidth) {
                case 1 -> { return INT1; }
                case 8 -> { return INT8; }
                case 32 -> { return INT32; }
                default -> { return new IntegerType(bitWidth); }
            }
        }

        public int getBitWidth() { return bitWidth; }

        @Override
        public String toString() { return "i" + bitWidth; }
    }

    public static class  ArrayType extends Type {
        private final Type elementType;
        private final int size;
        
        private ArrayType(Type elementType, int size) {
            super(TypeID.ArrayTyID);
            this.elementType = elementType;
            this.size = size;
        }

        public static ArrayType get(Type elmentType, int size) {
            return new ArrayType(elmentType, size);
        }

        public Type getElementType() { return elementType; }
        public int getSize() { return size; }

        @Override
        public String toString() { return "[" + size + " x " + elementType + "]"; }
    }

    public static class PointerType extends Type {
        private final Type elemenType;
        
        private PointerType(Type elemenType) {
            super(TypeID.PointerTyID);
            this.elemenType = elemenType;
        }

        public static PointerType get(Type elemenType) {
            return new PointerType(elemenType);
        }

        public Type getElementType() { return elemenType; }

        @Override
        public String toString() { return elemenType + "*"; }
    }

    public static class FunctionType extends Type {
        private final Type returnType;
        private final List<Type> paramTypes;
        
        private FunctionType(Type returnType, List<Type> paramTypes) {
            super(TypeID.FunctionTyID);
            this.returnType = returnType;
            this.paramTypes = paramTypes;
        }

        public static FunctionType get(Type returnType, List<Type> paramTypes) {
            return new FunctionType(returnType, paramTypes);
        }

        public Type getReturnType() { return returnType; }
        public List<Type> getParamTypes() { return paramTypes; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(returnType).append(" (");
            for (int i = 0; i < paramTypes.size(); i++) {
                sb.append(paramTypes.get(i));
                if (i != paramTypes.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
