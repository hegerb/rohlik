import React from 'react';
import { AuthProvider } from './contexts/AuthContext';
import { AppRouter } from './router/AppRouter';
import 'react-toastify/dist/ReactToastify.css';
import 'bootstrap/dist/css/bootstrap.min.css';

export const App: React.FC = () => {
    return (
        <AuthProvider>
            <AppRouter />
        </AuthProvider>
    );
}; 