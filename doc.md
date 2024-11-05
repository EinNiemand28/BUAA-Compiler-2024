# SysY 编译器设计文档

> 本文档是 BUAA 2024 秋季编译原理课程的大作业——SysY 编译器的设计文档，包含了编译器的总体设计、词法分析、语法分析、语义分析、代码生成、代码优化等方面的设计，随着项目的迭代设计不断更新。

## 参考编译器介绍

> 我选择了 PL/0 编译器的源码进行阅读分析，因为其语言简单而结构又较为完整，方便迅速理解编译器的基本原理，从而能对将要实现的 SysY 编译器的架构设计有所帮助。

### PL/0 简介

PL/0 作为 Pascal 语言的子集，语法和语义较为简单，包含以下特点：

- 仅支持无符号整数类型，不支持浮点数、字符（串）等复杂数据类型，且最多 14 位
- 标识符由数字和字母组成，以字母开头，最长 10 位
- 支持过程定义和调用，过程无参、最多嵌套三层
- 变量的作用域在编译时确定，常量作用域为全局
- 有 13 个保留字：
    - `if` / `then` （条件语句），`while` / `do` （循环语句），`begin` / `end` （复合语句）
    - `read` （读语句），`write` （写语句），`call` （过程调用），`odd` （判断是否为奇数）
    - `var` ，`const` ，`procedure` （变量，常量，过程声明）

### 整体结构

PL/0 编译器的源码可大致分为一下几个部分：

- 基本数据结构、常量和各种全局变量的定义
- 一些过程（函数）的定义和实现，包括错误信息的输出、token (symbol) 流的读取、中间代码的生成和相关的测试、对一个块 (block) 进行编译、中间代码的解释执行等
- 编译器主程序 (main block) ，读取源文件，并调用上述的编译过程

### 接口设计

该编译器有以下几个主要接口：

- `program pl0` ：编译器的入口点

- `procedure error`  ：输出错误信息并增加错误计数器，其中错误信息由一个固定前缀、标识错误位置的若干空格 (cc) 、错误类型组成

- `procedure getsym` ：编译器的词法分析器，从源文件中读取下一个符号 (sym) 。将源程序分成单独的行来处理，采用有穷自动机识别各种符号

- `procedure gen` ：根据输入参数生成一条包含操作码、层次、地址的指令，检查代码索引并将指令存储在代码数组中 (code)

- `procedure test` ：检查当前符号是否在指定的符号集中

- `procedure block` ：编译器的核心部分，将指定块的代码转换为中间代码。其中：

    > 该部分参考了 copilot 的解释

    1. 变量声明：
        - `dx` ：数据分配索引
        - `tx0` ：初始表索引
        - `cx0` ：初始代码索引
    2. 内部过程：
        - `enter` ：将符号插入符号表
        - `position` ：查找标识符在符号表中的位置
        - `constdeclaration` ：处理常量声明
        - `vardeclaration` ：处理变量声明
        - `listcode` ：列出生成的代码
        - `statement` ：处理语句
        - `expression` ：处理表达式
        - `term` ：处理项
        - `factor` ：处理因子
        - `condition` ：处理条件
    3. 主要逻辑：
        - 初始化数据分配索引和符号表索引
        - 生成跳转指令，从声明部分跳转到语句部分
        - 处理常量声明、变量声明和过程声明
        - 处理语句
        - 生成返回指令
        - 列出生成的代码

- `procedure interpret` ：解释器，通过实现一个简单的栈式虚拟机，来解释和执行生成的中间代码

### 文件组织

由于 PL/0 语言和编译器使用的 Pascal 语言的特性，编译器的各个功能部分都写在了一个文件中；不过相应的，其各个功能部分也较为分明，也可为之后课程编译器的编写提供不错的示例。

## 编译器总体设计

目前的想法暂且是将要完成的编译器的各个功能部分分开，在主程序中主要提供词法分析、语法分析、语义分析与中间代码生成、代码优化和目标代码生成这几个接口。

