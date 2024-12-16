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

import java.util.ArrayList;
import java.util.List;

public class Visitor {
    private static class Result {
        public Type type = null;
        public List<Type> paramTypes = new ArrayList<>();
        public boolean isConst = false;
        public int constValue = 0;
        public List<Integer> constValues = new ArrayList<>();
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

    public void visit(CompUnitNode node) {
        if (curTable != null) { return; }
        curTable = new SymbolTable(symbolTableNum++, null);

        for (DeclNode decl : node.getDecls()) {
            visitDecl(decl);
        }
        for (FuncDefNode funcDef : node.getFuncDefs()) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(node.getMainFunc());
        // curTable.print();
    }

    private void visitMainFuncDef(MainFuncDefNode node) {
        SymbolTable preTable = curTable;
        curTable = new SymbolTable(symbolTableNum++, preTable);
        preTable.insertSubTable(curTable);

        curFunc = new FuncSymbol(node.getIdent(), "int");
//        preTable.insertSymbol(curFunc);

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

        Type type = new Type(varType);
        for (ConstExpNode constExp : node.getConstExps()) {
            type.addDim(visitConstExp(constExp).constValue);
        }
        VarSymbol var = new VarSymbol(ident, true, type);
        if (node.getConstInitVal() != null) {
            Result result = visitConstInitVal(node.getConstInitVal());
            for (int i = 0; i < result.constValues.size(); i++) {
                var.addConstValue(result.constValues.get(i));
            }
        }
        curTable.insertSymbol(var);
    }

    private Result visitConstInitVal(ConstInitValNode node) {
        Result result = new Result();
        result.isConst = true;
        if (node.getConstExp() != null) {
            result.constValues.add(visitConstExp(node.getConstExp()).constValue);
        } else if (node.getStringConst() != null) {
            String str = node.getStringConst();
            for (int i = 0; i < str.length(); i++) {
                result.constValues.add((int) str.charAt(i));
            }
        } else {
            for (ConstExpNode constExp : node.getConstExps()) {
                result.constValues.add(visitConstExp(constExp).constValue);
            }
        }
        return result;
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

        Type type = new Type(varType);
        for (ConstExpNode constExp : node.getConstExps()) {
            type.addDim(visitConstExp(constExp).constValue);
        }
        if (node.getInitVal() != null) {
            visitInitVal(node.getInitVal());
        }
        curTable.insertSymbol(new VarSymbol(ident, false, type));
    }

    private void visitInitVal(InitValNode node) {
        if (node.getExp() != null) {
            visitExp(node.getExp());
        } else if (node.getStringConst() == null) {
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

        result.type = new Type(varType);
        for (Node child : node.getChildren()) {
            if (child instanceof TokenNode && ((TokenNode) child).getToken().type() == TokenType.LBRACK) {
                result.type.addDim(0);
            }
        }
        curTable.insertSymbol(new VarSymbol(ident, false, result.type));

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
        String format = node.getStringConst();
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
        VarSymbol var = (VarSymbol) curTable.getSymbol(ident.content());
        if (var == null) {
            Recorder.addErrorMessage(ErrorType.UndefinedName, ident.lineno());
            result.type = new Type("int");
            return result;
        }
        if (checkConst && var.isConst()) {
            Recorder.addErrorMessage(ErrorType.AssignToConst, node.getBeginLine());
        }

        result.type = new Type(var.getVarType());
        int offset = 0;
        boolean flag = true;
        for (ExpNode exp : node.getExps()) {
            Result expResult = visitExp(exp);
            if (expResult.isConst) {
                offset = expResult.constValue;
            } else {
                flag = false;
            }
            result.type.delDim();
        }
        if (var.isConst() && result.type.getDimSize() == 0 && flag) {
            result.isConst = true;
            if (offset < var.getConstValues().size()) {
                result.constValue = var.getConstValues().get(offset);
            } else {
                result.constValue = 0;
            }
        }
        return result;
    }

    private void visitCond(CondNode node) {
        Result result = visitLOrExp(node.getLOrExp());
        if (result.isConst) {
            node.setConstValue(result.constValue);
        }
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
                result.type = new Type("int");
            }
            if (result.isConst) {
                if (left.isConst) {
                    switch (operator.type()) {
                        case PLUS -> result.constValue = left.constValue + result.constValue;
                        case MINU -> result.constValue = left.constValue - result.constValue;
                        default -> {}
                    }
                }
                else { result.isConst = false; }
            }
        }
        if (result.isConst) {
            node.setConstValue(result.constValue);
        }
        return result;
    }

