import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
import { WordSet, Word, AddWordRequest } from '../../types/wordSet';
import AddWordModal from '../../components/WordSets/AddWordModal';
import EditWordModal from '../../components/WordSets/EditWordModal';
import { ArrowLeft, Edit2, Trash2 } from 'lucide-react';
import './WordSetDetailsPage.css';

const WordSetDetailsPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [wordSet, setWordSet] = useState<WordSet | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [selectedWords, setSelectedWords] = useState<Set<number>>(new Set());
    const [showAddModal, setShowAddModal] = useState(false);
    const [editingWord, setEditingWord] = useState<Word | null>(null);

    const loadWordSet = useCallback(async () => {
        if (!id) return;

        try {
            setLoading(true);
            const sets = await wordSetService.getAllWordSets();
            const currentSet = sets.find(set => set.id === Number(id));

            if (!currentSet) {
                setError('Nie znaleziono zestawu');
                return;
            }

            setWordSet(currentSet);
            setError(null);
        } catch (err) {
            setError('Nie udało się załadować zestawu');
            console.error('Error loading word set:', err);
        } finally {
            setLoading(false);
        }
    }, [id]);

    useEffect(() => {
        loadWordSet();
    }, [loadWordSet]);

    const handleAddWords = async (request: AddWordRequest) => {
        if (!wordSet) return;

        try {
            await wordSetService.addWordsToSet(wordSet.id, request);
            await loadWordSet();
            setShowAddModal(false);
        } catch (err) {
            console.error('Error adding words:', err);
            throw err;
        }
    };

    const handleEditWord = async (wordId: number, englishWord: string, polishTranslation: string) => {
        if (!wordSet) return;

        try {
            await wordSetService.updateWord(wordSet.id, wordId, {
                word: englishWord,
                translation: polishTranslation
            });
            await loadWordSet();
            setEditingWord(null);
        } catch (err) {
            console.error('Error updating word:', err);
            throw err;
        }
    };

    const handleDeleteWord = async (wordId: number) => {
        if (!wordSet || !window.confirm('Czy na pewno chcesz usunąć to słówko?')) return;

        try {
            await wordSetService.deleteWord(wordSet.id, wordId);
            await loadWordSet();
        } catch (err) {
            console.error('Error deleting word:', err);
        }
    };

    const handleDeleteSelectedWords = async () => {
        if (!wordSet || selectedWords.size === 0) return;
        if (!window.confirm(`Czy na pewno chcesz usunąć ${selectedWords.size} słówek?`)) return;

        try {
            await wordSetService.deleteWords(wordSet.id, {
                wordIds: Array.from(selectedWords)
            });
            setSelectedWords(new Set());
            await loadWordSet();
        } catch (err) {
            console.error('Error deleting words:', err);
        }
    };

    const toggleWordSelection = (wordId: number) => {
        setSelectedWords(prev => {
            const newSet = new Set(prev);
            if (newSet.has(wordId)) {
                newSet.delete(wordId);
            } else {
                newSet.add(wordId);
            }
            return newSet;
        });
    };

    const selectAllWords = () => {
        if (!wordSet) return;
        setSelectedWords(new Set(wordSet.words.map(w => w.id)));
    };

    const clearSelection = () => {
        setSelectedWords(new Set());
    };

    const handleStartFlashcards = () => {
        if (!wordSet) return;
        if (wordSet.words.length === 0) {
            alert('Ten zestaw nie zawiera żadnych słówek.');
            return;
        }
        navigate(`/flashcards/${wordSet.id}`);
    };

    const handleStartQuiz = () => {
        if (!wordSet) return;
        if (wordSet.words.length === 0) {
            alert('Ten zestaw nie zawiera żadnych słówek.');
            return;
        }
        navigate(`/quiz/${wordSet.id}`);
    };

    if (loading) {
        return (
            <div className="word-set-details-page">
                <div className="loading">Ładowanie zestawu...</div>
            </div>
        );
    }

    if (error || !wordSet) {
        return (
            <div className="word-set-details-page">
                <div className="error-message">{error || 'Nie znaleziono zestawu'}</div>
                <button onClick={() => navigate('/word-sets')} className="btn btn-primary-solid">
                    Powrót do zestawów
                </button>
            </div>
        );
    }

    return (
        <div className="word-set-details-page">
            <div className="word-set-details-container">
                <div className="page-header">
                    <button
                        onClick={() => navigate('/word-sets')}
                        className="btn btn-glass-icon"
                    >
                        <ArrowLeft size={18} /> Powrót
                    </button>
                    <div className="header-actions">
                        <button
                            onClick={handleStartQuiz}
                            className="btn btn-primary-solid btn-quiz"
                            disabled={wordSet.words.length === 0}
                        >
                            🧠 Rozpocznij quiz
                        </button>
                        <button
                            onClick={handleStartFlashcards}
                            className="btn btn-primary-solid btn-flashcards"
                            disabled={wordSet.words.length === 0}
                        >
                            🎴 Rozpocznij fiszki
                        </button>
                        <div className="header-actions-secondary">
                            <button
                                onClick={() => navigate(`/word-sets/${wordSet.id}/edit`)}
                                className="btn btn-glass"
                            >
                                Edytuj zestaw
                            </button>
                            <button
                                onClick={() => setShowAddModal(true)}
                                className="btn btn-primary-solid"
                            >
                                + Dodaj słówka
                            </button>
                        </div>
                    </div>
                </div>

                <div className="words-section">
                    {selectedWords.size > 0 && (
                        <div className="selection-bar">
                            <span>Wybrano {selectedWords.size} słówek</span>
                            <div className="selection-actions">
                                <button onClick={clearSelection} className="btn btn-glass">
                                    Odznacz wszystko
                                </button>
                                <button
                                    onClick={handleDeleteSelectedWords}
                                    className="btn btn-glass-danger"
                                >
                                    Usuń wybrane
                                </button>
                            </div>
                        </div>
                    )}

                    <div className="words-controls">
                        <div className="words-count">
                            Słówka ({wordSet.words.length})
                        </div>
                        <div className="bulk-actions">
                            <button
                                onClick={selectAllWords}
                                className="btn btn-glass"
                                disabled={wordSet.words.length === 0}
                            >
                                Zaznacz wszystko
                            </button>
                        </div>
                    </div>

                    {wordSet.words.length === 0 ? (
                        <div className="empty-words">
                            <h3>Brak słówek w zestawie</h3>
                            <p>Dodaj pierwsze słówka, aby rozpocząć naukę!</p>
                            <button
                                onClick={() => setShowAddModal(true)}
                                className="btn btn-primary-solid"
                            >
                                Dodaj słówka
                            </button>
                        </div>
                    ) : (
                        <>
                            <div className="learning-cta-section">
                                <div className="flashcards-cta">
                                    <div className="cta-content">
                                        <h3>🎴 Gotowy na fiszki?</h3>
                                        <p>Przetestuj swoją wiedzę za pomocą interaktywnych fiszek!</p>
                                        <button
                                            onClick={handleStartFlashcards}
                                            className="btn btn-primary-solid btn-flashcards"
                                        >
                                            Start ({wordSet.words.length} słówek)
                                        </button>
                                    </div>
                                </div>
                                <div className="quiz-cta">
                                    <div className="cta-content">
                                        <h3>🧠 Sprawdź swoją wiedzę!</h3>
                                        <p>Przetestuj się różnymi typami pytań!</p>
                                        <button
                                            onClick={handleStartQuiz}
                                            className="btn btn-primary-solid btn-quiz"
                                        >
                                            Start ({wordSet.words.length} pytań)
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <div className="words-grid">
                                {wordSet.words.map(word => (
                                    <div
                                        key={word.id}
                                        className={`word-card ${selectedWords.has(word.id) ? 'selected' : ''}`}
                                    >
                                        <div className="word-checkbox">
                                            <input
                                                type="checkbox"
                                                checked={selectedWords.has(word.id)}
                                                onChange={() => toggleWordSelection(word.id)}
                                            />
                                        </div>
                                        <div className="word-content">
                                            <div className="english-word">{word.word}</div>
                                            <div className="polish-translation">{word.translation}</div>
                                        </div>
                                        <div className="word-actions">
                                            <button
                                                onClick={() => setEditingWord(word)}
                                                className="btn-icon-action"
                                                aria-label="Edytuj"
                                            >
                                                <Edit2 size={16} />
                                            </button>
                                            <button
                                                onClick={() => handleDeleteWord(word.id)}
                                                className="btn-icon-action btn-danger"
                                                aria-label="Usuń"
                                            >
                                                <Trash2 size={16} />
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </>
                    )}
                </div>
            </div>

            {showAddModal && (
                <AddWordModal
                    onClose={() => setShowAddModal(false)}
                    onAdd={handleAddWords}
                />
            )}

            {editingWord && (
                <EditWordModal
                    word={editingWord}
                    onClose={() => setEditingWord(null)}
                    onUpdate={handleEditWord}
                />
            )}
        </div>
    );
};

export default WordSetDetailsPage;