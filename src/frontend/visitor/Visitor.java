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

import frontend.enums.TokenType;
import frontend.enums.ErrorType;
import frontend.lexer.Token;
import utils.Recorder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Visitor {
    private static class Result {
        public Type type = null;
        public List<Type> paramTypes = new ArrayList<>();
        public boolean isConst = false;
        public int constValue = 0;
    }

    private final static Visitor instance = new Visitor();
    private int symbolTableNum = 1;
    private SymbolTable curTable = null;
    private FuncSymbol curFunc = null;
    private int loopDepth = 0;
    private int blockDepth = 0;
    private String varType = null;

    private Visitor() {}
    public static Visitor getInstance() { return instance; }
    public SymbolTable getCurTable() { return curTable; }

    public void visit(CompUnitNode node) throws IOException {
        if (curTable != null) { return; }
        curTable = new SymbolTable(symbolTableNum++, null);

        for (DeclNode decl : node.getDecls()) {
            visitDecl(decl);
        }
        for (FuncDefNode funcDef : node.getFuncDefs()) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(node.getMainFunc());

        curTable.print();
    }

    private void visitMainFuncDef(MainFuncDefNode node) {
        SymbolTable preTable = curTable;
        curTable = new SymbolTable(symbolTableNum++, preTable);
        preTable.insertSubTable(curTable);

        curFunc = new FuncSymbol(node.getIdent(), "int");
        preTable.insertSymbol(curFunc);

        visitBlock(node.getBlock());

        curTable = preTable;
        curFunc = null;
    }

    private void visitDecl(DeclNode node) {
        if (node.getConstDecl() != null) {
            visitConstDecl(node.getConstDecl());
        } else if (node.getVarDecl() != null) {
            visitVarDecl(node.getVarDecl());
        }
    }

    private void visitConstDecl(ConstDeclNode node) {
        varType = node.getBType().getTypeName();
        for (ConstDefNode constDef : node.getConstDefs()) {
            visitConstDef(constDef);
        }
    }

    private void visitConstDef(ConstDefNode node) {
        Token ident = node.getIdent();
        if (curTable.contains(ident.content())) {
            Recorder.addErrorMessage(ErrorType.RedefinedName, ident.lineno());
            return;
        }

        int dim = 0;
        for (ConstExpNode constExp : node.getConstExps()) {
            dim++;
            visitConstExp(constExp);
        }
        if (node.getConstInitVal() != null) {
            visitConstInitVal(node.getConstInitVal());
        }
        curTable.insertSymbol(new VarSymbol(ident, true, varType, dim));
    }

    private void visitConstInitVal(ConstInitValNode node) {
        if (node.getStringConst() == null) {
            for (ConstExpNode constExp : node.getConstExps()) {
                visitConstExp(constExp);
            }
        }
    }

    private void visitVarDecl(VarDeclNode node) {
        varType = node.getBType().getTypeName();
        for (VarDefNode varDef : node.getVarDefs()) {
            visitVarDef(varDef);
        }
    }

    private void visitVarDef(VarDefNode node) {
        Token ident = node.getIdent();
        if (curTable.contains(ident.content())) {
            Recorder.addErrorMessage(ErrorType.RedefinedName, ident.lineno());
            return;
        }

        int dim = 0;
        for (ConstExpNode constExp : node.getConstExps()) {
            dim++;
            visitConstExp(constExp);
        }
        if (node.getInitVal() != null) {
            visitInitVal(node.getInitVal());
        }
        curTable.insertSymbol(new VarSymbol(ident, false, varType, dim));
    }

    private void visitInitVal(InitValNode node) {
        if (node.getStringConst() == null) {
            for (ExpNode exp : node.getExps()) {
                visitExp(exp);
            }
        }
    }

    private void visitFuncDef(FuncDefNode node) {
        SymbolTable preTable = curTable;
        curTable = new SymbolTable(symbolTableNum++, preTable);
        preTable.insertSubTable(curTable);

        String funcType = node.getFuncType().getTypeName();
        Token ident = node.getIdent();  
        Symbol symbol = preTable.getSymbol(ident.content());
        if (symbol != null) {
            Recorder.addErrorMessage(ErrorType.RedefinedName, ident.lineno());
            curFunc = new FuncSymbol(new Token(TokenType.IDENFR, symbolTableNum + "", ident.lineno()), funcType);
        } else {
            curFunc = new FuncSymbol(ident, funcType);
            preTable.insertSymbol(curFunc);
        }

        if (node.getFuncFParams() != null) {
            curFunc.setParamTypeList(visitFuncFParams(node.getFuncFParams()).paramTypes);
        }
        visitBlock(node.getBlock());
        
        curFunc = null;
        curTable = preTable;
    }

    private Result visitFuncFParams(FuncFParamsNode node) {
        Result result = new Result();
        for (FuncFParamNode funcFParam : node.getFuncFParams()) {
            Type paramType = visitFuncFParam(funcFParam).type;
            if (paramType != null) {
                result.paramTypes.add(paramType);
            }
        }
        return result;
    }

    private Result visitFuncFParam(FuncFParamNode node) {
        Result result = new Result();
        varType = node.getBType().getTypeName();
        Token ident = node.getIdent();
        if (curTable.contains(ident.content())) {
            Recorder.addErrorMessage(ErrorType.RedefinedName, ident.lineno());
            return result;
        }

        int dim = 0;
        for (Node child : node.getChildren()) {
            if (child instanceof TokenNode && ((TokenNode) child).getToken().type() == TokenType.LBRACK) {
                dim++;
            }
        }
        curTable.insertSymbol(new VarSymbol(ident, false, varType, dim));

        result.type = new Type(varType, dim);
        return result;
    }

    private void visitBlock(BlockNode node) {
        blockDepth++;
        List<BlockItemNode> blockItems = node.getBlockItems();
        for (BlockItemNode blockItem : blockItems) {
            visitBlockItem(blockItem);
        }

        if (blockDepth == 1) {
            if (curFunc != null && !curFunc.getFuncType().getTypeName().equals("void")) {
                BlockItemNode blockItem = null;
                if (!blockItems.isEmpty()) {
                    blockItem = blockItems.get(blockItems.size() - 1);
                }
                if (blockItem == null || !(blockItem.getStmt() instanceof ReturnStmtNode)) {
                    Recorder.addErrorMessage(ErrorType.MissingReturnStmt, node.getChildren().get(node.getChildren().size() - 1).getEndLine());
                }
            }
        }
        blockDepth--;
    }

    private void visitBlockItem(BlockItemNode node) {
        if (node.getDecl() != null) {
            visitDecl(node.getDecl());
        } else if (node.getStmt() != null) {
            visitStmt(node.getStmt());
        }
    }

    private void visitStmt(StmtNode node) {
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
        visitLVal(node.getLVal(), true);
        visitExp(node.getExp());
    }

    private void visitExpStmt(ExpStmtNode node) {
        if (node.getExp() != null) {
            visitExp(node.getExp());
        }
    }

    private void visitBlockStmt(BlockStmtNode node) {
        SymbolTable preTable = curTable;
        curTable = new SymbolTable(symbolTableNum++, preTable);
        preTable.insertSubTable(curTable);
        visitBlock(node.getBlock());
        curTable = preTable;
    }

    private void visitIfStmt(IfStmtNode node) {
        if (node.getCond() != null) {
            visitCond(node.getCond());
        }
        if (node.getThenStmt() != null) {
            visitStmt(node.getThenStmt());
        }
        if (node.getElseStmt() != null) {
            visitStmt(node.getElseStmt());
        }
    }

    private void visitForLoopStmt(ForLoopStmtNode node) {
        loopDepth++;
        if (node.getInitStmt() != null) {
            visitForStmt(node.getInitStmt());
        }
        if (node.getCondStmt() != null) {
            visitCond(node.getCondStmt());
        }
        if (node.getForStmt() != null) {
            visitForStmt(node.getForStmt());
        }
        if (node.getStmt() != null) {
            visitStmt(node.getStmt());
        }
        loopDepth--;
    }

    private void visitForStmt(ForStmtNode node) {
        visitLVal(node.getLVal(), true);
        visitExp(node.getExp());
    }

    private void visitBreakStmt(BreakStmtNode node) {
        if (loopDepth == 0) {
            Recorder.addErrorMessage(ErrorType.BreakOrContinueOutsideBlock, 
                node.getBeginLine());
        }
    }

    private void visitReturnStmt(ReturnStmtNode node) {
        if (node.getExp() != null) {
            if (curFunc != null && curFunc.getFuncType().getTypeName().equals("void")) {
                Recorder.addErrorMessage(ErrorType.MismatchedReturnStmt, 
                    node.getBeginLine());
            }
            visitExp(node.getExp());
        }
        // no need to check return type
    }

    private void visitGetIntStmt(GetIntStmtNode node) {
        visitLVal(node.getLVal(), true);
    }

    private void visitGetCharStmt(GetCharStmtNode node) {
        visitLVal(node.getLVal(), true);
    }

    private void visitPrintfStmt(PrintfStmtNode node) {
        String format = node.getStringConst().content();
        int formatCount = 0;
        for (int i = 0; i < format.length() - 1; i++) {
            if (format.charAt(i) == '%' && (format.charAt(i + 1) == 'd' || format.charAt(i + 1) == 'c')) {
                formatCount++;
            }
        }
        for (ExpNode exp : node.getExps()) {
            visitExp(exp);
            formatCount--;
        }
        if (formatCount != 0) {
            Recorder.addErrorMessage(ErrorType.MismatchedPrintfArgs, 
                node.getBeginLine());
        }
    }

    private Result visitLVal(LValNode node, boolean checkConst) {
        Result result = new Result();
        Token ident = node.getIdent();
        int dim = 0;
        VarSymbol var = (VarSymbol) curTable.getSymbol(ident.content());
        if (var == null) {
            Recorder.addErrorMessage(ErrorType.UndefinedName, ident.lineno());
            result.type = new Type("int", 0);
            return result;
        }
        if (checkConst && var.isConst()) {
            Recorder.addErrorMessage(ErrorType.AssignToConst, node.getBeginLine());
        }

        dim = var.getVarType().getDim();
        for (ExpNode exp : node.getExps()) {
            visitExp(exp);
            dim--;
        }
        result.type = new Type(var.getVarType().getTypeName(), dim);
        return result;
    }

    private void visitCond(CondNode node) {
        visitLOrExp(node.getLOrEXp());
    }

    private Result visitExp(ExpNode node) {
        Result result = visitAddExp(node.getAddExp());
        if (result.isConst) {
            node.setConstValue(result.constValue);
        }
        return result;
    }

    private Result visitAddExp(AddExpNode node) {
        Token operator = node.getOperator();
        Result result = visitMulExp(node.getMulExp());
        if (operator != null) {
            Result left = visitAddExp(node.getAddExp());
            if (left.type.getTypeName().equals("int")) {
                result.type = new Type("int", 0);
            }
            if (result.isConst) {
                if (left.isConst) { result.constValue += left.constValue; }
                else { result.isConst = false; }
            }
        }
        return result;
    }

    private Result visitMulExp(MulExpNode node) {
        Token operator = node.getOperator();
        Result result = visitUnaryExp(node.getUnaryExp());
        if (operator != null) {
            Result left = visitMulExp(node.getMulExp());
            if (left.type.getTypeName().equals("int")) {
                result.type = new Type("int", 0);
            }
            if (result.isConst) {
                if (left.isConst) { result.constValue *= left.constValue; }
                else { result.isConst = false; }
            }
        }
        return result;
    }

    private Result visitUnaryExp(UnaryExpNode node) {
        Result result = new Result();
        FuncSymbol func = null;
        
        if (node.getUnaryOp() != null) {
            result = visitUnaryExp(node.getUnaryExp());
            if (node.getUnaryOp().getOperator().type() == TokenType.MINU) {
                if (result.isConst) { result.constValue = -result.constValue; }
            }
        }
        if (node.getPrimaryExp() != null) {
            result = visitPrimaryExp(node.getPrimaryExp());
        }
        if (node.getIdent() != null) {
            func = (FuncSymbol) curTable.getSymbol(node.getIdent().content());
            if (func == null) {
                Recorder.addErrorMessage(ErrorType.UndefinedName, node.getIdent().lineno());
            }
            if (node.getFuncRParams() != null) {
                result = visitFuncRParams(node.getFuncRParams());
            }
        }

        boolean typeMatched = true;
        if (func != null) {
            if (func.getParamTypeList().size() != result.paramTypes.size()) {
                Recorder.addErrorMessage(ErrorType.MismatchedParamNum, 
                    node.getBeginLine());
            } else {
                for (int i = 0; i < func.getParamTypeList().size(); i++) {
                    if (!func.getParamTypeList().get(i).match(result.paramTypes.get(i))) {
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

        return result;
    }

    private Result visitFuncRParams(FuncRParamsNode node) {
        Result result = new Result();
        for (ExpNode exp : node.getExps()) {
            result.paramTypes.add(visitExp(exp).type);
        }
        return result;
    }

    private Result visitPrimaryExp(PrimaryExpNode node) {
        if (node.getCharacter() != null) {
            return visitCharacterNode(node.getCharacter());
        } else if (node.getNumber() != null) {
            return visitNumberNode(node.getNumber());
        } else if (node.getLVal() != null) {
            return visitLVal(node.getLVal(), false);
        } else if (node.getExp() != null) {
            return visitExp(node.getExp());
        }
        return new Result();
    }

    private Result visitNumberNode(NumberNode node) {
        Result result = new Result();
        result.type = new Type("int", 0);
        result.isConst = true;
        result.constValue = Integer.parseInt(node.getNumber().content());
        return result;
    }

    private Result visitCharacterNode(CharacterNode node) {
        Result result = new Result();
        result.type = new Type("char", 0);
        result.isConst = true;
        result.constValue = node.getCharacter().content().charAt(0);
        return result;
    }

    private void visitLOrExp(LOrExpNode node) {
        Token operator = node.getOperator();
        if (operator != null) {
            visitLOrExp(node.getLOrExp());
        }
        visitLAndExp(node.getLAndExp());
    }

    private void visitLAndExp(LAndExpNode node) {
        Token operator = node.getOperator();
        if (operator != null) {
            visitLAndExp(node.getLAndExp());
        }
        visitEqExp(node.getEqExp());
    }

    private void visitEqExp(EqExpNode node) {
        Token operator = node.getOperator();
        if (operator != null) {
            visitEqExp(node.getEqExp());
        }
        visitRelExp(node.getRelExp());
    }

    private void visitRelExp(RelExpNode node) {
        Token operator = node.getOperator();
        if (operator != null) {
            visitRelExp(node.getRelExp());
        }
        visitAddExp(node.getAddExp());
    }

    private void visitConstExp(ConstExpNode node) {
        node.setConstValue(visitAddExp(node.getAddExp()).constValue);
    }
}
