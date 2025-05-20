import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import {
  Box,
  Container,
  Link,
  Typography,
  IconButton,
  Divider,
  Stack,
} from '@mui/material';
import {
  Facebook,
  Twitter,
  Instagram,
  Pinterest,
  Email,
  Phone,
  LocationOn,
} from '@mui/icons-material';

const Footer: React.FC = () => {
  return (
    <Box
      sx={{
        backgroundColor: 'text.secondary',
        color: 'white',
        py: 6,
        mt: 'auto',
      }}
    >
      <Container maxWidth="lg">
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 4 }}>
          {/* Company Information */}
          <Box sx={{ flex: '1 1 300px', minWidth: { xs: '100%', sm: 'auto' } }}>
            <Typography variant="h6" gutterBottom>
              CLOTHING STORE
            </Typography>
            <Typography variant="body2" paragraph>
              Your one-stop destination for trendy and quality apparel.
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <LocationOn fontSize="small" sx={{ mr: 1 }} />
              <Typography variant="body2">
                123 Fashion Street, Style City, SC 12345
              </Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <Phone fontSize="small" sx={{ mr: 1 }} />
              <Typography variant="body2">+1 (555) 123-4567</Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
              <Email fontSize="small" sx={{ mr: 1 }} />
              <Typography variant="body2">info@clothingstore.com</Typography>
            </Box>
          </Box>

          {/* Quick Links */}
          <Box sx={{ flex: '1 1 300px', minWidth: { xs: '100%', sm: 'auto' } }}>
            <Typography variant="h6" gutterBottom>
              Quick Links
            </Typography>
            <Link
              component={RouterLink}
              to="/"
              color="inherit"
              underline="hover"
              sx={{ mb: 1, display: 'block' }}
            >
              Home
            </Link>
            <Link
              component={RouterLink}
              to="/products"
              color="inherit"
              underline="hover"
              sx={{ mb: 1, display: 'block' }}
            >
              Products
            </Link>
            <Link
              component={RouterLink}
              to="/categories"
              color="inherit"
              underline="hover"
              sx={{ mb: 1, display: 'block' }}
            >
              Categories
            </Link>
            <Link
              component={RouterLink}
              to="/about"
              color="inherit"
              underline="hover"
              sx={{ mb: 1, display: 'block' }}
            >
              About Us
            </Link>
            <Link
              component={RouterLink}
              to="/contact"
              color="inherit"
              underline="hover"
              sx={{ mb: 1, display: 'block' }}
            >
              Contact
            </Link>
          </Box>

          {/* Customer Service */}
          <Box sx={{ flex: '1 1 300px', minWidth: { xs: '100%', sm: 'auto' } }}>
            <Typography variant="h6" gutterBottom>
              Customer Service
            </Typography>
            <Link
              component={RouterLink}
              to="/faq"
              color="inherit"
              underline="hover"
              sx={{ mb: 1, display: 'block' }}
            >
              FAQs
            </Link>
            <Link
              component={RouterLink}
              to="/shipping"
              color="inherit"
              underline="hover"
              sx={{ mb: 1, display: 'block' }}
            >
              Shipping & Returns
            </Link>
            <Link
              component={RouterLink}
              to="/terms"
              color="inherit"
              underline="hover"
              sx={{ mb: 1, display: 'block' }}
            >
              Terms & Conditions
            </Link>
            <Link
              component={RouterLink}
              to="/privacy"
              color="inherit"
              underline="hover"
              sx={{ mb: 1, display: 'block' }}
            >
              Privacy Policy
            </Link>
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1" gutterBottom>
                Follow Us
              </Typography>
              <Stack direction="row" spacing={1}>
                <IconButton color="inherit" aria-label="Facebook">
                  <Facebook />
                </IconButton>
                <IconButton color="inherit" aria-label="Twitter">
                  <Twitter />
                </IconButton>
                <IconButton color="inherit" aria-label="Instagram">
                  <Instagram />
                </IconButton>
                <IconButton color="inherit" aria-label="Pinterest">
                  <Pinterest />
                </IconButton>
              </Stack>
            </Box>
          </Box>
        </Box>

        <Divider sx={{ my: 3, backgroundColor: 'rgba(255,255,255,0.2)' }} />

        <Typography variant="body2" align="center" sx={{ pt: 2 }}>
          © {new Date().getFullYear()} Clothing Store. All rights reserved.
        </Typography>
      </Container>
    </Box>
  );
};

export default Footer; 