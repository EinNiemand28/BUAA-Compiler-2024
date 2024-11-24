package frontend.symbol;

import frontend.lexer.Token;
import frontend.enums.SymbolType;

public class VarSymbol extends Symbol {
    private final boolean isConst;
    private final Type varType;

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

    public VarSymbol(Token token, boolean isConst, String varType, int dim) {
        super(token, getSymbolType(isConst, varType, dim));
        this.isConst = isConst;
        this.varType = new Type(varType, dim);
    }

    public boolean isConst() {
        return isConst;
    }

    public Type getVarType() {
        return varType;
    }
}
