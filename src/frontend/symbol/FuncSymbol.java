package frontend.symbol;

import frontend.lexer.Token;
import frontend.enums.SymbolType;

import java.util.ArrayList;
import java.util.List;

public class FuncSymbol extends Symbol {
    private final Type funcType;
    private List<Type> paramTypeList;

    private static SymbolType getSymbolType(String funcType) {
        switch (funcType) {
            case "int" -> { return SymbolType.IntFunc; }
            case "char" -> { return SymbolType.CharFunc; }
            default -> { return SymbolType.VoidFunc; }
        }
    }

    public FuncSymbol(Token token, String funcType) {
        super(token, getSymbolType(funcType));
        this.funcType = new Type(funcType, 0);
        this.paramTypeList = new ArrayList<>();
    }

    public void setParamTypeList(List<Type> paramTypeList) {
        this.paramTypeList = paramTypeList;
    }
    
    public Type getFuncType() {
        return funcType;
    }

    public List<Type> getParamTypeList() {
        return paramTypeList;
    }
}
