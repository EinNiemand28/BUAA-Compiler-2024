package llvm.ir;

public class SlotTracker {
    private int count;
    private static final SlotTracker instance = new SlotTracker();

    public static SlotTracker getInstance() { return instance; }

    public void reset() { count = 0; }
    public String getSlot() {
        return "" + count++;
    }
}