package llvm.value;

import llvm.ir.Type;

import java.util.List;
import java.util.ArrayList;

public class Value {
    private Type type;
    private String name;
    private List<Use> uses;

    protected Value(Type type, String name) {
        this.type = type;
        this.name = name;
        this.uses = new ArrayList<>();
    }

    public Type getType() { return type; }
    public String getName() { return name; }
    public List<Use> getUses() { return uses; }

    public void addUse(Use use) {
        uses.add(use);
    }

    public void removeUse(Use use) {
        uses.remove(use);
    }

    public void replaceAllUsesWith(Value value) {
        for (Use use : uses) {
            use.setValue(value);
        }
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}
