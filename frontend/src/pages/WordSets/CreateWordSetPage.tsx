import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
import { CreateWordSetRequest } from '../../types/wordSet';
import { ArrowLeft } from 'lucide-react';
import './CreateWordSetPage.css';

const CreateWordSetPage: React.FC = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState<CreateWordSetRequest>({
        title: '',
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

        if (!formData.title.trim()) {
            setError('Nazwa zestawu jest wymagana');
            return;
        }

        try {
            setLoading(true);
            setError(null);

            const newWordSet = await wordSetService.createWordSet({
                title: formData.title.trim(),
                description: formData.description?.trim() || undefined
            });

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
                    className="btn-glass-icon"
                >
                    <ArrowLeft size={18} /> Powrót do zestawów
                </button>
                <h1>Nowy zestaw słówek</h1>
            </div>

            <form onSubmit={handleSubmit} className="create-word-set-form">
                <div className="form-group">
                    <label htmlFor="title">Nazwa zestawu *</label>
                    <input
                        type="text"
                        id="title"
                        name="title"
                        value={formData.title}
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
                        className="btn-glass"
                        disabled={loading}
                    >
                        Anuluj
                    </button>
                    <button
                        type="submit"
                        className="btn-primary-solid"
                        disabled={loading || !formData.title.trim()}
                    >
                        {loading ? 'Tworzę...' : 'Utwórz zestaw'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreateWordSetPage;