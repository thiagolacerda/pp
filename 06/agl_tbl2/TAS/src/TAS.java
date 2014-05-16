import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;

public class TAS {

    public static CounterThread[] threads;

    public static void stopAll() {
        System.out.println("stopAll callled");
        for (CounterThread thread : threads)
            thread.stopThread();
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out
                    .println("Error: run as follows: TAS <number_of_threads> <number_of_counters> <time_in_ms> [q, e, a]");
            System.exit(1);
        }

        int numberOfThreads = Integer.parseInt(args[0]);
        int numberOfCounters = Integer.parseInt(args[1]);
        int time = Integer.parseInt(args[2]);

        if (numberOfThreads <= 0 || numberOfCounters <= 0 || time <= 0) {
            System.out.println("Error: the number of threads, counters and time must be positive integers");
            System.exit(2);
        }

        boolean queue = false;
        CounterLock.BackOff backoff = CounterLock.BackOff.NoBackoff;

        if (args.length > 3)
            queue = args[3].equals("q");

        if (args.length > 3)
            backoff = args[3].equals("e") ? CounterLock.BackOff.Exponential
                    : (args[3].equals("a") ? CounterLock.BackOff.Additive : CounterLock.BackOff.NoBackoff);

        Counter[] counters = new Counter[numberOfCounters];
        TAS.threads = new CounterThread[numberOfThreads];

        String lockType = queue ? "queue" : "normal";
        System.out.println("Will start with " + numberOfCounters + " counters and " + numberOfThreads
                + " threads, with " + lockType + " lock. Backoff type: " + backoff);

        Lock lock;
        if (queue)
            lock = new QueueLock();
        else
            lock = new CounterLock(backoff);

        for (int i = 0; i < numberOfCounters; ++i)
            counters[i] = new Counter(i, lock);

        for (int i = 0; i < numberOfThreads; ++i)
            TAS.threads[i] = new CounterThread(counters);

        Timer timer = new Timer();

        for (CounterThread thread : threads)
            thread.start();

        System.out.println("Threads started");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TAS.stopAll();
            }
        }, time);

        System.out.println("Timer scheduled");

        for (CounterThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        long sum = 0;
        for (Counter counter : counters) {
            System.out.println("Counter " + counter.index + ", counted: " + counter.counter());
            sum += counter.counter();
        }

        for (CounterThread thread : threads) {
            System.out.println("CounterThread " + thread + ", counted: " + thread.counter());
        }

        System.out.println("Sum: " + sum);
        System.exit(0);
    }
}
