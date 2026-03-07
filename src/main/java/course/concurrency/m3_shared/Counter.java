package course.concurrency.m3_shared;

public class Counter {
    private static final Object lock = new Object();
    private static volatile int threadNum = 1;

    public static void first() {
        for (int i = 0; i < 3; i++) {
            synchronized (lock) {
                while (threadNum != 1) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(1);
                threadNum = 2;
                lock.notifyAll();
            }
        }
    }

    public static void second() {
        for (int i = 0; i < 3; i++) {
            synchronized (lock) {
                while (threadNum != 2) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(2);
                threadNum = 3;
                lock.notifyAll();
            }
        }
    }

    public static void third() {
        for (int i = 0; i < 3; i++) {
            synchronized (lock) {
                while (threadNum != 3) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(3);
                threadNum = 1;
                lock.notifyAll();
            }
        }
    }


    public static void main(String[] args) {
        Thread t1 = new Thread(() -> first());
        Thread t2 = new Thread(() -> second());
        Thread t3 = new Thread(() -> third());
        t1.start();
        t2.start();
        t3.start();
    }
}
