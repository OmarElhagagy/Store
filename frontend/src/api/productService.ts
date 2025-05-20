import axiosInstance from './axiosConfig';

export interface Product {
  id: number;
  productName: string;
  size: string;
  brand: string;
  price: number;
  color: string;
  launchDate: string;
  description: string;
  isActive: boolean;
  images?: Array<{
    id: number;
    url: string;
    isPrimary: boolean;
  }>;
}

export interface ProductFilter {
  categoryId?: number;
  brand?: string;
  color?: string;
  sizeFilter?: string;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  pageSize?: number;
  sort?: string;
}

export const productService = {
  async getProducts(page = 0, size = 10, sort = 'id,asc'): Promise<{
    content: Product[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
  }> {
    const response = await axiosInstance.get('/products', {
      params: {
        page,
        size,
        sort,
      },
    });
    return response.data;
  },

  async getProductById(id: number): Promise<Product> {
    const response = await axiosInstance.get(`/products/${id}`);
    return response.data;
  },

  async getProductsByCategory(categoryId: number): Promise<Product[]> {
    const response = await axiosInstance.get(`/products/category/${categoryId}`);
    return response.data;
  },

  async searchProducts(query: string): Promise<Product[]> {
    const response = await axiosInstance.get('/products/search', {
      params: { query },
    });
    return response.data;
  },

  async createProduct(product: Omit<Product, 'id'>): Promise<Product> {
    const response = await axiosInstance.post('/products', product);
    return response.data;
  },

  async updateProduct(id: number, product: Partial<Product>): Promise<Product> {
    const response = await axiosInstance.put(`/products/${id}`, product);
    return response.data;
  },

  async deleteProduct(id: number): Promise<void> {
    await axiosInstance.delete(`/products/${id}`);
  },
};

export default productService; 