import React from 'react';
import { useNavigate } from 'react-router-dom';
import { BookCopy, PlaySquare, Users } from 'lucide-react';
import './HomePage.css';

const HomePage: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div className="home-page">
            <section className="home-hero">
                <h1 className="home-hero-title">Wybierz swoją aktywność</h1>
                <p className="home-hero-subtitle">
                    Kontynuuj naukę, powtarzaj materiał lub odkryj nowe treści.
                </p>
            </section>

            <section className="home-grid-container">
                <div className="home-grid">

                    <div className="activity-card" onClick={() => navigate('/word-sets')}>
                        <div className="card-icon-wrapper">
                            <BookCopy size={32} className="card-icon" />
                        </div>
                        <h2 className="card-title">Przeglądaj Słówka</h2>
                        <p className="card-description">
                            Powtórz i utrwal słownictwo, którego uczyłeś się wcześniej.
                        </p>
                        <button className="card-button">
                            Przeglądaj słowa
                        </button>
                    </div>

                    <div className="activity-card" onClick={() => navigate('/videos')}>
                        <div className="card-icon-wrapper">
                            <PlaySquare size={32} className="card-icon" />
                        </div>
                        <h2 className="card-title">Obejrzyj Filmy</h2>
                        <p className="card-description">
                            Popraw swoje umiejętności językowe oglądając filmy edukacyjne.
                        </p>
                        <button className="card-button">
                            Oglądaj filmy
                        </button>
                    </div>

                    <div className="activity-card" onClick={() => navigate('/social/friends')}>
                        <div className="card-icon-wrapper">
                            <Users size={32} className="card-icon" />
                        </div>
                        <h2 className="card-title">Czat i Znajomi</h2>
                        <p className="card-description">
                            Rozmawiaj ze znajomymi, wysyłaj wiadomości i bądź częścią społeczności.
                        </p>
                        <button className="card-button">
                            Przejdź do Social
                        </button>
                    </div>

                </div>
            </section>
        </div>
    );
};

export default HomePage;