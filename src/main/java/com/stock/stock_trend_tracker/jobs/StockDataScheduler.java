package com.stock.stock_trend_tracker.jobs;

import com.stock.stock_trend_tracker.domain.Stock;
import com.stock.stock_trend_tracker.domain.PriceCandle;
import com.stock.stock_trend_tracker.repository.StockRepository;
import com.stock.stock_trend_tracker.repository.PriceCandleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class StockDataScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(StockDataScheduler.class);
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private PriceCandleRepository priceCandleRepository;
    
    private final Random random = new Random();
    
    /**
     * Scheduled job to fetch and update stock price data every 5 minutes
     * This is a demo implementation that generates random price data
     * In a real application, this would fetch data from external APIs
     */
    @Scheduled(fixedRate = 300000) // 5 minutes = 300,000 milliseconds
    public void updateStockPrices() {
        logger.info("Starting scheduled stock price update...");
        
        try {
            List<Stock> stocks = stockRepository.findAll();
            
            if (stocks.isEmpty()) {
                logger.info("No stocks found in database. Creating sample stocks...");
                createSampleStocks();
                stocks = stockRepository.findAll();
            }
            
            for (Stock stock : stocks) {
                generatePriceCandle(stock);
            }
            
            logger.info("Completed stock price update for {} stocks", stocks.size());
            
        } catch (Exception e) {
            logger.error("Error occurred during stock price update", e);
        }
    }
    
    /**
     * Scheduled job to clean up old price candle data every day at 2 AM
     * Removes price candles older than 30 days
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void cleanupOldPriceData() {
        logger.info("Starting cleanup of old price data...");
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            int deletedCount = priceCandleRepository.deleteByTimestampBefore(cutoffDate);
            
            logger.info("Cleaned up {} old price candles older than {}", deletedCount, cutoffDate);
            
        } catch (Exception e) {
            logger.error("Error occurred during price data cleanup", e);
        }
    }
    
    /**
     * Weekly job to analyze stock trends (every Sunday at 1 AM)
     */
    @Scheduled(cron = "0 0 1 * * SUN")
    public void analyzeStockTrends() {
        logger.info("Starting weekly stock trend analysis...");
        
        try {
            List<Stock> stocks = stockRepository.findStocksOrderByPriceCandlesCount();
            
            for (Stock stock : stocks) {
                long candleCount = priceCandleRepository.countByStock(stock);
                List<String> timeframes = priceCandleRepository.findDistinctTimeframesByStockId(stock.getId());
                
                logger.info("Stock: {} ({}) - Total candles: {}, Timeframes: {}", 
                          stock.getSymbol(), stock.getName(), candleCount, timeframes);
            }
            
            logger.info("Completed weekly trend analysis for {} stocks", stocks.size());
            
        } catch (Exception e) {
            logger.error("Error occurred during trend analysis", e);
        }
    }
    
    /**
     * Create sample stocks for demonstration purposes
     */
    private void createSampleStocks() {
        logger.info("Creating sample stocks...");
        
        String[][] sampleStocks = {
            {"AAPL", "Apple Inc.", "NASDAQ"},
            {"GOOGL", "Alphabet Inc.", "NASDAQ"},
            {"MSFT", "Microsoft Corporation", "NASDAQ"},
            {"AMZN", "Amazon.com Inc.", "NASDAQ"},
            {"TSLA", "Tesla Inc.", "NASDAQ"},
            {"NVDA", "NVIDIA Corporation", "NASDAQ"},
            {"META", "Meta Platforms Inc.", "NASDAQ"},
            {"NFLX", "Netflix Inc.", "NASDAQ"}
        };
        
        for (String[] stockData : sampleStocks) {
            if (!stockRepository.existsBySymbol(stockData[0])) {
                Stock stock = new Stock(stockData[0], stockData[1]);
                stock.setExchange(stockData[2]);
                stockRepository.save(stock);
                logger.info("Created sample stock: {} ({})", stock.getSymbol(), stock.getName());
            }
        }
    }
    
    /**
     * Generate a random price candle for a stock
     * In a real application, this would fetch actual market data
     */
    private void generatePriceCandle(Stock stock) {
        try {
            // Get the latest price candle for this stock
            PriceCandle latestCandle = priceCandleRepository.findLatestByStockId(stock.getId()).orElse(null);
            
            BigDecimal basePrice;
            if (latestCandle != null) {
                // Use the close price of the latest candle as base
                basePrice = latestCandle.getClosePrice();
            } else {
                // Generate a random starting price between $50 and $500
                basePrice = BigDecimal.valueOf(50 + random.nextDouble() * 450);
            }
            
            // Generate price movement (±5%)
            double changePercent = (random.nextDouble() - 0.5) * 0.10; // ±5%
            BigDecimal priceChange = basePrice.multiply(BigDecimal.valueOf(changePercent));
            
            // Calculate OHLC prices
            BigDecimal openPrice = basePrice;
            BigDecimal closePrice = basePrice.add(priceChange);
            
            // High and low prices with some randomness
            BigDecimal highPrice = (openPrice.compareTo(closePrice) > 0 ? openPrice : closePrice)
                    .add(BigDecimal.valueOf(random.nextDouble() * 5)); // Add up to $5
            BigDecimal lowPrice = (openPrice.compareTo(closePrice) < 0 ? openPrice : closePrice)
                    .subtract(BigDecimal.valueOf(random.nextDouble() * 5)); // Subtract up to $5
            
            // Generate random volume between 1M and 10M
            Long volume = 1000000L + random.nextInt(9000000);
            
            // Create price candle
            PriceCandle priceCandle = new PriceCandle(stock, LocalDateTime.now(), 
                    openPrice, highPrice, lowPrice, closePrice);
            priceCandle.setVolume(volume);
            priceCandle.setTimeframe("5m"); // 5-minute candles
            
            priceCandleRepository.save(priceCandle);
            
            logger.debug("Generated price candle for {}: O={}, H={}, L={}, C={}, V={}", 
                       stock.getSymbol(), openPrice, highPrice, lowPrice, closePrice, volume);
                       
        } catch (Exception e) {
            logger.error("Error generating price candle for stock: {}", stock.getSymbol(), e);
        }
    }
}
