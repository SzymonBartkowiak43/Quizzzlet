import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
import { WordSet } from '../../types/wordSet';
import './WordSetsPage.css';

const WordSetsPage: React.FC = () => {
    const [wordSets, setWordSets] = useState<WordSet[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        loadWordSets();
    }, []);

    const loadWordSets = async () => {
        try {
            setLoading(true);
            const sets = await wordSetService.getAllWordSets();
            setWordSets(sets);
            setError(null);
        } catch (err) {
            setError('Nie udało się załadować zestawów');
            console.error('Error loading word sets:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteSet = async (setId: number) => {
        if (!window.confirm('Czy na pewno chcesz usunąć ten zestaw?')) return;

        try {
            await wordSetService.deleteWordSet(setId);
            setWordSets(prev => prev.filter(set => set.id !== setId));
        } catch (err) {
            setError('Nie udało się usunąć zestawu');
            console.error('Error deleting word set:', err);
        }
    };

    if (loading) {
        return (
            <div className="word-sets-page">
                <div className="loading">Ładowanie zestawów...</div>
            </div>
        );
    }

    return (
        <div className="word-sets-page">
            <div className="word-sets-header">
                <h1>Moje zestawy słówek</h1>
                <Link to="/word-sets/new" className="btn btn-primary">
                    + Nowy zestaw
                </Link>
            </div>

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            <div className="word-sets-grid">
                {wordSets.map(wordSet => (
                    <div key={wordSet.id} className="word-set-card">
                        <div className="word-set-header">
                            <h3>{wordSet.name}</h3>
                            <div className="word-set-actions">
                                <Link to={`/word-sets/${wordSet.id}`} className="btn btn-small">
                                    Otwórz
                                </Link>
                                <Link to={`/word-sets/${wordSet.id}/edit`} className="btn btn-small btn-secondary">
                                    Edytuj
                                </Link>
                                <button
                                    onClick={() => handleDeleteSet(wordSet.id)}
                                    className="btn btn-small btn-danger"
                                >
                                    Usuń
                                </button>
                            </div>
                        </div>

                        {wordSet.description && (
                            <p className="word-set-description">{wordSet.description}</p>
                        )}

                        <div className="word-set-stats">
                            <span className="word-count">{wordSet.words.length} słówek</span>
                            <span className="created-date">
                Utworzono: {new Date(wordSet.createdAt).toLocaleDateString('pl-PL')}
              </span>
                        </div>
                    </div>
                ))}
            </div>

            {wordSets.length === 0 && !loading && (
                <div className="empty-state">
                    <h3>Nie masz jeszcze żadnych zestawów</h3>
                    <p>Stwórz pierwszy zestaw słówek, aby rozpocząć naukę!</p>
                    <Link to="/word-sets/new" className="btn btn-primary">
                        Stwórz pierwszy zestaw
                    </Link>
                </div>
            )}
        </div>
    );
};

export default WordSetsPage;