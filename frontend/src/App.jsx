import { Routes, Route, Link } from 'react-router-dom';
import StocksList from './pages/StocksList';
import StockDetail from './pages/StockDetail';
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <div className="header-content">
          <Link to="/" className="logo">
            <h1>Stock Trend Tracker</h1>
          </Link>
          <nav className="main-nav">
            <Link to="/" className="nav-link">Stocks</Link>
          </nav>
        </div>
      </header>
      
      <main className="main-content">
        <Routes>
          <Route path="/" element={<StocksList />} />
          <Route path="/stock/:symbol" element={<StockDetail />} />
        </Routes>
      </main>
      
      <footer className="App-footer">
        <p>&copy; 2025 Stock Trend Tracker. Built with React & Vite.</p>
      </footer>
    </div>
  );
}

export default App;
