import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
import { WordSet, UpdateWordSetRequest } from '../../types/wordSet';
import './EditWordSetPage.css';

const EditWordSetPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [wordSet, setWordSet] = useState<WordSet | null>(null);
    const [formData, setFormData] = useState<UpdateWordSetRequest>({
        name: '',
        description: ''
    });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (id) {
            loadWordSet();
        }
    }, [id]);

    const loadWordSet = async () => {
        try {
            setLoading(true);
            const sets = await wordSetService.getAllWordSets();
            const currentSet = sets.find(set => set.id === Number(id));

            if (!currentSet) {
                setError('Nie znaleziono zestawu');
                return;
            }

            setWordSet(currentSet);
            setFormData({
                name: currentSet.name,
                description: currentSet.description || ''
            });
            setError(null);
        } catch (err) {
            setError('Nie udało się załadować zestawu');
            console.error('Error loading word set:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!formData.name.trim()) {
            setError('Nazwa zestawu jest wymagana');
            return;
        }

        if (!wordSet) return;

        try {
            setSaving(true);
            setError(null);

            await wordSetService.updateWordSet(wordSet.id, {
                name: formData.name.trim(),
                description: formData.description?.trim() || undefined
            });

            // Przekieruj do szczegółów zestawu
            navigate(`/word-sets/${wordSet.id}`);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Nie udało się zaktualizować zestawu');
            console.error('Error updating word set:', err);
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async () => {
        if (!wordSet) return;

        const confirmMessage = `Czy na pewno chcesz usunąć zestaw "${wordSet.name}"?\n\nTa akcja jest nieodwracalna i usunie również wszystkie słówka z zestawu.`;

        if (!window.confirm(confirmMessage)) return;

        try {
            setSaving(true);
            await wordSetService.deleteWordSet(wordSet.id);
            navigate('/word-sets');
        } catch (err: any) {
            setError(err.response?.data?.message || 'Nie udało się usunąć zestawu');
            console.error('Error deleting word set:', err);
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className="edit-word-set-page">
                <div className="loading">Ładowanie zestawu...</div>
            </div>
        );
    }

    if (error && !wordSet) {
        return (
            <div className="edit-word-set-page">
                <div className="error-message">{error}</div>
                <button onClick={() => navigate('/word-sets')} className="btn btn-primary">
                    Powrót do zestawów
                </button>
            </div>
        );
    }

    if (!wordSet) {
        return (
            <div className="edit-word-set-page">
                <div className="error-message">Nie znaleziono zestawu</div>
                <button onClick={() => navigate('/word-sets')} className="btn btn-primary">
                    Powrót do zestawów
                </button>
            </div>
        );
    }

    return (
        <div className="edit-word-set-page">
            <div className="page-header">
                <button
                    onClick={() => navigate(`/word-sets/${wordSet.id}`)}
                    className="btn btn-secondary"
                >
                    ← Powrót do zestawu
                </button>
                <h1>Edytuj zestaw słówek</h1>
            </div>

            <div className="edit-content">
                <div className="edit-form-section">
                    <form onSubmit={handleSubmit} className="edit-word-set-form">
                        <div className="form-group">
                            <label htmlFor="name">Nazwa zestawu *</label>
                            <input
                                type="text"
                                id="name"
                                name="name"
                                value={formData.name}
                                onChange={handleInputChange}
                                placeholder="np. Słówka z angielskiego - poziom podstawowy"
                                maxLength={100}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="description">Opis (opcjonalny)</label>
                            <textarea
                                id="description"
                                name="description"
                                value={formData.description}
                                onChange={handleInputChange}
                                placeholder="Krótki opis zestawu słówek..."
                                maxLength={500}
                                rows={3}
                            />
                        </div>

                        {error && (
                            <div className="error-message">
                                {error}
                            </div>
                        )}

                        <div className="form-actions">
                            <button
                                type="button"
                                onClick={() => navigate(`/word-sets/${wordSet.id}`)}
                                className="btn btn-secondary"
                                disabled={saving}
                            >
                                Anuluj
                            </button>
                            <button
                                type="submit"
                                className="btn btn-primary"
                                disabled={saving || !formData.name.trim()}
                            >
                                {saving ? 'Zapisuję...' : 'Zapisz zmiany'}
                            </button>
                        </div>
                    </form>
                </div>

                <div className="word-set-info">
                    <div className="info-card">
                        <h3>Informacje o zestawie</h3>
                        <div className="info-item">
                            <span className="info-label">Liczba słówek:</span>
                            <span className="info-value">{wordSet.words.length}</span>
                        </div>
                        <div className="info-item">
                            <span className="info-label">Utworzono:</span>
                            <span className="info-value">
                {new Date(wordSet.createdAt).toLocaleDateString('pl-PL', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                })}
              </span>
                        </div>
                        <div className="info-item">
                            <span className="info-label">Ostatnia modyfikacja:</span>
                            <span className="info-value">
                {new Date(wordSet.updatedAt).toLocaleDateString('pl-PL', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                })}
              </span>
                        </div>
                    </div>

                    <div className="danger-zone">
                        <h3>Strefa niebezpieczna</h3>
                        <p>Usuń ten zestaw na stałe. Ta akcja jest nieodwracalna.</p>
                        <button
                            onClick={handleDelete}
                            className="btn btn-danger"
                            disabled={saving}
                        >
                            {saving ? 'Usuwam...' : 'Usuń zestaw'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default EditWordSetPage;