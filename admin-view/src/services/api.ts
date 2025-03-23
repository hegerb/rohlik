import axios from 'axios';
import { toast } from 'react-toastify';

const api = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

// Request interceptor
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            window.location.href = window.location.origin + '/login';
        } else if (error.response) {
            // Pro ladění vypisujeme do konzole
            console.error('API Error:', error.response);
            
            switch (error.response.status) {
                case 403:
                    toast.error('Nemáte oprávnění k této akci.');
                    break;
                case 404:
                    toast.error('Požadovaný zdroj nebyl nalezen.');
                    break;
                case 500:
                    // Získání chybové zprávy ze serveru pro kód 500
                    const errorMessage = error.response.data?.message || 
                                        error.response.data?.error ||
                                        error.response.data;
                    
                    // Pokud máme konkrétní chybovou zprávu ze serveru, použijeme ji
                    if (errorMessage && typeof errorMessage === 'string') {
                        toast.error(errorMessage);
                    } else {
                        toast.error('Došlo k chybě serveru. Zkuste to prosím později.');
                    }
                    break;
                default:
                    toast.error('Došlo k neočekávané chybě.');
            }
        } else if (error.request) {
            toast.error('Server není dostupný. Zkontrolujte připojení k internetu.');
        } else {
            toast.error('Došlo k chybě při odesílání požadavku.');
        }
        return Promise.reject(error);
    }
);

export default api; 