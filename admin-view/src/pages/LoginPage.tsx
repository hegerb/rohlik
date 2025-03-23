import React from 'react';
import { Container, Card, Form, Button } from 'react-bootstrap';
import { useForm } from 'react-hook-form';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-toastify';
import { authService } from '../services/authService';

interface LoginForm {
    username: string;
    password: string;
}

export const LoginPage: React.FC = () => {
    const { register, handleSubmit, formState: { errors } } = useForm<LoginForm>();
    const { login } = useAuth();
    const navigate = useNavigate();

    const onSubmit = async (data: LoginForm) => {
        try {
            const response = await authService.login(data);
            login({ id: 1, username: data.username }, response.token);
            toast.success('Přihlášení bylo úspěšné');
            navigate('/orders');
        } catch (error) {
            toast.error('Přihlášení selhalo');
        }
    };

    return (
        <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: '100vh' }}>
            <div style={{ width: '100%', maxWidth: '400px' }}>
                <Card>
                    <Card.Body>
                        <h2 className="text-center mb-4">Přihlášení</h2>
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

                            <Button type="submit" variant="primary" className="w-100 mb-3">
                                Přihlásit se
                            </Button>
                            <div className="text-center">
                                Nemáte účet? <Link to="/register">Registrovat se</Link>
                            </div>
                        </Form>
                    </Card.Body>
                </Card>
            </div>
        </Container>
    );
}; 