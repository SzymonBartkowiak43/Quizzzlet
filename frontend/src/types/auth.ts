export interface User {
    id: number;
    email: string;
    userName: string;
    roles: string[];
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    name: string;
    password: string;
}

export interface AuthResponse {
    email: string;
    token: string;
}

export interface RegisterResponse {
    id: number;
    email: string;
    userName: string;
    roles: string[];
}