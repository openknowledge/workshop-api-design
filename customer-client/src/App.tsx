/*
 * Copyright 2026 open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Routes, Route } from 'react-router-dom';
import './App.css';
import CustomerList from './pages/CustomerList';
import CustomerDetail from './pages/CustomerDetail';
import CreateCustomer from './pages/CreateCustomer';

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
  );
}

export default App;
