import java.util.Random;

public class CounterThread extends Thread {

    private final Counter[] mCounters;
    private volatile boolean mRun = true;
    private long mCounter = 0;
    private final Random mRand;

    public CounterThread(Counter[] counters) {
        mCounters = counters;
        mRand = new Random();
    }

    public void stopThread() {
        mRun = false;
    }

    public long counter() {
        return mCounter;
    }

    @Override
    public void run() {
        while (mRun) {
            int index = mRand.nextInt(mCounters.length);
            mCounters[index].lock.lock();
            mCounters[index].increment();
            ++mCounter;
            mCounters[index].lock.unlock();
        }
        System.out.println("Exited thread " + this);
    }
}
