import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { stocksApi } from '../api/stocks';

function StockDetail() {
  const { symbol } = useParams();
  const [stock, setStock] = useState(null);
  const [priceHistory, setPriceHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchStockData = async () => {
      try {
        setLoading(true);
        
        // Fetch stock details and price history
        const [stockData, historyData] = await Promise.all([
          stocksApi.getStock(symbol),
          stocksApi.getPriceHistory(symbol)
        ]);
        
        setStock(stockData);
        setPriceHistory(historyData);
        setError(null);
      } catch (err) {
        setError('Failed to load stock data. Please try again.');
        console.error('Error fetching stock data:', err);
      } finally {
        setLoading(false);
      }
    };

    if (symbol) {
      fetchStockData();
    }
  }, [symbol]);

  if (loading) {
    return (
      <div className="stock-detail">
        <div className="loading">Loading stock data...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="stock-detail">
        <div className="error">
          <h2>Error</h2>
          <p>{error}</p>
          <Link to="/" className="back-link">← Back to Stocks List</Link>
        </div>
      </div>
    );
  }

  if (!stock) {
    return (
      <div className="stock-detail">
        <div className="not-found">
          <h2>Stock not found</h2>
          <p>The stock symbol '{symbol}' was not found.</p>
          <Link to="/" className="back-link">← Back to Stocks List</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="stock-detail">
      <div className="stock-header">
        <Link to="/" className="back-link">← Back to Stocks List</Link>
        <h1>{stock.symbol} - {stock.companyName}</h1>
      </div>

      <div className="stock-info">
        <div className="price-section">
          <div className="current-price">
            <span className="price">${stock.currentPrice?.toFixed(2)}</span>
            <span className={`change ${stock.priceChange >= 0 ? 'positive' : 'negative'}`}>
              {stock.priceChange >= 0 ? '+' : ''}{stock.priceChange?.toFixed(2)} 
              ({stock.priceChangePercentage >= 0 ? '+' : ''}{stock.priceChangePercentage?.toFixed(2)}%)
            </span>
          </div>
        </div>

        <div className="stock-details">
          <div className="detail-row">
            <span className="label">Sector:</span>
            <span className="value">{stock.sector}</span>
          </div>
          <div className="detail-row">
            <span className="label">Industry:</span>
            <span className="value">{stock.industry}</span>
          </div>
          <div className="detail-row">
            <span className="label">Market Cap:</span>
            <span className="value">{stock.marketCap ? `$${(stock.marketCap / 1e9).toFixed(2)}B` : 'N/A'}</span>
          </div>
          <div className="detail-row">
            <span className="label">P/E Ratio:</span>
            <span className="value">{stock.peRatio ? stock.peRatio.toFixed(2) : 'N/A'}</span>
          </div>
          <div className="detail-row">
            <span className="label">52 Week High:</span>
            <span className="value">${stock.weekHigh52?.toFixed(2)}</span>
          </div>
          <div className="detail-row">
            <span className="label">52 Week Low:</span>
            <span className="value">${stock.weekLow52?.toFixed(2)}</span>
          </div>
          <div className="detail-row">
            <span className="label">Volume:</span>
            <span className="value">{stock.volume?.toLocaleString()}</span>
          </div>
          <div className="detail-row">
            <span className="label">Average Volume:</span>
            <span className="value">{stock.averageVolume?.toLocaleString()}</span>
          </div>
        </div>

        {priceHistory.length > 0 && (
          <div className="price-history">
            <h3>Recent Price History</h3>
            <div className="history-list">
              {priceHistory.slice(0, 10).map((entry, index) => (
                <div key={index} className="history-item">
                  <span className="date">{new Date(entry.date).toLocaleDateString()}</span>
                  <span className="price">${entry.closingPrice?.toFixed(2)}</span>
                  <span className="volume">{entry.volume?.toLocaleString()}</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default StockDetail;
