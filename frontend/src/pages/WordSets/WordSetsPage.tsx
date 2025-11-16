import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
import { WordSet } from '../../types/wordSet';
import { Plus, Eye, Edit2, Trash2, BookText } from 'lucide-react';
import './WordSetsPage.css';

const WordSetsPage: React.FC = () => {
    const [wordSets, setWordSets] = useState<WordSet[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

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

    const handleDeleteSet = async (e: React.MouseEvent, setId: number) => {
        e.stopPropagation();
        if (!window.confirm('Czy na pewno chcesz usunąć ten zestaw?')) return;

        try {
            await wordSetService.deleteWordSet(setId);
            setWordSets(prev => prev.filter(set => set.id !== setId));
        } catch (err) {
            setError('Nie udało się usunąć zestawu');
            console.error('Error deleting word set:', err);
        }
    };

    const handleCardClick = (setId: number) => {
        navigate(`/word-sets/${setId}`);
    };

    const handleEditClick = (e: React.MouseEvent, setId: number) => {
        e.stopPropagation();
        navigate(`/word-sets/${setId}/edit`);
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
            <div className="word-sets-header-container">
                <div className="word-sets-header">
                    <h1>Moje zestawy słówek</h1>
                    <Link to="/word-sets/new" className="btn-new-set">
                        <Plus size={20} />
                        Nowy zestaw
                    </Link>
                </div>
            </div>

            {error && (
                <div className="word-sets-content-container">
                    <div className="error-message">
                        {error}
                    </div>
                </div>
            )}

            <div className="word-sets-grid-container">
                <div className="word-sets-grid">
                    {wordSets.map(wordSet => (
                        <div
                            key={wordSet.id}
                            className="word-set-card"
                            onClick={() => handleCardClick(wordSet.id)}
                        >
                            <div className="word-set-card-icon">
                                <BookText size={32} />
                            </div>

                            <div className="word-set-header">
                                <h3>{wordSet.title}</h3>
                                <div className="word-set-actions">
                                    <button
                                        onClick={(e) => handleEditClick(e, wordSet.id)}
                                        className="btn-icon-action"
                                        aria-label="Edytuj"
                                    >
                                        <Edit2 size={18} />
                                    </button>
                                    <button
                                        onClick={(e) => handleDeleteSet(e, wordSet.id)}
                                        className="btn-icon-action btn-danger"
                                        aria-label="Usuń"
                                    >
                                        <Trash2 size={18} />
                                    </button>
                                </div>
                            </div>

                            {wordSet.description && (
                                <p className="word-set-description">{wordSet.description}</p>
                            )}

                            <div className="word-set-stats">
                                <span className="word-count">{wordSet.words.length} słówek</span>
                                <span className="created-date">
                                    {new Date(wordSet.createdAt).toLocaleDateString('pl-PL')}
                                </span>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {wordSets.length === 0 && !loading && (
                <div className="word-sets-content-container">
                    <div className="empty-state">
                        <div className="word-set-card-icon">
                            <BookText size={40} />
                        </div>
                        <h3>Nie masz jeszcze żadnych zestawów</h3>
                        <p>Stwórz pierwszy zestaw słówek, aby rozpocząć naukę!</p>
                        <Link to="/word-sets/new" className="btn-new-set">
                            Stwórz pierwszy zestaw
                        </Link>
                    </div>
                </div>
            )}
        </div>
    );
};

export default WordSetsPage;