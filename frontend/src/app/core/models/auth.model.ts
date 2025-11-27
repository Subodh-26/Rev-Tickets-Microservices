export interface User {
  id: number;
  email: string;
  name: string;
  phone: string;
  role: 'USER' | 'ADMIN';
}

export interface AuthResponse {
  success: boolean;
  message: string;
  data: {
    token: string;
    user: User;
  };
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  phone: string;
}
