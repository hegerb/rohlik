import api from './api';
import { User } from '../types';

interface AuthResponse {
    token: string;
}

interface AuthRequest {
    username: string;
    password: string;
}

interface RegisterRequest {
    username: string;
    password: string;
}

export const authService = {
    async login(credentials: AuthRequest): Promise<AuthResponse> {
        try {
            const response = await api.post<AuthResponse>('/auth/login', credentials);
            localStorage.setItem('token', response.data.token);
            return response.data;
        } catch (error) {
            console.error('Login error:', error);
            throw error;
        }
    },

    async register(data: RegisterRequest): Promise<AuthResponse> {
        try {
            const response = await api.post<AuthResponse>('/auth/register', data);
            localStorage.setItem('token', response.data.token);
            return response.data;
        } catch (error) {
            console.error('Registration error:', error);
            throw error;
        }
    },

    async getCurrentUser(): Promise<User> {
        const response = await api.get<User>('/auth/me');
        return response.data;
    },

    logout(): void {
        localStorage.removeItem('token');
    },

    isAuthenticated(): boolean {
        return !!localStorage.getItem('token');
    }
}; 