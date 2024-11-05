package frontend.visitor;

import frontend.symbol.SymbolTable;
import frontend.symbol.VarSymbol;
import frontend.symbol.FuncSymbol;
import frontend.symbol.Symbol;
import frontend.symbol.Type;
import frontend.parser.node.CompUnitNode;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;
import frontend.parser.node.declaration.*;
import frontend.parser.node.expression.*;
import frontend.parser.node.function.*;
import frontend.parser.node.statement.*;

import enums.TokenType;
import enums.ErrorType;
import frontend.lexer.Token;
import utils.Recorder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Visitor {
    private final static Visitor instance = new Visitor();
    private int symbolTableNum = 1;
    private SymbolTable curTable = null;
    private FuncSymbol curFunc = null;
    private int loopDepth = 0;
    private int blockDepth = 0;
    private String varType = null;

    private Visitor() {}

    public static Visitor getInstance() {
        return instance;
    }

    public void visit(CompUnitNode node) throws IOException {
        curTable = new SymbolTable(symbolTableNum++, null);

        for (Node child : node.getChildren()) {
            if (child instanceof DeclNode) {
                visitDecl((DeclNode) child);
            } else if (child instanceof FuncDefNode) {
                visitFuncDef((FuncDefNode) child);
            } else if (child instanceof MainFuncDefNode) {
                visitMainFuncDef((MainFuncDefNode) child);
            }
        }

        curTable.print();
    }

    private void visitMainFuncDef(MainFuncDefNode node) {
        SymbolTable preTable = curTable;
        curTable = new SymbolTable(symbolTableNum++, preTable);
        preTable.insertSubTable(curTable);
        if (node.getChildren().get(0) instanceof TokenNode) {
            curFunc = new FuncSymbol(((TokenNode) node.getChildren().get(0)).getToken(), "int");
        } else { System.out.println("Error: MainFuncDefNode type error"); }

        for (Node child : node.getChildren()) {
            if (child instanceof BlockNode) {
                visitBlock((BlockNode) child);
            }
        }
        curTable = preTable;
    }

    private void visitDecl(DeclNode node) {
        Node child = node.getChildren().get(0);
        if (child instanceof ConstDeclNode) {
            visitConstDecl((ConstDeclNode) child);
        } else {
            visitVarDecl((VarDeclNode) child);
        }
    }

    private void visitConstDecl(ConstDeclNode node) {
        if (node.getChildren().get(1) instanceof BTypeNode) {
            varType = ((BTypeNode) node.getChildren().get(1)).getTypeName();
        } else { System.out.println("Error: ConstDeclNode type error"); }
        for (Node child : node.getChildren()) {
            if (child instanceof ConstDefNode) {
                visitConstDef((ConstDefNode) child);
            }
        }
    }

    private void visitConstDef(ConstDefNode node) {
        Token ident = null;
        if (node.getChildren().get(0) instanceof TokenNode) {
            ident = ((TokenNode) node.getChildren().get(0)).getToken();
        } else { System.out.println("Error: ConstDefNode type error"); }
        if (curTable.contains(ident.content())) {
            Recorder.addErrorMessage(ErrorType.RedefinedName, ident.lineno());
            return;
        }

        int dim = 0;
        for (Node child : node.getChildren()) {
            if (child instanceof ConstExpNode) {
                dim++;
                visitConstExp((ConstExpNode) child);
            } else if (child instanceof ConstInitValNode) {
                visitConstInitVal((ConstInitValNode) child);
            }
        }
        curTable.insertSymbol(new VarSymbol(ident, true, varType, dim));
    }

    private void visitConstInitVal(ConstInitValNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof ConstExpNode) {
                visitConstExp((ConstExpNode) child);
            }
        }
    }

    private void visitVarDecl(VarDeclNode node) {
        if (node.getChildren().get(0) instanceof BTypeNode) {
            varType = ((BTypeNode) node.getChildren().get(0)).getTypeName();
        } else { System.out.println("Error: VarDeclNode type error"); }
        for (Node child : node.getChildren()) {
            if (child instanceof VarDefNode) {
                visitVarDef((VarDefNode) child);
            }
        }
    }

    private void visitVarDef(VarDefNode node) {
        Token ident = null;
        if (node.getChildren().get(0) instanceof TokenNode) {
            ident = ((TokenNode) node.getChildren().get(0)).getToken();
        } else { System.out.println("Error: VarDefNode ident error"); }
        if (curTable.contains(ident.content())) {
            Recorder.addErrorMessage(ErrorType.RedefinedName, ident.lineno());
            return;
        }

        int dim = 0;
        for (Node child : node.getChildren()) {
            if (child instanceof ConstExpNode) {
                dim++;
                visitConstExp((ConstExpNode) child);
            } else if (child instanceof InitValNode) {
                visitInitVal((InitValNode) child);
            }
        }
        curTable.insertSymbol(new VarSymbol(ident, false, varType, dim));
    }

    private void visitInitVal(InitValNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof ExpNode) {
                visitExp((ExpNode) child);
            }
        }
    }

    private void visitFuncDef(FuncDefNode node) {
        SymbolTable preTable = curTable;
        curTable = new SymbolTable(symbolTableNum++, preTable);
        preTable.insertSubTable(curTable);

        String funcType = null;
        if (node.getChildren().get(0) instanceof FuncTypeNode) {
            funcType = ((FuncTypeNode) node.getChildren().get(0)).getTypeName();
        } else { System.out.println("Error: FuncDefNode type error"); }
        Token ident = null;
        if (node.getChildren().get(1) instanceof TokenNode) {
            ident = ((TokenNode) node.getChildren().get(1)).getToken();
        } else { System.out.println("Error: FuncDefNode ident error"); }

        Symbol symbol = preTable.getSymbol(ident.content());
        if (symbol != null) {
            Recorder.addErrorMessage(ErrorType.RedefinedName, ident.lineno());
            curFunc = new FuncSymbol(new Token(TokenType.IDENFR, symbolTableNum + "", ident.lineno()), funcType);
        } else {
            curFunc = new FuncSymbol(ident, funcType);
            preTable.insertSymbol(curFunc);
        }

        for (Node child : node.getChildren()) {
            if (child instanceof FuncFParamsNode) {
                curFunc.setParamTypeList(visitFuncFParams((FuncFParamsNode) child));

            } else if (child instanceof BlockNode) {
                visitBlock((BlockNode) child);
            }
        }
        
        curFunc = null;
        curTable = preTable;
    }

    private List<Type> visitFuncFParams(FuncFParamsNode node) {
        List<Type> paramTypeList = new ArrayList<>();
        for (Node child : node.getChildren()) {
            if (child instanceof FuncFParamNode) {
                Type paramType = visitFuncFParam((FuncFParamNode) child);
                if (paramType != null) {
                    paramTypeList.add(paramType);
                }
            }
        }
        return paramTypeList;
    }

    private Type visitFuncFParam(FuncFParamNode node) {
        if (node.getChildren().get(0) instanceof BTypeNode) {
            varType = ((BTypeNode) node.getChildren().get(0)).getTypeName();
        } else { System.out.println("Error: FuncFParamNode type error"); }
        Token ident = null;
        if (node.getChildren().get(1) instanceof TokenNode) {
            ident = ((TokenNode) node.getChildren().get(1)).getToken();
        } else { System.out.println("Error: FuncFParamNode ident error"); }
        if (curTable.contains(ident.content())) {
            Recorder.addErrorMessage(ErrorType.RedefinedName, ident.lineno());
            return null;
        }

        int dim = 0;
        for (Node child : node.getChildren()) {
            if (child instanceof TokenNode && ((TokenNode) child).getToken().type() == TokenType.LBRACK) {
                dim++;
            }
        }
        curTable.insertSymbol(new VarSymbol(ident, false, varType, dim));

        return new Type(varType, dim);
    }

    private void visitBlock(BlockNode node) {
        blockDepth++;
        for (Node child : node.getChildren()) {
            if (child instanceof BlockItemNode) {
                visitBlockItem((BlockItemNode) child);
            }
        }

        if (blockDepth == 1) {
            if (!curFunc.getFuncType().getTypeName().equals("void")) {
                Node blockItem = node.getChildren().get(node.getChildren().size() - 2);
                if (!(blockItem instanceof BlockItemNode) || !(blockItem.getChildren().get(0) instanceof ReturnStmtNode)) {
                    Recorder.addErrorMessage(ErrorType.MissingReturnStmt, node.getChildren().get(node.getChildren().size() - 1).getEndLine());
                }
            }
        }
        blockDepth--;
    }

    private void visitBlockItem(BlockItemNode node) {
        Node child = node.getChildren().get(0);
        if (child instanceof DeclNode) {
            visitDecl((DeclNode) child);
        } else {
            visitStmt(child);
        }
    }

    private void visitStmt(Node node) {
        switch (node.getType()) {
            case AssignStmt -> visitAssignStmt((AssignStmtNode) node);
            case ExpStmt -> visitExpStmt((ExpStmtNode) node);
            case BlockStmt -> visitBlockStmt((BlockStmtNode) node);
            case IfStmt -> visitIfStmt((IfStmtNode) node);
            case ForLoopStmt -> visitForLoopStmt((ForLoopStmtNode) node);
            case BreakStmt -> visitBreakStmt((BreakStmtNode) node);
            case ReturnStmt -> visitReturnStmt((ReturnStmtNode) node);
            case GetIntStmt -> visitGetIntStmt((GetIntStmtNode) node);
            case GetCharStmt -> visitGetCharStmt((GetCharStmtNode) node);
            case PrintfStmt -> visitPrintfStmt((PrintfStmtNode) node);
            default -> {}
        }
    }

    private void visitAssignStmt(AssignStmtNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof LValNode) {
                visitLVal((LValNode) child, true);
            } else if (child instanceof ExpNode) {
                visitExp((ExpNode) child);
            }
        }
    }

    private void visitExpStmt(ExpStmtNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof ExpNode) {
                visitExp((ExpNode) child);
            }
        }
    }

    private void visitBlockStmt(BlockStmtNode node) {
        SymbolTable preTable = curTable;
        curTable = new SymbolTable(symbolTableNum++, preTable);
        preTable.insertSubTable(curTable);
        visitBlock((BlockNode) node.getChildren().get(0));
        curTable = preTable;
    }

    private void visitIfStmt(IfStmtNode node) {
        for (Node child : node.getChildren()) {
            visitStmt(child);
        }
    }

    private void visitForLoopStmt(ForLoopStmtNode node) {
        loopDepth++;
        for (Node child : node.getChildren()) {
            if (child instanceof ForStmtNode) {
                visitForStmt((ForStmtNode) child);
            } else if (child instanceof CondNode) {
                visitCond((CondNode) child);
            } else {
                visitStmt(child);
            }
        }
        loopDepth--;
    }

    private void visitForStmt(ForStmtNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof LValNode) {
                visitLVal((LValNode) child, true);
            } else if (child instanceof ExpNode) {
                visitExp((ExpNode) child);
            }
        }
    }

    private void visitBreakStmt(BreakStmtNode node) {
        if (loopDepth == 0) {
            Recorder.addErrorMessage(ErrorType.BreakOrContinueOutsideBlock, 
                node.getBeginLine());
        }
    }

    private void visitReturnStmt(ReturnStmtNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof ExpNode) {
                if (curFunc != null && curFunc.getFuncType().getTypeName().equals("void")) {
                    Recorder.addErrorMessage(ErrorType.MismatchedReturnStmt, 
                        node.getBeginLine());
                }
                visitExp((ExpNode) child);
            }
        }
    }

    private void visitGetIntStmt(GetIntStmtNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof LValNode) {
                visitLVal((LValNode) child, true);
            }
        }
    }

    private void visitGetCharStmt(GetCharStmtNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof LValNode) {
                visitLVal((LValNode) child, true);
            }
        }
    }

    private void visitPrintfStmt(PrintfStmtNode node) {
        int formatCount = 0;
        for (Node child : node.getChildren()) {
            if (child instanceof TokenNode) {
                Token token = ((TokenNode) child).getToken();
                if (token.type() == TokenType.STRCON) {
                    String format = token.content();
                    for (int i = 0; i < format.length() - 1; i++) {
                        if (format.charAt(i) == '%' && (format.charAt(i + 1) == 'd' || format.charAt(i + 1) == 'c')) {
                            formatCount++;
                        }
                    }
                }
            } else if (child instanceof ExpNode) {
                visitExp((ExpNode) child);
                formatCount--;
            }
        }
        if (formatCount != 0) {
            Recorder.addErrorMessage(ErrorType.MismatchedPrintfArgs, 
                node.getBeginLine());
        }
    }

    private VarSymbol visitLVal(LValNode node, boolean checkConst) {
        VarSymbol var = null;
        int dim = 0;
        Token ident = null;
        if (node.getChildren().get(0) instanceof TokenNode) {
            ident = ((TokenNode) node.getChildren().get(0)).getToken();
        } else { System.out.println("Error: LValNode ident error"); }

        var = (VarSymbol) curTable.getSymbol(ident.content());
        if (var == null) {
            Recorder.addErrorMessage(ErrorType.UndefinedName, ident.lineno());
        } else {
            dim = var.getVarType().getDim();
            if (checkConst && var.isConst()) {
                Recorder.addErrorMessage(ErrorType.AssignToConst, node.getBeginLine());
            }
        }
        // System.out.println("LValNode ident: " + ident.content());
        // System.out.println(var);

        for (Node child : node.getChildren()) {
            if (child instanceof ExpNode) {
                visitExp((ExpNode) child);
                dim--;
            }
        }
        if (var != null) {
            return new VarSymbol(ident, var.isConst(), var.getVarType().getTypeName(), dim);
        }
        return new VarSymbol(ident, false, "int", 0);
    }

    private void visitCond(CondNode node) {
        visitLOrExp((LOrExpNode) node.getChildren().get(0));
    }

    private Type visitExp(ExpNode node) {
        return new Type(visitAddExp((AddExpNode) node.getChildren().get(0)));
    }

    private Type visitAddExp(AddExpNode node) {
        Type type = null;
        boolean hasInt = false;
        for (Node child : node.getChildren()) {
            if (child instanceof AddExpNode) {
                type = visitAddExp((AddExpNode) child);
            } else if (child instanceof MulExpNode) {
                type = visitMulExp((MulExpNode) child);
            }

            if (type != null && type.getTypeName().equals("int")) {
                hasInt = true;
            }
        }
        return new Type(hasInt ? "int" : "char", type == null ? 0 : type.getDim());
    }

    private Type visitMulExp(MulExpNode node) {
        Type type = null;
        boolean hasInt = false;
        for (Node child : node.getChildren()) {
            if (child instanceof MulExpNode) {
                type = visitMulExp((MulExpNode) child);
            } else if (child instanceof UnaryExpNode) {
                type = visitUnaryExp((UnaryExpNode) child);
            }

            if (type != null && type.getTypeName().equals("int")) {
                hasInt = true;
            }
        }
        return new Type(hasInt ? "int" : "char", type == null ? 0 : type.getDim());
    }

    private Type visitUnaryExp(UnaryExpNode node) {
        Type type = null;
        FuncSymbol func = null;
        List<Type> paramTypeList = new ArrayList<>();
        
        for (Node child : node.getChildren()) {
            if (child instanceof UnaryExpNode) {
                type = new Type(visitUnaryExp((UnaryExpNode) child));
            } else if (child instanceof PrimaryExpNode) {
                type = new Type(visitPrimaryExp((PrimaryExpNode) child));
            } else if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.LPARENT ||
                    ((TokenNode) child).getToken().type() == TokenType.RPARENT) {
                    continue;
                }
                Token token = ((TokenNode) child).getToken();
                func = (FuncSymbol) curTable.getSymbol(token.content());
                if (func == null) {
                    Recorder.addErrorMessage(ErrorType.UndefinedName, token.lineno());
                }
            } else if (child instanceof FuncRParamsNode) {
                paramTypeList = visitFuncRParams((FuncRParamsNode) child);
            }
        }

        boolean typeMatched = true;
        if (func != null) {
            if (func.getParamTypeList().size() != paramTypeList.size()) {
                Recorder.addErrorMessage(ErrorType.MismatchedParamNum, 
                    node.getBeginLine());
            } else {
                for (int i = 0; i < func.getParamTypeList().size(); i++) {
                    if (!func.getParamTypeList().get(i).match(paramTypeList.get(i))) {
                        typeMatched = false;
                        break;
                    }
                }
            }
        }
        if (!typeMatched) {
            Recorder.addErrorMessage(ErrorType.MismatchedParamType, 
                node.getBeginLine());
        }

        if (func != null) {
            return new Type(func.getFuncType());
        } else { return type; }
    }

    private List<Type> visitFuncRParams(FuncRParamsNode node) {
        List<Type> paramTypeList = new ArrayList<>();
        for (Node child : node.getChildren()) {
            if (child instanceof ExpNode) {
                paramTypeList.add(new Type(visitExp((ExpNode) child)));
            }
        }
        return paramTypeList;
    }

    private Type visitPrimaryExp(PrimaryExpNode node) {
        Type type = null;
        for (Node child : node.getChildren()) {
            if (child instanceof ExpNode) {
                type = new Type(visitExp((ExpNode) child));
            } else if (child instanceof LValNode) {
                type = new Type(visitLVal((LValNode) child, false).getVarType());
            } else if (child instanceof NumberNode) {
                type = new Type("int", 0);
            } else if (child instanceof CharacterNode) {
                type = new Type("char", 0);
            }
        }
        return type;
    }

    private void visitLOrExp(LOrExpNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof LOrExpNode) {
                visitLOrExp((LOrExpNode) child);
            } else if (child instanceof LAndExpNode) {
                visitLAndExp((LAndExpNode) child);
            }
        }
    }

    private void visitLAndExp(LAndExpNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof LAndExpNode) {
                visitLAndExp((LAndExpNode) child);
            } else if (child instanceof EqExpNode) {
                visitEqExp((EqExpNode) child);
            }
        }
    }

    private void visitEqExp(EqExpNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof EqExpNode) {
                visitEqExp((EqExpNode) child);
            } else if (child instanceof RelExpNode) {
                visitRelExp((RelExpNode) child);
            }
        }
    }

    private void visitRelExp(RelExpNode node) {
        for (Node child : node.getChildren()) {
            if (child instanceof RelExpNode) {
                visitRelExp((RelExpNode) child);
            } else if (child instanceof AddExpNode) {
                visitAddExp((AddExpNode) child);
            }
        }
    }

    private void visitConstExp(ConstExpNode node) {
        visitAddExp((AddExpNode) node.getChildren().get(0));
    }
}
