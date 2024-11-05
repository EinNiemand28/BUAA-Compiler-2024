package frontend.symbol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Printer;

public class SymbolTable {
    private final int id;
    private SymbolTable preTable;
    private final List<SymbolTable> subTables;
    private final Map<String, Symbol> symbols;
    private final List<Symbol> symbolList;

    public SymbolTable(int id, SymbolTable preTable) {
        this.id = id;
        this.preTable = preTable;
        this.subTables = new ArrayList<>();
        this.symbols = new HashMap<>();
        this.symbolList = new ArrayList<>();
    }

    public void insertSymbol(Symbol symbol) {
        if (contains(symbol.getContent())) {
            return;
        }
        symbols.put(symbol.getContent(), symbol);
        symbolList.add(symbol);
    }

    public boolean contains(String content) {
        return symbols.containsKey(content);
    }

    public Symbol getSymbol(String content) {
        // System.out.println("table id: " + id + " get: " + content);
        // for (String key : symbols.keySet()) {
        //     System.out.println("table id: " + id + " key: " + key);
        // }
        if (contains(content)) {
            return symbols.get(content);
        }
        if (preTable != null) {
            return preTable.getSymbol(content);
        } else {
            return null;
        }
    }

    public void insertSubTable(SymbolTable subTable) {
        subTables.add(subTable);
    }

    public List<SymbolTable> getSubTables() {
        return subTables;
    }

    public int getId() {
        return id;
    }

    public SymbolTable getPreTable() {
        return preTable;
    }

    public void print() throws IOException {
        for (Symbol symbol : symbolList) {
            Printer.printSymbol(id + " " + symbol.toString() + "\n");
        }
        for (SymbolTable subTable : subTables) {
            subTable.print();
        }
    }
}
