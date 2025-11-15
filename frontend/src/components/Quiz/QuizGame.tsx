import React, { useState, useEffect } from 'react';
import { QuizSession, QuizAnswer } from '../../types/quiz';
import './QuizGame.css';

interface QuizGameProps {
    session: QuizSession;
    onAnswer: (answer: QuizAnswer) => void;
    onComplete: () => void;
    onSessionUpdate: (session: QuizSession) => void;
}

const QuizGame: React.FC<QuizGameProps> = ({
                                               session,
                                               onAnswer,
                                               onComplete,
                                               onSessionUpdate
                                           }) => {
    const [userAnswer, setUserAnswer] = useState('');
    const [selectedOption, setSelectedOption] = useState<string | null>(null);
    const [timeLeft, setTimeLeft] = useState<number | null>(null);
    const [questionStartTime, setQuestionStartTime] = useState(Date.now());

    const currentQuestion = session.questions[session.currentQuestionIndex];
    const isLastQuestion = session.currentQuestionIndex === session.questions.length - 1;
    const progress = ((session.currentQuestionIndex + 1) / session.questions.length) * 100;

    useEffect(() => {
        if (session.settings.timeLimit) {
            const totalTime = session.settings.timeLimit * 1000;
            const elapsed = Date.now() - session.startTime.getTime();
            const remaining = Math.max(0, totalTime - elapsed);

            setTimeLeft(Math.floor(remaining / 1000));

            const interval = setInterval(() => {
                const newElapsed = Date.now() - session.startTime.getTime();
                const newRemaining = Math.max(0, totalTime - newElapsed);
                const newTimeLeft = Math.floor(newRemaining / 1000);

                setTimeLeft(newTimeLeft);

                if (newTimeLeft === 0) {
                    clearInterval(interval);
                    onComplete();
                }
            }, 1000);

            return () => clearInterval(interval);
        }
    }, [session.startTime, session.settings.timeLimit, onComplete]);

    useEffect(() => {
        setUserAnswer('');
        setSelectedOption(null);
        setQuestionStartTime(Date.now());
    }, [session.currentQuestionIndex]);

    const handleSubmitAnswer = () => {
        const finalAnswer = currentQuestion.type === 'multiple-choice'
            ? selectedOption || ''
            : userAnswer.trim();

        if (!finalAnswer) {
            alert('Proszę podać odpowiedź');
            return;
        }

        const timeSpent = (Date.now() - questionStartTime) / 1000;
        const isCorrect = finalAnswer.toLowerCase() === currentQuestion.correctAnswer.toLowerCase();

        const answer: QuizAnswer = {
            questionId: currentQuestion.id,
            userAnswer: finalAnswer,
            isCorrect,
            timeSpent,
            points: isCorrect ? currentQuestion.points : 0
        };

        onAnswer(answer);

        if (isLastQuestion) {
            onComplete();
        } else {
            const updatedSession = {
                ...session,
                currentQuestionIndex: session.currentQuestionIndex + 1,
                answers: [...session.answers, answer]
            };
            onSessionUpdate(updatedSession);
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && currentQuestion.type === 'typing') {
            handleSubmitAnswer();
        }
    };

    const formatTime = (seconds: number) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    if (!currentQuestion) {
        return <div className="quiz-error">Błąd: Brak pytania</div>;
    }

    return (
        <div className="quiz-game">
            <div className="quiz-header">
                <div className="quiz-progress">
                    <div className="progress-info">
                        <span>Pytanie {session.currentQuestionIndex + 1} z {session.questions.length}</span>
                        <span>{Math.round(progress)}%</span>
                    </div>
                    <div className="progress-bar">
                        <div
                            className="progress-fill"
                            style={{ width: `${progress}%` }}
                        />
                    </div>
                </div>

                {timeLeft !== null && (
                    <div className={`quiz-timer ${timeLeft <= 60 ? 'timer-warning' : ''}`}>
                        ⏰ {formatTime(timeLeft)}
                    </div>
                )}
            </div>

            <div className="quiz-question">
                <div className="question-header">
          <span className="question-type">
            {currentQuestion.type === 'multiple-choice' ? '🔤 Multiple Choice' : '⌨️ Wpisz odpowiedź'}
          </span>
                    <span className="question-points">{currentQuestion.points} pkt</span>
                </div>

                <h2 className="question-text">{currentQuestion.question}</h2>

                {currentQuestion.type === 'multiple-choice' && currentQuestion.options && (
                    <div className="quiz-options">
                        {currentQuestion.options.map((option, index) => (
                            <button
                                key={index}
                                className={`quiz-option ${selectedOption === option ? 'selected' : ''}`}
                                onClick={() => setSelectedOption(option)}
                            >
                                <span className="option-letter">{String.fromCharCode(65 + index)}</span>
                                <span className="option-text">{option}</span>
                            </button>
                        ))}
                    </div>
                )}

                {currentQuestion.type === 'typing' && (
                    <div className="quiz-input">
                        <input
                            type="text"
                            value={userAnswer}
                            onChange={(e) => setUserAnswer(e.target.value)}
                            onKeyPress={handleKeyPress}
                            placeholder="Wpisz tłumaczenie..."
                            className="answer-input"
                            autoFocus
                        />
                        <div className="input-hint">
                            💡 Naciśnij Enter, aby potwierdzić odpowiedź
                        </div>
                    </div>
                )}
            </div>

            <div className="quiz-actions">
                <div className="quiz-stats">
                    <span>✅ {session.answers.filter(a => a.isCorrect).length}</span>
                    <span>❌ {session.answers.filter(a => !a.isCorrect).length}</span>
                </div>

                <button
                    onClick={handleSubmitAnswer}
                    className="btn-primary-solid btn-large"
                    disabled={
                        (currentQuestion.type === 'multiple-choice' && !selectedOption) ||
                        (currentQuestion.type === 'typing' && !userAnswer.trim())
                    }
                >
                    {isLastQuestion ? '🏁 Zakończ test' : '➡️ Następne pytanie'}
                </button>
            </div>
        </div>
    );
};

export default QuizGame;