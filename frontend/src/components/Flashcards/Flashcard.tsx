import React from 'react';
import { FlashcardItem, FlashcardDifficulty } from '../../types/flashcard';
import './Flashcard.css';

interface FlashcardProps {
    card: FlashcardItem;
    isFlipped: boolean;
    showWordFirst: boolean;
    onFlip: () => void;
    difficulty: FlashcardDifficulty;
}

const Flashcard: React.FC<FlashcardProps> = ({
                                                 card,
                                                 isFlipped,
                                                 showWordFirst,
                                                 onFlip,
                                                 difficulty
                                             }) => {
    const frontText = showWordFirst ? card.word : card.translation;
    const backText = showWordFirst ? card.translation : card.word;

    const frontLanguage = showWordFirst ? 'en' : 'pl';
    const backLanguage = showWordFirst ? 'pl' : 'en';

    const difficultyColors = {
        easy: '#10b981',    // zielony
        medium: '#f59e0b',  // żółty
        hard: '#ef4444'     // czerwony
    };

    const difficultyLabels = {
        easy: 'Łatwe',
        medium: 'Średnie',
        hard: 'Trudne'
    };

    return (
        <div className="flashcard-container">
            <div
                className={`flashcard ${isFlipped ? 'flipped' : ''} difficulty-${difficulty}`}
                onClick={onFlip}
            >
                <div className="card-inner">
                    {/* Przód karty */}
                    <div className="card-front">
                        <div className="card-header">
                            <div className="language-indicator">
                                {frontLanguage === 'en' ? '🇬🇧' : '🇵🇱'}
                                {frontLanguage.toUpperCase()}
                            </div>
                            <div
                                className="difficulty-badge"
                                style={{ backgroundColor: difficultyColors[difficulty] }}
                            >
                                {difficultyLabels[difficulty]}
                            </div>
                        </div>

                        <div className="card-content">
                            <div className="card-text">
                                {frontText}
                            </div>
                        </div>

                        <div className="card-footer">
                            <div className="flip-hint">
                                📖 Kliknij aby zobaczyć tłumaczenie
                            </div>
                        </div>
                    </div>

                    {/* Tył karty */}
                    <div className="card-back">
                        <div className="card-header">
                            <div className="language-indicator">
                                {backLanguage === 'en' ? '🇬🇧' : '🇵🇱'}
                                {backLanguage.toUpperCase()}
                            </div>
                            <div className="card-stats">
                                {card.timesShown > 0 && (
                                    <span className="success-rate">
                    {Math.round((card.timesCorrect / card.timesShown) * 100)}% ✅
                  </span>
                                )}
                            </div>
                        </div>

                        <div className="card-content">
                            <div className="original-text">
                                {frontText}
                            </div>
                            <div className="translation-arrow">↓</div>
                            <div className="card-text translation">
                                {backText}
                            </div>
                        </div>

                        <div className="card-footer">
                            <div className="card-history">
                                {card.timesShown > 0 ? (
                                    <span>
                    Widziane: {card.timesShown}x |
                    Poprawne: {card.timesCorrect} |
                    Błędne: {card.timesIncorrect}
                  </span>
                                ) : (
                                    <span>Nowe słówko!</span>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Wskaźnik przewracania */}
            {!isFlipped && (
                <div className="flip-indicator">
                    <div className="flip-animation">
                        📱 Tap to flip
                    </div>
                </div>
            )}
        </div>
    );
};

export default Flashcard;