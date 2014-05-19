import re
import subprocess
import time

def measure(threads, counters, duration, limit, lockType):
    for i in range(30):
        start = time.time()
        output = subprocess.check_output(["java", "-jar", "TAS.jar", str(threads), str(counters), str(duration),
                str(limit), lockType])
        elapsed = (time.time() - start)

        #print 'Execution %d, elapsed: %f' % (i, elapsed)
        #print output


if __name__ == '__main__':
    measure(10, 10, 10, 0, 'r')
