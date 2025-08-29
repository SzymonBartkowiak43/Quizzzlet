import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
    User,
    LogOut,
    Settings,
    ChevronDown,
    Home,
    BookOpen,
    Users,
    MessageCircle,
    Users2,
    Bell,
    Search,
    Video
} from 'lucide-react';
import './Header.css';

const Header: React.FC = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [showDropdown, setShowDropdown] = useState(false);
    const [showNotifications, setShowNotifications] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);
    const notificationsRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setShowDropdown(false);
            }
            if (notificationsRef.current && !notificationsRef.current.contains(event.target as Node)) {
                setShowNotifications(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const handleLogout = async () => {
        try {
            await logout();
            navigate('/login');
        } catch (error) {
            console.error('Logout failed:', error);
        }
    };

    const isActive = (path: string) => {
        if (path === '/') {
            return location.pathname === '/';
        }
        return location.pathname.startsWith(path);
    };

    const navItems = [
        { path: '/', label: 'Home', icon: Home },
        { path: '/word-sets', label: 'Słówka', icon: BookOpen },
        { path: '/videos', label: 'Filmy', icon: Video },
        { path: '/social/community', label: 'Wszyscy użytkownicy', icon: Users2 },
        { path: '/social/friends', label: 'Przyjaciele', icon: Users2 },
        { path: '/social/groups', label: 'Chaty grupowe', icon: Users },
    ];

    // Mock notification count
    const notificationCount = 3;
    const messageCount = 2;

    return (
        <header className="header">
            <div className="header-container">
                <div className="header-content">
                    {/* Logo */}
                    <div className="logo-container">
                        <Link to="/" className="logo-link">
                            <div className="logo-icon">
                                <BookOpen size={20} color="white" />
                            </div>
                            <span className="logo-text">EnglishApp</span>
                        </Link>
                    </div>

                    {/* Navigation Links - Desktop */}
                    {user && (
                        <nav className="nav-desktop">
                            {navItems.map(item => {
                                const Icon = item.icon;
                                const active = isActive(item.path);

                                return (
                                    <Link
                                        key={item.path}
                                        to={item.path}
                                        className={`nav-link ${active ? 'active' : ''}`}
                                    >
                                        <Icon size={16} />
                                        {item.label}
                                        {item.path.includes('messages') && messageCount > 0 && (
                                            <span className="message-badge">
                                                {messageCount}
                                            </span>
                                        )}
                                    </Link>
                                );
                            })}
                        </nav>
                    )}

                    {/* Right side */}
                    <div className="right-actions">
                        {user ? (
                            <div className="user-actions">
                                {/* Search */}
                                <button className="icon-button" aria-label="Wyszukaj">
                                    <Search size={20} />
                                </button>

                                {/* Notifications */}
                                <div className="dropdown" ref={notificationsRef}>
                                    <button
                                        className="icon-button"
                                        onClick={() => setShowNotifications(!showNotifications)}
                                        aria-label="Powiadomienia"
                                    >
                                        <Bell size={20} />
                                        {notificationCount > 0 && (
                                            <span className="notification-badge">
                                                {notificationCount}
                                            </span>
                                        )}
                                    </button>

                                    {/* Notifications Dropdown */}
                                    {showNotifications && (
                                        <div className="dropdown-menu notifications">
                                            <div className="dropdown-header">
                                                <h3>Powiadomienia</h3>
                                            </div>
                                            <div className="dropdown-content">
                                                <div className="notification-item">
                                                    <p className="notification-title">Nowe zaproszenie do przyjaźni</p>
                                                    <p className="notification-text">Anna Kowalska wysłała Ci zaproszenie</p>
                                                    <p className="notification-time">2 godziny temu</p>
                                                </div>
                                                <div className="notification-item">
                                                    <p className="notification-title">Nowa wiadomość grupowa</p>
                                                    <p className="notification-text">W grupie "Angielski dla początkujących"</p>
                                                    <p className="notification-time">4 godziny temu</p>
                                                </div>
                                                <div className="notification-item">
                                                    <p className="notification-title">Udostępniono zestaw słówek</p>
                                                    <p className="notification-text">Piotr Nowak udostępnił Ci swój zestaw</p>
                                                    <p className="notification-time">1 dzień temu</p>
                                                </div>
                                            </div>
                                            <div className="dropdown-footer">
                                                <Link
                                                    to="/social/notifications"
                                                    onClick={() => setShowNotifications(false)}
                                                >
                                                    Zobacz wszystkie →
                                                </Link>
                                            </div>
                                        </div>
                                    )}
                                </div>

                                {/* User Menu */}
                                <div className="dropdown" ref={dropdownRef}>
                                    <button
                                        className="user-button"
                                        onClick={() => setShowDropdown(!showDropdown)}
                                        aria-label="Menu użytkownika"
                                    >
                                        <div className="user-avatar">
                                            <span className="user-avatar-text">
                                                {user.userName?.charAt(0)?.toUpperCase() || user.email?.charAt(0)?.toUpperCase() || 'U'}
                                            </span>
                                        </div>
                                        <span className="user-name">
                                            {user.userName || user.email}
                                        </span>
                                        <ChevronDown size={16} />
                                    </button>

                                    {/* Dropdown Menu */}
                                    {showDropdown && (
                                        <div className="dropdown-menu">
                                            <div className="user-info">
                                                <p className="user-info-name">{user.userName || user.email}</p>
                                                <p className="user-info-email">{user.email}</p>
                                            </div>

                                            <Link
                                                to="/profile"
                                                className="dropdown-item"
                                                onClick={() => setShowDropdown(false)}
                                            >
                                                <User size={16} />
                                                Profil
                                            </Link>

                                            <Link
                                                to="/settings"
                                                className="dropdown-item"
                                                onClick={() => setShowDropdown(false)}
                                            >
                                                <Settings size={16} />
                                                Ustawienia
                                            </Link>

                                            <hr />

                                            <button
                                                onClick={handleLogout}
                                                className="dropdown-item danger"
                                            >
                                                <LogOut size={16} />
                                                Wyloguj się
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ) : (
                            <div className="auth-buttons">
                                <Link to="/login" className="login-link">
                                    Zaloguj się
                                </Link>
                                <Link to="/register" className="register-link">
                                    Zarejestruj się
                                </Link>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Mobile menu */}
            {user && (
                <div className="mobile-menu">
                    <div className="mobile-menu-content">
                        {navItems.map(item => {
                            const Icon = item.icon;
                            const active = isActive(item.path);

                            return (
                                <Link
                                    key={item.path}
                                    to={item.path}
                                    className={`mobile-nav-link ${active ? 'active' : ''}`}
                                >
                                    <Icon size={16} />
                                    {item.label}
                                    {item.path.includes('messages') && messageCount > 0 && (
                                        <span className="mobile-message-badge">
                                            {messageCount}
                                        </span>
                                    )}
                                </Link>
                            );
                        })}
                    </div>
                </div>
            )}
        </header>
    );
};

export default Header;