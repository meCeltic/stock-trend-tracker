import React from 'react';

const Pagination = ({
  currentPage = 0,
  totalPages = 0,
  onPageChange,
  pageSize = 20,
  onPageSizeChange,
  totalElements = 0
}) => {
  // Calculate page numbers to display
  const getPageNumbers = () => {
    const delta = 2; // Number of pages to show on each side of current page
    const range = [];
    const rangeWithDots = [];

    for (
      let i = Math.max(2, currentPage - delta);
      i <= Math.min(totalPages - 1, currentPage + delta);
      i++
    ) {
      range.push(i);
    }

    if (currentPage - delta > 2) {
      rangeWithDots.push(1, '...');
    } else {
      rangeWithDots.push(1);
    }

    rangeWithDots.push(...range);

    if (currentPage + delta < totalPages - 1) {
      rangeWithDots.push('...', totalPages);
    } else {
      rangeWithDots.push(totalPages);
    }

    return rangeWithDots;
  };

  const handlePageChange = (page) => {
    if (page !== currentPage && page >= 0 && page < totalPages) {
      onPageChange(page);
    }
  };

  const handlePageSizeChange = (newSize) => {
    if (onPageSizeChange) {
      onPageSizeChange(newSize);
    }
  };

  if (totalPages <= 1) return null;

  const startItem = currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalElements);

  return (
    <div className="flex flex-col sm:flex-row justify-between items-center space-y-3 sm:space-y-0">
      {/* Results info */}
      <div className="text-sm text-gray-700">
        Showing <span className="font-medium">{startItem}</span> to{' '}
        <span className="font-medium">{endItem}</span> of{' '}
        <span className="font-medium">{totalElements}</span> results
      </div>

      <div className="flex items-center space-x-4">
        {/* Page size selector */}
        <div className="flex items-center space-x-2">
          <label className="text-sm text-gray-700">Show:</label>
          <select
            value={pageSize}
            onChange={(e) => handlePageSizeChange(Number(e.target.value))}
            className="border border-gray-300 rounded px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value={10}>10</option>
            <option value={20}>20</option>
            <option value={50}>50</option>
            <option value={100}>100</option>
          </select>
        </div>

        {/* Pagination buttons */}
        <nav aria-label="Pagination">
          <ul className="flex items-center space-x-1">
            {/* Previous button */}
            <li>
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className={`
                  px-3 py-2 text-sm font-medium rounded-md transition-colors
                  ${
                    currentPage === 0
                      ? 'text-gray-400 cursor-not-allowed'
                      : 'text-gray-600 hover:text-blue-600 hover:bg-gray-50'
                  }
                `}
              >
                Previous
              </button>
            </li>

            {/* Page numbers */}
            {getPageNumbers().map((pageNum, index) => {
              if (pageNum === '...') {
                return (
                  <li key={`dots-${index}`}>
                    <span className="px-3 py-2 text-sm text-gray-500">...</span>
                  </li>
                );
              }

              const page = pageNum - 1; // Convert to 0-based index
              const isCurrentPage = page === currentPage;

              return (
                <li key={pageNum}>
                  <button
                    onClick={() => handlePageChange(page)}
                    className={`
                      px-3 py-2 text-sm font-medium rounded-md transition-colors
                      ${
                        isCurrentPage
                          ? 'bg-blue-600 text-white'
                          : 'text-gray-600 hover:text-blue-600 hover:bg-gray-50'
                      }
                    `}
                  >
                    {pageNum}
                  </button>
                </li>
              );
            })}

            {/* Next button */}
            <li>
              <button
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage >= totalPages - 1}
                className={`
                  px-3 py-2 text-sm font-medium rounded-md transition-colors
                  ${
                    currentPage >= totalPages - 1
                      ? 'text-gray-400 cursor-not-allowed'
                      : 'text-gray-600 hover:text-blue-600 hover:bg-gray-50'
                  }
                `}
              >
                Next
              </button>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  );
};

export default Pagination;
