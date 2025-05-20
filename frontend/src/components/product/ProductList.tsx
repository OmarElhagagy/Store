import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Container,
  Card,
  CardContent,
  CardMedia,
  CardActionArea,
  Button,
  Pagination,
  Skeleton,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Stack,
  IconButton,
  Divider,
  SelectChangeEvent,
} from '@mui/material';
import { ShoppingCart, Favorite } from '@mui/icons-material';
import { 
  fetchProducts, 
  selectProducts, 
  selectProductLoading, 
  selectProductError, 
  selectProductPagination
} from '../../features/product/productSlice';
import { addToCart } from '../../features/cart/cartSlice';
import { AppDispatch } from '../../features/store';
import { Product } from '../../api/productService';

const ProductList: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  
  const products = useSelector(selectProducts);
  const loading = useSelector(selectProductLoading);
  const error = useSelector(selectProductError);
  const { totalPages, currentPage } = useSelector(selectProductPagination);

  const [sortBy, setSortBy] = useState('newest');
  
  useEffect(() => {
    let sort = 'id,desc';
    
    switch (sortBy) {
      case 'newest':
        sort = 'launchDate,desc';
        break;
      case 'price-asc':
        sort = 'price,asc';
        break;
      case 'price-desc':
        sort = 'price,desc';
        break;
      case 'name-asc':
        sort = 'productName,asc';
        break;
      default:
        sort = 'id,desc';
    }
    
    dispatch(fetchProducts({ page: currentPage, sort }));
  }, [dispatch, currentPage, sortBy]);

  const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
    dispatch(fetchProducts({ page: value - 1 }));
  };

  const handleSortChange = (event: SelectChangeEvent) => {
    setSortBy(event.target.value);
  };

  const handleAddToCart = (product: Product) => {
    dispatch(addToCart({ productId: product.id, quantity: 1 }));
  };

  const handleProductClick = (productId: number) => {
    navigate(`/products/${productId}`);
  };

  // Handle loading state
  if (loading && products.length === 0) {
    return (
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Products
        </Typography>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', margin: -2 }}>
          {Array.from(new Array(8)).map((_, index) => (
            <Box key={index} sx={{ 
              width: { xs: '100%', sm: '50%', md: '33.333%', lg: '25%' }, 
              padding: 2 
            }}>
              <Card>
                <Skeleton variant="rectangular" height={140} />
                <CardContent>
                  <Skeleton height={40} />
                  <Skeleton height={20} width="60%" />
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
                    <Skeleton height={40} width="40%" />
                    <Skeleton height={40} width="40%" />
                  </Box>
                </CardContent>
              </Card>
            </Box>
          ))}
        </Box>
      </Container>
    );
  }

  // Handle error state
  if (error && !loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Typography variant="h4" component="h1" gutterBottom color="error">
          Error loading products
        </Typography>
        <Typography color="text.secondary">{error}</Typography>
        <Button 
          variant="contained" 
          onClick={() => dispatch(fetchProducts({}))}
          sx={{ mt: 2 }}
        >
          Try Again
        </Button>
      </Container>
    );
  }
  
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Products
      </Typography>
      
      {/* Sort control */}
      <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 3 }}>
        <FormControl sx={{ minWidth: 200 }}>
          <InputLabel id="sort-select-label">Sort by</InputLabel>
          <Select
            labelId="sort-select-label"
            id="sort-select"
            value={sortBy}
            label="Sort by"
            onChange={handleSortChange}
          >
            <MenuItem value="newest">Newest Arrivals</MenuItem>
            <MenuItem value="price-asc">Price: Low to High</MenuItem>
            <MenuItem value="price-desc">Price: High to Low</MenuItem>
            <MenuItem value="name-asc">Name: A-Z</MenuItem>
          </Select>
        </FormControl>
      </Box>
      
      {/* Products grid */}
      <Box sx={{ display: 'flex', flexWrap: 'wrap', mx: -2 }}>
        {products.map((product) => (
          <Box key={product.id} sx={{ width: { xs: '100%', sm: '50%', md: '33.333%', lg: '25%' }, p: 2 }}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardActionArea onClick={() => handleProductClick(product.id)}>
                <CardMedia
                  component="img"
                  height="200"
                  image={product.images && product.images.length > 0 
                    ? product.images.find(img => img.isPrimary)?.url || product.images[0].url
                    : `https://via.placeholder.com/300x200?text=${encodeURIComponent(product.productName)}`
                  }
                  alt={product.productName}
                />
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography gutterBottom variant="h6" component="h2" noWrap>
                    {product.productName}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" paragraph sx={{ mb: 1 }}>
                    {product.brand} • {product.color} • Size: {product.size}
                  </Typography>
                  <Typography variant="h6" color="primary" sx={{ fontWeight: 'bold', mb: 1 }}>
                    ${Number(product.price).toFixed(2)}
                  </Typography>
                </CardContent>
              </CardActionArea>
              
              <Divider />
              
              <Stack direction="row" spacing={1} sx={{ p: 1, justifyContent: 'space-between' }}>
                <Button 
                  variant="contained" 
                  size="small"
                  startIcon={<ShoppingCart />}
                  onClick={() => handleAddToCart(product)}
                  sx={{ flexGrow: 1 }}
                >
                  Add to Cart
                </Button>
                <IconButton size="small" color="primary" aria-label="add to wishlist">
                  <Favorite />
                </IconButton>
              </Stack>
            </Card>
          </Box>
        ))}
      </Box>
      
      {/* Pagination */}
      {totalPages > 1 && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <Pagination 
            count={totalPages} 
            page={currentPage + 1} 
            onChange={handlePageChange} 
            color="primary" 
          />
        </Box>
      )}
    </Container>
  );
};

export default ProductList; 