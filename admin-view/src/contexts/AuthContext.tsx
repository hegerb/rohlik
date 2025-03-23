import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '../types';
import { authService } from '../services/authService';
import { toast } from 'react-toastify';

interface AuthContextType {
    user: User | null;
    loading: boolean;
    login: (user: User, token: string) => void;
    logout: () => void;
    isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    const [isTokenPresent, setIsTokenPresent] = useState(authService.isAuthenticated());

    useEffect(() => {
        const loadUser = async () => {
            try {
                if (authService.isAuthenticated()) {
                    setIsTokenPresent(true);
                    try {
                        const userData = await authService.getCurrentUser();
                        setUser(userData);
                    } catch (error) {
                        console.error('Chyba při načítání uživatele:', error);
                        authService.logout();
                        setIsTokenPresent(false);
                        // Nepřesměrováváme zde, necháme to na interceptoru v api.ts
                    }
                } else {
                    setIsTokenPresent(false);
                }
            } catch (error) {
                console.error('Neočekávaná chyba v loadUser:', error);
                authService.logout();
                setIsTokenPresent(false);
            } finally {
                setLoading(false);
            }
        };

        loadUser();
    }, []);

    const login = (userData: User, token: string) => {
        setUser(userData);
        localStorage.setItem('token', token);
        setIsTokenPresent(true);
        toast.success('Přihlášení proběhlo úspěšně');
    };

    const logout = () => {
        authService.logout();
        setUser(null);
        setIsTokenPresent(false);
        toast.info('Byli jste odhlášeni');
    };

    const value = {
        user,
        loading,
        login,
        logout,
        isAuthenticated: isTokenPresent,
    };

    if (loading) {
        return <div>Načítání...</div>;
    }

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth musí být použit uvnitř AuthProvider');
    }
    return context;
}; 