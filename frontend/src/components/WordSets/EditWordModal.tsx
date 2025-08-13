import React, { useState } from 'react';
import { Word } from '../../types/wordSet';
import './EditWordModal.css';

interface EditWordModalProps {
    word: Word;
    onClose: () => void;
    onUpdate: (wordId: number, englishWord: string, polishTranslation: string) => Promise<void>;
}

const EditWordModal: React.FC<EditWordModalProps> = ({ word, onClose, onUpdate }) => {
    const [englishWord, setEnglishWord] = useState(word.word);
    const [polishTranslation, setPolishTranslation] = useState(word.translation);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!englishWord.trim() || !polishTranslation.trim()) {
            setError('Oba pola są wymagane');
            return;
        }

        try {
            setLoading(true);
            setError(null);

            await onUpdate(word.id, englishWord.trim(), polishTranslation.trim());
        } catch (err: any) {
            setError(err.response?.data?.message || 'Nie udało się zaktualizować słówka');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content edit-word-modal" onClick={e => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Edytuj słówko</h2>
                    <button onClick={onClose} className="modal-close">×</button>
                </div>

                <form onSubmit={handleSubmit} className="edit-word-form">
                    <div className="form-group">
                        <label htmlFor="englishWord">Słowo angielskie</label>
                        <input
                            type="text"
                            id="englishWord"
                            value={englishWord}
                            onChange={(e) => setEnglishWord(e.target.value)}
                            placeholder="Wprowadź słowo angielskie"
                            maxLength={100}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="polishTranslation">Tłumaczenie polskie</label>
                        <input
                            type="text"
                            id="polishTranslation"
                            value={polishTranslation}
                            onChange={(e) => setPolishTranslation(e.target.value)}
                            placeholder="Wprowadź tłumaczenie"
                            maxLength={100}
                            required
                        />
                    </div>

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
                            disabled={loading || !englishWord.trim() || !polishTranslation.trim()}
                        >
                            {loading ? 'Zapisuję...' : 'Zapisz zmiany'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditWordModal;