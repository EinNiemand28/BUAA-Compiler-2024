package frontend.symbol;

import java.util.ArrayList;
import java.util.List;

public class Type {
    private final String typeName;
    private final List<Integer> dims;

    public Type(String typeName) {
        this.typeName = typeName;
        this.dims = new ArrayList<>();
    }

    public Type(Type type) {
        this.typeName = type.getTypeName();
        this.dims = new ArrayList<>(type.getDims());
    }

    public  void addDim(int dim) {
        dims.add(dim);
    }

    public void delDim() {
        if (!dims.isEmpty()) {
            dims.remove(0);
        }
    }

    public String getTypeName() {
        return typeName;
    }
    public List<Integer> getDims() { return dims; }
    public int getDimSize() { return dims.size(); }

    public int getBitWidth() {
        if (typeName.equals("int")) {
            return 32;
        } else if (typeName.equals("char")) {
            return 8;
        } else {
            return 0;
        }
    }

    public boolean match(Type type) {
        if (getDimSize() == 0 && type.getDimSize() == 0) {
            return (typeName.equals("int") || 
                    typeName.equals("char")) && 
                    (type.getTypeName().equals("int") || 
                    type.getTypeName().equals("char"));
        } else {
            return typeName.equals(type.getTypeName()) && getDimSize() == type.getDimSize();
        }
    }
}
