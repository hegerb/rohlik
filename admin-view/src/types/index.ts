export interface Product {
    id: number;
    name: string;
    price: number;
    stockQuantity: number;
    version: number;
}

export interface OrderItem {
    id: number;
    productId: number;
    productName: string;
    quantity: number;
    price: number;
}

export enum OrderStatus {
    PENDING = 'PENDING',
    COMPLETED = 'COMPLETED',
    CANCELLED = 'CANCELLED'
}

export interface Order {
    id: number;
    createdAt: string;
    expiresAt?: string;
    status: OrderStatus;
    items: OrderItem[];
    version: number;
    expired: boolean;
}

export interface LoginCredentials {
    email: string;
    password: string;
}

export interface User {
    id: number;
    username: string;
} 