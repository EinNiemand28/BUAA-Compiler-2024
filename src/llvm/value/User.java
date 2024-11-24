package llvm.value;

import llvm.ir.Type;

import java.util.List;
import java.util.ArrayList;

public class User extends Value {
    private final List<Use> operands;

    protected User(Type type, String name, int num) {
        super(type, name);
        this.operands = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            operands.add(new Use(null, this, i));
        }
    }

    public List<Use> getOperands() { return operands; }

    public Value getOperand(int index) {
        return operands.get(index).getValue();
    }

    public void setOperand(int index, Value value) {
        operands.get(index).setValue(value);
    }
}
