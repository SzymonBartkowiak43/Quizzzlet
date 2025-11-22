import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
    User, LogOut, Settings, ChevronDown, Home, BookOpen,
    Users, Users2, Video, Menu, X, Shield
} from 'lucide-react';
import './Header.css';

const navItems = [
    { path: '/', label: 'Home', icon: Home },
    { path: '/word-sets', label: 'Słówka', icon: BookOpen },
    { path: '/videos', label: 'Filmy', icon: Video },
    { path: '/social/community', label: 'Wszyscy użytkownicy', icon: Users2 },
    { path: '/social/friends', label: 'Przyjaciele', icon: Users2 },
    { path: '/social/groups', label: 'Chaty grupowe', icon: Users },
];

const Header: React.FC = () => {
    const { user, logout } = useAuth();

    console.log("DANE UŻYTKOWNIKA W HEADERZE:", user);
    const navigate = useNavigate();
    const location = useLocation();

    const [showUserDropdown, setShowUserDropdown] = useState(false);
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [isScrolled, setIsScrolled] = useState(false);

    const userDropdownRef = useRef<HTMLDivElement>(null);
    const mobileMenuRef = useRef<HTMLDivElement>(null);

    // <<< 2. Logika sprawdzająca czy użytkownik jest adminem
    // Używamy 'any' rzutowania dla user, jeśli TypeScript krzyczy o brak pola roles,
    // ale w produkcji lepiej dodać to do interfejsu User.
    const isAdmin = (user as any)?.roles?.includes('ADMIN') || (user as any)?.role === 'ADMIN';

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 10);
        };
        window.addEventListener('scroll', handleScroll);
        handleScroll();
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            const target = event.target as Node;
            if (userDropdownRef.current && !userDropdownRef.current.contains(target)) {
                setShowUserDropdown(false);
            }
            if (mobileMenuRef.current && mobileMenuRef.current === target) {
                setIsMobileMenuOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    useEffect(() => {
        if (isMobileMenuOpen) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = 'unset';
        }
        return () => {
            document.body.style.overflow = 'unset';
        };
    }, [isMobileMenuOpen]);


    const handleLogout = async () => {
        try {
            await logout();
            navigate('/login');
            setIsMobileMenuOpen(false);
        } catch (error) {
            console.error('Logout failed:', error);
        }
    };

    const isActive = (path: string) => {
        if (path === '/') return location.pathname === '/';
        return location.pathname.startsWith(path);
    };

    const handleMobileLinkClick = (path: string) => {
        navigate(path);
        setIsMobileMenuOpen(false);
    };

    const messageCount = 2; // Tutaj w przyszłości podepniesz prawdziwą liczbę wiadomości

    const headerClassName = `header ${isScrolled ? 'scrolled' : ''} ${location.pathname !== '/' ? 'solid' : ''}`;

    return (
        <>
            <header className={headerClassName}>
                <div className="header-container">
                    <div className="header-content">

                        <div className="logo-container">
                            <Link to="/" className="logo-link">
                                <div className="logo-icon">
                                    <BookOpen size={20} color="white" />
                                </div>
                                <span className="logo-text">EnglishApp</span>
                            </Link>
                        </div>

                        {user && (
                            <nav className="nav-desktop">
                                {/* Standardowe linki */}
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

                                {/* <<< 3. Link dla Admina (Desktop) */}
                                {isAdmin && (
                                    <Link
                                        to="/admin/users"
                                        className={`nav-link ${isActive('/admin') ? 'active' : ''}`}
                                        style={{ color: '#ff6b6b', fontWeight: 500 }}
                                    >
                                        <Shield size={16} />
                                        Panel Admina
                                    </Link>
                                )}
                            </nav>
                        )}

                        <div className="right-actions">
                            {user ? (
                                <>
                                    <div className="dropdown user-dropdown-desktop" ref={userDropdownRef}>
                                        <button
                                            className="user-button"
                                            onClick={() => setShowUserDropdown(!showUserDropdown)}
                                            aria-label="Menu użytkownika"
                                        >
                                            <div className="user-avatar">
                                                <span className="user-avatar-text">
                                                    {user.userName?.charAt(0)?.toUpperCase() || 'U'}
                                                </span>
                                            </div>
                                            <span className="user-name">
                                                {user.userName || user.email}
                                            </span>
                                            <ChevronDown size={16} />
                                        </button>
                                        {showUserDropdown && (
                                            <div className="dropdown-menu">
                                                <div className="user-info">
                                                    <p className="user-info-name">{user.userName || user.email}</p>
                                                    <p className="user-info-email">{user.email}</p>
                                                </div>
                                                <Link to="/profile" className="dropdown-item" onClick={() => setShowUserDropdown(false)}>
                                                    <User size={16} /> Profil
                                                </Link>
                                                <Link to="/settings" className="dropdown-item" onClick={() => setShowUserDropdown(false)}>
                                                    <Settings size={16} /> Ustawienia
                                                </Link>
                                                <hr />
                                                <button onClick={handleLogout} className="dropdown-item danger">
                                                    <LogOut size={16} /> Wyloguj się
                                                </button>
                                            </div>
                                        )}
                                    </div>

                                    <button
                                        className="icon-button mobile-menu-toggle"
                                        onClick={() => setIsMobileMenuOpen(true)}
                                        aria-label="Otwórz menu"
                                    >
                                        <Menu size={24} />
                                    </button>
                                </>
                            ) : (
                                <div className="auth-buttons">
                                    <Link to="/login" className="login-link">Zaloguj się</Link>
                                    <Link to="/register" className="register-link">Zarejestruj się</Link>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </header>

            {user && (
                <div
                    className={`mobile-nav-overlay ${isMobileMenuOpen ? 'open' : ''}`}
                    ref={mobileMenuRef}
                >
                    <div className="mobile-nav-content">
                        <div className="mobile-nav-header">
                            <span className="logo-text">Menu</span>
                            <button
                                className="icon-button"
                                onClick={() => setIsMobileMenuOpen(false)}
                                aria-label="Zamknij menu"
                            >
                                <X size={24} />
                            </button>
                        </div>

                        <div className="mobile-user-info">
                            <div className="user-avatar large">
                                <span className="user-avatar-text">
                                    {user.userName?.charAt(0)?.toUpperCase() || 'U'}
                                </span>
                            </div>
                            <p className="user-info-name">{user.userName || user.email}</p>
                            <p className="user-info-email">{user.email}</p>
                        </div>

                        <nav className="mobile-nav-links">
                            {navItems.map(item => {
                                const Icon = item.icon;
                                const active = isActive(item.path);
                                return (
                                    <button
                                        key={item.path}
                                        onClick={() => handleMobileLinkClick(item.path)}
                                        className={`mobile-nav-link ${active ? 'active' : ''}`}
                                    >
                                        <Icon size={20} />
                                        {item.label}
                                    </button>
                                );
                            })}

                            {/* <<< 4. Link dla Admina (Mobile) */}
                            {isAdmin && (
                                <button
                                    onClick={() => handleMobileLinkClick('/admin/users')}
                                    className={`mobile-nav-link ${isActive('/admin') ? 'active' : ''}`}
                                    style={{ color: '#ff6b6b' }}
                                >
                                    <Shield size={20} />
                                    Panel Admina
                                </button>
                            )}
                        </nav>

                        <hr className="mobile-nav-divider" />

                        <div className="mobile-user-actions">
                            <button
                                onClick={() => handleMobileLinkClick('/profile')}
                                className="mobile-nav-link"
                            >
                                <User size={20} /> Profil
                            </button>
                            <button
                                onClick={() => handleMobileLinkClick('/settings')}
                                className="mobile-nav-link"
                            >
                                <Settings size={20} /> Ustawienia
                            </button>
                            <button
                                onClick={handleLogout}
                                className="mobile-nav-link danger"
                            >
                                <LogOut size={20} /> Wyloguj się
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
};

export default Header;