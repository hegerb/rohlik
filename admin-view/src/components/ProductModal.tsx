import React from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { Product } from '../types';
import { productSchema } from '../validation/productSchema';

interface ProductModalProps {
    show: boolean;
    onHide: () => void;
    onSave: (data: Omit<Product, 'id' | 'version'>) => Promise<void>;
    product: Product | null;
}

export const ProductModal: React.FC<ProductModalProps> = ({
    show,
    onHide,
    onSave,
    product
}) => {
    const {
        register,
        handleSubmit,
        reset,
        formState: { errors, isSubmitting }
    } = useForm({
        resolver: yupResolver(productSchema),
        defaultValues: {
            name: product?.name || '',
            price: product?.price || 0,
            stockQuantity: product?.stockQuantity || 0
        }
    });

    React.useEffect(() => {
        if (show) {
            reset({
                name: product?.name || '',
                price: product?.price || 0,
                stockQuantity: product?.stockQuantity || 0
            });
        }
    }, [show, product, reset]);

    const onSubmit = handleSubmit(async (data) => {
        await onSave(data);
    });

    return (
        <Modal show={show} onHide={onHide}>
            <Form onSubmit={onSubmit}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        {product ? 'Upravit produkt' : 'Přidat nový produkt'}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form.Group className="mb-3">
                        <Form.Label>Název</Form.Label>
                        <Form.Control
                            type="text"
                            {...register('name')}
                            isInvalid={!!errors.name}
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.name?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Cena (Kč)</Form.Label>
                        <Form.Control
                            type="number"
                            step="0.01"
                            {...register('price')}
                            isInvalid={!!errors.price}
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.price?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Skladem (ks)</Form.Label>
                        <Form.Control
                            type="number"
                            {...register('stockQuantity')}
                            isInvalid={!!errors.stockQuantity}
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.stockQuantity?.message}
                        </Form.Control.Feedback>
                    </Form.Group>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={onHide}>
                        Zrušit
                    </Button>
                    <Button
                        variant="primary"
                        type="submit"
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? 'Ukládání...' : 'Uložit'}
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    );
}; 