import React from 'react';
import './Footer.css';

const Footer: React.FC = () => {
    const currentYear = new Date().getFullYear();

    return (
        <footer className="app-footer">
            <p>&copy; {currentYear} Szymon Bartkowiak. Wszelkie prawa zastrzeżone.</p>
        </footer>
    );
};

export default Footer;