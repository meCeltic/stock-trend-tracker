package com.stock.stock_trend_tracker.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "watchlist_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"watchlist_id", "stock_id"})
})
public class WatchlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchlist_id", nullable = false)
    private Watchlist watchlist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Default constructor
    public WatchlistItem() {}
    
    // Constructor with required fields
    public WatchlistItem(Watchlist watchlist, Stock stock) {
        this.watchlist = watchlist;
        this.stock = stock;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Watchlist getWatchlist() {
        return watchlist;
    }
    
    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }
    
    public Stock getStock() {
        return stock;
    }
    
    public void setStock(Stock stock) {
        this.stock = stock;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