实现不同接口和其他辅助功能（比如错误信息的处理、枚举类的使用）的文件要做到分门别类，在实现接口功能时也要注意对功能进行合理的拆解，让各个模块尽量符合“高内聚，低耦合”的思想。

## 词法分析设计

### 总体概述

词法分析的任务是从源程序中识别出每个 `word` 的 `type` 和 `value` ，以及这一编译阶段会查出的错误。所有单词的类别码如下表：

| 单词名称        | 类别码     | 单词名称 | 类别码    | 单词名称 | 类别码 | 单词名称 | 类别码  |
| --------------- | ---------- | -------- | --------- | -------- | ------ | -------- | ------- |
| **Ident**       | IDENFR     | else     | ELSETK    | void     | VOIDTK | ;        | SEMICN  |
| **IntConst**    | INTCON     | !        | NOT       | *        | MULT   | ,        | COMMA   |
| **StringConst** | STRCON     | &&       | AND       | /        | DIV    | (        | LPARENT |
| **CharConst**   | CHRCON     | \|\|     | OR        | %        | MOD    | )        | RPARENT |
| main            | MAINTK     | for      | FORTK     | <        | LSS    | [        | LBRACK  |
| const           | CONSTTK    | getint   | GETINTTK  | <=       | LEQ    | ]        | RBRACK  |
| int             | INTTK      | getchar  | GETCHARTK | >        | GRE    | {        | LBRACE  |
| char            | CHARTK     | printf   | PRINTFTK  | >=       | GEQ    | }        | RBRACE  |
| break           | BREAKTK    | return   | RETURNTK  | ==       | EQL    |          |         |
| continue        | CONTINUETK | +        | PLUS      | !=       | NEQ    |          |         |
| if              | IFTK       | -        | MINU      | =        | ASSIGN |          |         |

在从文件流中读取字符解析单词的过程中，同时还需要注意处理空白与注释、并记录更新行号。

总之，需要提供正确且尽可能丰富的信息，以便后续语法分析等部分使用。

### 架构设计

#### 最基本的类与接口

- `Token` ：token 的记录类
    - attr ：
        - `type(TokenType)` ：token 类型
        - `content(String)` ：token 内容
        - `lineno(int)` ：token 所在行号
- `TokenType` ：token 类型枚举类
- `lexer` ：词法分析器
    - attr ：
        - `stream` ：输入流
        - `curChar(Char)` ：当前字符
        - `lineno(int)` ：当前行号
        - `tokenList(List)` ：已识别的 token 集合
    - `getToken()` ：识别下一个 token
    - `getTokenList()` ：调用 `getToken()` 对 `stream` 进行识别直至结束，并返回所有的 token 集合

#### 识别过程

> 采用有限状态自动机的思想一个一个处理、添加字符

首先，为了贴合自动机处理的整个流程，我将源文件转化成了可回退的输入流 。`PushbackInputStream` （提供 `read` 和 `unread` 接口，分别用于读取和回退字符）

然后，注意到可以先将 token 分为是否有固定 content 两类。若有固定 content ，那么就可以在 `TokenType` 设计一个 Map 类属性来将 content 与 type 进行一一映射，并设计 `getTokenType()` 接口方便地得到这部分 token 的类型。而对于其他四个无固定 content 的 token，我选择将变量 **Ident** 的类型设为 tokenType 的**默认类型**，对其他三种常量则分别判断处理。

```java
// TokenType.java
public enum TokenType {
    IDENFR,
    MAINTK("main"),
    // ...
    private final String lexeme;
    private static final Map<String, TokenType> TOKEN_MAP = new HashMap<>();

    static {
        for (TokenType tokenType : TokenType.values()) {
            if (!tokenType.lexeme.isEmpty()) { TOKEN_MAP.put(tokenType.lexeme, tokenType); }
        }
    }
    
    TokenType(final String lexeme) {
        this.lexeme = lexeme;
    }

    TokenType() {
        this.lexeme = "";
    }
    
    public static TokenType getTokenType(String lexeme) {
        return TOKEN_MAP.getOrDefault(lexeme, TokenType.IDENFR); // 意想不到的可供使用的接口
    }
}
```

