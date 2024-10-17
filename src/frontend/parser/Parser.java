package frontend.parser;

import enums.ErrorType;
import enums.SyntaxCompType;
import enums.TokenType;
import frontend.lexer.TokenStream;
import frontend.lexer.Token;
import frontend.parser.node.CompUnitNode;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;
import frontend.parser.node.declaration.*;
import frontend.parser.node.expression.*;
import frontend.parser.node.function.*;
import frontend.parser.node.statement.*;
import utils.Recorder;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final TokenStream tokenStream;
    private Token curToken;
    private Node compUnitNode;

    public Parser(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
        this.curToken = null;
        this.compUnitNode = null;
    }

    public Node getCompUnit() {
        return compUnitNode;
    }

    public void parse() {
        if (compUnitNode == null) { compUnitNode = parseCompUnit(); }
    }

    private void read() {
        curToken = tokenStream.read();
    }

    private void unread() {
        tokenStream.unread();
    }

    private void unread(int n) {
        tokenStream.unread(n);
    }

    /**
     * CompUnit -> {Decl} {FuncDef} MainFuncDef
     */
    private Node parseCompUnit() {
        List<Node> children = new ArrayList<>();
        while (true) {
            Node declNode = parseDecl();
            if (declNode.getType() == SyntaxCompType.FAIL) {
                unread(declNode.getSize());
                break;
            } else { children.add(declNode); }
        }

        while (true) {
            Node funcDefNode = parseFuncDef();
            if (funcDefNode.getType() == SyntaxCompType.FAIL) {
                unread(funcDefNode.getSize());
                break;
            } else { children.add(funcDefNode); }
        }

        Node mainFuncDefNode = parseMainFuncDef();
        if (mainFuncDefNode.getType() == SyntaxCompType.FAIL) {
            unread(mainFuncDefNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(mainFuncDefNode); }
        return new CompUnitNode(SyntaxCompType.CompUnit, children);
    }

    /**
     * Decl -> ConstDecl | VarDecl
     */
    private Node parseDecl() {
        List<Node> children = new ArrayList<>();
        Node constDeclNode = parseConstDecl();
        if (constDeclNode.getType() == SyntaxCompType.FAIL) {
            unread(constDeclNode.getSize());
        } else {
            children.add(constDeclNode);
            return new DeclNode(SyntaxCompType.Decl, children);
        }

        Node varDeclNode = parseVarDecl();
        if (varDeclNode.getType() == SyntaxCompType.FAIL) {
            unread(varDeclNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(varDeclNode); }
        return new DeclNode(SyntaxCompType.Decl, children);
    }

    /**
     * ConstDecl -> 'const' BType ConstDef { ',' ConstDef } ';'
     * <p>
     * error -> i
     */
    private Node parseConstDecl() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.CONSTTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node bTypeNode = parseBType();
        if (bTypeNode.getType() == SyntaxCompType.FAIL) {
            unread(bTypeNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(bTypeNode); }

        Node constDefNode = parseConstDef();
        if (constDefNode.getType() == SyntaxCompType.FAIL) {
            unread(constDefNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(constDefNode); }
        while (true) {
            read();
            if (curToken.type() != TokenType.COMMA) {
                unread();
                break;
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
            constDefNode = parseConstDef();
            if (constDefNode.getType() == SyntaxCompType.FAIL) {
                unread(constDefNode.getSize());
                break;
            } else { children.add(constDefNode); }
        }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingSEMICN, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.SEMICN, ";", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new ConstDeclNode(SyntaxCompType.ConstDecl, children);
    }

    /**
     * BType -> 'int' | 'char'
     */
    private Node parseBType() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.CHARTK &&
            curToken.type() != TokenType.INTTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new BTypeNode(SyntaxCompType.BType, children);
    }

    /**
     * ConstDef -> Ident [ '[' ConstExp ']' ] '=' ConstInitVal
     * <p>
     * error -> k
     */
    private Node parseConstDef() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.IDENFR) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        
        read();
        if (curToken.type() == TokenType.LBRACK) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
            Node constExpNode = parseConstExp();
            if (constExpNode.getType() == SyntaxCompType.FAIL) {
                unread(constExpNode.getSize());
                return new Node(SyntaxCompType.FAIL, children);
            } else { children.add(constExpNode); }

            int lineno = curToken.lineno();
            read();
            if (curToken.type() != TokenType.RBRACK) {
                unread();
                Recorder.addErrorMessage(ErrorType.missingRBRACK, lineno);
                children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RBRACK, "]", lineno)));
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        } else { unread(); }

        read();
        if (curToken.type() != TokenType.ASSIGN) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node constInitValNode = parseConstInitVal();
        if (constInitValNode.getType() == SyntaxCompType.FAIL) {
            unread(constInitValNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(constInitValNode); }

        return new ConstDefNode(SyntaxCompType.ConstDef, children);
    }

    /**
     * ConstInitVal -> ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
     */
    private Node parseConstInitVal() {
        List<Node> children = new ArrayList<>();
        Node constExpNode = parseConstExp();
        if (constExpNode.getType() == SyntaxCompType.FAIL) {
            unread(constExpNode.getSize());
        } else {
            children.add(constExpNode);
            return new ConstInitValNode(SyntaxCompType.ConstInitVal, children);
        }

        read();
        if (curToken.type() == TokenType.STRCON) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
            return new ConstInitValNode(SyntaxCompType.ConstInitVal, children);
        } else if (curToken.type() == TokenType.LBRACE) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
        } else {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        }

        constExpNode = parseConstExp();
        if (constExpNode.getType() == SyntaxCompType.FAIL) {
            unread(constExpNode.getSize());
            read();
            if (curToken.type() != TokenType.RBRACE) {
                unread();
                return new Node(SyntaxCompType.FAIL, children);
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
            return new ConstInitValNode(SyntaxCompType.ConstInitVal, children);
        } else { children.add(constExpNode); }
        while (true) {
            read();
            if (curToken.type() != TokenType.COMMA) {
                unread();
                break;
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
            constExpNode = parseConstExp();
            if (constExpNode.getType() == SyntaxCompType.FAIL) {
                unread(constExpNode.getSize());
                break;
            } else { children.add(constExpNode); }
        }
        
        read();
        if (curToken.type() != TokenType.RBRACE) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new ConstInitValNode(SyntaxCompType.ConstInitVal, children);
    }

    /**
     * VarDecl -> BType VarDef { ',' VarDef } ';'
     * <p>
     * error -> i
     */
    private Node parseVarDecl() {
        List<Node> children = new ArrayList<>();
        Node bTypeNode = parseBType();
        if (bTypeNode.getType() == SyntaxCompType.FAIL) {
            unread(bTypeNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(bTypeNode); }

        Node varDefNode = parseVarDef();
        if (varDefNode.getType() == SyntaxCompType.FAIL) {
            unread(varDefNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(varDefNode); }
        while (true) {
            read();
            if (curToken.type() != TokenType.COMMA) {
                unread();
                break;
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
            varDefNode = parseVarDef();
            if (varDefNode.getType() == SyntaxCompType.FAIL) {
                unread(varDefNode.getSize());
                break;
            } else { children.add(varDefNode); }
        }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingSEMICN, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.SEMICN, ";", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new VarDeclNode(SyntaxCompType.VarDecl, children);
    }

    /**
     * VarDef -> Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
     * <p>
     * error -> k
     */
    private Node parseVarDef() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.IDENFR) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        read();
        if (curToken.type() == TokenType.LBRACK) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
            Node constExpNode = parseConstExp();
            if (constExpNode.getType() == SyntaxCompType.FAIL) {
                unread(constExpNode.getSize());
                return new Node(SyntaxCompType.FAIL, children);
            } else { children.add(constExpNode); }

            int lineno = curToken.lineno();
            read();
            if (curToken.type() != TokenType.RBRACK) {
                unread();
                Recorder.addErrorMessage(ErrorType.missingRBRACK, lineno);
                children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RBRACK, "]", lineno)));
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        } else { unread(); }

        read();
        if (curToken.type() == TokenType.ASSIGN) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
            Node initValNode = parseInitVal();
            if (initValNode.getType() == SyntaxCompType.FAIL) {
                unread(initValNode.getSize());
                return new Node(SyntaxCompType.FAIL, children);
            } else { children.add(initValNode); }
        } else { unread(); }
        return new VarDefNode(SyntaxCompType.VarDef, children);
    }

    /**
     * InitVal -> Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
     */
    private Node parseInitVal() {
        List<Node> children = new ArrayList<>();
        Node expNode = parseExp();
        if (expNode.getType() == SyntaxCompType.FAIL) {
            unread(expNode.getSize());
        } else {
            children.add(expNode);
            return new Node(SyntaxCompType.InitVal, children);
        }

        read();
        if (curToken.type() == TokenType.STRCON) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
            return new Node(SyntaxCompType.InitVal, children);
        } else if (curToken.type() == TokenType.LBRACE) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
        } else {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        }

        expNode = parseExp();
        if (expNode.getType() == SyntaxCompType.FAIL) {
            unread(expNode.getSize());
            read();
            if (curToken.type() != TokenType.RBRACE) {
                unread();
                return new Node(SyntaxCompType.FAIL, children);
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
            return new Node(SyntaxCompType.InitVal, children);
        } else { children.add(expNode); }
        while (true) {
            read();
            if (curToken.type() != TokenType.COMMA) {
                unread();
                break;
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
            expNode = parseExp();
            if (expNode.getType() == SyntaxCompType.FAIL) {
                unread(expNode.getSize());
                break;
            } else { children.add(expNode); }
        }

        read();
        if (curToken.type() != TokenType.RBRACE) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new InitValNode(SyntaxCompType.InitVal, children);
    }

    /**
     * FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
     * <p>
     * error -> j
     */
    private Node parseFuncDef() {
        List<Node> children = new ArrayList<>();
        Node funcTypeNode = parseFuncType();
        if (funcTypeNode.getType() == SyntaxCompType.FAIL) {
            unread(funcTypeNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(funcTypeNode); }

        read();
        if (curToken.type() != TokenType.IDENFR) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        read();
        if (curToken.type() != TokenType.LPARENT) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node funcFParamsNode = parseFuncFParams();
        if (funcFParamsNode.getType() == SyntaxCompType.FAIL) {
            unread(funcFParamsNode.getSize());
        } else { children.add(funcFParamsNode); }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.RPARENT) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingRPARENT, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RPARENT, ")", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node blockNode = parseBlock();
        if (blockNode.getType() == SyntaxCompType.FAIL) {
            unread(blockNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(blockNode); }
        return new FuncDefNode(SyntaxCompType.FuncDef, children);
    }

    /**
     * MainFuncDef -> 'int' 'main' '(' ')' Block
     * <p>
     * error -> j
     */
    private Node parseMainFuncDef() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.INTTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        read();
        if (curToken.type() != TokenType.MAINTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        read();
        if (curToken.type() != TokenType.LPARENT) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.RPARENT) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingRPARENT, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RPARENT, ")", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node blockNode = parseBlock();
        if (blockNode.getType() == SyntaxCompType.FAIL) {
            unread(blockNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(blockNode); }
        return new MainFuncDefNode(SyntaxCompType.MainFuncDef, children);
    }

    /**
     * FuncType -> 'void' | 'int' | 'char'
     */
    private Node parseFuncType() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.VOIDTK &&
            curToken.type() != TokenType.INTTK &&
            curToken.type() != TokenType.CHARTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new FuncTypeNode(SyntaxCompType.FuncType, children);
    }

    /**
     * FuncFParams -> FuncFParam { ',' FuncFParam }
     */
    private Node parseFuncFParams() {
        List<Node> children = new ArrayList<>();
        Node funcFParamNode = parseFuncFParam();
        if (funcFParamNode.getType() == SyntaxCompType.FAIL) {
            unread(funcFParamNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(funcFParamNode); }

        while (true) {
            read();
            if (curToken.type() != TokenType.COMMA) {
                unread();
                break;
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
            funcFParamNode = parseFuncFParam();
            if (funcFParamNode.getType() == SyntaxCompType.FAIL) {
                unread(funcFParamNode.getSize());
                break;
            } else { children.add(funcFParamNode); }
        }
        return new FuncFParamsNode(SyntaxCompType.FuncFParams, children);
    }

    /**
     * FuncFParam -> BType Ident ['[' ']']
     * <p>
     * error -> k
     */
    private Node parseFuncFParam() {
        List<Node> children = new ArrayList<>();
        Node bTypeNode = parseBType();
        if (bTypeNode.getType() == SyntaxCompType.FAIL) {
            unread(bTypeNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(bTypeNode); }

        read();
        if (curToken.type() == TokenType.LBRACK) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
            int lineno = curToken.lineno();
            read();
            if (curToken.type() != TokenType.RBRACK) {
                unread();
                Recorder.addErrorMessage(ErrorType.missingRBRACK, lineno);
                children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RBRACK, "]", lineno)));
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        } else { unread(); }
        return new FuncFParamNode(SyntaxCompType.FuncFParams, children);
    }

    /**
     * Block -> '{' { BlockItem } '}'
     */
    private Node parseBlock() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.LBRACE) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        while (true) {
            Node blockItemNode = parseBlockItem();
            if (blockItemNode.getType() == SyntaxCompType.FAIL) {
                unread(blockItemNode.getSize());
                break;
            } else { children.add(blockItemNode); }
        }

        read();
        if (curToken.type() != TokenType.RBRACE) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new BlockNode(SyntaxCompType.Block, children);
    }

    /**
     * BlockItem -> Decl | Stmt
     */
    private Node parseBlockItem() {
        List<Node> children = new ArrayList<>();
        Node declNode = parseDecl();
        if (declNode.getType() == SyntaxCompType.FAIL) {
            unread(declNode.getSize());
        } else {
            children.add(declNode);
            return new BlockItemNode(SyntaxCompType.BlockItem, children);
        }

        Node stmtNode = parseStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(stmtNode); }
        return new BlockItemNode(SyntaxCompType.BlockItem, children);
    }

    /**
     * Stmt → AssignStmt | ExpStmt | BlockStmt | IfStmt | ForLoopStmt | BreakStmt
     * | ReturnStmt | GetIntStmt | GetCharStmt | PrintfStmt
     */
    private Node parseStmt() {
        Node stmtNode = parseAssignStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
        } else { return stmtNode; }

        stmtNode = parseExpStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
        } else { return stmtNode; }

        stmtNode = parseBlockStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
        } else { return stmtNode; }

        stmtNode = parseIfStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
        } else { return stmtNode; }

        stmtNode = parseForLoopStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
        } else { return stmtNode; }

        stmtNode = parseBreakStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
        } else { return stmtNode; }

        stmtNode = parseReturnStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
        } else { return stmtNode; }

        stmtNode = parseGetIntStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
        } else { return stmtNode; }

        stmtNode = parseGetCharStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
        } else { return stmtNode; }

        stmtNode = parsePrintfStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
            return new Node(SyntaxCompType.FAIL, new ArrayList<>());
        } else { return stmtNode; }
    }

    /**
     * AssignStmt -> LVal '=' Exp ';'
     * <p>
     * error -> i
     */
    private Node parseAssignStmt() {
        List<Node> children = new ArrayList<>();
        Node lValNode = parseLVal();
        if (lValNode.getType() == SyntaxCompType.FAIL) {
            unread(lValNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(lValNode); }

        read();
        if (curToken.type() != TokenType.ASSIGN) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node expNode = parseExp();
        if (expNode.getType() == SyntaxCompType.FAIL) {
            unread(expNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(expNode); }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingSEMICN, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.SEMICN, ";", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new AssignStmtNode(SyntaxCompType.AssignStmt, children);
    }

    /**
     * ExpStmt -> [Exp] ';'
     * <p>
     * error -> i
     */
    private Node parseExpStmt() {
        List<Node> children = new ArrayList<>();
        Node expNode = parseExp();
        if (expNode.getType() == SyntaxCompType.FAIL) {
            unread(expNode.getSize());
        } else { children.add(expNode); }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            if (!children.isEmpty()) {
                Recorder.addErrorMessage(ErrorType.missingSEMICN, lineno);
                children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.SEMICN, ";", lineno)));
            } else {
                return new Node(SyntaxCompType.FAIL, new ArrayList<>());
            }
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new ExpStmtNode(SyntaxCompType.ExpStmt, children);
    }

    /**
     * BlockStmt -> Block
     */
    private Node parseBlockStmt() {
        List<Node> children = new ArrayList<>();
        Node blockNode = parseBlock();
        if (blockNode.getType() == SyntaxCompType.FAIL) {
            unread(blockNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(blockNode); }
        return new BlockStmtNode(SyntaxCompType.BlockStmt, children);
    }

    /**
     * IfStmt -> 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
     * <p>
     * error -> j
     */
    private Node parseIfStmt() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.IFTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        read();
        if (curToken.type() != TokenType.LPARENT) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        Node condNode = parseCond();
        if (condNode.getType() == SyntaxCompType.FAIL) {
            unread(condNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(condNode); }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.RPARENT) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingRPARENT, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RPARENT, ")", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node stmtNode = parseStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(stmtNode); }

        read();
        if (curToken.type() == TokenType.ELSETK) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
            stmtNode = parseStmt();
            if (stmtNode.getType() == SyntaxCompType.FAIL) {
                unread(stmtNode.getSize());
                return new Node(SyntaxCompType.FAIL, children);
            } else { children.add(stmtNode); }
        } else { unread(); }
        return new IfStmtNode(SyntaxCompType.IfStmt, children);
    }

    /**
     * ForLoopStmt -> 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
     * <p>
     * error -> j
     */
    private Node parseForLoopStmt() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.FORTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        read();
        if (curToken.type() != TokenType.LPARENT) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node forStmtNode = parseForStmt();
        if (forStmtNode.getType() == SyntaxCompType.FAIL) {
            unread(forStmtNode.getSize());
        } else { children.add(forStmtNode); }
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        Node condStmt = parseCond();
        if (condStmt.getType() == SyntaxCompType.FAIL) {
            unread(condStmt.getSize());
        } else { children.add(condStmt); }
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        forStmtNode = parseForStmt();
        if (forStmtNode.getType() == SyntaxCompType.FAIL) {
            unread(forStmtNode.getSize());
        } else { children.add(forStmtNode); }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.RPARENT) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingRPARENT, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RPARENT, ")", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node stmtNode = parseStmt();
        if (stmtNode.getType() == SyntaxCompType.FAIL) {
            unread(stmtNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(stmtNode); }
        return new ForLoopStmtNode(SyntaxCompType.ForLoopStmt, children);
    }

    /**
     * BreakStmt -> 'break' ';' | 'continue' ';'
     * <p>
     * error -> i
     */
    private Node parseBreakStmt() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.BREAKTK &&
            curToken.type() != TokenType.CONTINUETK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingSEMICN, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.SEMICN, ";", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new BreakStmtNode(SyntaxCompType.BreakStmt, children);
    }

    /**
     * ReturnStmt -> 'return' [Exp] ';'
     * <p>
     * error -> i
     */
    private Node parseReturnStmt() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.RETURNTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node expNode = parseExp();
        if (expNode.getType() == SyntaxCompType.FAIL) {
            unread(expNode.getSize());
        } else { children.add(expNode); }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingSEMICN, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.SEMICN, ";", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new ReturnStmtNode(SyntaxCompType.ReturnStmt, children);
    }

    /**
     * GetIntStmt -> LVal '=' 'getint''('')'';'
     * <p>
     * error -> i j
     */
    private Node parseGetIntStmt() {
        List<Node> children = new ArrayList<>();
        Node lValNode = parseLVal();
        if (lValNode.getType() == SyntaxCompType.FAIL) {
            unread(lValNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(lValNode); }

        read();
        if (curToken.type() != TokenType.ASSIGN) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        read();
        if (curToken.type() != TokenType.GETINTTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        read();
        if (curToken.type() != TokenType.LPARENT) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.RPARENT) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingRPARENT, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RPARENT, ")", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingSEMICN, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.SEMICN, ";", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new GetIntStmtNode(SyntaxCompType.GetIntStmt, children);
    }

    /**
     * GetCharStmt -> LVal '=' 'getchar''('')'';'
     * <p>
     * error -> i j
     */
    private Node parseGetCharStmt() {
        List<Node> children = new ArrayList<>();
        Node lValNode = parseLVal();
        if (lValNode.getType() == SyntaxCompType.FAIL) {
            unread(lValNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(lValNode); }

        read();
        if (curToken.type() != TokenType.ASSIGN) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        read();
        if (curToken.type() != TokenType.GETCHARTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        read();
        if (curToken.type() != TokenType.LPARENT) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.RPARENT) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingRPARENT, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RPARENT, ")", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingSEMICN, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.SEMICN, ";", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new GetCharStmtNode(SyntaxCompType.GetCharStmt, children);
    }

    /**
     * PrintfStmt -> 'printf''('StringConst {','Exp}')'';'
     * <p>
     * error -> i j
     */
    private Node parsePrintfStmt() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.PRINTFTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        read();
        if (curToken.type() != TokenType.LPARENT) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        read();
        if (curToken.type() != TokenType.STRCON) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        while (true) {
            read();
            if (curToken.type() != TokenType.COMMA) {
                unread();
                break;
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
            Node expNode = parseExp();
            if (expNode.getType() == SyntaxCompType.FAIL) {
                unread(expNode.getSize());
                break;
            } else { children.add(expNode); }
        }

        int lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.RPARENT) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingRPARENT, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RPARENT, ")", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        lineno = curToken.lineno();
        read();
        if (curToken.type() != TokenType.SEMICN) {
            unread();
            Recorder.addErrorMessage(ErrorType.missingSEMICN, lineno);
            children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.SEMICN, ";", lineno)));
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new PrintfStmtNode(SyntaxCompType.PrintfStmt, children);
    }

    /**
     * ForStmt -> LVal '=' Exp
     */
    private Node parseForStmt() {
        List<Node> children = new ArrayList<>();
        Node lValNode = parseLVal();
        if (lValNode.getType() == SyntaxCompType.FAIL) {
            unread(lValNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(lValNode); }

        read();
        if (curToken.type() != TokenType.ASSIGN) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        Node expNode = parseExp();
        if (expNode.getType() == SyntaxCompType.FAIL) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(expNode); }
        return new ForStmtNode(SyntaxCompType.ForStmt, children);
    }

    /**
     * Exp -> AddExp
     */
    private Node parseExp() {
        List<Node> children = new ArrayList<>();
        Node addExpNode = parseAddExp();
        if (addExpNode.getType() == SyntaxCompType.FAIL) {
            unread(addExpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(addExpNode); }
        return new ExpNode(SyntaxCompType.Exp, children);
    }

    /**
     * Cond -> LOrExp
     */
    private Node parseCond() {
        List<Node> children = new ArrayList<>();
        Node lOrExpNode = parseLOrExp();
        if (lOrExpNode.getType() == SyntaxCompType.FAIL) {
            unread(lOrExpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(lOrExpNode); }
        return new CondNode(SyntaxCompType.Cond, children);
    }

    /**
     * LVal -> Ident ['[' Exp ']']
     * <p>
     * error -> k
     */
    private Node parseLVal() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.IDENFR) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }

        read();
        if (curToken.type() == TokenType.LBRACK) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
            Node expNode = parseExp();
            if (expNode.getType() == SyntaxCompType.FAIL) {
                unread();
                return new Node(SyntaxCompType.FAIL, children);
            } else { children.add(expNode); }

            int lineno = curToken.lineno();
            read();
            if (curToken.type() != TokenType.RBRACK) {
                unread();
                Recorder.addErrorMessage(ErrorType.missingRBRACK, lineno);
                children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RBRACK, "]", lineno)));
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        } else { unread(); }
        return new LValNode(SyntaxCompType.LVal, children);
    }

    /**
     * PrimaryExp → '(' Exp ')' | LVal | Number | Character
     * <p>
     * error -> j
     */
    private Node parsePrimaryExp() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() == TokenType.LPARENT) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
            Node expNode = parseExp();
            if (expNode.getType() == SyntaxCompType.FAIL) {
                unread(expNode.getSize());
                return new Node(SyntaxCompType.FAIL, children);
            } else { children.add(expNode); }

            int lineno = curToken.lineno();
            read();
            if (curToken.type() != TokenType.RPARENT) {
                unread();
                Recorder.addErrorMessage(ErrorType.missingRPARENT, lineno);
                children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RPARENT, ")", lineno)));
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
            return new PrimaryExpNode(SyntaxCompType.PrimaryExp, children);
        } else { unread(); }

        Node lValNode = parseLVal();
        if (lValNode.getType() == SyntaxCompType.FAIL) {
            unread(lValNode.getSize());
        } else {
            children.add(lValNode);
            return new PrimaryExpNode(SyntaxCompType.PrimaryExp, children);
        }

        Node numberNode = parseNumber();
        if (numberNode.getType() == SyntaxCompType.FAIL) {
            unread(numberNode.getSize());
        } else {
            children.add(numberNode);
            return new PrimaryExpNode(SyntaxCompType.PrimaryExp, children);
        }

        Node characterNode = parseCharacter();
        if (characterNode.getType() == SyntaxCompType.FAIL) {
            unread(characterNode.getSize());
        } else {
            children.add(characterNode);
            return new PrimaryExpNode(SyntaxCompType.PrimaryExp, children);
        }
        return new Node(SyntaxCompType.FAIL, children);
    }

    /**
     * Number -> IntConst
     */
    private Node parseNumber() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.INTCON) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new NumberNode(SyntaxCompType.Number, children);
    }

    /**
     * Character -> CharConst
     */
    private Node parseCharacter() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.CHARTK) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new CharacterNode(SyntaxCompType.Character, children);
    }

    /**
     * UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
     * <p>
     * error -> j
     */
    private Node parseUnaryExp() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() == TokenType.IDENFR) {
            children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
            read();
            if (curToken.type() == TokenType.LPARENT) {
                children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
                Node funcRParamsNode = parseFuncRParams();
                if (funcRParamsNode.getType() == SyntaxCompType.FAIL) {
                    unread(funcRParamsNode.getSize());
                } else { children.add(funcRParamsNode); }

                int lineno = curToken.lineno();
                read();
                if (curToken.type() != TokenType.RPARENT) {
                    unread();
                    Recorder.addErrorMessage(ErrorType.missingRPARENT, lineno);
                    children.add(new TokenNode(SyntaxCompType.TOKEN, new Token(TokenType.RPARENT, ")", lineno)));
                } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
                return new UnaryExpNode(SyntaxCompType.UnaryExp, children);
            } else {
                unread(2);
                children.clear();
            }
        } else { unread(); }

        Node primaryExpNode = parsePrimaryExp();
        if (primaryExpNode.getType() == SyntaxCompType.FAIL) {
            unread(primaryExpNode.getSize());
        } else {
            children.add(primaryExpNode);
            return new UnaryExpNode(SyntaxCompType.UnaryExp, children);
        }

        Node unaryOpNode = parseUnaryOp();
        if (unaryOpNode.getType() == SyntaxCompType.FAIL) {
            unread(unaryOpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(unaryOpNode); }
        Node unaryExpNode = parseUnaryExp();
        if (unaryExpNode.getType() == SyntaxCompType.FAIL) {
            unread(unaryExpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(unaryExpNode); }
        return new UnaryExpNode(SyntaxCompType.UnaryExp, children);
    }

    /**
     * UnaryOp -> '+' | '−' | '!'
     * <p>
     * '!' appears only in conditional expressions
     */
    private Node parseUnaryOp() {
        List<Node> children = new ArrayList<>();
        read();
        if (curToken.type() != TokenType.PLUS &&
            curToken.type() != TokenType.MINU &&
            curToken.type() != TokenType.NOT) {
            unread();
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
        return new UnaryOpNode(SyntaxCompType.UnaryOp, children);
    }

    /**
     * FuncRParams -> Exp { ',' Exp }
     */
    private Node parseFuncRParams() {
        List<Node> children = new ArrayList<>();
        Node expNode = parseExp();
        if (expNode.getType() == SyntaxCompType.FAIL) {
            unread(expNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(expNode); }

        while (true) {
            read();
            if (curToken.type() != TokenType.COMMA) {
                unread();
                break;
            } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
            expNode = parseExp();
            if (expNode.getType() == SyntaxCompType.FAIL) {
                unread(expNode.getSize());
                break;
            } else { children.add(expNode); }
        }
        return new FuncRParamsNode(SyntaxCompType.FuncRParams, children);
    }

    /**
     * MulExp -> UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
     */
    private Node parseMulExp() {
        List<Node> children = new ArrayList<>();
        Node unaryExpNode = parseUnaryExp();
        if (unaryExpNode.getType() == SyntaxCompType.FAIL) {
            unread(unaryExpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(unaryExpNode); }

        while (true) {
            read();
            if (curToken.type() == TokenType.MULT || 
                curToken.type() == TokenType.DIV || 
                curToken.type() == TokenType.MOD) {
                Node mulExpNode = new MulExpNode(SyntaxCompType.MulExp, new ArrayList<>(children));
                children.clear();
                children.add(mulExpNode);
                children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
                unaryExpNode = parseUnaryExp();
                if (unaryExpNode.getType() == SyntaxCompType.FAIL) {
                    unread(unaryExpNode.getSize());
                    return new Node(SyntaxCompType.FAIL, children);
                } else { children.add(unaryExpNode); }
            } else {
                unread();
                break;
            }
        }
        return new MulExpNode(SyntaxCompType.MulExp, children);
    }

    /**
     * AddExp -> MulExp | AddExp ('+' | '−') MulExp
     */
    private Node parseAddExp() {
        List<Node> children = new ArrayList<>();
        Node mulExpNode = parseMulExp();
        if (mulExpNode.getType() == SyntaxCompType.FAIL) {
            unread(mulExpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(mulExpNode); }

        while (true) {
            read();
            if (curToken.type() == TokenType.PLUS || 
                curToken.type() == TokenType.MINU) {
                Node addExpNode = new AddExpNode(SyntaxCompType.AddExp, new ArrayList<>(children));
                children.clear();
                children.add(addExpNode);
                children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
                mulExpNode = parseMulExp();
                if (mulExpNode.getType() == SyntaxCompType.FAIL) {
                    unread(mulExpNode.getSize());
                    return new Node(SyntaxCompType.FAIL, children);
                } else { children.add(mulExpNode); }
            } else {
                unread();
                break;
            }
        }
        return new AddExpNode(SyntaxCompType.AddExp, children);
    }

    /**
     * RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
     */
    private Node parseRelExp() {
        List<Node> children = new ArrayList<>();
        Node addExpNode = parseAddExp();
        if (addExpNode.getType() == SyntaxCompType.FAIL) {
            unread(addExpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(addExpNode); }

        while (true) {
            read();
            if (curToken.type() == TokenType.LSS || 
                curToken.type() == TokenType.LEQ || 
                curToken.type() == TokenType.GRE || 
                curToken.type() == TokenType.GEQ) {
                Node relExpNode = new RelExpNode(SyntaxCompType.RelExp, new ArrayList<>(children));
                children.clear();
                children.add(relExpNode);
                children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
                addExpNode = parseAddExp();
                if (addExpNode.getType() == SyntaxCompType.FAIL) {
                    unread(addExpNode.getSize());
                    return new Node(SyntaxCompType.FAIL, children);
                } else { children.add(addExpNode); }
            } else {
                unread();
                break;
            }
        }
        return new RelExpNode(SyntaxCompType.RelExp, children);
    }

    /**
     * EqExp -> RelExp | EqExp ('==' | '!=') RelExp
     */
    private Node parseEqExp() {
        List<Node> children = new ArrayList<>();
        Node relExpNode = parseRelExp();
        if (relExpNode.getType() == SyntaxCompType.FAIL) {
            unread(relExpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(relExpNode); }

        while (true) {
            read();
            if (curToken.type() == TokenType.EQL || 
                curToken.type() == TokenType.NEQ) {
                Node eqExpNode = new EqExpNode(SyntaxCompType.EqExp, new ArrayList<>(children));
                children.clear();
                children.add(eqExpNode);
                children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
                relExpNode = parseRelExp();
                if (relExpNode.getType() == SyntaxCompType.FAIL) {
                    unread(relExpNode.getSize());
                    return new Node(SyntaxCompType.FAIL, children);
                } else { children.add(relExpNode); }
            } else {
                unread();
                break;
            }
        }
        return new EqExpNode(SyntaxCompType.EqExp, children);
    }

    /**
     * LAndExp -> EqExp | LAndExp '&&' EqExp
     */
    private Node parseLAndExp() {
        List<Node> children = new ArrayList<>();
        Node eqExpNode = parseEqExp();
        if (eqExpNode.getType() == SyntaxCompType.FAIL) {
            unread(eqExpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(eqExpNode); }

        while (true) {
            read();
            if (curToken.type() == TokenType.AND) {
                Node lAndExpNode = new LAndExpNode(SyntaxCompType.LAndExp, new ArrayList<>(children));
                children.clear();
                children.add(lAndExpNode);
                children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
                eqExpNode = parseEqExp();
                if (eqExpNode.getType() == SyntaxCompType.FAIL) {
                    unread(eqExpNode.getSize());
                    return new Node(SyntaxCompType.FAIL, children);
                } else { children.add(eqExpNode); }
            } else {
                unread();
                break;
            }
        }
        return new LAndExpNode(SyntaxCompType.LAndExp, children);
    }

    /**
     * LOrExp -> LAndExp | LOrExp '||' LAndExp
     */
    private Node parseLOrExp() {
        List<Node> children = new ArrayList<>();
        Node lAndExpNode = parseLAndExp();
        if (lAndExpNode.getType() == SyntaxCompType.FAIL) {
            unread(lAndExpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(lAndExpNode); }

        while (true) {
            read();
            if (curToken.type() == TokenType.OR) {
                Node lOrExpNode = new LOrExpNode(SyntaxCompType.LOrExp, new ArrayList<>(children));
                children.clear();
                children.add(lOrExpNode);
                children.add(new TokenNode(SyntaxCompType.TOKEN, curToken));
                lAndExpNode = parseLAndExp();
                if (lAndExpNode.getType() == SyntaxCompType.FAIL) {
                    unread(lAndExpNode.getSize());
                    return new Node(SyntaxCompType.FAIL, children);
                } else { children.add(lAndExpNode); }
            } else {
                unread();
                break;
            }
        }
        return new LOrExpNode(SyntaxCompType.LOrExp, children);
    }

    /**
     * ConstExp -> AddExp
     * <p>
     * The Ident used must be constant
     */
    private Node parseConstExp() {
        List<Node> children = new ArrayList<>();
        Node addExpNode = parseAddExp();
        if (addExpNode.getType() == SyntaxCompType.FAIL) {
            unread(addExpNode.getSize());
            return new Node(SyntaxCompType.FAIL, children);
        } else { children.add(addExpNode); }
        return new ConstExpNode(SyntaxCompType.ConstExp, children);
    }
}
