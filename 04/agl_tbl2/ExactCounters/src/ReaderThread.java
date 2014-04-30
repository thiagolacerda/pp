import java.util.ArrayList;

public class ReaderThread extends Thread {

	private ArrayList<CounterThread> mCounters;
	private long mGlobalCount;
	private long mGlobalReserve;
	private final long mGlobalCountMax;

	public ReaderThread(long limit) {
		mGlobalCountMax = limit;
	}

	public long getGlobalCount() {
		return mGlobalCount;
	}

	public long getGlobalReserve() {
		return mGlobalReserve;
	}

	public long getGlobalCountMax() {
		return mGlobalCountMax;
	}

	public void subtractFromGlobalCount(long value) {
		mGlobalCount -= value;
	}

	public void addToGlobalCount(long value) {
		mGlobalCount += value;
	}

	public void subtractFromGlobalReserve(long value) {
		mGlobalReserve -= value;
	}

	public void addToGlobalReserve(long value) {
		mGlobalReserve += value;
	}

	public void setThreads(ArrayList<CounterThread> threads) {
		mCounters = threads;
	}

	private void stopThreads() {
		for (CounterThread thread : mCounters)
			thread.stopCounter();
	}

	public int numberOfThreads() {
		return mCounters.size();
	}

	public void flushThreadCount() {
		if (mGlobalReserve == 0)
			return;

		for (CounterThread thread : mCounters) {
			long[] counterAndMax = thread.splitCounterAndMax();
			thread.setCounterAndMax(0);
			mGlobalCount += counterAndMax[0];
			mGlobalReserve -= counterAndMax[1];
		}
	}

	@Override
	public void run() {
		do {
			try {
				synchronized (this) {
					readCount();
				}
				sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (mGlobalCount != mGlobalCountMax);
		stopThreads();
	}

	private void readCount() {
		for (CounterThread counter : mCounters) {
			mGlobalCount += counter.getSum();
		}
		System.out.println("Sum: " + mGlobalCount);
	}
}
