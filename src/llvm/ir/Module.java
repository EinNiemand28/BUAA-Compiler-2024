package llvm.ir;

import llvm.value.Function;
import llvm.value.GlobalValue;

import java.util.List;
import java.util.ArrayList;

public class Module {
    List<GlobalValue> globalsValues;
    List<Function> functions;

    public Module() {
        this.globalsValues = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void addGlobalValue(GlobalValue globalValue) {
        globalsValues.add(globalValue);
    }

    public void addFunction(Function function) {
        SlotTracker.getInstance().reset();
        functions.add(function);
    }

    public List<GlobalValue> getGlobalValues() { return globalsValues; }
    public List<Function> getFunctions() { return functions; }
}