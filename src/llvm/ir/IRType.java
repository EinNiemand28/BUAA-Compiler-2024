package llvm.ir;

import java.util.List;

public abstract class IRType {
    public enum TypeID {
        VoidTyID,
        LabelTyID,
        IntegerTyID,
        ArrayTyID,
        PointerTyID,
        FunctionTyID,
    }

    protected TypeID typeID;

    protected IRType(TypeID typeID) {
        this.typeID = typeID;
    }

    public TypeID getTypeID() { return typeID; }

    public boolean isVoidTy() { return typeID == TypeID.VoidTyID; }
    public boolean isLabelTy() { return typeID == TypeID.LabelTyID; }
    public boolean isIntegerTy() { return typeID == TypeID.IntegerTyID; }
    public boolean isArrayTy() { return typeID == TypeID.ArrayTyID; }
    public boolean isPointerTy() { return typeID == TypeID.PointerTyID; }
    public boolean isFunctionTy() { return typeID == TypeID.FunctionTyID; }

    public static IRType convert(String type, int dim, boolean isFunction) {
        IRType irType = null;
        if (type.equals("int")) {
            irType = IntegerIRType.get(32);
        } else if (type.equals("char")) {
            irType = IntegerIRType.get(8);
        } else if (type.equals("void")) {
            irType = VoidIRType.getInstance();
        }
        if (dim > 0) {
            irType = ArrayIRType.get(irType, dim);
        }
        if (isFunction) {
            irType = FunctionIRType.get(irType);
        }
        return irType;
    }

    public static class VoidIRType extends IRType {
        private static VoidIRType instance = new VoidIRType();

        public VoidIRType() { super(TypeID.VoidTyID); }
        public static VoidIRType getInstance() { return instance; }
        @Override
        public String toString() { return "void"; }
    }

    public static class LabelIRType extends IRType {
        private static LabelIRType instance = new LabelIRType();

        public LabelIRType() { super(TypeID.LabelTyID); }
        public static LabelIRType getInstance() { return instance; }
        @Override
        public String toString() { return "label"; }
    }

    public static class IntegerIRType extends IRType {
        private final int bitWidth;
        private static final IntegerIRType INT1 = new IntegerIRType(1);
        private static final IntegerIRType INT8 = new IntegerIRType(8);
        private static final IntegerIRType INT32 = new IntegerIRType(32);

        private IntegerIRType(int bitWidth) {
            super(TypeID.IntegerTyID);
            this.bitWidth = bitWidth;
        }

        public static IntegerIRType get(int bitWidth) {
            switch (bitWidth) {
                case 1 -> { return INT1; }
                case 8 -> { return INT8; }
                case 32 -> { return INT32; }
                default -> { return new IntegerIRType(bitWidth); }
            }
        }

        public int getBitWidth() { return bitWidth; }

        @Override
        public String toString() { return "i" + bitWidth; }
    }

    public static class ArrayIRType extends IRType {
        private final IRType elementIRType;
        private final int size;
        
        private ArrayIRType(IRType elementIRType, int size) {
            super(TypeID.ArrayTyID);
            this.elementIRType = elementIRType;
            this.size = size;
        }

        public static ArrayIRType get(IRType elmentIRType, int size) {
            return new ArrayIRType(elmentIRType, size);
        }

        public IRType getElementType() { return elementIRType; }
        public int getSize() { return size; }

        @Override
        public String toString() { return "[" + size + " x " + elementIRType + "]"; }
    }

    public static class PointerIRType extends IRType {
        private final IRType elemenIRType;
        
        private PointerIRType(IRType elemenIRType) {
            super(TypeID.PointerTyID);
            this.elemenIRType = elemenIRType;
        }

        public static PointerIRType get(IRType elemenIRType) {
            return new PointerIRType(elemenIRType);
        }

        public IRType getElementType() { return elemenIRType; }

        @Override
        public String toString() { return elemenIRType + "*"; }
    }

    public static class FunctionIRType extends IRType {
        private final IRType returnIRType;
        private List<IRType> paramIRTypes;
        
        private FunctionIRType(IRType returnIRType) {
            super(TypeID.FunctionTyID);
            this.returnIRType = returnIRType;
            this.paramIRTypes = null;
        }

        public static FunctionIRType get(IRType returnIRType) {
            return new FunctionIRType(returnIRType);
        }

        public void setParamIRTypes(List<IRType> paramIRTypes) {
            this.paramIRTypes = paramIRTypes;
        }

        public IRType getReturnType() { return returnIRType; }
        public List<IRType> getParamTypes() { return paramIRTypes; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(returnIRType).append(" (");
            for (int i = 0; i < paramIRTypes.size(); i++) {
                sb.append(paramIRTypes.get(i));
                if (i != paramIRTypes.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