    private Result visitMulExp(MulExpNode node) {
        Token operator = node.getOperator();
        Result result = visitUnaryExp(node.getUnaryExp());
        if (operator != null) {
            Result left = visitMulExp(node.getMulExp());
            if (left.type.getTypeName().equals("int")) {
                result.type = new Type("int");
            }
            if (result.isConst) {
                if (left.isConst) {
                    switch (operator.type()) {
                        case MULT -> result.constValue = left.constValue * result.constValue;
                        case DIV -> result.constValue = left.constValue / result.constValue;
                        case MOD -> result.constValue = left.constValue % result.constValue;
                        default -> {}
                    }
                }
                else { result.isConst = false; }
            }
        }
        if (result.isConst) {
            node.setConstValue(result.constValue);
        }
        return result;
    }

    private Result visitUnaryExp(UnaryExpNode node) {
        Result result = new Result();
        FuncSymbol func = null;
        
        if (node.getUnaryOp() != null) {
            result = visitUnaryExp(node.getUnaryExp());
            if (result.isConst) {
                if (node.getUnaryOp().getOperator().type() == TokenType.MINU) {
                    result.constValue = -result.constValue;
                } else if (node.getUnaryOp().getOperator().type() == TokenType.NOT) {
                    result.constValue = result.constValue == 0 ? 1 : 0;
                }
                node.setConstValue(result.constValue);
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
            if (func != null) {
                result.type = new Type(func.getFuncType());
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
        result.type = new Type("int");
        result.isConst = true;
        result.constValue = Integer.parseInt(node.getNumber().content());
        return result;
    }

    private Result visitCharacterNode(CharacterNode node) {
        Result result = new Result();
        result.type = new Type("char");
        result.isConst = true;
        result.constValue = node.getCharacter();
        return result;
    }

    private Result visitLOrExp(LOrExpNode node) {
        Token operator = node.getOperator();
        Result result = visitLAndExp(node.getLAndExp());
        if (operator != null) {
            Result left = visitLOrExp(node.getLOrExp());
            if (left.isConst) {
                if (left.constValue == 1) {
                    result.constValue = 1;
                    result.isConst = true;
                }
            } else { result.isConst = false; }
        }
        if (result.isConst) {
            node.setConstValue(result.constValue);
        }
        return result;
    }

    private Result visitLAndExp(LAndExpNode node) {
        Token operator = node.getOperator();
        Result result = visitEqExp(node.getEqExp());
        if (operator != null) {
            Result left = visitLAndExp(node.getLAndExp());
            if (left.isConst) {
                if (left.constValue == 0) {
                    result.constValue = 0;
                    result.isConst = true;
                }
            } else { result.isConst = false; }
        }
        if (result.isConst) {
            node.setConstValue(result.constValue);
        }
        return result;
    }

    private Result visitEqExp(EqExpNode node) {
        Token operator = node.getOperator();
        Result result = visitRelExp(node.getRelExp());
        if (operator != null) {
            Result left = visitEqExp(node.getEqExp());
            if (result.isConst) {
                if (left.isConst) {
                    switch (operator.type()) {
                        case EQL -> result.constValue = left.constValue == result.constValue ? 1 : 0;
                        case NEQ -> result.constValue = left.constValue != result.constValue ? 1 : 0;
                        default -> {}
                    }
                } else { result.isConst = false; }
            }
        }
        if (result.isConst) {
            node.setConstValue(result.constValue > 0 ? 1 : 0);
        }
        return result;
    }

    private Result visitRelExp(RelExpNode node) {
        Token operator = node.getOperator();
        Result result = visitAddExp(node.getAddExp());
        if (operator != null) {
            Result left = visitRelExp(node.getRelExp());
            if (result.isConst) {
                if (left.isConst) {
                    switch (operator.type()) {
                        case GEQ -> result.constValue = left.constValue >= result.constValue ? 1 : 0;
                        case LEQ -> result.constValue = left.constValue <= result.constValue ? 1 : 0;
                        case GRE -> result.constValue = left.constValue > result.constValue ? 1 : 0;
                        case LSS -> result.constValue = left.constValue < result.constValue ? 1 : 0;
                        default -> {}
                    }
                } else { result.isConst = false; }
            }
        }
        if (result.isConst) {
            node.setConstValue(result.constValue);
        }
        return result;
    }

    private Result visitConstExp(ConstExpNode node) {
        Result result = visitAddExp(node.getAddExp());
        node.setConstValue(result.constValue);
        return result;
    }
}
