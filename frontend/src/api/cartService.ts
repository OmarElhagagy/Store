import axiosInstance from './axiosConfig';
import { Product } from './productService';

export interface CartItem {
  id: number;
  product: Product;
  quantity: number;
}

export interface Cart {
  id: number;
  cartItems: CartItem[];
  totalPrice: number;
}

export const cartService = {
  async getCart(): Promise<Cart> {
    const response = await axiosInstance.get('/cart');
    return response.data;
  },

  async addToCart(productId: number, quantity = 1): Promise<Cart> {
    const response = await axiosInstance.post('/cart', {
      productId,
      quantity,
    });
    return response.data;
  },

  async updateCartItem(itemId: number, quantity: number): Promise<Cart> {
    const response = await axiosInstance.put(`/cart/${itemId}`, {
      quantity,
    });
    return response.data;
  },

  async removeCartItem(itemId: number): Promise<void> {
    await axiosInstance.delete(`/cart/${itemId}`);
  },

  async clearCart(): Promise<void> {
    await axiosInstance.delete('/cart');
  },
};

export default cartService; 