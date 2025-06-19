import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Header.css';

interface HeaderProps {
    isAuthenticated?: boolean;
    userEmail?: string;
    onLogout?: () => void;
}

const Header: React.FC<HeaderProps> = ({
                                           isAuthenticated = false,
                                           userEmail = '',
                                           onLogout
                                       }) => {
    const navigate = useNavigate();

    const handleLogout = () => {
        if (onLogout) {
            onLogout();
        }
        // Tutaj dodasz logikę wylogowania (np. usunięcie tokenu z localStorage)
        localStorage.removeItem('authToken');
        navigate('/login');
    };

    return (
        <header className="main-header">
            <div className="logo-container">
                <Link to="/">
                    <img
                        alt="Logo"
                        className="logo"
                        id="logo"
                        src="/img/logo.png"
                        style={{ cursor: 'pointer' }}
                    />
                </Link>
            </div>

            <nav className="main-nav">
                <ul>
                    <li><Link to="/">Home</Link></li>
                    <li><Link to="/wordSet">My sets</Link></li>
                    <li><Link to="/profileSettings">My profile</Link></li>
                    <li><Link to="/video/showAll">Watch video</Link></li>
                </ul>
            </nav>

            <div className="user-actions">
                {isAuthenticated ? (
                    <div className="profile">
                        {userEmail && <span>{userEmail}</span>}
                        <a href="#" onClick={(e) => { e.preventDefault(); handleLogout(); }}>
                            Logout
                        </a>
                    </div>
                ) : (
                    <div className="auth-links">
                        <Link to="/login">Login</Link>
                        <Link to="/register">Register</Link>
                    </div>
                )}
            </div>
        </header>
    );
};

export default Header;