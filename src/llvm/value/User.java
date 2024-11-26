package llvm.value;

import llvm.ir.IRType;

import java.util.List;
import java.util.ArrayList;

public class User extends Value {
    private final List<Use> operands;

    protected User(IRType IRType, String name, int num) {
        super(IRType, name);
        this.operands = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            operands.add(new Use(null, this, i));
        }
    }

    public int getNumOperands() { return operands.size(); }

    public List<Use> getOperands() { return operands; }

    public Value getOperand(int index) {
        if (index < 0 || index >= operands.size()) {
            throw new IllegalArgumentException("Index wrong");
        }
        return operands.get(index).getValue();
    }

    public void setOperand(int index, Value value) {
        operands.get(index).setValue(value);
    }
}
