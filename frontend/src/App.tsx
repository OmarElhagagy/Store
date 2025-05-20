import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import store from './features/store';
import ThemeProvider from './theme/ThemeProvider';
import Layout from './components/layout/Layout';
import Login from './components/auth/Login';
import ProductList from './components/product/ProductList';
import CartPage from './components/cart/CartPage';

function App() {
  return (
    <Provider store={store}>
      <ThemeProvider>
        <Router>
          <Layout>
            <Routes>
              <Route path="/" element={<ProductList />} />
              <Route path="/products" element={<ProductList />} />
              <Route path="/login" element={<Login />} />
              <Route path="/cart" element={<CartPage />} />
              {/* Add more routes as needed */}
            </Routes>
          </Layout>
        </Router>
        <ToastContainer position="bottom-right" autoClose={3000} />
      </ThemeProvider>
    </Provider>
  );
}

export default App;
