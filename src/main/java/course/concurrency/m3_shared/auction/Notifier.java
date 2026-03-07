package course.concurrency.m3_shared.auction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notifier {

    private final ExecutorService executorService = Executors.newFixedThreadPool(100);

    public void sendOutdatedMessage(Bid bid) {
        executorService.submit(this::imitateSending);
    }

    private void imitateSending() {
        // don't remove this delay, deal with it properly
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}