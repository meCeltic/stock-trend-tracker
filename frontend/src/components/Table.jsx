import React from 'react';

const Table = ({ 
  columns = [],
  data = [],
  loading = false,
  onRowClick = null,
  emptyMessage = 'No data available'
}) => {
  // Coerce data to array to prevent crashes from undefined or non-array data
  const rows = Array.isArray(data) ? data : [];
  
  if (loading) {
    return (
      <div className="w-full p-8 text-center">
        <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <p className="mt-2 text-gray-500">Loading...</p>
      </div>
    );
  }

  if (!rows || rows.length === 0) {
    return (
      <div className="w-full p-8 text-center">
        <p className="text-gray-500">{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full bg-white">
        <thead className="bg-gray-50">
          <tr>
            {columns.map((column, index) => (
              <th
                key={column.key || index}
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                {column.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {rows.map((row, rowIndex) => (
            <tr
              key={row.id || rowIndex}
              onClick={onRowClick ? () => onRowClick(row) : undefined}
              className={`
                ${onRowClick ? 'cursor-pointer hover:bg-gray-50 transition-colors' : ''}
              `}
            >
              {columns.map((column, colIndex) => (
                <td
                  key={`${row.id || rowIndex}-${column.key || colIndex}`}
                  className="px-6 py-4 whitespace-nowrap text-sm"
                >
                  {column.render
                    ? column.render(row[column.key], row, rowIndex)
                    : row[column.key] || ''
                  }
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Table;
