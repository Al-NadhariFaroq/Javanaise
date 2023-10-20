package burst;

public class CounterImpl implements Counter {
	private long value;

	public CounterImpl() {
		value = 0;
	}

	public long getValue() {
		return value;
	}

	public void incrementByOne() {
		value++;
	}
}
