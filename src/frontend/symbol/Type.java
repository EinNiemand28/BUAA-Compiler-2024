package frontend.symbol;

public class Type {
    private final String typeName;
    private int dim;

    public Type(String typeName, int dim) {
        this.typeName = typeName;
        this.dim = dim;
    }

    public Type(Type type) {
        this.typeName = type.getTypeName();
        this.dim = type.getDim();
    }

    public String getTypeName() {
        return typeName;
    }

    public int getBitWidth() {
        if (typeName.equals("int")) {
            return 32;
        } else if (typeName.equals("char")) {
            return 8;
        } else {
            return 0;
        }
    }

    public int getDim() {
        return dim;
    }

    public boolean match(Type type) {
        if (dim == 0 && type.getDim() == 0) {
            return (typeName.equals("int") || 
                    typeName.equals("char")) && 
                    (type.getTypeName().equals("int") || 
                    type.getTypeName().equals("char"));
        } else {
            return typeName.equals(type.getTypeName()) && dim == type.getDim();
        }
    }
}
