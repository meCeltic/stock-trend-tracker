package com.stock.stock_trend_tracker.repository;

import com.stock.stock_trend_tracker.domain.Watchlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    Page<Watchlist> findAllByOrderByUpdatedAtDesc(Pageable pageable);
}
