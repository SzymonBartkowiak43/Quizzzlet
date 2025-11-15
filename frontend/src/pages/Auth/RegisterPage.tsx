import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import { RegisterRequest } from '../../types/auth';
import './AuthPages.css';

const RegisterPage: React.FC = () => {
    const [formData, setFormData] = useState<RegisterRequest>({
        email: '',
        name: '',
        password: ''
    });
    const [confirmPassword, setConfirmPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);

    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        if (name === 'confirmPassword') {
            setConfirmPassword(value);
        } else {
            setFormData(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    const validateForm = (): boolean => {
        if (!formData.email || !formData.name || !formData.password) {
            setError('Wszystkie pola są wymagane');
            return false;
        }

        if (formData.password !== confirmPassword) {
            setError('Hasła nie są zgodne');
            return false;
        }

        if (formData.password.length < 6) {
            setError('Hasło musi mieć co najmniej 6 znaków');
            return false;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(formData.email)) {
            setError('Proszę podać poprawny adres email');
            return false;
        }

        return true;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        if (!validateForm()) {
            return;
        }

        setLoading(true);

        try {
            await authService.register(formData);
            setSuccess(true);
            setTimeout(() => {
                navigate('/login');
            }, 2000);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Rejestracja nie powiodła się. Spróbuj ponownie.');
        } finally {
            setLoading(false);
        }
    };

    if (success) {
        return (
            <div className="auth-container">
                <div className="auth-card">
                    <div className="success-message">
                        <h2>Rejestracja udana!</h2>
                        <p>Twoje konto zostało pomyślnie utworzone. Za chwilę zostaniesz przekierowany na stronę logowania.</p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <h2>Utwórz konto</h2>
                    <p>Wypełnij poniższe informacje</p>
                </div>

                <form onSubmit={handleSubmit} className="auth-form">
                    {error && (
                        <div className="error-message">
                            {error}
                        </div>
                    )}

                    <div className="form-group">
                        <label htmlFor="name">Nazwa użytkownika</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            placeholder="Wprowadź swoją nazwę"
                            className="form-input"
                        />
                    </div>

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
                            placeholder="Wpisz hasło (min. 6 znaków)"
                            className="form-input"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="confirmPassword">Potwierdź hasło</label>
                        <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            value={confirmPassword}
                            onChange={handleChange}
                            required
                            placeholder="Potwierdź swoje hasło"
                            className="form-input"
                        />
                    </div>

                    <button
                        type="submit"
                        className="auth-button register-button"
                        disabled={loading}
                    >
                        {loading ? 'Tworzenie konta...' : 'Utwórz konto'}
                    </button>
                </form>

                <div className="auth-footer">
                    <p>Masz już konto? <Link to="/login" className="auth-link">Zaloguj się tutaj</Link></p>
                </div>
            </div>
        </div>
    );
};

export default RegisterPage;