import React from 'react';
import { FlashcardSession } from '../../types/flashcard';
import './FlashcardProgress.css';

interface FlashcardProgressProps {
    session: FlashcardSession;
    totalCards: number;
    reviewQueueSize: number;
}

const FlashcardProgress: React.FC<FlashcardProgressProps> = ({
                                                                 session,
                                                                 totalCards,
                                                                 reviewQueueSize
                                                             }) => {
    const progressPercentage = (session.completedCards / totalCards) * 100;
    const accuracy = session.completedCards > 0
        ? (session.correctAnswers / session.completedCards) * 100
        : 0;

    const sessionDuration = new Date().getTime() - session.startTime.getTime();
    const minutes = Math.floor(sessionDuration / 60000);
    const seconds = Math.floor((sessionDuration % 60000) / 1000);

    return (
        <div className="flashcard-progress">
            <div className="progress-header">
                <div className="session-info">
                    <h3>{session.wordSetTitle}</h3>
                    <div className="session-meta">
            <span className="timer">
              ⏱️ {minutes}:{seconds.toString().padStart(2, '0')}
            </span>
                        <span className="cards-remaining">
              📚 {session.completedCards}/{totalCards}
            </span>
                    </div>
                </div>
            </div>

            <div className="progress-bar-container">
                <div className="progress-bar">
                    <div
                        className="progress-fill"
                        style={{ width: `${progressPercentage}%` }}
                    >
                        <div className="progress-shimmer"></div>
                    </div>
                </div>
                <div className="progress-text">
                    {Math.round(progressPercentage)}% ukończone
                </div>
            </div>

            <div className="progress-stats">
                <div className="stat-item correct">
                    <div className="stat-icon">✅</div>
                    <div className="stat-content">
                        <div className="stat-value">{session.correctAnswers}</div>
                        <div className="stat-label">Poprawne</div>
                    </div>
                </div>

                <div className="stat-item incorrect">
                    <div className="stat-icon">❌</div>
                    <div className="stat-content">
                        <div className="stat-value">{session.incorrectAnswers}</div>
                        <div className="stat-label">Błędne</div>
                    </div>
                </div>

                <div className="stat-item accuracy">
                    <div className="stat-icon">🎯</div>
                    <div className="stat-content">
                        <div className="stat-value">{Math.round(accuracy)}%</div>
                        <div className="stat-label">Skuteczność</div>
                    </div>
                </div>

                {reviewQueueSize > 0 && (
                    <div className="stat-item review">
                        <div className="stat-icon">🔄</div>
                        <div className="stat-content">
                            <div className="stat-value">{reviewQueueSize}</div>
                            <div className="stat-label">Do powtórki</div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default FlashcardProgress;