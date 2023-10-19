package burst;

public class Counter implements ICounter {
    private long value;

    public Counter(){
        value = 0;
    }
    @Override
    public long getValue() {
        return value;
    }

    @Override
    public void incrementByOne() {
        value++;
    }
}
