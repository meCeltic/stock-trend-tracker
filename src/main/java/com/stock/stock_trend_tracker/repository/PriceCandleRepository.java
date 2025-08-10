package com.stock.stock_trend_tracker.repository;

import com.stock.stock_trend_tracker.domain.PriceCandle;
import com.stock.stock_trend_tracker.domain.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceCandleRepository extends JpaRepository<PriceCandle, Long> {
    
    /**
     * Find price candles by stock
     * @param stock The stock entity
     * @return List of price candles for the stock
     */
    List<PriceCandle> findByStock(Stock stock);
    
    /**
     * Find price candles by stock ID
     * @param stockId Stock ID
     * @return List of price candles for the stock
     */
    List<PriceCandle> findByStockId(Long stockId);
    
    /**
     * Find price candles by stock and timeframe
     * @param stock The stock entity
     * @param timeframe Timeframe (e.g., "1m", "5m", "1h", "1d")
     * @return List of price candles for the stock and timeframe
     */
    List<PriceCandle> findByStockAndTimeframe(Stock stock, String timeframe);
    
    /**
     * Find price candles by stock ID and timeframe
     * @param stockId Stock ID
     * @param timeframe Timeframe
     * @return List of price candles
     */
    List<PriceCandle> findByStockIdAndTimeframe(Long stockId, String timeframe);
    
    /**
     * Find price candles between timestamps
     * @param stock The stock entity
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @return List of price candles within the time range
     */
    List<PriceCandle> findByStockAndTimestampBetween(Stock stock, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Find price candles by stock, timeframe and between timestamps
     * @param stock The stock entity
     * @param timeframe Timeframe
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @return List of price candles
     */
    List<PriceCandle> findByStockAndTimeframeAndTimestampBetween(
            Stock stock, String timeframe, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Find latest price candle for a stock
     * @param stock The stock entity
     * @return Optional containing the latest price candle
     */
    Optional<PriceCandle> findTopByStockOrderByTimestampDesc(Stock stock);
    
    /**
     * Find latest price candle by stock ID
     * @param stockId Stock ID
     * @return Optional containing the latest price candle
     */
    @Query("SELECT pc FROM PriceCandle pc WHERE pc.stock.id = :stockId ORDER BY pc.timestamp DESC")
    Optional<PriceCandle> findLatestByStockId(@Param("stockId") Long stockId);
    
    /**
     * Find price candles by stock ordered by timestamp descending with pagination
     * @param stock The stock entity
     * @param pageable Pagination information
     * @return Page of price candles
     */
    Page<PriceCandle> findByStockOrderByTimestampDesc(Stock stock, Pageable pageable);
    
    /**
     * Get count of price candles by stock
     * @param stock The stock entity
     * @return Count of price candles
     */
    long countByStock(Stock stock);
    
    /**
     * Get distinct timeframes for a stock
     * @param stockId Stock ID
     * @return List of distinct timeframes
     */
    @Query("SELECT DISTINCT pc.timeframe FROM PriceCandle pc WHERE pc.stock.id = :stockId")
    List<String> findDistinctTimeframesByStockId(@Param("stockId") Long stockId);
    
    /**
     * Find price candles with high price above threshold
     * @param stock The stock entity
     * @param threshold Price threshold
     * @return List of price candles with high price above threshold
     */
    List<PriceCandle> findByStockAndHighPriceGreaterThan(Stock stock, BigDecimal threshold);
    
    /**
     * Delete price candles older than specified date
     * @param cutoffDate Cutoff date
     * @return Number of deleted records
     */
    @Query("DELETE FROM PriceCandle pc WHERE pc.timestamp < :cutoffDate")
    int deleteByTimestampBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find average closing price for a stock within time range
     * @param stockId Stock ID
     * @param startTime Start time
     * @param endTime End time
     * @return Average closing price
     */
    @Query("SELECT AVG(pc.closePrice) FROM PriceCandle pc WHERE pc.stock.id = :stockId " +
           "AND pc.timestamp BETWEEN :startTime AND :endTime")
    BigDecimal findAverageClosePriceInRange(@Param("stockId") Long stockId, 
                                           @Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);
}
