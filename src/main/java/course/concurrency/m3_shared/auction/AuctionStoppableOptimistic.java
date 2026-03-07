package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;
    private final AtomicMarkableReference<Bid> latestBid;

    public AuctionStoppableOptimistic(Notifier notifier) {
        latestBid = new AtomicMarkableReference<>(new Bid(0L, 0L, 0L), false);
        this.notifier = notifier;
    }


    public boolean propose(Bid bid) {
        Bid currentBid;
        do {
            if (latestBid.isMarked()) {
                return false;
            }
            currentBid = latestBid.getReference();
            if (currentBid != null && currentBid.getPrice() >= bid.getPrice()) {
                return false;
            }

        } while (!latestBid.compareAndSet(currentBid, bid, false, false));

        notifier.sendOutdatedMessage(currentBid);

        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        Bid current;
        do {
            current = latestBid.getReference();
        } while (!latestBid.attemptMark(current, true));

        return current;
    }
}