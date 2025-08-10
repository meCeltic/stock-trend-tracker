package com.stock.stock_trend_tracker.repository;

import com.stock.stock_trend_tracker.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    /**
     * Find stock by symbol
     * @param symbol Stock symbol (e.g., "AAPL", "GOOGL")
     * @return Optional containing the stock if found
     */
    Optional<Stock> findBySymbol(String symbol);
    
    /**
     * Find stock by symbol (case insensitive)
     * @param symbol Stock symbol (case insensitive)
     * @return Optional containing the stock if found
     */
    Optional<Stock> findBySymbolIgnoreCase(String symbol);
    
    /**
     * Find stocks by exchange
     * @param exchange Exchange name
     * @return List of stocks in the exchange
     */
    List<Stock> findByExchange(String exchange);
    
    /**
     * Find stocks by name containing (case insensitive)
     * @param name Part of the stock name
     * @return List of stocks matching the name
     */
    List<Stock> findByNameContainingIgnoreCase(String name);
    
    /**
     * Check if stock exists by symbol
     * @param symbol Stock symbol
     * @return true if stock exists
     */
    boolean existsBySymbol(String symbol);
    
    /**
     * Get all distinct exchanges
     * @return List of unique exchange names
     */
    @Query("SELECT DISTINCT s.exchange FROM Stock s WHERE s.exchange IS NOT NULL ORDER BY s.exchange")
    List<String> findAllDistinctExchanges();
    
    /**
     * Find stocks with price candles count
     * @return List of stocks ordered by price candles count descending
     */
    @Query("SELECT s FROM Stock s LEFT JOIN s.priceCandles pc GROUP BY s ORDER BY COUNT(pc) DESC")
    List<Stock> findStocksOrderByPriceCandlesCount();
    
    /**
     * Search stocks by symbol or name containing
     * @param searchTerm Search term to match against symbol or name
     * @return List of matching stocks
     */
    @Query("SELECT s FROM Stock s WHERE LOWER(s.symbol) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Stock> searchBySymbolOrName(@Param("searchTerm") String searchTerm);
}
