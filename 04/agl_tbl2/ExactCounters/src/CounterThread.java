import java.util.concurrent.atomic.AtomicLong;

public class CounterThread extends Thread {

	private boolean mRun = true;
	private final AtomicLong mCounterAndMax;
	private final ReaderThread mReader;
	final static long MAX_COUNTERMAX = (1l << 32) - 1;

	public CounterThread(ReaderThread reader) {
		mCounterAndMax = new AtomicLong();
		mReader = reader;
	}

	public long[] splitCounterAndMax() {
		long counterAndMax = mCounterAndMax.get();
		long counter = (counterAndMax >> 32) & MAX_COUNTERMAX;
		long max = counterAndMax & MAX_COUNTERMAX;
		return new long[] { counter, max };
	}

	public long mergeCounterAndMax(long counter, long max) {
		return (counter << 32 | max);
	}

	public void setCounterAndMax(long value) {
		mCounterAndMax.set(value);
	}

	public void increment() {
		long[] counterAndMax;
		long n;
		boolean fastPath = false;
		do {
			counterAndMax = splitCounterAndMax();
			if (counterAndMax[1] - counterAndMax[0] < 1)
				break;
			n = mergeCounterAndMax(counterAndMax[0] + 1, counterAndMax[1]);
			fastPath = true;
		} while (mCounterAndMax.compareAndSet(mCounterAndMax.get(), n));
		if (fastPath)
			return;

		synchronized (mReader) {
			globalizeCount();
			if (mReader.getGlobalCountMax() - mReader.getGlobalCount()
					- mReader.getGlobalReserve() < 1) {
				mReader.flushThreadCount();
				if (mReader.getGlobalCountMax() - mReader.getGlobalCount()
						- mReader.getGlobalReserve() < 1) {
					System.out.println("ERROR updating counter");
					return;
				}
			}
			mReader.addToGlobalCount(1);
			balanceCount();
		}
	}

	private void globalizeCount() {
		long[] counterAndMax = splitCounterAndMax();
		mReader.addToGlobalCount(counterAndMax[0]);
		mReader.subtractFromGlobalReserve(counterAndMax[1]);
		mCounterAndMax.set(0);
	}

	private void balanceCount() {
		long counter;
		long counterMax;
		long limit = mReader.getGlobalCountMax() - mReader.getGlobalCount()
				- mReader.getGlobalReserve();

		limit /= mReader.numberOfThreads();
		if (limit > MAX_COUNTERMAX) {
			counterMax = MAX_COUNTERMAX;
		} else {
			counterMax = limit;
		}
		mReader.addToGlobalReserve(counterMax);
		counter = 0;
		mCounterAndMax.set(mergeCounterAndMax(counter, counterMax));
	}

	public long getSum() {
		return splitCounterAndMax()[0];
	}

	public void stopCounter() {
		mRun = false;
	}

	@Override
	public void run() {
		while (mRun) {
			increment();
		}
	}
}
