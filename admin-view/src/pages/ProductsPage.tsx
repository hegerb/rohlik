import React, { useEffect, useState } from 'react';
import { Button, Table, Container, Form, Row, Col } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { Product } from '../types';
import { productService } from '../services/productService';
import { ProductModal } from '../components/ProductModal';

export const ProductsPage: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [editingProduct, setEditingProduct] = useState<Product | null>(null);
    const [searchTerm, setSearchTerm] = useState('');

    const loadProducts = async () => {
        try {
            setLoading(true);
            const data = await productService.getProducts();
            setProducts(data);
        } catch (error) {
            toast.error('Nepodařilo se načíst produkty');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadProducts();
    }, []);

    const handleAddProduct = () => {
        setEditingProduct(null);
        setShowModal(true);
    };

    const handleEditProduct = (product: Product) => {
        setEditingProduct(product);
        setShowModal(true);
    };

    const handleDeleteProduct = async (product: Product) => {
        if (window.confirm('Opravdu chcete smazat tento produkt?')) {
            try {
                await productService.deleteProduct(product.id);
                toast.success('Produkt byl úspěšně smazán');
                await loadProducts();
            } catch (error) {
                toast.error('Nepodařilo se smazat produkt');
            }
        }
    };

    const handleModalClose = () => {
        setShowModal(false);
        setEditingProduct(null);
    };

    const handleModalSave = async (productData: Omit<Product, 'id' | 'version'>) => {
        try {
            if (editingProduct) {
                await productService.updateProduct(editingProduct.id, {
                    ...productData,
                    version: editingProduct.version
                });
                toast.success('Produkt byl úspěšně upraven');
            } else {
                await productService.createProduct(productData);
                toast.success('Produkt byl úspěšně vytvořen');
            }
            await loadProducts();
            handleModalClose();
        } catch (error) {
            toast.error('Nepodařilo se uložit produkt');
        }
    };

    const filteredProducts = products.filter(product =>
        product.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <Container>
            <Row className="mb-4">
                <Col>
                    <h1>Správa produktů</h1>
                </Col>
            </Row>
            <Row className="mb-4">
                <Col md={6}>
                    <Form.Control
                        type="text"
                        placeholder="Vyhledat produkt..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </Col>
                <Col md={6} className="text-end">
                    <Button variant="primary" onClick={handleAddProduct}>
                        Přidat produkt
                    </Button>
                </Col>
            </Row>

            {loading ? (
                <div className="text-center">Načítání...</div>
            ) : (
                <Table striped bordered hover responsive>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Název</th>
                            <th>Cena</th>
                            <th>Skladem</th>
                            <th>Akce</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredProducts.map((product) => (
                            <tr key={product.id}>
                                <td>{product.id}</td>
                                <td>{product.name}</td>
                                <td>{product.price.toFixed(2)} Kč</td>
                                <td>{product.stockQuantity} ks</td>
                                <td>
                                    <Button
                                        variant="outline-primary"
                                        size="sm"
                                        className="me-2"
                                        onClick={() => handleEditProduct(product)}
                                    >
                                        Upravit
                                    </Button>
                                    <Button
                                        variant="outline-danger"
                                        size="sm"
                                        onClick={() => handleDeleteProduct(product)}
                                    >
                                        Smazat
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            )}

            <ProductModal
                show={showModal}
                onHide={handleModalClose}
                onSave={handleModalSave}
                product={editingProduct}
            />
        </Container>
    );
}; 