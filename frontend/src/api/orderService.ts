import axiosInstance from './axiosConfig';
import { Product } from './productService';

export interface Address {
  id?: number;
  streetAddress: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  isDefault?: boolean;
}

export interface OrderItem {
  id?: number;
  product: Product;
  quantity: number;
  price: number;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
  RETURNED = 'RETURNED'
}

export interface Order {
  id?: number;
  orderNumber?: string;
  orderDate?: string;
  orderStatus: OrderStatus;
  totalAmount: number;
  shippingAddress: Address;
  paymentMethod: string;
  orderItems: OrderItem[];
}

export const orderService = {
  async createOrder(orderData: Omit<Order, 'id' | 'orderNumber' | 'orderDate'>): Promise<Order> {
    const response = await axiosInstance.post('/orders', orderData);
    return response.data;
  },

  async getOrders(): Promise<Order[]> {
    const response = await axiosInstance.post('/orders');
    return response.data;
  },

  async getOrderById(id: number): Promise<Order> {
    const response = await axiosInstance.get(`/orders/${id}`);
    return response.data;
  },
  
  async cancelOrder(id: number): Promise<Order> {
    const response = await axiosInstance.put(`/orders/${id}/cancel`);
    return response.data;
  }
};

export default orderService; 