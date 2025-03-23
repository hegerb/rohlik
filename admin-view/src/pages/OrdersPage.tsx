import React, { useEffect, useState } from 'react';
import { Container, Table, Badge, Button, Form, Row, Col } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { Order, OrderStatus } from '../types';
import { orderService } from '../services/orderService';

export const OrdersPage: React.FC = () => {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [statusFilter, setStatusFilter] = useState<OrderStatus | ''>('');

    const loadOrders = async () => {
        try {
            setLoading(true);
            const data = await orderService.getOrders();
            console.log('Orders data:', data);
            setOrders(data);
        } catch (error) {
            toast.error('Nepodařilo se načíst objednávky');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadOrders();
    }, []);

    const getStatusBadgeVariant = (status: OrderStatus) => {
        switch (status) {
            case OrderStatus.PENDING:
                return 'primary';
            case OrderStatus.COMPLETED:
                return 'success';
            case OrderStatus.CANCELLED:
                return 'danger';
            default:
                return 'secondary';
        }
    };

    const getStatusText = (status: OrderStatus) => {
        switch (status) {
            case OrderStatus.PENDING:
                return 'Čeká';
            case OrderStatus.COMPLETED:
                return 'Dokončeno';
            case OrderStatus.CANCELLED:
                return 'Zrušeno';
            default:
                return status;
        }
    };

    const handleStatusChange = async (orderId: number, newStatus: OrderStatus) => {
        try {
            await orderService.updateOrderStatus(orderId, newStatus);
            toast.success('Status objednávky byl úspěšně změněn');
            await loadOrders();
        } catch (error) {
            toast.error('Nepodařilo se změnit status objednávky');
        }
    };

    const filteredOrders = orders.filter(order => {
        const matchesSearch = order.id.toString().includes(searchTerm.toLowerCase());
        const matchesStatus = !statusFilter || order.status === statusFilter;
        return matchesSearch && matchesStatus;
    });

    return (
        <Container>
            <Row className="mb-4">
                <Col>
                    <h1>Správa objednávek</h1>
                </Col>
            </Row>
            <Row className="mb-4">
                <Col md={4}>
                    <Form.Control
                        type="text"
                        placeholder="Vyhledat podle ID..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </Col>
                <Col md={4}>
                    <Form.Select
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value as OrderStatus | '')}
                    >
                        <option value="">Všechny statusy</option>
                        {Object.values(OrderStatus).map((status) => (
                            <option key={status} value={status}>
                                {getStatusText(status)}
                            </option>
                        ))}
                    </Form.Select>
                </Col>
            </Row>

            {loading ? (
                <div className="text-center">Načítání...</div>
            ) : (
                <Table striped bordered hover responsive>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Vytvořeno</th>
                            <th>Status</th>
                            <th>Produkty</th>
                            <th>Celková cena</th>
                            <th>Akce</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredOrders.map((order) => (
                            <tr key={order.id}>
                                <td>{order.id}</td>
                                <td>
                                    {new Date(order.createdAt).toLocaleDateString('cs-CZ')}
                                </td>
                                <td>
                                    <Badge bg={getStatusBadgeVariant(order.status)}>
                                        {getStatusText(order.status)}
                                    </Badge>
                                </td>
                                <td>
                                    <ul className="list-unstyled mb-0">
                                        {order.items.map((item) => (
                                            <li key={item.id}>
                                                {item.productName} x {item.quantity} ({item.price.toFixed(2)} Kč/ks)
                                            </li>
                                        ))}
                                    </ul>
                                </td>
                                <td>
                                    {order.items
                                        .reduce((sum, item) => sum + item.price * item.quantity, 0)
                                        .toFixed(2)}{' '}
                                    Kč
                                </td>
                                <td>
                                    <Form.Select
                                        size="sm"
                                        value={order.status}
                                        onChange={(e) =>
                                            handleStatusChange(
                                                order.id,
                                                e.target.value as OrderStatus
                                            )
                                        }
                                        disabled={order.status !== OrderStatus.PENDING}
                                    >
                                        {Object.values(OrderStatus).map((status) => (
                                            <option key={status} value={status}>
                                                {getStatusText(status)}
                                            </option>
                                        ))}
                                    </Form.Select>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            )}
        </Container>
    );
}; 