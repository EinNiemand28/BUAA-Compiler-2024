package frontend.symbol;

import frontend.lexer.Token;
import frontend.enums.SymbolType;

import java.util.List;
import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private final boolean isConst;
    private final Type varType;
    private final List<Integer> constValues;

    private static SymbolType getSymbolType(boolean isConst, String varType, int dim) {
        if (isConst) {
            if (varType.equals("int")) {
                if (dim == 0) { return SymbolType.ConstInt; }
                else { return SymbolType.ConstIntArray; }
            } else {
                if (dim == 0) { return SymbolType.ConstChar; }
                else { return SymbolType.ConstCharArray; }
            }
        } else {
            if (varType.equals("int")) {
                if (dim == 0) { return SymbolType.Int; }
                else { return SymbolType.IntArray; }
            } else {
                if (dim == 0) { return SymbolType.Char; }
                else { return SymbolType.CharArray; }
            }
        }
    }

    public VarSymbol(Token token, boolean isConst, Type type) {
        super(token, getSymbolType(isConst, type.getTypeName(), type.getDimSize()));
        this.isConst = isConst;
        this.constValues = new ArrayList<>();
        this.varType = new Type(type);
    }

    public void addConstValue(int value) {
        constValues.add(value);
    }

    public boolean isConst() {
        return isConst;
    }

    public List<Integer> getConstValues() { return constValues; }

    public Type getVarType() {
        return varType;
    }
}
