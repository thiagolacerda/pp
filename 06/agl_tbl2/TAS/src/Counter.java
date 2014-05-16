import java.util.concurrent.locks.Lock;

public class Counter {
    private long mCounter = 0;
    public final Lock lock;
    public int index;

    public Counter(int index, Lock lock) {
        this.index = index;
        this.lock = lock;
    }

    public void increment() {
        ++mCounter;
    }

    public long counter() {
        return mCounter;
    }
}
