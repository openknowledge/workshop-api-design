import { Routes, Route } from 'react-router-dom'
import './App.css'
import CustomerList from './pages/CustomerList'
import CustomerDetail from './pages/CustomerDetail'
import CreateCustomer from './pages/CreateCustomer'

function App() {
  return (
    <div className="app">
      <header className="app-header">
        <h1>Kundenpflege</h1>
      </header>
      <main className="app-main">
        <Routes>
          <Route path="/" element={<CustomerList />} />
          <Route path="/customers/new" element={<CreateCustomer />} />
          <Route path="/customers/:customerNumber" element={<CustomerDetail />} />
        </Routes>
      </main>
    </div>
  )
}

export default App
