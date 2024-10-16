package enums;

public enum SyntaxCompType {
    CompUnit,

    // Del
    Decl,
    ConstDecl,
    BType,
    ConstDef,
    ConstInitVal,
    VarDecl,
    VarDef,
    InitVal,

    // Func
    FuncDef,
    MainFuncDef,
    FuncType,
    FuncFParams,
    FuncFParam,
    Block,
    BlockItem,

    // Stmt
    AssignStmt("Stmt"),
    ExpStmt("Stmt"),
    BlockStmt("Stmt"),
    IfStmt("Stmt"),
    ForLoopStmt("Stmt"),
    BreakStmt("Stmt"),
    ReturnStmt("Stmt"),
    GetIntStmt("Stmt"),
    GetCharStmt("Stmt"),
    PrintfStmt("Stmt"),
    ForStmt,

    // Exp
    Exp,
    Cond,
    LVal,
    PrimaryExp,
    Number,
    Character,
    UnaryExp,
    UnaryOp,
    FuncRParams,
    MulExp,
    AddExp,
    RelExp,
    EqExp,
    LAndExp,
    LOrExp,
    ConstExp,

    TOKEN,
    FAIL,
    ;

    private final String type;

    SyntaxCompType() {
        this.type = this.name();
    }

    SyntaxCompType(String type) {
        this.type = type;
    }

    public String toString() {
        return "<" + this.type + ">\n";
    }
}
