package com.stock.stock_trend_tracker.web;

import com.stock.stock_trend_tracker.domain.Stock;
import com.stock.stock_trend_tracker.domain.PriceCandle;
import com.stock.stock_trend_tracker.repository.StockRepository;
import com.stock.stock_trend_tracker.repository.PriceCandleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StockController {
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private PriceCandleRepository priceCandleRepository;
    
    /**
     * Get all stocks
     * @return List of all stocks
     */
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        return ResponseEntity.ok(stocks);
    }
    
    /**
     * Get stock by ID
     * @param id Stock ID
     * @return Stock entity or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        Optional<Stock> stock = stockRepository.findById(id);
        return stock.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get stock by symbol
     * @param symbol Stock symbol
     * @return Stock entity or 404 if not found
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<Stock> getStockBySymbol(@PathVariable String symbol) {
        Optional<Stock> stock = stockRepository.findBySymbolIgnoreCase(symbol);
        return stock.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Search stocks by symbol or name
     * @param query Search query
     * @return List of matching stocks
     */
    @GetMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(@RequestParam String query) {
        List<Stock> stocks = stockRepository.searchBySymbolOrName(query);
        return ResponseEntity.ok(stocks);
    }
    
    /**
     * Get stocks by exchange
     * @param exchange Exchange name
     * @return List of stocks in the exchange
     */
    @GetMapping("/exchange/{exchange}")
    public ResponseEntity<List<Stock>> getStocksByExchange(@PathVariable String exchange) {
        List<Stock> stocks = stockRepository.findByExchange(exchange);
        return ResponseEntity.ok(stocks);
    }
    
    /**
     * Get all distinct exchanges
     * @return List of exchange names
     */
    @GetMapping("/exchanges")
    public ResponseEntity<List<String>> getAllExchanges() {
        List<String> exchanges = stockRepository.findAllDistinctExchanges();
        return ResponseEntity.ok(exchanges);
    }
    
    /**
     * Create a new stock
     * @param stock Stock entity to create
     * @return Created stock entity
     */
    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) {
        // Check if stock with same symbol already exists
        if (stockRepository.existsBySymbol(stock.getSymbol())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        stock.setCreatedAt(LocalDateTime.now());
        stock.setUpdatedAt(LocalDateTime.now());
        Stock savedStock = stockRepository.save(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStock);
    }
    
    /**
     * Update an existing stock
     * @param id Stock ID
     * @param stockDetails Updated stock details
     * @return Updated stock entity or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stockDetails) {
        Optional<Stock> optionalStock = stockRepository.findById(id);
        
        if (!optionalStock.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Stock stock = optionalStock.get();
        stock.setName(stockDetails.getName());
        stock.setExchange(stockDetails.getExchange());
        stock.setUpdatedAt(LocalDateTime.now());
        
        Stock updatedStock = stockRepository.save(stock);
        return ResponseEntity.ok(updatedStock);
    }
    
    /**
     * Delete a stock
     * @param id Stock ID
     * @return 204 No Content or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        if (!stockRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        stockRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get price candles for a stock
     * @param id Stock ID
     * @param timeframe Optional timeframe filter
     * @param page Page number (default 0)
     * @param size Page size (default 20)
     * @return Page of price candles
     */
    @GetMapping("/{id}/candles")
    public ResponseEntity<Page<PriceCandle>> getStockCandles(
            @PathVariable Long id,
            @RequestParam(required = false) String timeframe,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Optional<Stock> stockOpt = stockRepository.findById(id);
        if (!stockOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Stock stock = stockOpt.get();
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        
        Page<PriceCandle> candles;
        if (timeframe != null && !timeframe.isEmpty()) {
            List<PriceCandle> list = priceCandleRepository.findByStockAndTimeframe(stock, timeframe);
            List<PriceCandle> pageContent = list.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).toList();
            candles = new PageImpl<>(pageContent, pageable, list.size());
        } else {
            candles = priceCandleRepository.findByStockOrderByTimestampDesc(stock, pageable);
        }
        
        return ResponseEntity.ok(candles);
    }
    
    /**
     * Get latest price candle for a stock
     * @param id Stock ID
     * @return Latest price candle or 404 if not found
     */
    @GetMapping("/{id}/candles/latest")
    public ResponseEntity<PriceCandle> getLatestCandle(@PathVariable Long id) {
        Optional<PriceCandle> candle = priceCandleRepository.findLatestByStockId(id);
        return candle.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get stock statistics
     * @param id Stock ID
     * @return Stock statistics
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<Object> getStockStats(@PathVariable Long id) {
        Optional<Stock> stockOpt = stockRepository.findById(id);
        if (!stockOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Stock stock = stockOpt.get();
        long candleCount = priceCandleRepository.countByStock(stock);
        List<String> timeframes = priceCandleRepository.findDistinctTimeframesByStockId(id);
        
        return ResponseEntity.ok(new Object() {
            public final String symbol = stock.getSymbol();
            public final String name = stock.getName();
            public final String exchange = stock.getExchange();
            public final long totalCandles = candleCount;
            public final List<String> availableTimeframes = timeframes;
            public final LocalDateTime createdAt = stock.getCreatedAt();
            public final LocalDateTime updatedAt = stock.getUpdatedAt();
        });
    }
    
    /**
     * Trigger manual fetch for a stock symbol
     * @param symbol Stock symbol to fetch
     * @return Response indicating fetch trigger status
     */
    @PostMapping("/{symbol}/fetch")
    public ResponseEntity<Object> triggerManualFetch(@PathVariable String symbol) {
        // Find stock by symbol
        Optional<Stock> stockOpt = stockRepository.findBySymbolIgnoreCase(symbol);
        if (!stockOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Stock stock = stockOpt.get();
        
        // TODO: Implement actual fetch logic here
        // For now, just return a placeholder response indicating the fetch was triggered
        return ResponseEntity.ok(new Object() {
            public final String message = "Manual fetch triggered for stock: " + symbol;
            public final String symbol = stock.getSymbol();
            public final String name = stock.getName();
            public final LocalDateTime triggeredAt = LocalDateTime.now();
            public final String status = "triggered";
        });
    }
}
