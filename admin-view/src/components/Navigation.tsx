import React from 'react';
import { Navbar, Nav, Container, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export const Navigation: React.FC = () => {
    const { logout, user, isAuthenticated } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <Navbar bg="dark" variant="dark" expand="lg">
            <Container>
                <Navbar.Brand as={Link} to="/">
                    Administrace
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Nav.Link as={Link} to="/products">
                            Produkty
                        </Nav.Link>
                        <Nav.Link as={Link} to="/orders">
                            Objednávky
                        </Nav.Link>
                    </Nav>
                    <Nav>
                        {isAuthenticated && (
                            <div className="d-flex align-items-center text-light">
                                <span className="me-3">{user?.username}</span>
                                <Button variant="outline-light" onClick={handleLogout}>
                                    Odhlásit se
                                </Button>
                            </div>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}; 