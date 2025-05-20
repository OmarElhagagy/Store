import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Container,
  Divider,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
  Alert,
  Skeleton,
} from '@mui/material';
import {
  Add as AddIcon,
  Remove as RemoveIcon,
  Delete as DeleteIcon,
  ShoppingCart,
} from '@mui/icons-material';
import {
  fetchCart,
  updateCartItem,
  removeCartItem,
  clearCart,
  selectCart,
} from '../../features/cart/cartSlice';
import { selectIsAuthenticated } from '../../features/auth/authSlice';
import { AppDispatch } from '../../features/store';

const CartPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  
  const { items, totalPrice, isLoading, error } = useSelector(selectCart);
  const isAuthenticated = useSelector(selectIsAuthenticated);
  
  useEffect(() => {
    if (isAuthenticated) {
      dispatch(fetchCart());
    }
  }, [dispatch, isAuthenticated]);

  const handleQuantityChange = (itemId: number, newQuantity: number) => {
    if (newQuantity > 0) {
      dispatch(updateCartItem({ itemId, quantity: newQuantity }));
    }
  };

  const handleRemoveItem = (itemId: number) => {
    dispatch(removeCartItem(itemId));
  };

  const handleClearCart = () => {
    dispatch(clearCart());
  };

  const handleCheckout = () => {
    navigate('/checkout');
  };

  const handleContinueShopping = () => {
    navigate('/products');
  };

  if (!isAuthenticated) {
    return (
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Alert severity="info" sx={{ mb: 4 }}>
          Please log in to view your cart.
        </Alert>
        <Button 
          variant="contained" 
          color="primary" 
          onClick={() => navigate('/login')}
        >
          Login
        </Button>
      </Container>
    );
  }

  if (isLoading) {
    return (
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Your Cart
        </Typography>
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Product</TableCell>
                <TableCell align="right">Price</TableCell>
                <TableCell align="center">Quantity</TableCell>
                <TableCell align="right">Total</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {[1, 2, 3].map((item) => (
                <TableRow key={item}>
                  <TableCell>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                      <Skeleton variant="rectangular" width={80} height={80} sx={{ mr: 2 }} />
                      <Skeleton variant="text" width={120} />
                    </Box>
                  </TableCell>
                  <TableCell align="right">
                    <Skeleton variant="text" width={60} />
                  </TableCell>
                  <TableCell align="center">
                    <Skeleton variant="rectangular" width={100} height={40} />
                  </TableCell>
                  <TableCell align="right">
                    <Skeleton variant="text" width={60} />
                  </TableCell>
                  <TableCell align="center">
                    <Skeleton variant="circular" width={40} height={40} />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Alert severity="error" sx={{ mb: 4 }}>
          {error}
        </Alert>
        <Button 
          variant="contained" 
          color="primary" 
          onClick={() => dispatch(fetchCart())}
        >
          Try Again
        </Button>
      </Container>
    );
  }

  if (items.length === 0) {
    return (
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <ShoppingCart sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h5" gutterBottom>
            Your cart is empty
          </Typography>
          <Typography variant="body1" color="text.secondary" paragraph>
            Looks like you haven't added any products to your cart yet.
          </Typography>
          <Button 
            variant="contained" 
            color="primary" 
            onClick={handleContinueShopping}
            sx={{ mt: 2 }}
          >
            Continue Shopping
          </Button>
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Your Cart
      </Typography>
      
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 4 }}>
        <Box sx={{ flexGrow: 1, flexBasis: { xs: '100%', md: '66%' }, minWidth: 0 }}>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Product</TableCell>
                  <TableCell align="right">Price</TableCell>
                  <TableCell align="center">Quantity</TableCell>
                  <TableCell align="right">Total</TableCell>
                  <TableCell align="center">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {items.map((item) => (
                  <TableRow key={item.id}>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Box
                          component="img"
                          src={
                            item.product.images && item.product.images.length > 0
                              ? item.product.images[0].url
                              : `https://via.placeholder.com/80x80?text=${encodeURIComponent(
                                  item.product.productName
                                )}`
                          }
                          alt={item.product.productName}
                          sx={{ width: 80, height: 80, mr: 2, objectFit: 'cover' }}
                        />
                        <Box>
                          <Typography variant="subtitle1">
                            {item.product.productName}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            {item.product.brand} • {item.product.color} • Size: {item.product.size}
                          </Typography>
                        </Box>
                      </Box>
                    </TableCell>
                    <TableCell align="right">
                      ${Number(item.product.price).toFixed(2)}
                    </TableCell>
                    <TableCell align="center">
                      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <IconButton
                          size="small"
                          onClick={() => handleQuantityChange(item.id, item.quantity - 1)}
                          disabled={item.quantity <= 1}
                        >
                          <RemoveIcon fontSize="small" />
                        </IconButton>
                        <TextField
                          size="small"
                          value={item.quantity}
                          onChange={(e) => {
                            const value = parseInt(e.target.value);
                            if (!isNaN(value) && value > 0) {
                              handleQuantityChange(item.id, value);
                            }
                          }}
                          InputProps={{
                            inputProps: { min: 1, style: { textAlign: 'center', width: '40px' } },
                          }}
                          variant="outlined"
                          sx={{ mx: 1 }}
                        />
                        <IconButton
                          size="small"
                          onClick={() => handleQuantityChange(item.id, item.quantity + 1)}
                        >
                          <AddIcon fontSize="small" />
                        </IconButton>
                      </Box>
                    </TableCell>
                    <TableCell align="right">
                      ${(item.quantity * Number(item.product.price)).toFixed(2)}
                    </TableCell>
                    <TableCell align="center">
                      <IconButton
                        color="error"
                        onClick={() => handleRemoveItem(item.id)}
                        size="small"
                      >
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          
          <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between' }}>
            <Button
              variant="outlined"
              onClick={handleContinueShopping}
            >
              Continue Shopping
            </Button>
            <Button
              variant="outlined"
              color="error"
              onClick={handleClearCart}
              disabled={isLoading}
            >
              Clear Cart
            </Button>
          </Box>
        </Box>
        
        <Box sx={{ flexGrow: 1, flexBasis: { xs: '100%', md: '30%' } }}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Order Summary
            </Typography>
            <Divider sx={{ my: 2 }} />
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="body1">Subtotal</Typography>
              <Typography variant="body1">${totalPrice.toFixed(2)}</Typography>
            </Box>
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="body1">Shipping</Typography>
              <Typography variant="body1">Free</Typography>
            </Box>
            
            <Divider sx={{ my: 2 }} />
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
              <Typography variant="h6">Total</Typography>
              <Typography variant="h6">${totalPrice.toFixed(2)}</Typography>
            </Box>
            
            <Button
              variant="contained"
              color="primary"
              size="large"
              fullWidth
              onClick={handleCheckout}
              disabled={isLoading}
            >
              Proceed to Checkout
            </Button>
          </Paper>
        </Box>
      </Box>
    </Container>
  );
};

export default CartPage; 