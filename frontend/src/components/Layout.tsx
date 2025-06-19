import React from 'react';
import Header from './Header';

interface LayoutProps {
    children: React.ReactNode;
    isAuthenticated?: boolean;
    userEmail?: string;
    onLogout?: () => void;
}

const Layout: React.FC<LayoutProps> = ({
                                           children,
                                           isAuthenticated = false,
                                           userEmail = '',
                                           onLogout
                                       }) => {
    return (
        <div className="app-layout">
            <Header
                isAuthenticated={isAuthenticated}
                userEmail={userEmail}
                onLogout={onLogout}
            />
            <main className="main-content">
                {children}
            </main>
        </div>
    );
};

export default Layout;