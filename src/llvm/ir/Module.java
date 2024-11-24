package llvm.ir;

import llvm.value.*;

import java.util.List;
import java.util.ArrayList;

public class Module {
    List<GlobalValue> globalsValues;
    List<Function> functions;

    public Module() {
        this.globalsValues = new ArrayList<>();
        this.functions = new ArrayList<>();
    }
}