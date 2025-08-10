import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { stocksApi } from '../api/stocks';
import Table from '../components/Table';
import Pagination from '../components/Pagination';
import { toast } from 'react-hot-toast';

const StocksList = () => {
  const navigate = useNavigate();
  const [stocks, setStocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedExchange, setSelectedExchange] = useState('');
  const [exchanges, setExchanges] = useState([]);

  useEffect(() => {
    fetchStocks();
    fetchExchanges();
  }, [page, size, searchQuery, selectedExchange]);

  const fetchStocks = async () => {
    try {
      setLoading(true);
      let response;
      
      if (searchQuery) {
        response = await stocksApi.searchStocks(searchQuery, page, size);
      } else if (selectedExchange) {
        response = await stocksApi.getStocksByExchange(selectedExchange, page, size);
      } else {
        response = await stocksApi.getStocks(page, size);
      }
      
      // Normalize API response - handle different response structures
      const stocksData = response?.content || response?.data || response || [];
      setStocks(Array.isArray(stocksData) ? stocksData : []);
      
      // Safely set pagination data with fallbacks
      setTotalPages(response?.totalPages || Math.ceil((response?.totalElements || 0) / size) || 0);
      setTotalElements(response?.totalElements || response?.total || (Array.isArray(stocksData) ? stocksData.length : 0) || 0);
    } catch (error) {
      console.error('Error fetching stocks:', error);
      toast.error('Failed to fetch stocks');
    } finally {
      setLoading(false);
    }
  };

  const fetchExchanges = async () => {
    try {
      const response = await stocksApi.getExchanges();
      // Normalize exchanges response to ensure it's always an array
      const exchangesData = response?.data || response?.content || response || [];
      setExchanges(Array.isArray(exchangesData) ? exchangesData : []);
    } catch (error) {
      console.error('Error fetching exchanges:', error);
    }
  };

  const handleRowClick = (stock) => {
    // Guard against undefined stock or missing id
    if (stock?.id) {
      navigate(`/stocks/${stock.id}`);
    }
  };

  const handleSearch = (e) => {
    setSearchQuery(e.target.value);
    setPage(0); // Reset to first page
  };

  const handleExchangeChange = (e) => {
    setSelectedExchange(e.target.value);
    setPage(0); // Reset to first page
  };

  const clearFilters = () => {
    setSearchQuery('');
    setSelectedExchange('');
    setPage(0);
  };

  const columns = [
    {
      key: 'symbol',
      label: 'Symbol',
      render: (value, row) => (
        <span className="font-semibold text-blue-600">{value}</span>
      )
    },
    {
      key: 'name',
      label: 'Company Name',
      render: (value) => (
        <span className="text-gray-800">{value}</span>
      )
    },
    {
      key: 'exchange',
      label: 'Exchange',
      render: (value) => (
        <span className="px-2 py-1 bg-gray-100 rounded text-sm">{value}</span>
      )
    },
    {
      key: 'sector',
      label: 'Sector',
      render: (value) => value || 'N/A'
    },
    {
      key: 'marketCap',
      label: 'Market Cap',
      render: (value) => {
        if (!value) return 'N/A';
        const billions = value / 1000000000;
        return billions >= 1 ? `$${billions.toFixed(2)}B` : `$${(value / 1000000).toFixed(2)}M`;
      }
    },
    {
      key: 'lastPrice',
      label: 'Last Price',
      render: (value) => value ? `$${value.toFixed(2)}` : 'N/A'
    }
  ];

  return (
    <div className="container mx-auto px-4 py-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Stocks List</h1>
        <button
          onClick={() => navigate('/stocks/add')}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-colors"
        >
          Add Stock
        </button>
      </div>

      {/* Filters */}
      <div className="bg-white p-4 rounded-lg shadow mb-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Search Stocks
            </label>
            <input
              type="text"
              placeholder="Search by symbol or name..."
              value={searchQuery}
              onChange={handleSearch}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Filter by Exchange
            </label>
            <select
              value={selectedExchange}
              onChange={handleExchangeChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Exchanges</option>
              {exchanges.map((exchange) => (
                <option key={exchange} value={exchange}>
                  {exchange}
                </option>
              ))}
            </select>
          </div>
          <div className="flex items-end">
            <button
              onClick={clearFilters}
              className="w-full bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded-md transition-colors"
            >
              Clear Filters
            </button>
          </div>
        </div>
      </div>

      {/* Results Summary */}
      <div className="mb-4">
        <p className="text-gray-600">
          Showing {stocks.length} of {totalElements} stocks
          {searchQuery && ` matching "${searchQuery}"`}
          {selectedExchange && ` from ${selectedExchange}`}
        </p>
      </div>

      {/* Stocks Table */}
      <div className="bg-white rounded-lg shadow">
        <Table
          columns={columns}
          data={stocks}
          loading={loading}
          onRowClick={handleRowClick}
          emptyMessage="No stocks found. Try adjusting your filters."
        />
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="mt-6">
          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={setPage}
            pageSize={size}
            onPageSizeChange={setSize}
            totalElements={totalElements}
          />
        </div>
      )}
    </div>
  );
};

export default StocksList;
