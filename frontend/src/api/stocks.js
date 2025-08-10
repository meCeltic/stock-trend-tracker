import apiClient from './client.js';

// Stocks API functions
export const stocksApi = {
  // Get all stocks with pagination
  getStocks: (params = {}) => {
    const queryParams = new URLSearchParams({
      page: params.page || 0,
      size: params.size || 20,
      sort: params.sort || 'symbol',
      ...params
    });
    return apiClient.get(`/stocks?${queryParams}`);
  },

  // Get stock by ID
  getStockById: (id) => {
    return apiClient.get(`/stocks/${id}`);
  },

  // Get stock by symbol
  getStockBySymbol: (symbol) => {
    return apiClient.get(`/stocks/symbol/${symbol}`);
  },

  // Search stocks
  searchStocks: (query, params = {}) => {
    const queryParams = new URLSearchParams({
      q: query,
      page: params.page || 0,
      size: params.size || 20,
      ...params
    });
    return apiClient.get(`/stocks/search?${queryParams}`);
  },

  // Get stock price history
  getStockHistory: (stockId, params = {}) => {
    const queryParams = new URLSearchParams({
      period: params.period || '1M',
      interval: params.interval || 'DAILY',
      ...params
    });
    return apiClient.get(`/stocks/${stockId}/history?${queryParams}`);
  },

  // Get trending stocks
  getTrendingStocks: (params = {}) => {
    const queryParams = new URLSearchParams({
      limit: params.limit || 10,
      period: params.period || '1D',
      ...params
    });
    return apiClient.get(`/stocks/trending?${queryParams}`);
  },

  // Get stock analytics
  getStockAnalytics: (stockId) => {
    return apiClient.get(`/stocks/${stockId}/analytics`);
  },

  // Add stock to watchlist
  addToWatchlist: (stockId, watchlistId) => {
    return apiClient.post(`/stocks/${stockId}/watchlist/${watchlistId}`);
  },

  // Remove stock from watchlist
  removeFromWatchlist: (stockId, watchlistId) => {
    return apiClient.delete(`/stocks/${stockId}/watchlist/${watchlistId}`);
  }
};
