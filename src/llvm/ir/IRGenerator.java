package llvm.ir;

public class IRGenerator {
    private static final IRGenerator instance = new IRGenerator();
    private IRGenerator() {}
    public static IRGenerator getInstance() { return instance; }
}
