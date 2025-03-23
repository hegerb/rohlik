import api from './api';
import { Product } from '../types';

export const productService = {
    async getProducts(): Promise<Product[]> {
        const response = await api.get<Product[]>('/products');
        return response.data;
    },

    async getProduct(id: number): Promise<Product> {
        const response = await api.get<Product>(`/products/${id}`);
        return response.data;
    },

    async createProduct(product: Omit<Product, 'id' | 'version'>): Promise<Product> {
        const response = await api.post<Product>('/products', product);
        return response.data;
    },

    async updateProduct(id: number, product: Omit<Product, 'id'> & { version: number }): Promise<Product> {
        const response = await api.put<Product>(`/products/${id}`, product);
        return response.data;
    },

    async deleteProduct(id: number): Promise<void> {
        await api.delete(`/products/${id}`);
    }
}; 