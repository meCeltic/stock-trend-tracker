# Stock Trend Tracker

A full-stack application for tracking stock trends with real-time price candle data visualization.

## Project Structure

```
stock-trend-tracker/
├── src/                          # Backend (Spring Boot)
│   ├── main/java/com/stock/stock_trend_tracker/
│   │   ├── domain/               # JPA Entities
│   │   ├── repository/           # Data repositories
│   │   ├── web/                  # REST Controllers
│   │   ├── jobs/                 # Scheduled tasks
│   │   └── StockTrendTrackerApplication.java
│   └── main/resources/
│       └── application.yml       # Application configuration
├── frontend/                     # React Frontend
│   ├── src/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
├── build.gradle                  # Backend dependencies
└── README.md
```

## Technologies Used

### Backend
- **Java 17** with Spring Boot 3.x
- **Spring Data JPA** for database operations
- **H2 Database** for development
- **Springdoc OpenAPI** for API documentation
- **Gradle** for build management

### Frontend
- **React 18** with Vite
- **React Router** for navigation
- **Axios** for HTTP requests
- **React Query** for state management
- **React Hook Form** for form handling
- **Recharts** for data visualization
- **React Hot Toast** for notifications

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 16+ and npm
- Git

### Backend Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/meCeltic/stock-trend-tracker.git
   cd stock-trend-tracker
   ```

2. **Run the backend:**
   ```bash
   # Using Gradle wrapper (recommended)
   ./gradlew bootRun
   
   # Or build and run JAR
   ./gradlew build
   java -jar build/libs/stock-trend-tracker-0.0.1-SNAPSHOT.jar
   ```

3. **Verify backend is running:**
   - API will be available at `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console`

### Frontend Setup

1. **Navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Run development server:**
   ```bash
   npm run dev
   ```

4. **Access the application:**
   - Frontend will be available at `http://localhost:5173`
   - API calls are proxied to backend at `http://localhost:8080`

## API Endpoints

### Stock Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/stocks` | Get all stocks |
| `GET` | `/api/stocks/{id}` | Get stock by ID |
| `GET` | `/api/stocks/symbol/{symbol}` | Get stock by symbol |
| `GET` | `/api/stocks/search?query={}` | Search stocks by symbol/name |
| `GET` | `/api/stocks/exchange/{exchange}` | Get stocks by exchange |
| `GET` | `/api/stocks/exchanges` | Get all distinct exchanges |
| `POST` | `/api/stocks` | Create new stock |
| `PUT` | `/api/stocks/{id}` | Update stock |
| `DELETE` | `/api/stocks/{id}` | Delete stock |

### Price Candle Data

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/stocks/{id}/candles` | Get paginated price candles |
| `GET` | `/api/stocks/{id}/candles?timeframe={tf}` | Filter candles by timeframe |
| `GET` | `/api/stocks/{id}/candles/latest` | Get latest price candle |
| `GET` | `/api/stocks/{id}/stats` | Get stock statistics |

### Query Parameters

- **Pagination:** `page` (default: 0), `size` (default: 20)
- **Timeframe:** `1m`, `5m`, `15m`, `1h`, `4h`, `1d` etc.

### Example API Calls

```bash
# Get all stocks
curl http://localhost:8080/api/stocks

# Search for stocks
curl "http://localhost:8080/api/stocks/search?query=AAPL"

# Get price candles with pagination
curl "http://localhost:8080/api/stocks/1/candles?page=0&size=10"

# Get 1-hour timeframe candles
curl "http://localhost:8080/api/stocks/1/candles?timeframe=1h"
```

## Frontend UI Flows

### Main Application Flow

1. **Dashboard/Home Page**
   - Displays list of tracked stocks
   - Shows real-time price updates
   - Quick search functionality

2. **Stock Search & Discovery**
   - Search stocks by symbol or name
   - Filter by exchange
   - Add new stocks to tracking list

3. **Stock Detail View**
   - Individual stock information
   - Historical price chart (using Recharts)
   - Timeframe selection (1m, 5m, 15m, 1h, 4h, 1d)
   - Price statistics and metrics

4. **Data Management**
   - Add/Edit/Delete stocks
   - Form validation using React Hook Form
   - Success/Error notifications via React Hot Toast

### Navigation Structure

```
/                    # Dashboard - Stock list overview
/stocks              # All stocks with search/filter
/stocks/:id          # Individual stock detail view
/stocks/add          # Add new stock form
/stocks/:id/edit     # Edit stock information
```

### Key Components

- **StockList:** Displays paginated list of stocks
- **StockDetail:** Shows individual stock with chart
- **StockForm:** Add/Edit stock form with validation
- **PriceChart:** Interactive price chart with timeframe selection
- **SearchBar:** Real-time stock search
- **StockCard:** Stock summary card component

## Development Features

### Backend Features
- **Scheduled Data Jobs:** Automated price data collection
- **RESTful API:** Complete CRUD operations
- **Database Integration:** JPA with H2 for development
- **API Documentation:** Auto-generated Swagger docs
- **Cross-Origin Support:** CORS enabled for frontend
- **Pagination:** Built-in pagination support
- **Error Handling:** Comprehensive error responses

### Frontend Features
- **Modern React:** Hooks, functional components
- **Client-side Routing:** React Router integration
- **State Management:** React Query for server state
- **Form Management:** React Hook Form with validation
- **Data Visualization:** Interactive charts with Recharts
- **Real-time Updates:** Polling for live data
- **Responsive Design:** Mobile-friendly UI
- **Hot Reloading:** Vite development server

## Build and Deployment

### Backend Production Build
```bash
./gradlew build
# JAR file will be in build/libs/
```

### Frontend Production Build
```bash
cd frontend
npm run build
# Built files will be in dist/
```

### Docker Deployment (Future)
```dockerfile
# Multi-stage build for both backend and frontend
# TODO: Add Dockerfile for containerization
```

## Configuration

### Backend Configuration (application.yml)
- Database connection settings
- Server port (default: 8080)
- Swagger UI configuration
- Compression and performance settings

### Frontend Configuration (vite.config.js)
- Development server settings
- API proxy configuration
- Build optimization settings

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Commit your changes (`git commit -m 'Add some amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
- Create an issue in the GitHub repository
- Check the Swagger documentation at `/swagger-ui.html`
- Review the application logs for debugging

---

**Last Updated:** August 2025