进一步细分，保留字的识别可与 **Ident** 放在一起，单个字符操作符可以直接返回 Token ，多个字符的操作符再分别判断处理。

此时抛开空白、换行和注释不谈，可以写出以下代码（以 **Ident** 、 **IntConst** 和一部分操作符的识别为例）

```java
StringBuilder sb = new StringBuilder();
read();
sb.append(curChar);
if (isLetter() || isUnderscore()) { // Ident or Reserved word
    read();
    while (isLetter() || isUnderscore() || isDigit()) {
        sb.append(curChar);
        read();
    }
    unread();
} else if (isDigit()) {
    read();
    while (isDigit()) {
        sb.append(curChar);
        read();
    }
    unread();
    return new Token(TokenType.INTCON, sb.toString(), lineno);
} else if (curChar == '<' || curChar == '>' || curChar == '=' || curChar == '!') {
    read();
    if (curChar == '=') { sb.append('='); }
    else { unread(); }
} else {
    // TODO
}
return new Token(TokenType.getTokenType(sb.toString()), sb.toString(), lineno);
```

经过上述处理，并提出 `isLetter()`  、`read()` 、`skipBlank()` 等辅助方法，`getToken()` 整个方法仅有 60 余行，且整个词法分析器 `Lexer` 类中没有其他的复杂方法。

### 实现细节

#### 辅助方法

- `isLineFeed` ：虽然实验保证了不会涉及换行符在不同操作系统的不一致性，但还是进行了以下的处理~~（看起来符合逻辑很舒服）~~

    ```java
    private boolean isLineFeed() throws IOException {
        if (curChar == '\r') { read(); }
        return curChar == '\n';
    }
    ```

- `skipBlank` ：有着非常重要的作用，可以使 `getToken` 方法专注于 token 识别自动机的运作（不过这个方法也不失为一个自动机）

    ```java
    private void skipBlank() throws IOException {
        if (!isEOF()) { read(); }
        while (isBlank()) {
            if (isLineFeed()) { lineno++; } // 更新行号
            read();
        }
    }
    ```

- `skipComment` ：注释的处理有点麻烦，不仅因为它和空白都是无用的信息、且可以相互组合穿插，还因为注释的**开始符号**是 `/` ，所以如果没处理好，在 `getToken` 中调用处理这两类对象的方法后，下一个进入自动机流程的字符仍可能属于这两类对象、或者跳过了不该跳过的`/` ~~（就寄了）~~。

    因此，我考虑再新设一种 tokenType —— `COMMENT`，在自动机最开始进行如下的处理：

    ```java
    if (curChar == '/') {
        if (skipComment()) { return new Token(TokenType.COMMENT, "", lineno); }
        // 然后丢掉此类 token 
    }
    ```

    然后 `skipComment()` 的逻辑就比较自然了，单行和多行注释分别判断处理即可（多行注释结束的标志需要注意一下）

    ```java
    private boolean skipComment() throws IOException {
        read();
        if (curChar == '/' || curChar == '*') {
            // TODO , 别忘了处理换行
            return true;
        } else { unread(); }
        return false;
    }
    ```

#### 其他工具类

- `ErrorType` ：错误信息类型枚举类

    ```java
    public enum ErrorType {
        illegalSymbol("a"),
        nameRedefinition("b"),
        ;
        // TODO
    }
    ```

- `TokenStream` ：将词法分析得到的 tokenList 转化为 token 流，并实现了 `read()` 和 `unread()` 等接口

- `Printer` ：负责控制所有信息的输出

