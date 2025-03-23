import api from './api';
import { Order, OrderStatus } from '../types';
import { API_ENDPOINTS } from '../config/api';

export const orderService = {
    async getOrders(): Promise<Order[]> {
        const response = await api.get<Order[]>('/orders');
        return response.data;
    },

    async getOrder(id: number): Promise<Order> {
        const response = await api.get<Order>(`/orders/${id}`);
        return response.data;
    },

    async updateOrderStatus(id: number, status: OrderStatus): Promise<Order> {
        switch (status) {
            case OrderStatus.COMPLETED:
                return this.completeOrder(id);
            case OrderStatus.CANCELLED:
                return this.cancelOrder(id);
            default:
                throw new Error('Nepodporovaný status');
        }
    },

    async completeOrder(id: number): Promise<Order> {
        const response = await api.post<Order>(`/orders/${id}/complete`);
        return response.data;
    },

    async cancelOrder(id: number): Promise<Order> {
        const response = await api.post<Order>(`/orders/${id}/cancel`);
        return response.data;
    }
}; 