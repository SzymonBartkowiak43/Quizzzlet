import React, { useState } from 'react';
import { AddWordRequest } from '../../types/wordSet';
import './AddWordModal.css';

interface AddWordModalProps {
    onClose: () => void;
    onAdd: (request: AddWordRequest) => Promise<void>;
}

interface WordPair {
    id: string;
    englishWord: string;
    polishTranslation: string;
}

const AddWordModal: React.FC<AddWordModalProps> = ({ onClose, onAdd }) => {
    const [words, setWords] = useState<WordPair[]>([
        { id: '1', englishWord: '', polishTranslation: '' }
    ]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const addNewWordPair = () => {
        const newId = Date.now().toString();
        setWords(prev => [...prev, { id: newId, englishWord: '', polishTranslation: '' }]);
    };

    const removeWordPair = (id: string) => {
        if (words.length <= 1) return;
        setWords(prev => prev.filter(word => word.id !== id));
    };

    const updateWord = (id: string, field: 'englishWord' | 'polishTranslation', value: string) => {
        setWords(prev => prev.map(word =>
            word.id === id ? { ...word, [field]: value } : word
        ));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        const validWords = words.filter(word =>
            word.englishWord.trim() && word.polishTranslation.trim()
        );

        if (validWords.length === 0) {
            setError('Dodaj przynajmniej jedną parę słówek');
            return;
        }

        try {
            setLoading(true);
            setError(null);

            await onAdd({
                words: validWords.map(word => ({
                    word: word.englishWord.trim(),
                    translation: word.polishTranslation.trim()
                }))
            });
        } catch (err: any) {
            setError(err.response?.data?.message || 'Nie udało się dodać słówek');
        } finally {
            setLoading(false);
        }
    };

    const handleBulkImport = (text: string) => {
        const lines = text.split('\n').filter(line => line.trim());
        const newWords: WordPair[] = [];

        lines.forEach((line, index) => {
            const parts = line.split(/[;,|\t]/).map(part => part.trim());
            if (parts.length >= 2) {
                newWords.push({
                    id: `bulk_${Date.now()}_${index}`,
                    englishWord: parts[0],
                    polishTranslation: parts[1]
                });
            }
        });

        if (newWords.length > 0) {
            setWords(newWords);
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content add-word-modal" onClick={e => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Dodaj słówka</h2>
                    <button onClick={onClose} className="modal-close">×</button>
                </div>

                <form onSubmit={handleSubmit} className="add-word-form">
                    <div className="bulk-import-section">
                        <details>
                            <summary>Import masowy (opcjonalny)</summary>
                            <textarea
                                placeholder="Wklej słówka w formacie: angielski;polski (każda para w nowej linii)"
                                rows={4}
                                onPaste={(e) => {
                                    setTimeout(() => {
                                        const text = e.currentTarget.value;
                                        if (text.includes(';') || text.includes(',') || text.includes('\t')) {
                                            handleBulkImport(text);
                                            e.currentTarget.value = '';
                                        }
                                    }, 100);
                                }}
                            />
                            <small>Obsługiwane separatory: ; , | Tab</small>
                        </details>
                    </div>

                    <div className="words-list">
                        {words.map((word, index) => (
                            <div key={word.id} className="word-pair">
                                <div className="word-pair-number">{index + 1}.</div>
                                <input
                                    type="text"
                                    placeholder="Słowo angielskie"
                                    value={word.englishWord}
                                    onChange={(e) => updateWord(word.id, 'englishWord', e.target.value)}
                                    maxLength={100}
                                />
                                <span className="separator">→</span>
                                <input
                                    type="text"
                                    placeholder="Tłumaczenie polskie"
                                    value={word.polishTranslation}
                                    onChange={(e) => updateWord(word.id, 'polishTranslation', e.target.value)}
                                    maxLength={100}
                                />
                                {words.length > 1 && (
                                    <button
                                        type="button"
                                        onClick={() => removeWordPair(word.id)}
                                        className="remove-word-btn"
                                        title="Usuń parę"
                                    >
                                        ×
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>

                    <button
                        type="button"
                        onClick={addNewWordPair}
                        className="btn btn-secondary add-another-btn"
                    >
                        + Dodaj kolejną parę
                    </button>

                    {error && (
                        <div className="error-message">{error}</div>
                    )}

                    <div className="modal-actions">
                        <button
                            type="button"
                            onClick={onClose}
                            className="btn btn-secondary"
                            disabled={loading}
                        >
                            Anuluj
                        </button>
                        <button
                            type="submit"
                            className="btn btn-primary"
                            disabled={loading || words.every(w => !w.englishWord.trim() || !w.polishTranslation.trim())}
                        >
                            {loading ? 'Dodaję...' : `Dodaj ${words.filter(w => w.englishWord.trim() && w.polishTranslation.trim()).length} słówek`}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AddWordModal;