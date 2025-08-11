import axios from 'axios';
import { LoginRequest, RegisterRequest, AuthResponse, RegisterResponse } from '../types/auth';

const API_URL = 'http://localhost:8080/api';

const axiosInstance = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor dodający token do requestów
axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export const authService = {
    async login(credentials: LoginRequest): Promise<AuthResponse> {
        const response = await axiosInstance.post('/auth/token', credentials);
        return response.data;
    },

    async register(userData: RegisterRequest): Promise<RegisterResponse> {
        const response = await axiosInstance.post('/auth/register', userData);
        return response.data;
    },

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }
};

export default axiosInstance;