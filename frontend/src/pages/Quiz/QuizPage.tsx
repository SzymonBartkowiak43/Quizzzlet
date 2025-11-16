import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
import { WordSet } from '../../types/wordSet';
import { QuizSession, QuizSettings as QuizSettingsType, QuizQuestion, QuizAnswer, QuizResult, QuestionType } from '../../types/quiz';
import QuizSettings from '../../components/Quiz/QuizSettings';
import QuizGame from '../../components/Quiz/QuizGame';
import { ArrowLeft, RefreshCcw } from 'lucide-react';
import './QuizPage.css';

type PageState = 'loading' | 'settings' | 'playing' | 'results';

const QuizPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [wordSet, setWordSet] = useState<WordSet | null>(null);
    const [pageState, setPageState] = useState<PageState>('loading');
    const [session, setSession] = useState<QuizSession | null>(null);
    const [result, setResult] = useState<QuizResult | null>(null);
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

    const generateQuestions = (wordSet: WordSet, settings: QuizSettingsType): QuizQuestion[] => {
        let words = [...wordSet.words];

        if (settings.questionCount && settings.questionCount < words.length) {
            words = words.slice(0, settings.questionCount);
        }

        if (settings.shuffleQuestions) {
            words.sort(() => Math.random() - 0.5);
        }

        const questions: QuizQuestion[] = [];

        words.forEach((word, index) => {
            const questionTypes: QuestionType[] = [];
            if (settings.includeMultipleChoice) questionTypes.push('multiple-choice');
            if (settings.includeTyping) questionTypes.push('typing');

            const questionType = questionTypes[Math.floor(Math.random() * questionTypes.length)];

            const question = settings.showWordFirst ? word.word : word.translation;
            const correctAnswer = settings.showWordFirst ? word.translation : word.word;

            let options: string[] | undefined;

            if (questionType === 'multiple-choice') {
                const otherWords = wordSet.words.filter(w => w.id !== word.id);
                const wrongAnswers = otherWords
                    .map(w => settings.showWordFirst ? w.translation : w.word)
                    .filter(answer => answer.toLowerCase() !== correctAnswer.toLowerCase())
                    .sort(() => Math.random() - 0.5)
                    .slice(0, 3);

                options = [correctAnswer, ...wrongAnswers];

                if (settings.shuffleOptions) {
                    options.sort(() => Math.random() - 0.5);
                }
            }

            questions.push({
                id: index + 1,
                wordId: word.id,
                question,
                correctAnswer,
                options,
                type: questionType,
                points: 1
            });
        });

        return questions;
    };

    const handleStartQuiz = (settings: QuizSettingsType) => {
        if (!wordSet) return;

        const questions = generateQuestions(wordSet, settings);

        const newSession: QuizSession = {
            id: `quiz_${Date.now()}`,
            wordSetId: wordSet.id,
            wordSetTitle: wordSet.title,
            type: 'mixed',
            questions,
            currentQuestionIndex: 0,
            answers: [],
            startTime: new Date(),
            timeLimit: settings.timeLimit,
            settings,
            status: 'active'
        };

        setSession(newSession);
        setPageState('playing');
    };

    const handleAnswer = (answer: QuizAnswer) => {
        if (!session) return;

        const updatedSession = {
            ...session,
            answers: [...session.answers, answer]
        };

        setSession(updatedSession);
    };

    const handleQuizComplete = () => {
        if (!session) return;

        const endTime = new Date();
        const totalTime = (endTime.getTime() - session.startTime.getTime()) / 1000;

        const correctAnswers = session.answers.filter(a => a.isCorrect).length;
        const totalQuestions = session.questions.length;
        const totalPoints = session.answers.reduce((sum, a) => sum + a.points, 0);
        const maxPoints = session.questions.reduce((sum, q) => sum + q.points, 0);
        const accuracy = totalQuestions > 0 ? (correctAnswers / totalQuestions) * 100 : 0;
        const averageTimePerQuestion = totalQuestions > 0 ? totalTime / totalQuestions : 0;

        const quizResult: QuizResult = {
            sessionId: session.id,
            totalQuestions,
            correctAnswers,
            incorrectAnswers: totalQuestions - correctAnswers,
            totalPoints,
            maxPoints,
            accuracy,
            averageTimePerQuestion,
            totalTime,
            completedAt: endTime
        };

        setResult(quizResult);
        setPageState('results');
    };

    const handleRestart = () => {
        setSession(null);
        setResult(null);
        setPageState('settings');
    };

    const handleBackToWordSet = () => {
        navigate(`/word-sets/${id}`);
    };

    const formatTime = (seconds: number) => {
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    if (pageState === 'loading') {
        return (
            <div className="quiz-page">
                <div className="loading">Ładowanie zestawu...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="quiz-page">
                <div className="error-container">
                    <div className="error-message">{error}</div>
                    <button onClick={() => navigate('/word-sets')} className="btn-primary-solid">
                        Powrót do zestawów
                    </button>
                </div>
            </div>
        );
    }

    if (!wordSet) {
        return (
            <div className="quiz-page">
                <div className="error-message">Nie znaleziono zestawu</div>
            </div>
        );
    }

    return (
        <div className="quiz-page">
            <div className="page-header">
                <button onClick={handleBackToWordSet} className="btn-glass-icon">
                    <ArrowLeft size={18} /> Powrót do zestawu
                </button>
                <h1>🧠 Quiz: {wordSet.title}</h1>
            </div>

            {pageState === 'settings' && (
                <QuizSettings
                    wordSet={wordSet}
                    onStart={handleStartQuiz}
                    onBack={handleBackToWordSet}
                />
            )}

            {pageState === 'playing' && session && (
                <QuizGame
                    session={session}
                    onAnswer={handleAnswer}
                    onComplete={handleQuizComplete}
                    onSessionUpdate={setSession}
                />
            )}

            {pageState === 'results' && result && session && (
                <div className="quiz-results">
                    <div className="results-header">
                        <h2>🎉 Quiz zakończony!</h2>
                        <div className="results-summary">
                            <div className="summary-item">
                                <span className="summary-value">{result.correctAnswers}</span>
                                <span className="summary-label">Poprawne</span>
                            </div>
                            <div className="summary-item">
                                <span className="summary-value">{result.incorrectAnswers}</span>
                                <span className="summary-label">Błędne</span>
                            </div>
                            <div className="summary-item">
                                <span className="summary-value">{Math.round(result.accuracy)}%</span>
                                <span className="summary-label">Skuteczność</span>
                            </div>
                            <div className="summary-item">
                                <span className="summary-value">{formatTime(result.totalTime)}</span>
                                <span className="summary-label">Czas</span>
                            </div>
                        </div>
                    </div>

                    <div className="results-details">
                        <h3>📊 Szczegóły</h3>
                        <div className="details-grid">
                            <div className="detail-item">
                                <span className="detail-label">Punkty:</span>
                                <span className="detail-value">{result.totalPoints} / {result.maxPoints}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Średni czas na pytanie:</span>
                                <span className="detail-value">{formatTime(result.averageTimePerQuestion)}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Rodzaje pytań:</span>
                                <span className="detail-value">
                  {[
                      session.settings.includeMultipleChoice && 'Multiple Choice',
                      session.settings.includeTyping && 'Wpisywanie'
                  ].filter(Boolean).join(', ')}
                </span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Kierunek:</span>
                                <span className="detail-value">
                  {session.settings.showWordFirst ? 'Angielski → Polski' : 'Polski → Angielski'}
                </span>
                            </div>
                        </div>
                    </div>

                    <div className="results-breakdown">
                        <h3>📝 Przegląd odpowiedzi</h3>
                        <div className="answers-review">
                            {session.questions.map((question, index) => {
                                const answer = session.answers[index];
                                if (!answer) return null;

                                return (
                                    <div
                                        key={question.id}
                                        className={`answer-review ${answer.isCorrect ? 'correct' : 'incorrect'}`}
                                    >
                                        <div className="answer-header">
                                            <span className="answer-number">#{index + 1}</span>
                                            <span className={`answer-status ${answer.isCorrect ? 'correct' : 'incorrect'}`}>
                        {answer.isCorrect ? '✅' : '❌'}
                      </span>
                                        </div>
                                        <div className="answer-content">
                                            <div className="answer-question">{question.question}</div>
                                            <div className="answer-details">
                                                <div className="answer-your">
                                                    <span className="answer-label">Twoja odpowiedź:</span>
                                                    <span className={`answer-text ${answer.isCorrect ? 'correct' : 'incorrect'}`}>
                            {answer.userAnswer}
                          </span>
                                                </div>
                                                {!answer.isCorrect && (
                                                    <div className="answer-correct">
                                                        <span className="answer-label">Poprawna odpowiedź:</span>
                                                        <span className="answer-text correct">{question.correctAnswer}</span>
                                                    </div>
                                                )}
                                                <div className="answer-time">
                                                    <span className="answer-label">Czas:</span>
                                                    <span className="answer-text">{formatTime(answer.timeSpent)}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    </div>

                    <div className="results-actions">
                        <button onClick={handleRestart} className="btn-primary-solid">
                            <RefreshCcw size={16} /> Powtórz quiz
                        </button>
                        <button onClick={handleBackToWordSet} className="btn-glass">
                            Powrót do zestawu
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default QuizPage;