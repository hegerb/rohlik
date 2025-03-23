export const API_BASE_URL = 'http://localhost:8080/api';

export const API_ENDPOINTS = {
    products: '/products',
    orders: '/orders',
    login: '/auth/login',
} as const;

export const API_CONFIG = {
    headers: {
        'Content-Type': 'application/json',
    },
} as const; 