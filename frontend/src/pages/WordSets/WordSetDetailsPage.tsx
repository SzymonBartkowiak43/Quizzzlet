import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
import { WordSet, Word, AddWordRequest } from '../../types/wordSet';
import AddWordModal from '../../components/WordSets/AddWordModal';
import EditWordModal from '../../components/WordSets/EditWordModal';
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
    }, [id]); // ← Zależność tylko od id

    useEffect(() => {
        loadWordSet();
    }, [loadWordSet]); // ← Teraz ESLint się nie będzie żalił

    const handleAddWords = async (request: AddWordRequest) => {
        if (!wordSet) return;

        try {
            await wordSetService.addWordsToSet(wordSet.id, request);
            await loadWordSet(); // Przeładuj dane
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
                word: englishWord,           // ← Poprawione nazwy pól
                translation: polishTranslation
            });
            await loadWordSet(); // Przeładuj dane
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

    // Reszta komponentu bez zmian...
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
                <button onClick={() => navigate('/word-sets')} className="btn btn-primary">
                    Powrót do zestawów
                </button>
            </div>
        );
    }

    return (
        <div className="word-set-details-page">
            <div className="page-header">
                <button
                    onClick={() => navigate('/word-sets')}
                    className="btn btn-secondary"
                >
                    ← Powrót
                </button>
                <div className="header-content">
                    <h1>{wordSet.name}</h1>
                    {wordSet.description && (
                        <p className="word-set-description">{wordSet.description}</p>
                    )}
                    <div className="word-set-meta">
                        <span>{wordSet.words.length} słówek</span>
                        <span>Utworzono: {new Date(wordSet.createdAt).toLocaleDateString('pl-PL')}</span>
                    </div>
                </div>
                <div className="header-actions">
                    <button
                        onClick={() => navigate(`/word-sets/${wordSet.id}/edit`)}
                        className="btn btn-secondary"
                    >
                        Edytuj zestaw
                    </button>
                    <button
                        onClick={() => setShowAddModal(true)}
                        className="btn btn-primary"
                    >
                        + Dodaj słówka
                    </button>
                </div>
            </div>

            <div className="words-section">
                {selectedWords.size > 0 && (
                    <div className="selection-bar">
                        <span>Wybrano {selectedWords.size} słówek</span>
                        <div className="selection-actions">
                            <button onClick={clearSelection} className="btn btn-small">
                                Odznacz wszystko
                            </button>
                            <button
                                onClick={handleDeleteSelectedWords}
                                className="btn btn-small btn-danger"
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
                            className="btn btn-small"
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
                            className="btn btn-primary"
                        >
                            Dodaj słówka
                        </button>
                    </div>
                ) : (
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
                                    <div className="english-word">{word.englishWord}</div>
                                    <div className="polish-translation">{word.polishTranslation}</div>
                                </div>
                                <div className="word-actions">
                                    <button
                                        onClick={() => setEditingWord(word)}
                                        className="btn btn-small"
                                    >
                                        Edytuj
                                    </button>
                                    <button
                                        onClick={() => handleDeleteWord(word.id)}
                                        className="btn btn-small btn-danger"
                                    >
                                        Usuń
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
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