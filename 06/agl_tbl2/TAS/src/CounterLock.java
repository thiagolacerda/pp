import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CounterLock implements Lock {

    private final AtomicBoolean mState = new AtomicBoolean(false);
    private final long mMaxMillisecondsWait = 65536;
    private long mWait = 1;
    private final BackOff mBackoff;

    public enum BackOff {
        NoBackoff, Exponential, Additive
    }

    public CounterLock(BackOff backoff) {
        System.out.println("New CounterLock, backoff " + backoff);
        mBackoff = backoff;
    }

    @Override
    public void lock() {
        while (true) {
            while (mState.get())
                ;
            if (!mState.getAndSet(true))
                return;

            if (mBackoff == BackOff.NoBackoff)
                continue;

            long wait;
            if (mWait < mMaxMillisecondsWait) {
                if (mBackoff == BackOff.Exponential)
                    wait = mWait << 1;
                else
                    wait = mWait + 10;
                mWait = Math.min(mMaxMillisecondsWait, wait);
            }

            try {
                Thread.sleep(mWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean tryLock() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void unlock() {
        mState.set(false);
    }

    @Override
    public Condition newCondition() {
        // TODO Auto-generated method stub
        return null;
    }
}
