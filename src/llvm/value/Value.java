package llvm.value;

import llvm.ir.IRType;
import llvm.ir.SlotTracker;

import java.util.List;
import java.util.ArrayList;

public class Value {
    private IRType type;
    private String name;
    private List<Use> uses;

    protected Value(IRType IRType, String name) {
        this.type = IRType;
        this.name = name;
        this.uses = new ArrayList<>();
    }

    public IRType getType() { return type; }
    public String getName() {
        if (name == null || name.isEmpty()) {
            if (this instanceof BasicBlock) {
                name = SlotTracker.getInstance().getBBSlot();
            } else {
                name = SlotTracker.getInstance().getSlot();
            }
        }
        return name;
    }
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
