package llvm.ir;

import frontend.enums.TokenType;
import frontend.parser.node.CompUnitNode;
import frontend.parser.node.declaration.*;
import frontend.parser.node.expression.*;
import frontend.parser.node.function.*;
import frontend.parser.node.statement.*;
import frontend.symbol.*;
import llvm.value.*;
import llvm.value.constant.Constant;
import llvm.value.constant.ConstantArray;
import llvm.value.constant.ConstantInt;
import llvm.value.constant.ConstantString;
import llvm.value.instruction.base.BinaryInstruction;
import llvm.value.instruction.base.CallInstruction;
import llvm.value.instruction.base.Instruction;
import llvm.value.instruction.base.UnaryInstruction;
import llvm.value.instruction.memory.AllocaInstruction;
import llvm.value.instruction.memory.GetElementPtrInstruction;
import llvm.value.instruction.memory.LoadInstruction;
import llvm.value.instruction.memory.StoreInstruction;
import llvm.value.instruction.terminator.ReturnInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRGenerator {
    // all for value!!!
    private static class Result {
        public boolean isConst = false;
        public Value value = null;
        public List<Value> values = new ArrayList<>();
    }

    private static final IRGenerator instance = new IRGenerator();
    private IRGenerator() {}
    public static IRGenerator getInstance() { return instance; }
    public void setSymbolTable(SymbolTable table) { curTable = table; }

    private final Module irModule = new Module();
    private boolean isGlobal = false;
    private Function curFunction = null;
    private BasicBlock curBasicBlock = null;
    private SymbolTable curTable = null;

    public Module getIrModule() { return irModule; }

    public void move2NextTable() {
        curTable = curTable.next();
    }

    public void visit(CompUnitNode node) {
        if (!irModule.getFunctions().isEmpty()) { return; }
        isGlobal = true;
        for (DeclNode decl : node.getDecls()) {
            visitDecl(decl);
        }
        isGlobal = false;
        for (FuncDefNode funcDef : node.getFuncDefs()) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(node.getMainFunc());
    }

    private void visitMainFuncDef(MainFuncDefNode node) {
        IRType retType = IRType.FunctionIRType.get(IRType.IntegerIRType.get(32), new ArrayList<>());
        curFunction = new Function(retType, "main");
        curBasicBlock = curFunction.getEntryBlock();
        irModule.addFunction(curFunction);

        FuncSymbol func = new FuncSymbol(node.getIdent(), "int");
        func.setValue(curFunction);
        move2NextTable();

        visitBlock(node.getBlock());
        curFunction = null;
        curBasicBlock = null;
        move2NextTable();
    }

    private void visitDecl(DeclNode node) {
        if (node.getConstDecl() != null) {
            visitConstDecl(node.getConstDecl());
        } else {
            visitVarDecl(node.getVarDecl());
        }
    }

    private void visitConstDecl(ConstDeclNode node) {
        for (ConstDefNode constDef : node.getConstDefs()) {
            visitConstDef(constDef);
        }
    }

    private void visitConstDef(ConstDefNode node) {
        String ident = node.getIdent().content();
        VarSymbol var = (VarSymbol) curTable.getSymbolByLine(ident, node.getIdent().lineno());
        IRType type = IRType.convert(var.getVarType().getTypeName(), var.getVarType().getDims());

        Result init;
        if (type.isArrayTy()) {
            init = visitConstInitVal(node.getConstInitVal(),
                    var.getVarType().getDims().get(0), ((IRType.ArrayIRType) type).getElementType());
        } else {
            init = visitConstInitVal(node.getConstInitVal(), 0, type);
        }
        if (isGlobal) {
            GlobalValue global = new GlobalValue(type, ident, true);
            if (init.value != null) {
                global.setInitializer((Constant) init.value);
            }
            irModule.addGlobalValue(global);
            var.setValue(global);
        } else {
            Instruction alloc = new AllocaInstruction(type);
            curBasicBlock.insertInstruction(alloc, curBasicBlock.getAllocNum());
            var.setValue(alloc);
            
            if (init.value instanceof ConstantInt) {
                Instruction store = new StoreInstruction(init.value, alloc);
                curBasicBlock.addInstruction(store);
            } else if (type.isArrayTy()) {
                int bitWidth = ((IRType.IntegerIRType) ((IRType.ArrayIRType) type).getElementType()).getBitWidth();
                ConstantInt zero = ConstantInt.get(bitWidth, 0);
                int arraySize = var.getVarType().getDims().get(0);
                if (init.value == null) {
                    for (int i = 0; i < arraySize; i++) {
                        List<Value> indices = new ArrayList<>();
                        indices.add(ConstantInt.get(32, 0));
                        indices.add(ConstantInt.get(32, i));
                        Instruction gep = new GetElementPtrInstruction(alloc, indices);
                        curBasicBlock.addInstruction(gep);

                        Instruction store = new StoreInstruction(zero, gep);
                        curBasicBlock.addInstruction(store);
                    }
                } else {
                    List<Constant> array = ((ConstantArray) init.value).getElements();

                    for (int i = 0; i < arraySize; i++) {
                        List<Value> indices = new ArrayList<>();
                        indices.add(ConstantInt.get(32, 0));
                        indices.add(ConstantInt.get(32, i));
                        Instruction gep = new GetElementPtrInstruction(alloc, indices);
                        curBasicBlock.addInstruction(gep);

                        Instruction store;
                        if (i < array.size()) {
                             store = new StoreInstruction(array.get(i), gep);
                        } else {
                            store = new StoreInstruction(zero, gep);
                        }
                        curBasicBlock.addInstruction(store);
                    }
                }
            }
            
        }
    }

    private Result visitConstInitVal(ConstInitValNode node, int size, IRType elementType) {
        Result result = new Result();
        result.isConst = true;
        if (node.getStringConst() != null) {
            String str = node.getStringConst();
            if (isGlobal) {
                result.value = ConstantString.get(str, size);
            } else {
                List<Constant> elements = new ArrayList<>();
                for (int i = 0; i < str.length(); i++) {
                    elements.add(ConstantInt.get(8, str.charAt(i)));
                }
                result.value = ConstantArray.get(elementType, elements.size(), elements);
            }
        } else if (node.getConstExp() != null) {
            result.value = ConstantInt.get(((IRType.IntegerIRType) elementType).getBitWidth(),
                    node.getConstExp().getConstValue());
        } else {
            List<Constant> elements = new ArrayList<>();
            int zeroCount = 0;
            for (ConstExpNode constExp : node.getConstExps()) {
                elements.add(ConstantInt.get(((IRType.IntegerIRType) elementType).getBitWidth(),
                        constExp.getConstValue()));
                if (constExp.getConstValue() == 0) { zeroCount++; }
            }
            if (zeroCount == elements.size()) {
                result.value = null;
            } else {
                result.value = ConstantArray.get(elementType, elements.size(), elements);
            }
        }
        return result;
    }

    private void visitVarDecl(VarDeclNode node) {
        for (VarDefNode varDef : node.getVarDefs()) {
            visitVarDef(varDef);
        }
    }

    private void visitVarDef(VarDefNode node) {
        String ident = node.getIdent().content();
        VarSymbol var = (VarSymbol) curTable.getSymbolByLine(ident, node.getIdent().lineno());
        IRType type = IRType.convert(var.getVarType().getTypeName(), var.getVarType().getDims());

        Result init = null;
        if (node.getInitVal() != null) {
            if (type.isArrayTy()) {
                init = visitInitVal(node.getInitVal(), 
                var.getVarType().getDims().get(0), ((IRType.ArrayIRType) type).getElementType());
            } else {
                init = visitInitVal(node.getInitVal(), 0, type);
            }
        }
        if (isGlobal) {
            GlobalValue global = new GlobalValue(type, ident, false);
            if (node.getInitVal() != null && init.value != null) {
                global.setInitializer((Constant) init.value);
            } else if (!type.isArrayTy()) {
                global.setInitializer(ConstantInt.get(((IRType.IntegerIRType) type).getBitWidth(), 0));
            }
            irModule.addGlobalValue(global);
            var.setValue(global);
        } else {
            Instruction alloc = new AllocaInstruction(type);
            curBasicBlock.insertInstruction(alloc, curBasicBlock.getAllocNum());
            var.setValue(alloc);

            if (node.getInitVal() != null) {
                if (type.isArrayTy()) {
                    List<Value> values = new ArrayList<>();
                    if (init.value != null) {
                        values.addAll(((ConstantArray) init.value).getElements());
                    } else {
                        values.addAll(init.values);
                    }
                    for (int i = 0; i < values.size(); i++) {
                        List<Value> indices = new ArrayList<>();
                        indices.add(ConstantInt.get(32, 0));
                        indices.add(ConstantInt.get(32, i));
                        Instruction gep = new GetElementPtrInstruction(alloc, indices);
                        curBasicBlock.addInstruction(gep);

                        Instruction store = new StoreInstruction(values.get(i), gep);
                        curBasicBlock.addInstruction(store);
                    }
                    if (init.value != null) {
                        ConstantInt zero = ConstantInt.get(8, 0);
                        for (int i = values.size(); i < var.getVarType().getDims().get(0); i++) {
                            List<Value> indices = new ArrayList<>();
                            indices.add(ConstantInt.get(32, 0));
                            indices.add(ConstantInt.get(32, i));
                            Instruction gep = new GetElementPtrInstruction(alloc, indices);
                            curBasicBlock.addInstruction(gep);

                            Instruction store = new StoreInstruction(zero, gep);
                            curBasicBlock.addInstruction(store);
                        }
                    }
                } else {
                    Instruction store = new StoreInstruction(init.value, alloc);
                    curBasicBlock.addInstruction(store);
                }
            }
        }
    }

    private Result visitInitVal(InitValNode node, int size, IRType elementType) {
        Result result = new Result();
        if (node.getStringConst() != null) {
            String str = node.getStringConst();
            if (isGlobal) {
                result.value = ConstantString.get(str, size);
            } else {
                List<Constant> elements = new ArrayList<>();
                for (int i = 0; i < str.length(); i++) {
                    elements.add(ConstantInt.get(8, str.charAt(i)));
                }
                result.value = ConstantArray.get(elementType, elements.size(), elements);
            }
        } else if (node.getExp() != null) {
            if (isGlobal) {
                result.value = ConstantInt.get(((IRType.IntegerIRType) elementType).getBitWidth(),
                        node.getExp().getConstValue());
            } else {
                result = visitExp(node.getExp());
                if (!result.value.getType().equals(elementType)) {
                    Instruction cast;
                    if (elementType.equals(IRType.IntegerIRType.get(8))) {
                        cast = new UnaryInstruction(
                                UnaryInstruction.Operator.Trunc,
                                result.value, elementType
                        );
                    } else {
                        cast = new UnaryInstruction(
                                UnaryInstruction.Operator.ZExt,
                                result.value, elementType
                        );
                    }
                    curBasicBlock.addInstruction(cast);
                    result.value = cast;
                }
            }
        } else {
            if (isGlobal) {
                List<Constant> elements = new ArrayList<>();
                int zeroCount = 0;
                for (ExpNode exp : node.getExps()) {
                    elements.add(ConstantInt.get(((IRType.IntegerIRType) elementType).getBitWidth(),
                            exp.getConstValue()));
                    if (exp.getConstValue() == 0) { zeroCount++; }
                }
                if (zeroCount == elements.size()) {
                    result.value = null;
                } else {
                    result.value = ConstantArray.get(elementType, size, elements);
                }
            } else {
                for (ExpNode exp : node.getExps()) {
                    Result expResult = visitExp(exp);
                    if (!expResult.value.getType().equals(elementType)) {
                        Instruction cast;
                        if (elementType.equals(IRType.IntegerIRType.get(8))) {
                            cast = new UnaryInstruction(
                                    UnaryInstruction.Operator.Trunc,
                                    expResult.value, elementType
                            );
                        } else {
                            cast = new UnaryInstruction(
                                    UnaryInstruction.Operator.ZExt,
                                    expResult.value, elementType
                            );
                        }
                        curBasicBlock.addInstruction(cast);
                        result.values.add(cast);
                    } else {
                        result.values.add(expResult.value);
                    }
                }
            }
        }
        return result;
    }

    private void visitFuncDef(FuncDefNode node) {
        FuncSymbol func = (FuncSymbol) curTable.getSymbolByLine(node.getIdent().content(), node.getIdent().lineno());
        IRType retType = IRType.convert(func.getFuncType().getTypeName(), func.getFuncType().getDims());
        ArrayList<IRType> paramTypes = new ArrayList<>();
        for (Type type : func.getParamTypeList()) {
            paramTypes.add(IRType.convert(type.getTypeName(), type.getDims()));
        }
        IRType funcType = IRType.FunctionIRType.get(retType, paramTypes);
        curFunction = new Function(funcType, node.getIdent().content());
        func.setValue(curFunction);
        curBasicBlock = curFunction.getEntryBlock();
        irModule.addFunction(curFunction);
        move2NextTable();

        if (node.getFuncFParams() != null) {
            visitFuncFParams(node.getFuncFParams());
        }
        visitBlock(node.getBlock());

        if (!curFunction.hasReturn()) {
            Instruction ret = new ReturnInstruction();
            curBasicBlock.addInstruction(ret);
        }
        curFunction = null;
        curBasicBlock = null;
        move2NextTable();
    }

    private void visitFuncFParams(FuncFParamsNode node) {
        for (int i = 0; i < node.getFuncFParams().size(); i++) {
            visitFuncFParam(node.getFuncFParams().get(i), i);
        }
    }

    private void visitFuncFParam(FuncFParamNode node, int num) {
        VarSymbol var = (VarSymbol) curTable.getSymbolByLine(node.getIdent().content(), node.getIdent().lineno());
        Parameter param = curFunction.getParameters().get(num);

        Instruction alloc = new AllocaInstruction(param.getType());
        curBasicBlock.insertInstruction(alloc, curBasicBlock.getAllocNum());

        Instruction store = new StoreInstruction(param, alloc);
        curBasicBlock.addInstruction(store);
        var.setValue(alloc);
    }

    private void visitBlock(BlockNode node) {
        for (BlockItemNode blockItem : node.getBlockItems()) {
            visitBlockItem(blockItem);
        }
    }

    private void visitBlockItem(BlockItemNode node) {
        if (node.getStmt() != null) {
            visitStmt(node.getStmt());
        } else {
            visitDecl(node.getDecl());
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
        Result lVal = visitLVal(node.getLVal(), true);
        Result rVal = visitExp(node.getExp());

        IRType varType = ((IRType.PointerIRType) lVal.value.getType()).getElementType();

        if (!rVal.value.getType().equals(varType)) {
            Instruction cast;
            if (varType.equals(IRType.IntegerIRType.get(32))) {
                cast = new UnaryInstruction(
                    UnaryInstruction.Operator.ZExt,
                    rVal.value,
                    IRType.IntegerIRType.get(32));
            } else {
                cast = new UnaryInstruction(
                    UnaryInstruction.Operator.Trunc,
                    rVal.value,
                    IRType.IntegerIRType.get(8));
            }
            curBasicBlock.addInstruction(cast);
            rVal.value = cast;
        }
        Instruction store = new StoreInstruction(rVal.value, lVal.value);
        curBasicBlock.addInstruction(store);
    }

    private void visitExpStmt(ExpStmtNode node) {
        if (node.getExp() != null) {
            visitExp(node.getExp());
        }
    }

    private void visitBlockStmt(BlockStmtNode node) {
        move2NextTable();
        visitBlock(node.getBlock());
        move2NextTable();
    }

    private void visitIfStmt(IfStmtNode node) {
        // tbd
    }

    private void visitForLoopStmt(ForLoopStmtNode node) {
        // tbd
    }

    private void visitForStmt(ForStmtNode node) {
        // tbd
    }

    private void visitBreakStmt(BreakStmtNode node) {
        // tbd
    }

    private void visitReturnStmt(ReturnStmtNode node) {
        if (node.getExp() == null) {
            Instruction ret = new ReturnInstruction();
            curBasicBlock.addInstruction(ret);
        } else {
            Result retValue = visitExp(node.getExp());
            IRType retType = ((IRType.FunctionIRType) curFunction.getType()).getReturnType();
            if (!retValue.value.getType().equals(retType)) {
                Instruction cast;
                if (retType.equals(IRType.IntegerIRType.get(32))) {
                    cast = new UnaryInstruction(
                        UnaryInstruction.Operator.ZExt,
                        retValue.value,
                        IRType.IntegerIRType.get(32));
                } else {
                    cast = new UnaryInstruction(
                        UnaryInstruction.Operator.Trunc,
                        retValue.value,
                        IRType.IntegerIRType.get(8));
                }
                curBasicBlock.addInstruction(cast);
                retValue.value = cast;
            }
            Instruction ret = new ReturnInstruction(retValue.value);
            curBasicBlock.addInstruction(ret);
        }
        curFunction.setHasReturn(true);
    }

    private void visitGetIntStmt(GetIntStmtNode node) {
        Result lVal = visitLVal(node.getLVal(), true);
        Instruction call = new CallInstruction(Function.GETINT, new ArrayList<>());
        curBasicBlock.addInstruction(call);

        Instruction store = new StoreInstruction(call, lVal.value);
        curBasicBlock.addInstruction(store);
    }

    private void visitGetCharStmt(GetCharStmtNode node) {
        Result lVal = visitLVal(node.getLVal(), true);
        Instruction call = new CallInstruction(Function.GETCHAR, new ArrayList<>());
        curBasicBlock.addInstruction(call);

        Instruction trunc = new UnaryInstruction(
            UnaryInstruction.Operator.Trunc,
            call, IRType.IntegerIRType.get(8));
        curBasicBlock.addInstruction(trunc);
        Instruction store = new StoreInstruction(trunc, lVal.value);
        curBasicBlock.addInstruction(store);
    }

    private void visitPrintfStmt(PrintfStmtNode node) {
        List<ExpNode> params = node.getExps();
        String format = node.getStringConst();

        String[] parts = format.split("%d|%c");
        Pattern pattern = Pattern.compile("%[d|c]");
        Matcher matcher = pattern.matcher(format);
        int paramIndex = 0;

        for (String part : parts) {
            if (!part.isEmpty()) {
                GlobalValue strCon = GlobalValue.createConstantString(part, -1);
                Instruction gep = new GetElementPtrInstruction(strCon, List.of(ConstantInt.get(32, 0), ConstantInt.get(32, 0)));
                curBasicBlock.addInstruction(gep);
                Instruction call = new CallInstruction(Function.PUTSTR, List.of(gep));
                curBasicBlock.addInstruction(call);
            }
            if (matcher.find()) {
                String match = matcher.group();
                Result param = visitExp(params.get(paramIndex++));

                if (param.value.getType().equals(IRType.IntegerIRType.get(8))) {
                    Instruction zext = new UnaryInstruction(
                            UnaryInstruction.Operator.ZExt, param.value, IRType.IntegerIRType.get(32));
                    curBasicBlock.addInstruction(zext);
                    param.value = zext;
                }
                if (match.equals("%d")) {
                    curBasicBlock.addInstruction(new CallInstruction(Function.PUTINT, List.of(param.value)));
                } else {
                    curBasicBlock.addInstruction(new CallInstruction(Function.PUTCH, List.of(param.value)));
                }
            }
        }

    }

    private Result visitLVal(LValNode node, boolean isLeft) {
        Result result = new Result();
        String ident = node.getIdent().content();
        VarSymbol var = (VarSymbol) curTable.getSymbolByLine(ident, node.getIdent().lineno());
        Value baseAddr = var.getValue();

        if (!node.getExps().isEmpty()) {
            if (baseAddr.getType().isPointerTy() &&
                    ((IRType.PointerIRType) baseAddr.getType()).getElementType().isPointerTy()) {
                Instruction load = new LoadInstruction(baseAddr);
                curBasicBlock.addInstruction(load);
                baseAddr = load;
                
                List<Value> indices = new ArrayList<>();
                for (ExpNode exp : node.getExps()) {
                    Result index = visitExp(exp);
                    indices.add(index.value);
                }
                Instruction gep = new GetElementPtrInstruction(baseAddr, indices);
                curBasicBlock.addInstruction(gep);
                baseAddr = gep;
            } else {
                List<Value> indices = new ArrayList<>();
                indices.add(ConstantInt.get(32,0));
                for (ExpNode exp : node.getExps()) {
                    Result index = visitExp(exp);
                    indices.add(index.value);
                }
                Instruction gep = new GetElementPtrInstruction(baseAddr, indices);
                curBasicBlock.addInstruction(gep);
                baseAddr = gep;
            }
        } else if (var.getVarType().getDimSize() > 0) {
            if (!isLeft) {
                List<Value> indices = List.of(ConstantInt.get(32, 0), ConstantInt.get(32,0));
                Instruction gep = new GetElementPtrInstruction(baseAddr, indices);
                curBasicBlock.addInstruction(gep);
                result.value = gep;
                return result;
            }
        }

        if (!isLeft) {
            Instruction load = new LoadInstruction(baseAddr);
            curBasicBlock.addInstruction(load);
            result.value = load;
        } else {
            result.value = baseAddr;
        }
        return result;
    }

    private void visitCond(CondNode node) {

    }

    private void visitLOrExp(LOrExpNode node) {

    }

    private void visitLAndExp(LAndExpNode node) {

    }

    private void visitEqExp(EqExpNode node) {

    }

    private void visitRelExp(RelExpNode node) {

    }

    private Result visitExp(ExpNode node) {
        Result result = new Result();
        if (node.isConst()) {
            result.value =  ConstantInt.get(32, node.getConstValue());
            result.isConst = true;
        } else {
            result = visitAddExp(node.getAddExp());
        }
        return result;
    }

    private Result visitConstExp(ConstExpNode node) {
        Result result = new Result();
        result.value = ConstantInt.get(32, node.getConstValue());
        return result;
    }

    private Result visitAddExp(AddExpNode node) {
        Result result = new Result();
        if (node.isConst()) {
            result.value = ConstantInt.get(32, node.getConstValue());
            result.isConst = true;
            return  result;
        }
        if (node.getOperator() == null) {
            return visitMulExp(node.getMulExp());
        }

        Result left = visitAddExp(node.getAddExp());
        Result right = visitMulExp(node.getMulExp());
        if (!left.value.getType().equals(right.value.getType())) {
            if (left.value.getType().equals(IRType.IntegerIRType.get(8))) {
                Instruction zext = new UnaryInstruction(
                        UnaryInstruction.Operator.ZExt,
                        left.value, IRType.IntegerIRType.get(32)
                );
                curBasicBlock.addInstruction(zext);
                left.value = zext;
            } else {
                Instruction zext = new UnaryInstruction(
                        UnaryInstruction.Operator.ZExt,
                        right.value, IRType.IntegerIRType.get(32)
                );
                curBasicBlock.addInstruction(zext);
                right.value = zext;
            }
        }
        BinaryInstruction.Operator op = BinaryInstruction.Operator.Add;
        if (node.getOperator().type() == TokenType.MINU) {
            op = BinaryInstruction.Operator.Sub;
        }
        Instruction binary = new BinaryInstruction(op, left.value, right.value);
        curBasicBlock.addInstruction(binary);
        result.value = binary;
        return result;
    }

    private Result visitMulExp(MulExpNode node) {
        Result result = new Result();
        if (node.isConst()) {
            result.value = ConstantInt.get(32, node.getConstValue());
            result.isConst = true;
            return  result;
        }
        if (node.getOperator() == null) {
            return visitUnaryExp(node.getUnaryExp());
        }

        Result left = visitMulExp(node.getMulExp());
        Result right = visitUnaryExp(node.getUnaryExp());
        if (!left.value.getType().equals(right.value.getType())) {
            if (left.value.getType().equals(IRType.IntegerIRType.get(8))) {
                Instruction zext = new UnaryInstruction(
                        UnaryInstruction.Operator.ZExt,
                        left.value, IRType.IntegerIRType.get(32)
                );
                curBasicBlock.addInstruction(zext);
                left.value = zext;
            } else {
                Instruction zext = new UnaryInstruction(
                        UnaryInstruction.Operator.ZExt,
                        right.value, IRType.IntegerIRType.get(32)
                );
                curBasicBlock.addInstruction(zext);
                right.value = zext;
            }
        }
        BinaryInstruction.Operator op = BinaryInstruction.Operator.Mul;
        switch (node.getOperator().type()) {
            case DIV -> op = BinaryInstruction.Operator.SDiv;
            case MOD -> op = BinaryInstruction.Operator.SRem;
            default -> {}
        }
        Instruction binary = new BinaryInstruction(op, left.value, right.value);
        curBasicBlock.addInstruction(binary);
        result.value = binary;
        return result;
    }

    private Result visitUnaryExp(UnaryExpNode node) {
        Result result = new Result();
        if (node.isConst()) {
            result.value = ConstantInt.get(32, node.getConstValue());
            result.isConst = true;
            return  result;
        }
        if (node.getPrimaryExp() != null) {
            result = visitPrimaryExp(node.getPrimaryExp());
        } else if (node.getIdent() != null) {
            String ident = node.getIdent().content();
            FuncSymbol func = (FuncSymbol) curTable.getSymbolByLine(ident, node.getIdent().lineno());
            Function function = (Function) func.getValue();
            List<IRType> paramTypes = ((IRType.FunctionIRType) function.getType()).getParamTypes();

            List<Value> args = new ArrayList<>();
            if (node.getFuncRParams() != null) {
                Result rParams = visitFuncRParams(node.getFuncRParams());
                for (int i = 0; i < rParams.values.size(); i++) {
                    Value arg = rParams.values.get(i);
                    IRType paramType = paramTypes.get(i);
                    if (!arg.getType().equals(paramType)) {
                        Instruction cast;
                        if (arg.getType().equals(IRType.IntegerIRType.get(8))) {
                            cast = new UnaryInstruction(
                                UnaryInstruction.Operator.ZExt,
                                arg,
                                IRType.IntegerIRType.get(32)
                            );
                        } else {
                            cast = new UnaryInstruction(
                                UnaryInstruction.Operator.Trunc,
                                arg,
                                IRType.IntegerIRType.get(8)
                            );
                        }
                        curBasicBlock.addInstruction(cast);
                        args.add(cast);
                    } else {
                        args.add(arg);
                    }
                }
                // args.addAll(rParams.values);
            }
            Instruction call = new CallInstruction(function, args);
            curBasicBlock.addInstruction(call);
            result.value = call;
        } else {
            Result operand = visitUnaryExp(node.getUnaryExp());
            switch (node.getUnaryOp().getOperator().type()) {
                case PLUS -> { result.value = operand.value; }
                case MINU -> {
                    Value zero = operand.value.getType().equals(IRType.IntegerIRType.get(32))
                            ? ConstantInt.get(32, 0)
                            : ConstantInt.get(8, 0);
                    Instruction sub = new BinaryInstruction(
                        BinaryInstruction.Operator.Sub,
                        zero, operand.value);
                    curBasicBlock.addInstruction(sub);
                    result.value = sub;
                }
                case NOT -> {
                    // tbd
                }
                default -> {}
            }
        }
        return result;
    }

    private Result visitFuncRParams(FuncRParamsNode node) {
        Result result = new Result();
        for (ExpNode exp : node.getExps()) {
            result.values.add(visitExp(exp).value);
        }
        return result;
    }

    private Result visitPrimaryExp(PrimaryExpNode node) {
        if (node.getCharacter() != null) {
            return visitCharacter(node.getCharacter());
        } else if (node.getNumber() != null) {
            return visitNumber(node.getNumber());
        } else if (node.getExp() != null) {
            return visitExp(node.getExp());
        } else {
            return visitLVal(node.getLVal(), false);
        }
    }

    private Result visitNumber(NumberNode node) {
        Result result = new Result();
        result.isConst = true;
        result.value = ConstantInt.get(32, Integer.parseInt(node.getNumber().content()));
        return result;
    }

    private Result visitCharacter(CharacterNode node) {
        Result result = new Result();
        result.isConst = true;
        result.value = ConstantInt.get(8, node.getCharacter());
        return result;
    }
}