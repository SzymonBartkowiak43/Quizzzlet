import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './Header.css';

const Header: React.FC = () => {
    const { isAuthenticated, user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <header className="main-header">
            <div className="logo-container">
                <Link to="/">
                    <img
                        alt="Logo"
                        className="logo"
                        src="/img/logo.png"
                        style={{ cursor: 'pointer' }}
                    />
                </Link>
            </div>

            <nav className="main-nav">
                <ul>
                    <li><Link to="/">Home</Link></li>
                    <li><Link to="/word-sets">My sets</Link></li>
                    <li><Link to="/profile">My profile</Link></li>
                    <li><Link to="/videos">Watch video</Link></li>
                </ul>
            </nav>

            <div className="user-actions">
                {isAuthenticated ? (
                    <div className="profile">
                        <span>Welcome, {user?.userName}!</span>
                        <button onClick={handleLogout} className="logout-btn">
                            Logout
                        </button>
                    </div>
                ) : (
                    <div className="auth-links">
                        <Link to="/login" className="auth-link">Login</Link>
                        <Link to="/register" className="auth-link">Register</Link>
                    </div>
                )}
            </div>
        </header>
    );
};

export default Header;