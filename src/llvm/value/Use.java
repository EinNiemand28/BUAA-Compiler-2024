package llvm.value;

public class Use {
    private Value value;
    private User user;
    private int index;

    public Use(Value value, User user, int index) {
        this.value = value;
        this.user = user;
        this.index = index;
    }

    public Value getValue() { return value; }
    public User getUser() { return user; }
    public int getIndex() { return index; }

    public void setValue(Value value) {
        if (this.value != null) {
            this.value.removeUse(this);
        }
        this.value = value;
        if (value != null) {
            value.addUse(this);
        }
    }
}
