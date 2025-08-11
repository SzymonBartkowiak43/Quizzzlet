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
            // (możesz rozszerzyć to jeśli API zwraca więcej danych o użytkowniku)
            const user = {
                id: 0, // Będzie zaktualizowane gdy API zwróci pełne dane użytkownika
                email: authResponse.email,
                userName: authResponse.email.split('@')[0], // Tymczasowo użyj części przed @
                roles: ['USER'] // Domyślnie, będzie zaktualizowane
            };

            login(authResponse.token, user);
            navigate('/');
        } catch (err: any) {
            setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <h2>Welcome Back!</h2>
                    <p>Please sign in to your account</p>
                </div>

                <form onSubmit={handleSubmit} className="auth-form">
                    {error && (
                        <div className="error-message">
                            {error}
                        </div>
                    )}

                    <div className="form-group">
                        <label htmlFor="email">Email Address</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            placeholder="Enter your email"
                            className="form-input"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            placeholder="Enter your password"
                            className="form-input"
                        />
                    </div>

                    <button
                        type="submit"
                        className="auth-button login-button"
                        disabled={loading}
                    >
                        {loading ? 'Signing In...' : 'Sign In'}
                    </button>
                </form>

                <div className="auth-footer">
                    <p>Don't have an account? <Link to="/register" className="auth-link">Sign up here</Link></p>
                </div>

                {/* Test credentials info */}
                <div className="test-credentials">
                    <h4>Test Credentials:</h4>
                    <p><strong>Email:</strong> admin@test.pl</p>
                    <p><strong>Password:</strong> password123</p>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;