### 文件组织

暂时如下

```markdown
├───📁 enums/
│   ├───📄 ErrorType.java
│   └───📄 TokenType.java
├───📁 frontend/
│   └───📁 lexer/
│       ├───📄 Lexer.java
│       ├───📄 Token.java
│       └───📄 TokenStream.java
├───📁 utils/
│   └───📄 Printer.java
└───📄 Compiler.java
```

### 修改

bug 修复：空字符串识别错误的问题

```java
else if (curChar == '\"') {
    read();
    while (curChar != '\"' || escape) { // 要先判断，不能用do-while
        sb.append(curChar);
        if (curChar == '\\') { escape = !escape; }
        else { escape = false; }
        // 虽然没要求转义符号的处理，但是还是写了
        read();
    }
    sb.append(curChar);
    return new Token(TokenType.STRCON, sb.toString(), lineno);
}
```

## 语法分析设计

### 总体概述

语法分析的任务是根据词法分析得到的 token 流，识别出源程序的语法结构，即将 token 流转化为抽象语法树（每个非叶节点表示一个语法成分，每个叶节点表示一个终结符）。

### 架构设计

我设计了一个基本类 `Node` 用于表示抽象语法树的节点，使用带有回溯的递归下降方法不断地构建新的节点（每种语法成分都有对应的 `parse` 方法），直到构建出整个抽象语法树。

构建完成后，递归地调用 `print()` 方法，以后序遍历的方式输出抽象语法树。


```java
public class Node {
    protected final SyntaxCompType type;
    protected final List<Node> children;
    protected int beginLine;
    protected int endLine;
    protected int size;

    public Node(SyntaxCompType type, List<Node> children) {
        // ...
    }

    public void print() throws IOException {
        for (Node child : children) {
            child.print();
        }
        if (type != SyntaxCompType.BlockItem &&
            type != SyntaxCompType.Decl &&
            type != SyntaxCompType.BType) {
            Printer.printSyntaxComp(type.toString());
        }
    }

    // ...
}
```

除了每种语法成分的节点类，还有一个 `TokenNode` 类，用于表示叶节点，即终结符。

```java
public class TokenNode extends Node {
    private final Token token;

    public TokenNode(SyntaxCompType type, Token token) {
        super(type, new ArrayList<>());
        this.token = token;
        super.beginLine = super.endLine = token.lineno();
    }

    @Override
    public void print() throws IOException {
        Printer.printSyntaxComp(token.toString());
    }

    // ...
}
```

此外，为了方便回溯，我还额外设计了一个枚举类型 `SyntaxCompType.FAIL` ，用于表示解析失败。`Node` 类的 `size` 属性表示当前节点的子节点数量，如果解析失败，unread 回溯时可以根据 `size` 的值回溯相应的 token 数量。

最后，我将所有的语法节点类都放在了 `node` 包下，并大致分为 `declaration` 、 `expression` 、 `function` 和 `statement` 四个包。

### 实现细节

#### 消除左递归

文法中的左递归会导致递归下降分析器陷入死循环，因此需要对文法进行一定的修改。例如，对于以下文法：

```plain
AddExp → MulExp | AddExp ('+' | '−') MulExp
```

可以将其改写为：

```plain
AddExp → MulExp { ('+' | '−') MulExp }
``` 

但是，这会导致语法树的结构发生改变。为了满足原文法的语法树结构，我在构建 `AddExp` 节点时，不断地将 `AddExp(最开始为 MulExp) ('+' | '−') MulExp` 的结构转化为新的 `AddExp` 节点。

