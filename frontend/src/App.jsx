import { useState, useEffect } from 'react'
import './App.css'

function App() {
  const [message, setMessage] = useState('Loading...')

  useEffect(() => {
    // Test API connection
    fetch('/api/health')
      .then(response => response.text())
      .then(data => setMessage(data))
      .catch(error => {
        console.error('API connection failed:', error)
        setMessage('API connection failed. Make sure backend is running.')
      })
  }, [])

  return (
    <div className="App">
      <header className="App-header">
        <h1>Stock Trend Tracker</h1>
        <p>React Frontend with Vite</p>
        <div className="api-status">
          <strong>Backend Status:</strong> {message}
        </div>
      </header>
    </div>
  )
}

export default App
