import axiosInstance from './axiosConfig';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  userId: number;
  email: string;
  role: string;
}

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await axiosInstance.post('/auth/login', credentials);
    
    if (response.data) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      localStorage.setItem('user', JSON.stringify({
        id: response.data.userId,
        email: response.data.email,
        role: response.data.role,
      }));
    }
    
    return response.data;
  },

  async register(userData: RegisterRequest): Promise<any> {
    const response = await axiosInstance.post('/auth/register', userData);
    return response.data;
  },

  async refreshToken(refreshToken: string): Promise<{ token: string, refreshToken: string }> {
    const response = await axiosInstance.post('/auth/refresh', { refreshToken });
    
    if (response.data) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('refreshToken', response.data.refreshToken);
    }
    
    return response.data;
  },

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    
    // Call the backend logout endpoint if needed
    axiosInstance.post('/auth/logout').catch(() => {
      // Silent catch - we're logging out anyway
    });
  },

  getCurrentUser(): { id: number, email: string, role: string } | null {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  },

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
};

export default authService; 