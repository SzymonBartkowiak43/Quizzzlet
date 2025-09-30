import React, { useState, useEffect } from 'react';
import { FlashcardSession, FlashcardResult, FlashcardDifficulty } from '../../types/flashcard';
import Flashcard from './Flashcard';
import FlashcardProgress from './FlashcardProgress';
import './FlashcardGame.css';

interface FlashcardGameProps {
    session: FlashcardSession;
    onSessionUpdate: (session: FlashcardSession) => void;
    onComplete: () => void;
}

const FlashcardGame: React.FC<FlashcardGameProps> = ({
                                                         session,
                                                         onSessionUpdate,
                                                         onComplete
                                                     }) => {
    const [isCardFlipped, setIsCardFlipped] = useState(false);
    const [showResult, setShowResult] = useState(false);
    const [cardStartTime, setCardStartTime] = useState<Date>(new Date());
    const [reviewQueue, setReviewQueue] = useState<number[]>([]);

    const currentCard = session.cards[session.currentCardIndex];
    const isLastCard = session.currentCardIndex >= session.cards.length - 1 && reviewQueue.length === 0;

    useEffect(() => {
        setCardStartTime(new Date());
    }, [session.currentCardIndex]);

    const handleCardFlip = () => {
        setIsCardFlipped(true);
    };

    const handleAnswer = (isCorrect: boolean) => {
        const responseTime = new Date().getTime() - cardStartTime.getTime();

        const result: FlashcardResult = {
            cardId: currentCard.id,
            isCorrect,
            responseTime
        };

        const updatedCard = {
            ...currentCard,
            timesShown: currentCard.timesShown + 1,
            timesCorrect: isCorrect ? currentCard.timesCorrect + 1 : currentCard.timesCorrect,
            timesIncorrect: !isCorrect ? currentCard.timesIncorrect + 1 : currentCard.timesIncorrect,
            lastShown: new Date(),
            difficulty: calculateDifficulty(currentCard, isCorrect) // 🔥 POPRAWKA - explicit typing
        };

        const updatedCards = session.cards.map(card =>
            card.id === currentCard.id ? updatedCard : card
        );

        let newReviewQueue = [...reviewQueue];
        if (!isCorrect && session.settings.reviewIncorrect && !reviewQueue.includes(currentCard.id)) {
            newReviewQueue.push(currentCard.id);
        }

        const updatedSession: FlashcardSession = {
            ...session,
            cards: updatedCards,
            completedCards: session.completedCards + 1,
            correctAnswers: isCorrect ? session.correctAnswers + 1 : session.correctAnswers,
            incorrectAnswers: !isCorrect ? session.incorrectAnswers + 1 : session.incorrectAnswers
        };

        onSessionUpdate(updatedSession);
        setReviewQueue(newReviewQueue);
        setShowResult(true);

        setTimeout(() => {
            nextCard(updatedSession, newReviewQueue);
        }, 1500);
    };

    const calculateDifficulty = (card: any, isCorrect: boolean): FlashcardDifficulty => {
        const accuracy = card.timesShown > 0
            ? (card.timesCorrect + (isCorrect ? 1 : 0)) / (card.timesShown + 1)
            : (isCorrect ? 1 : 0);

        if (accuracy >= 0.8) return 'easy';
        if (accuracy >= 0.5) return 'medium';
        return 'hard';
    };

    const nextCard = (updatedSession: FlashcardSession, newReviewQueue: number[]) => {
        setIsCardFlipped(false);
        setShowResult(false);

        if (updatedSession.currentCardIndex < session.cards.length - 1) {
            onSessionUpdate({
                ...updatedSession,
                currentCardIndex: updatedSession.currentCardIndex + 1
            });
        } else if (newReviewQueue.length > 0) {
            const nextReviewCardId = newReviewQueue[0];
            const reviewCardIndex = session.cards.findIndex(card => card.id === nextReviewCardId);

            onSessionUpdate({
                ...updatedSession,
                currentCardIndex: reviewCardIndex
            });

            setReviewQueue(newReviewQueue.slice(1));
        } else {
            onComplete();
        }
    };

    const handleSkipCard = () => {
        handleAnswer(false);
    };

    const totalCards = session.cards.length + (session.settings.reviewIncorrect ? reviewQueue.length : 0);

    return (
        <div className="flashcard-game">
            <FlashcardProgress
                session={session}
                totalCards={totalCards}
                reviewQueueSize={reviewQueue.length}
            />

            <div className="game-area">
                <div className="card-counter">
                    <span className="current-card">{session.currentCardIndex + 1}</span>
                    <span className="separator">/</span>
                    <span className="total-cards">{session.cards.length}</span>
                    {reviewQueue.length > 0 && (
                        <span className="review-indicator">
              (+{reviewQueue.length} do powtórki)
            </span>
                    )}
                </div>

                <Flashcard
                    card={currentCard}
                    isFlipped={isCardFlipped}
                    showWordFirst={session.settings.showWordFirst}
                    onFlip={handleCardFlip}
                    difficulty={currentCard.difficulty}
                />

                <div className="game-controls">
                    {!isCardFlipped ? (
                        <div className="flip-controls">
                            <button
                                onClick={handleCardFlip}
                                className="btn btn-primary btn-large flip-btn"
                            >
                                📖 Pokaż tłumaczenie
                            </button>
                            <button
                                onClick={handleSkipCard}
                                className="btn btn-secondary btn-small"
                            >
                                Pomiń
                            </button>
                        </div>
                    ) : (
                        <div className="answer-controls">
                            {showResult ? (
                                <div className="result-feedback">
                                    <div className="result-message">
                                        {session.correctAnswers > session.incorrectAnswers ? (
                                            <span className="success">✅ Dobrze!</span>
                                        ) : (
                                            <span className="error">❌ Spróbuj ponownie</span>
                                        )}
                                    </div>
                                </div>
                            ) : (
                                <>
                                    <button
                                        onClick={() => handleAnswer(false)}
                                        className="btn btn-danger btn-large answer-btn"
                                    >
                                        ❌ Nie znam
                                    </button>
                                    <button
                                        onClick={() => handleAnswer(true)}
                                        className="btn btn-success btn-large answer-btn"
                                    >
                                        ✅ Znam
                                    </button>
                                </>
                            )}
                        </div>
                    )}
                </div>

                {isLastCard && (
                    <div className="final-card-indicator">
                        🏁 Ostatnia karta!
                    </div>
                )}
            </div>

            <div className="live-stats">
                <div className="stat">
                    <span className="stat-value">{session.correctAnswers}</span>
                    <span className="stat-label">Poprawne</span>
                </div>
                <div className="stat">
                    <span className="stat-value">{session.incorrectAnswers}</span>
                    <span className="stat-label">Błędne</span>
                </div>
                <div className="stat">
          <span className="stat-value">
            {session.completedCards > 0
                ? Math.round((session.correctAnswers / session.completedCards) * 100)
                : 0}%
          </span>
                    <span className="stat-label">Skuteczność</span>
                </div>
            </div>
        </div>
    );
};

export default FlashcardGame;