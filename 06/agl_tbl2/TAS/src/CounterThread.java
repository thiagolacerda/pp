import java.util.Random;

public class CounterThread extends Thread {

    private final Counter[] mCounters;
    private volatile boolean mRun = true;
    private long mCounter = 0;
    private final Random mRand;
    private final long mLimit;

    public CounterThread(Counter[] counters, long limit) {
        mCounters = counters;
        mRand = new Random();
        mLimit = limit;
    }

    public void stopThread() {
        mRun = false;
    }

    public long counter() {
        return mCounter;
    }

    @Override
    public void run() {
        if (mLimit > 0)
            mRun = false;

        while (mRun || (mLimit != 0 && mCounter < mLimit)) {
            int index = mRand.nextInt(mCounters.length);
            mCounters[index].lock.lock();
            mCounters[index].increment();
            ++mCounter;
            mCounters[index].lock.unlock();
        }
        System.out.println("Exited thread " + this);
    }
}