```java
/**
 * AddExp -> MulExp | AddExp ('+' | '−') MulExp
 */
private Node parseAddExp() {
    List<Node> children = new ArrayList<>();
    Node mulExpNode = parseMulExp();
    if (mulExpNode.getType() == SyntaxCompType.FAIL) {
        unread(mulExpNode.getSize()); // 回溯
        return new Node(SyntaxCompType.FAIL, children);
    } else { children.add(mulExpNode); }

    while (true) {
        read();
        if (curToken.type() == TokenType.PLUS || 
            curToken.type() == TokenType.MINU) {
            Node addExpNode = new AddExpNode(SyntaxCompType.AddExp, new ArrayList<>(children)); // 拷贝构造
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
```

#### FIRST 集合冲突

在递归下降分析中，如果两个产生式的 FIRST 集合有交集，那么就会导致分析器错误地选择产生式。例如，对于以下的文法：

```plain
Stmt -> LVal '=' Exp ';'
        | [Exp] ';'
        | LVal '=' 'getint''('')'';'
        | LVal '=' 'getchar''('')'';
        | ...

UnaryExp -> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
PrimaryExp -> '(' Exp ')' | LVal | Number | Character
LVal -> Ident ['[' Exp ']']
```
为了解决这个问题，我将能“推导得更远”的产生式作为优先产生式，这样就能避免 FIRST 集合的冲突。

```java
private Node parseStmt() {
    Node stmtNode = parseAssignStmt();
    if (stmtNode.getType() == SyntaxCompType.FAIL) {
        unread(stmtNode.getSize());
    } else { return stmtNode; }

    // ...

    stmtNode = parseGetIntStmt();
    if (stmtNode.getType() == SyntaxCompType.FAIL) {
        unread(stmtNode.getSize());
    } else { return stmtNode; }

    stmtNode = parseGetCharStmt();
    if (stmtNode.getType() == SyntaxCompType.FAIL) {
        unread(stmtNode.getSize());
    } else { return stmtNode; }

    // ...

    stmtNode = parseExpStmt();
    if (stmtNode.getType() == SyntaxCompType.FAIL) {
        unread(stmtNode.getSize());
        return new Node(SyntaxCompType.FAIL, new ArrayList<>());
    } else { return stmtNode; }
}

private Node parseUnaryExp() {
    List<Node> children = new ArrayList<>();
    read();
    if (curToken.type() == TokenType.IDENFR) {
        // ...
    } else {
        unread();
    }
    // ...
}
```

#### 错误处理

对于错误 `i, j, k` 的行号确定，我采用了以下的处理方式：

```java
if (children.isEmpty()) {
    // ...
} else {
    int lineno = children.get(children.size() - 1).getEndLine();
    // 前一个节点的结束行号
    read();
    if (curToken.type() != /* ... */) {
        unread();
        Recorder.addErrorMessage(/* ... */, lineno);
        children.add(new TokenNode(SyntaxCompType.TOKEN, /* ... */));
    } else { children.add(new TokenNode(SyntaxCompType.TOKEN, curToken)); }
    return new Node(/* ... */, children);
}
```

最后需要将所有的错误信息按照行号排序输出。

### 文件组织

暂时如下

```markdown
├───📁 enums/
│   ├───📄 ErrorType.java
│   ├───📄 SyntaxCompType.java
│   └───📄 TokenType.java
├───📁 frontend/
│   ├───📁 lexer/
│   │   ├───📄 Lexer.java
│   │   ├───📄 Token.java
│   │   └───📄 TokenStream.java
│   └───📁 parser/
│       ├───📁 node/
│       │   ├───📁 declaration/...
│       │   ├───📁 expression/...
│       │   ├───📁 function/...
│       │   ├───📁 statement/...
│       │   ├───📄 Node.java
│       │   └───📄 TokenNode.java
│       └───📄 Parser.java
├───📁 utils/
│   ├───📄 Error.java
│   ├───📄 Printer.java
│   └───📄 Recorder.java
└───📄 Compiler.java
```

### 修改

bug 修复：parse 各个节点时的返回值类型写错（`Node` -> `InitValNode`）

## 语义分析设计

### 总体概述

## 代码生成设计

## 代码优化设计