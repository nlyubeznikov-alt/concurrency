package course.concurrency.m3_shared.auction;

import java.util.concurrent.locks.ReentrantLock;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Notifier notifier;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile Bid latestBid = new Bid(0L, 0L, 0L);
    private volatile boolean isOpen = true;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        if (isOpen && (bid.getPrice() > latestBid.getPrice())) {
            lock.lock();
            try {
                if (isOpen && (bid.getPrice() > latestBid.getPrice())) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        lock.lock();
        try {
            isOpen = false;
        } finally {
            lock.unlock();
        }
        return latestBid;
    }
}
