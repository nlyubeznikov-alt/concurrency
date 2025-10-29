package course.concurrency.m2_async.minPrice;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public static final Long WAIT_TIME_LIMIT_MS = 2900L;

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        ExecutorService executor = Executors.newFixedThreadPool(shopIds.size());
        try {
            List<CompletableFuture<Double>> responseCFList = shopIds.stream().
                    map(id -> CompletableFuture.supplyAsync(
                            () -> priceRetriever.getPrice(itemId, id), executor)
                            .orTimeout(WAIT_TIME_LIMIT_MS, TimeUnit.MILLISECONDS)
                            .exceptionally(e -> Double.NaN)
                    ).toList();

            return responseCFList.stream()
                    .map(CompletableFuture::join)
                    .filter(v -> !Double.isNaN(v))
                    .min(Double::compareTo) // Ищем минимальное значение
                    .orElse(Double.NaN);
        } finally {
            executor.shutdownNow();
        }
    }
}
