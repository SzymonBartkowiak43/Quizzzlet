import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Home from './pages/Home';
import './App.css';

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [userEmail, setUserEmail] = useState('');

    useEffect(() => {
        // Sprawdź czy użytkownik jest zalogowany (np. sprawdź localStorage)
        const token = localStorage.getItem('authToken');
        const email = localStorage.getItem('userEmail');

        if (token && email) {
            setIsAuthenticated(true);
            setUserEmail(email);
        }
    }, []);

    const handleLogout = () => {
        setIsAuthenticated(false);
        setUserEmail('');
        localStorage.removeItem('authToken');
        localStorage.removeItem('userEmail');
    };

    return (
        <Router>
            <Layout
                isAuthenticated={isAuthenticated}
                userEmail={userEmail}
                onLogout={handleLogout}
            >
                <Routes>
                    <Route path="/" element={<Home />} />
                    {/* Tutaj dodasz więcej tras */}
                </Routes>
            </Layout>
        </Router>
    );
}

export default App;