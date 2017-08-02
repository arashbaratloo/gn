// Ideas from https://dzone.com/articles/java-concurrency-read-write-lo
// Extended a typical ReadWriteLock to be able to cap the maximum number
// of concurrent readers.
public class ReadWriteLock {
  private int readers       = 0;
  private int writers       = 0;
  private int writeRequests = 0;
  private int max_readers   = Integer.MAX_VALUE;

  ReadWriteLock() {}
  ReadWriteLock(int max_readers) {
    this.max_readers = max_readers;
  }

  public synchronized void lockRead() throws InterruptedException{
    while (writers > 0 || writeRequests > 0 || readers >= max_readers-1) {
      wait();
    }
    readers++;
  }

  public synchronized void unlockRead() {
    readers--;
    notifyAll();
  }

  public synchronized void lockWrite() throws InterruptedException {
    writeRequests++;
    while (readers > 0 || writers > 0) {
      wait();
    }
    writeRequests--;
    writers++;
  }

  public synchronized void unlockWrite() throws InterruptedException {
    writers--;
    notifyAll();
  }
} // end class ReadWriteLock
