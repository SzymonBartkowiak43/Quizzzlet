import React from 'react';
import { useNavigate } from 'react-router-dom';
import './HomePage.css';

const HomePage: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div className="home-page">
            <div className="container1">
                <h1>WYBIERZ SWOJĄ AKTYWNOŚĆ</h1>
            </div>

            <div className="container2">
                <div className="activity-card blue-frame">
                    <div className="content-frame">
                        <h2 className="title">Przejrzyj poprzednie słowka</h2>
                        <p className="description">
                            Tutaj możesz przejrzeć słowa, których się wcześniej nauczyłeś. Ta sekcja pozwala
                            na powtórzenie i utrwalenie słownictwa, zapewniając lepsze zapamiętanie i zrozumienie
                            nauczonych słów.
                        </p>
                        <button
                            className="action-button review-button"
                            onClick={() => navigate('/word-sets')}
                        >
                            PRZEGLĄDAJ SŁOWA
                        </button>
                    </div>
                </div>

                <div className="activity-card red-frame">
                    <div className="content-frame">
                        <h2 className="title">Obejrzyj filmy</h2>
                        <p className="description">
                            Oglądaj filmy edukacyjne, aby poprawić swoje umiejętności językowe. Ta sekcja zawiera różnorodne
                            filmy, które obejmują różne aspekty nauki języków, w tym gramatykę, słownictwo,
                            wymowę i wiele innych.
                        </p>
                        <button
                            className="action-button watch-button"
                            onClick={() => navigate('/videos')}
                        >
                            OGLĄDAJ FILMY
                        </button>
                    </div>
                </div>

                <div className="activity-card green-frame">
                    <div className="content-frame">
                        <h2 className="title">Moje zestawy</h2>
                        <p className="description">
                            Odkryj swoje spersonalizowane zestawy! Twórz i zarządzaj własnymi zestawami fiszek, aby ulepszyć
                            swoje doświadczenie w nauce. Zanurz się w świecie spersonalizowanych treści, które odpowiadają Twoim potrzebom w nauce i śledź
                            swoje postępy w miarę opanowywania nowych tematów.
                        </p>
                        <button
                            className="action-button sets-button"
                            onClick={() => navigate('/word-sets')}
                        >
                            PRZEJDŹ DO ZESTAWÓW
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default HomePage;