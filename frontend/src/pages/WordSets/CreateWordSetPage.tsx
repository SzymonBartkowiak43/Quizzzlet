import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
import { CreateWordSetRequest } from '../../types/wordSet';
import './CreateWordSetPage.css';

const CreateWordSetPage: React.FC = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState<CreateWordSetRequest>({
        name: '',
        description: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

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

        try {
            setLoading(true);
            setError(null);

            const newWordSet = await wordSetService.createWordSet({
                name: formData.name.trim(),
                description: formData.description?.trim() || undefined
            });

            // Przekieruj do szczegółów nowego zestawu
            navigate(`/word-sets/${newWordSet.id}`);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Nie udało się utworzyć zestawu');
            console.error('Error creating word set:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="create-word-set-page">
            <div className="page-header">
                <button
                    onClick={() => navigate('/word-sets')}
                    className="btn btn-secondary"
                >
                    ← Powrót do zestawów
                </button>
                <h1>Nowy zestaw słówek</h1>
            </div>

            <form onSubmit={handleSubmit} className="create-word-set-form">
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
                        onClick={() => navigate('/word-sets')}
                        className="btn btn-secondary"
                        disabled={loading}
                    >
                        Anuluj
                    </button>
                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={loading || !formData.name.trim()}
                    >
                        {loading ? 'Tworzę...' : 'Utwórz zestaw'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreateWordSetPage;