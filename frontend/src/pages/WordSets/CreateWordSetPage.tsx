import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { wordSetService } from '../../services/wordSetService';
// Upewnij się, że zaktualizowałeś typ w pliku types/wordSet.ts na pole 'name'!
import { CreateWordSetRequest } from '../../types/wordSet';
import { ArrowLeft } from 'lucide-react';
import './CreateWordSetPage.css';

// Lista dostępnych języków
const LANGUAGES = [
    { code: 'en', name: 'Angielski' },
    { code: 'pl', name: 'Polski' },
    { code: 'de', name: 'Niemiecki' },
    { code: 'es', name: 'Hiszpański' },
    { code: 'fr', name: 'Francuski' },
    { code: 'it', name: 'Włoski' },
];

const CreateWordSetPage: React.FC = () => {
    const navigate = useNavigate();

    // POPRAWKA: Używamy 'name' zamiast 'title', aby pasowało do Backend DTO
    const [formData, setFormData] = useState<CreateWordSetRequest>({
        name: '',               // <<< ZMIANA Z 'title'
        description: '',
        language: 'en',         // Domyślnie Angielski
        translationLanguage: 'pl' // Domyślnie Polski
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleInputChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
    ) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Walidacja pola 'name'
        if (!formData.name.trim()) {
            setError('Nazwa zestawu jest wymagana');
            return;
        }

        try {
            setLoading(true);
            setError(null);

            // Wysyłamy dane do serwisu. Teraz struktura idealnie pasuje do Javy.
            const newWordSet = await wordSetService.createWordSet({
                name: formData.name.trim(),
                description: formData.description?.trim() || undefined,
                language: formData.language,
                translationLanguage: formData.translationLanguage
            });

            navigate(`/word-sets/${newWordSet.id}`);
        } catch (err: any) {
            // Obsługa błędu z backendu (np. validation errors)
            const backendMsg = err.response?.data?.message;
            const backendErrors = err.response?.data?.errors; // Często Spring zwraca mapę błędów

            if (backendErrors) {
                // Jeśli backend zwraca listę błędów walidacji, wyświetl pierwszy
                const firstError = Object.values(backendErrors)[0];
                setError(`${firstError}`);
            } else {
                setError(backendMsg || 'Nie udało się utworzyć zestawu');
            }
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
                    <label htmlFor="name">Nazwa zestawu *</label>
                    <input
                        type="text"
                        id="name"
                        name="name"  // <<< WAŻNE: atrybut name musi być "name"
                        value={formData.name} // <<< WAŻNE: wartość z pola name
                        onChange={handleInputChange}
                        placeholder="np. Słówka z angielskiego - poziom podstawowy"
                        maxLength={100}
                        required
                    />
                </div>

                {/* --- POLA WYBORU JĘZYKA --- */}
                <div className="form-row-languages" style={{ display: 'flex', gap: '1rem' }}>
                    <div className="form-group" style={{ flex: 1 }}>
                        <label htmlFor="language">Język słówek (Nauka)</label>
                        <select
                            id="language"
                            name="language"
                            value={formData.language}
                            onChange={handleInputChange}
                            className="form-select"
                        >
                            {LANGUAGES.map(lang => (
                                <option key={lang.code} value={lang.code}>
                                    {lang.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group" style={{ flex: 1 }}>
                        <label htmlFor="translationLanguage">Język tłumaczenia</label>
                        <select
                            id="translationLanguage"
                            name="translationLanguage"
                            value={formData.translationLanguage}
                            onChange={handleInputChange}
                            className="form-select"
                        >
                            {LANGUAGES.map(lang => (
                                <option key={lang.code} value={lang.code}>
                                    {lang.name}
                                </option>
                            ))}
                        </select>
                    </div>
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