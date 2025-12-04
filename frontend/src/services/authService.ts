import axios from 'axios';
import { LoginRequest, RegisterRequest, AuthResponse, RegisterResponse } from '../types/auth';

const API_URL = '';

const axiosInstance = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export const authService = {
    async login(credentials: LoginRequest): Promise<AuthResponse> {
        // Teraz ścieżka jest relatywna
        const response = await axiosInstance.post('/api/auth/token', credentials);

        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            if (response.data.user) {
                localStorage.setItem('user', JSON.stringify(response.data.user));
            }
        }

        return response.data;
    },

    async register(userData: RegisterRequest): Promise<RegisterResponse> {
        const response = await axiosInstance.post('/api/auth/register', userData);
        return response.data;
    },

    logout(): void {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    getCurrentUser(): any {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    },

    getToken(): string | null {
        return localStorage.getItem('token');
    },

    isAuthenticated(): boolean {
        const token = this.getToken();
        return !!token;
    }
};

export default axiosInstance;