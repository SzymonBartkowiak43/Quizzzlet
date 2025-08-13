import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
import { WordSet } from '../../types/wordSet';
import { FlashcardSession, FlashcardSettings as FlashcardSettingsType, FlashcardItem } from '../../types/flashcard'; // ← POPRAWKA
import FlashcardSettings from '../../components/Flashcards/FlashcardSettings'; // ← Ten zostaje bez zmian
import FlashcardGame from '../../components/Flashcards/FlashcardGame';
import './FlashcardsPage.css';

type PageState = 'loading' | 'settings' | 'playing' | 'results';

const FlashcardsPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [wordSet, setWordSet] = useState<WordSet | null>(null);
    const [pageState, setPageState] = useState<PageState>('loading');
    const [session, setSession] = useState<FlashcardSession | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (id) {
            loadWordSet();
        }
    }, [id]);

    const loadWordSet = async () => {
        try {
            const sets = await wordSetService.getAllWordSets();
            const currentSet = sets.find(set => set.id === Number(id));

            if (!currentSet) {
                setError('Nie znaleziono zestawu');
                return;
            }

            if (currentSet.words.length === 0) {
                setError('Ten zestaw nie zawiera żadnych słówek');
                return;
            }

            setWordSet(currentSet);
            setPageState('settings');
        } catch (err) {
            setError('Nie udało się załadować zestawu');
            console.error('Error loading word set:', err);
        }
    };

    const handleStartSession = (settings: FlashcardSettingsType) => { // ← POPRAWKA - użyj aliasu
        if (!wordSet) return;

        const cards: FlashcardItem[] = wordSet.words.map(word => ({
            id: word.id,
            word: word.word,
            translation: word.translation,
            difficulty: 'medium' as const,
            timesShown: 0,
            timesCorrect: 0,
            timesIncorrect: 0  // ← POPRAWKA: było "timesIncorrected"
        }));

        // Pomieszaj karty jeśli ustawione
        if (settings.shuffleCards) {
            cards.sort(() => Math.random() - 0.5);
        }

        // Ogranicz liczbę kart jeśli ustawione
        const finalCards = settings.sessionLimit
            ? cards.slice(0, settings.sessionLimit)
            : cards;

        const newSession: FlashcardSession = {
            wordSetId: wordSet.id,
            wordSetTitle: wordSet.title,
            cards: finalCards,
            currentCardIndex: 0,
            completedCards: 0,
            correctAnswers: 0,
            incorrectAnswers: 0,
            startTime: new Date(),
            settings
        };

        setSession(newSession);
        setPageState('playing');
    };

    const handleSessionComplete = () => {
        setPageState('results');
    };

    const handleRestart = () => {
        setSession(null);
        setPageState('settings');
    };

    const handleBackToWordSet = () => {
        navigate(`/word-sets/${id}`);
    };

    if (pageState === 'loading') {
        return (
            <div className="flashcards-page">
                <div className="loading">Ładowanie zestawu...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flashcards-page">
                <div className="error-container">
                    <div className="error-message">{error}</div>
                    <button onClick={() => navigate('/word-sets')} className="btn btn-primary">
                        Powrót do zestawów
                    </button>
                </div>
            </div>
        );
    }

    if (!wordSet) {
        return (
            <div className="flashcards-page">
                <div className="error-message">Nie znaleziono zestawu</div>
            </div>
        );
    }

    return (
        <div className="flashcards-page">
            <div className="page-header">
                <button onClick={handleBackToWordSet} className="btn btn-secondary">
                    ← Powrót do zestawu
                </button>
                <h1>🎴 Fiszki: {wordSet.title}</h1>
            </div>

            {pageState === 'settings' && (
                <FlashcardSettings
                    wordSet={wordSet}
                    onStart={handleStartSession}
                    onBack={handleBackToWordSet}
                />
            )}

            {pageState === 'playing' && session && (
                <FlashcardGame
                    session={session}
                    onSessionUpdate={setSession}
                    onComplete={handleSessionComplete}
                />
            )}

            {pageState === 'results' && session && (
                <div className="session-results">
                    <h2>🎉 Sesja zakończona!</h2>
                    <div className="results-stats">
                        <div className="stat-card">
                            <div className="stat-value">{session.correctAnswers}</div>
                            <div className="stat-label">Poprawne</div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-value">{session.incorrectAnswers}</div>
                            <div className="stat-label">Błędne</div>
                        </div>
                        <div className="stat-card">
                            <div className="stat-value">
                                {Math.round((session.correctAnswers / session.completedCards) * 100)}%
                            </div>
                            <div className="stat-label">Skuteczność</div>
                        </div>
                    </div>
                    <div className="results-actions">
                        <button onClick={handleRestart} className="btn btn-primary">
                            Jeszcze raz
                        </button>
                        <button onClick={handleBackToWordSet} className="btn btn-secondary">
                            Powrót do zestawu
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default FlashcardsPage;