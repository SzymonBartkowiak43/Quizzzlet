import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
import { WordSet, Word } from '../../types/wordSet';
import './FlashcardsPage.css';

interface FlashcardSession {
    words: Word[];
    currentIndex: number;
    correctCount: number;
    incorrectCount: number;
    completedWords: Set<number>;
}

const FlashcardsPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [wordSet, setWordSet] = useState<WordSet | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Flashcard state
    const [session, setSession] = useState<FlashcardSession | null>(null);
    const [isFlipped, setIsFlipped] = useState(false);
    const [showAnswer, setShowAnswer] = useState(false);
    const [isAnimating, setIsAnimating] = useState(false);

    useEffect(() => {
        if (id) {
            loadWordSet();
        }
    }, [id]);

    const loadWordSet = async () => {
        try {
            setLoading(true);
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
            initializeSession(currentSet);
        } catch (err) {
            setError('Nie udało się załadować zestawu');
            console.error('Error loading word set:', err);
        } finally {
            setLoading(false);
        }
    };

    const initializeSession = (wordSet: WordSet) => {
        const shuffledWords = [...wordSet.words].sort(() => Math.random() - 0.5);

        const newSession: FlashcardSession = {
            words: shuffledWords,
            currentIndex: 0,
            correctCount: 0,
            incorrectCount: 0,
            completedWords: new Set()
        };

        setSession(newSession);
        resetCardState();
    };

    const resetCardState = () => {
        setIsFlipped(false);
        setShowAnswer(false);
        setIsAnimating(false);
    };

    const handleFlipCard = () => {
        if (isAnimating) return;

        setIsAnimating(true);
        setIsFlipped(!isFlipped);

        // Reset animation po 300ms
        setTimeout(() => {
            setIsAnimating(false);
        }, 300);
    };

    const handleShowAnswer = () => {
        if (!showAnswer) {
            setShowAnswer(true);
        }
    };

    const handleAnswer = (isCorrect: boolean) => {
        if (!session) return;

        const currentWord = session.words[session.currentIndex];

        setSession(prev => {
            if (!prev) return prev;

            return {
                ...prev,
                correctCount: prev.correctCount + (isCorrect ? 1 : 0),
                incorrectCount: prev.incorrectCount + (isCorrect ? 0 : 1),
                completedWords: new Set(Array.from(prev.completedWords).concat(currentWord.id))
            };
        });

        setTimeout(() => {
            goToNextCard();
        }, 500);
    };

    const goToNextCard = () => {
        if (!session) return;

        if (session.currentIndex >= session.words.length - 1) {
            return;
        }

        setSession(prev => {
            if (!prev) return prev;

            return {
                ...prev,
                currentIndex: prev.currentIndex + 1
            };
        });

        resetCardState();
    };

    const goToPrevCard = () => {
        if (!session || session.currentIndex <= 0) return;

        setSession(prev => {
            if (!prev) return prev;

            return {
                ...prev,
                currentIndex: prev.currentIndex - 1
            };
        });

        resetCardState();
    };

    const restartSession = () => {
        if (wordSet) {
            initializeSession(wordSet);
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        switch (e.code) {
            case 'Space':
                e.preventDefault();
                if (!showAnswer) {
                    handleShowAnswer();
                } else {
                    handleFlipCard();
                }
                break;
            case 'ArrowLeft':
                goToPrevCard();
                break;
            case 'ArrowRight':
                if (showAnswer) {
                    goToNextCard();
                }
                break;
            case 'KeyY':
            case 'Digit1':
                if (showAnswer) {
                    handleAnswer(true);
                }
                break;
            case 'KeyN':
            case 'Digit2':
                if (showAnswer) {
                    handleAnswer(false);
                }
                break;
        }
    };

    if (loading) {
        return (
            <div className="flashcards-page">
                <div className="loading">Ładowanie fiszek...</div>
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

    if (!wordSet || !session) {
        return (
            <div className="flashcards-page">
                <div className="error-message">Nie udało się załadować sesji</div>
            </div>
        );
    }

    const currentWord = session.words[session.currentIndex];
    const progress = ((session.currentIndex + 1) / session.words.length) * 100;
    const isSessionComplete = session.currentIndex >= session.words.length - 1 && session.completedWords.has(currentWord.id);

    if (isSessionComplete) {
        return (
            <div className="flashcards-page">
                <div className="session-complete">
                    <h2>🎉 Sesja zakończona!</h2>
                    <div className="final-stats">
                        <div className="stat-item">
                            <span className="stat-value">{session.correctCount}</span>
                            <span className="stat-label">Poprawne</span>
                        </div>
                        <div className="stat-item">
                            <span className="stat-value">{session.incorrectCount}</span>
                            <span className="stat-label">Błędne</span>
                        </div>
                        <div className="stat-item">
              <span className="stat-value">
                {Math.round((session.correctCount / (session.correctCount + session.incorrectCount)) * 100)}%
              </span>
                            <span className="stat-label">Skuteczność</span>
                        </div>
                    </div>
                    <div className="session-actions">
                        <button onClick={restartSession} className="btn btn-primary">
                            🔄 Powtórz sesję
                        </button>
                        <button onClick={() => navigate(`/word-sets/${id}`)} className="btn btn-secondary">
                            Powrót do zestawu
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div
            className="flashcards-page"
            onKeyDown={handleKeyPress}
            tabIndex={0}
        >
            <div className="flashcards-header">
                <button
                    onClick={() => navigate(`/word-sets/${id}`)}
                    className="btn btn-secondary btn-small"
                >
                    ← Zakończ
                </button>

                <div className="progress-info">
                    <span className="set-title">{wordSet.title}</span>
                    <span className="progress-text">
            {session.currentIndex + 1} / {session.words.length}
          </span>
                </div>

                <div className="session-stats">
                    <span className="correct-count">✅ {session.correctCount}</span>
                    <span className="incorrect-count">❌ {session.incorrectCount}</span>
                </div>
            </div>

            <div className="progress-bar">
                <div
                    className="progress-fill"
                    style={{ width: `${progress}%` }}
                />
            </div>

            <div className="flashcard-container">
                <div
                    className={`flashcard ${isFlipped ? 'flipped' : ''} ${isAnimating ? 'animating' : ''}`}
                    onClick={!showAnswer ? handleShowAnswer : handleFlipCard}
                >
                    <div className="flashcard-front">
                        <div className="card-content">
                            <div className="word-text">
                                {!isFlipped ? currentWord.word : currentWord.translation}
                            </div>
                            <div className="card-language">
                                {!isFlipped ? '🇬🇧 English' : '🇵🇱 Polski'}
                            </div>
                        </div>
                    </div>

                    <div className="flashcard-back">
                        <div className="card-content">
                            <div className="word-text">
                                {isFlipped ? currentWord.word : currentWord.translation}
                            </div>
                            <div className="card-language">
                                {isFlipped ? '🇬🇧 English' : '🇵🇱 Polski'}
                            </div>
                        </div>
                    </div>
                </div>

                {!showAnswer && (
                    <div className="card-hint">
                        <p>Kliknij kartę lub naciśnij <kbd>Spację</kbd>, aby zobaczyć tłumaczenie</p>
                    </div>
                )}
            </div>

            {showAnswer && (
                <div className="answer-section">
                    <div className="answer-display">
                        <div className="answer-label">
                            {isFlipped ? 'Słowo angielskie:' : 'Tłumaczenie polskie:'}
                        </div>
                        <div className="answer-text">
                            {isFlipped ? currentWord.word : currentWord.translation}
                        </div>
                    </div>

                    <div className="answer-actions">
                        <button
                            onClick={() => handleAnswer(false)}
                            className="btn btn-danger btn-large"
                        >
                            ❌ Nie wiedziałem
                        </button>
                        <button
                            onClick={handleFlipCard}
                            className="btn btn-secondary btn-large"
                        >
                            🔄 Odwróć kartę
                        </button>
                        <button
                            onClick={() => handleAnswer(true)}
                            className="btn btn-success btn-large"
                        >
                            ✅ Wiedziałem
                        </button>
                    </div>
                </div>
            )}

            <div className="flashcard-navigation">
                <button
                    onClick={goToPrevCard}
                    disabled={session.currentIndex <= 0}
                    className="btn btn-secondary"
                >
                    ← Poprzednia
                </button>

                <div className="keyboard-hints">
                    <kbd>←</kbd> Poprzednia | <kbd>Spacja</kbd> Pokaż/Odwróć | <kbd>1</kbd> Tak | <kbd>2</kbd> Nie
                </div>

                <button
                    onClick={goToNextCard}
                    disabled={!showAnswer}
                    className="btn btn-secondary"
                >
                    Następna →
                </button>
            </div>
        </div>
    );
};

export default FlashcardsPage;