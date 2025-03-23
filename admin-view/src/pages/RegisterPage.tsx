import React, { useState } from 'react';
import { Container, Card, Form, Button, Alert } from 'react-bootstrap';
import { useForm } from 'react-hook-form';
import { useNavigate, Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import { authService } from '../services/authService';
import { useAuth } from '../contexts/AuthContext';
import { User } from '../types';

interface RegisterForm {
    username: string;
    password: string;
    confirmPassword: string;
}

export const RegisterPage: React.FC = () => {
    const { register, handleSubmit, watch, formState: { errors } } = useForm<RegisterForm>();
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);
    const { login } = useAuth();

    const onSubmit = async (data: RegisterForm) => {
        try {
            setError(null);
            // Registrace uživatele a získání tokenu
            const authResponse = await authService.register({
                username: data.username,
                password: data.password
            });
            
            // Vytvoření objektu uživatele pro login
            const user: User = {
                id: 0, // ID bude aktualizováno při načtení uživatele
                username: data.username
            };
            
            // Přihlášení uživatele pomocí tokenu
            login(user, authResponse.token);
            
            toast.success('Registrace byla úspěšná. Jste automaticky přihlášen.');
            navigate('/'); // Přesměrování na hlavní stránku
        } catch (error: any) {
            const errorMessage = error.response?.data?.message || 'Registrace selhala';
            setError(errorMessage);
            toast.error(errorMessage);
        }
    };

    return (
        <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: '100vh' }}>
            <div style={{ width: '100%', maxWidth: '400px' }}>
                <Card>
                    <Card.Body>
                        <h2 className="text-center mb-4">Registrace</h2>
                        {error && <Alert variant="danger">{error}</Alert>}
                        <Form onSubmit={handleSubmit(onSubmit)}>
                            <Form.Group className="mb-3">
                                <Form.Label>Uživatelské jméno</Form.Label>
                                <Form.Control
                                    type="text"
                                    {...register('username', {
                                        required: 'Uživatelské jméno je povinné',
                                        minLength: {
                                            value: 3,
                                            message: 'Uživatelské jméno musí mít alespoň 3 znaky'
                                        }
                                    })}
                                    isInvalid={!!errors.username}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.username?.message}
                                </Form.Control.Feedback>
                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>Heslo</Form.Label>
                                <Form.Control
                                    type="password"
                                    {...register('password', {
                                        required: 'Heslo je povinné',
                                        minLength: {
                                            value: 5,
                                            message: 'Heslo musí mít alespoň 5 znaků'
                                        }
                                    })}
                                    isInvalid={!!errors.password}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.password?.message}
                                </Form.Control.Feedback>
                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>Potvrzení hesla</Form.Label>
                                <Form.Control
                                    type="password"
                                    {...register('confirmPassword', {
                                        required: 'Potvrzení hesla je povinné',
                                        validate: value => value === watch('password') || 'Hesla se neshodují'
                                    })}
                                    isInvalid={!!errors.confirmPassword}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.confirmPassword?.message}
                                </Form.Control.Feedback>
                            </Form.Group>

                            <Button type="submit" variant="primary" className="w-100 mb-3">
                                Registrovat se
                            </Button>
                            <div className="text-center">
                                Již máte účet? <Link to="/login">Přihlásit se</Link>
                            </div>
                        </Form>
                    </Card.Body>
                </Card>
            </div>
        </Container>
    );
}; 