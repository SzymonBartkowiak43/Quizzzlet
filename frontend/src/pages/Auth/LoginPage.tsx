import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { authService } from '../../services/authService';
import { LoginRequest } from '../../types/auth';
import './AuthPages.css';

const LoginPage: React.FC = () => {
    const [formData, setFormData] = useState<LoginRequest>({
        email: '',
        password: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const { login } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const authResponse = await authService.login(formData);

            // Utworzenie obiektu użytkownika na podstawie odpowiedzi
            const user = {
                id: 0, // Będzie zaktualizowane gdy API zwróci pełne dane użytkownika
                email: authResponse.email,
                userName: authResponse.email.split('@')[0], // Tymczasowo
                roles: ['USER'] // Domyślnie
            };

            login(authResponse.token, user);
            navigate('/');
        } catch (err: any) {
            setError(err.response?.data?.message || 'Logowanie nieudane. Sprawdź swoje dane.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <h2>Witaj z powrotem!</h2>
                    <p>Zaloguj się na swoje konto</p>
                </div>

                <form onSubmit={handleSubmit} className="auth-form">
                    {error && (
                        <div className="error-message">
                            {error}
                        </div>
                    )}

                    <div className="form-group">
                        <label htmlFor="email">Adres Email</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            placeholder="Wprowadź swój email"
                            className="form-input"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Hasło</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            placeholder="Wprowadź swoje hasło"
                            className="form-input"
                        />
                    </div>

                    <button
                        type="submit"
                        className="auth-button login-button"
                        disabled={loading}
                    >
                        {loading ? 'Logowanie...' : 'Zaloguj się'}
                    </button>
                </form>

                <div className="auth-footer">
                    <p>Nie masz konta? <Link to="/register" className="auth-link">Zarejestruj się tutaj</Link></p>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;