import React, { useState } from 'react';
import { StudyGroup, UpdateGroupRequest } from '../../../types/social';
import { X, Save, Globe, Lock, Users } from 'lucide-react';

interface EditGroupModalProps {
    group: StudyGroup;
    onClose: () => void;
    onUpdate: (groupId: number, request: UpdateGroupRequest) => Promise<void>;
}

const EditGroupModal: React.FC<EditGroupModalProps> = ({
                                                           group,
                                                           onClose,
                                                           onUpdate
                                                       }) => {
    const [formData, setFormData] = useState<UpdateGroupRequest>({
        name: group.name,
        description: group.description,
        isPrivate: group.isPrivate,
        maxMembers: group.maxMembers
    });
    const [saving, setSaving] = useState(false);
    const [errors, setErrors] = useState<Record<string, string>>({});

    // ... (Logika walidacji i obsługi bez zmian) ...
    const validateForm = () => {
        const newErrors: Record<string, string> = {};
        if (!formData.name?.trim()) {
            newErrors.name = 'Nazwa grupy jest wymagana';
        } else if (formData.name.length < 3) {
            newErrors.name = 'Nazwa musi mieć co najmniej 3 znaki';
        }
        if (!formData.description?.trim()) {
            newErrors.description = 'Opis grupy jest wymagany';
        }
        if (formData.maxMembers && (formData.maxMembers < 2 || formData.maxMembers > 100)) {
            newErrors.maxMembers = 'Liczba członków musi być między 2 a 100';
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!validateForm()) return;
        setSaving(true);
        try {
            await onUpdate(group.id, formData);
            onClose();
        } finally {
            setSaving(false);
        }
    };

    const handleInputChange = (field: keyof UpdateGroupRequest, value: any) => {
        setFormData(prev => ({
            ...prev,
            [field]: value
        }));
        if (errors[field]) {
            setErrors(prev => ({
                ...prev,
                [field]: ''
            }));
        }
    };
    // ... (Koniec logiki) ...

    return (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="glass-box-flat w-full max-w-lg max-h-[90vh] flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-white/20">
                    <h2 className="text-xl font-semibold text-white">Edytuj grupę</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-300 hover:text-white transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                {/* Content */}
                <form onSubmit={handleSubmit} className="p-6 space-y-6 overflow-y-auto">
                    {/* Nazwa grupy */}
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">
                            Nazwa grupy *
                        </label>
                        <input
                            type="text"
                            value={formData.name || ''}
                            onChange={(e) => handleInputChange('name', e.target.value)}
                            placeholder="np. Angielski dla początkujących"
                            className={`input-glass ${
                                errors.name ? 'border-red-500/50' : 'border-white/30'
                            }`}
                            maxLength={100}
                        />
                        {errors.name && (
                            <p className="text-red-400 text-sm mt-1">{errors.name}</p>
                        )}
                    </div>

                    {/* Opis */}
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">
                            Opis grupy *
                        </label>
                        <textarea
                            value={formData.description || ''}
                            onChange={(e) => handleInputChange('description', e.target.value)}
                            placeholder="Opisz czego dotyczy ta grupa..."
                            rows={4}
                            className={`input-glass resize-none ${
                                errors.description ? 'border-red-500/50' : 'border-white/30'
                            }`}
                            maxLength={500}
                        />
                        {errors.description && (
                            <p className="text-red-400 text-sm mt-1">{errors.description}</p>
                        )}
                    </div>

                    {/* Ustawienia prywatności */}
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-3">
                            Ustawienia prywatności
                        </label>
                        <div className="space-y-3">
                            <label className={`setting-option ${!formData.isPrivate ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="privacy"
                                    checked={!formData.isPrivate}
                                    onChange={() => handleInputChange('isPrivate', false)}
                                    className="form-radio"
                                />
                                <div className="ml-3 flex-1">
                                    <div className="flex items-center gap-2">
                                        <Globe className="h-5 w-5 text-green-300" />
                                        <span className="font-medium text-white">Grupa publiczna</span>
                                    </div>
                                    <p className="text-sm text-gray-300 mt-1">
                                        Każdy może znaleźć i dołączyć do tej grupy
                                    </p>
                                </div>
                            </label>

                            <label className={`setting-option ${formData.isPrivate ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="privacy"
                                    checked={!!formData.isPrivate}
                                    onChange={() => handleInputChange('isPrivate', true)}
                                    className="form-radio"
                                />
                                <div className="ml-3 flex-1">
                                    <div className="flex items-center gap-2">
                                        <Lock className="h-5 w-5 text-orange-300" />
                                        <span className="font-medium text-white">Grupa prywatna</span>
                                    </div>
                                    <p className="text-sm text-gray-300 mt-1">
                                        Tylko osoby z kodem zaproszenia mogą dołączyć
                                    </p>
                                </div>
                            </label>
                        </div>
                    </div>

                    {/* Max Members */}
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">
                            Maksymalna liczba członków
                        </label>
                        <div className="relative">
                            <Users className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                            <input
                                type="number"
                                value={formData.maxMembers || 25}
                                onChange={(e) => handleInputChange('maxMembers', parseInt(e.target.value) || 25)}
                                min="2"
                                max="100"
                                className={`input-glass pl-10 ${
                                    errors.maxMembers ? 'border-red-500/50' : 'border-white/30'
                                }`}
                            />
                        </div>
                        {errors.maxMembers && (
                            <p className="text-red-400 text-sm mt-1">{errors.maxMembers}</p>
                        )}
                    </div>
                </form>

                {/* Footer */}
                <div className="flex justify-end gap-3 p-6 border-t border-white/20">
                    <button
                        type="button"
                        onClick={onClose}
                        className="btn-glass"
                    >
                        Anuluj
                    </button>
                    <button
                        type="submit"
                        onClick={handleSubmit}
                        disabled={saving || !formData.name?.trim() || !formData.description?.trim()}
                        className="btn-primary-solid flex items-center gap-2"
                    >
                        {saving ? (
                            <>
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                                Zapisywanie...
                            </>
                        ) : (
                            <>
                                <Save className="h-4 w-4" />
                                Zapisz zmiany
                            </>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
};

// --- DODAJ TE STYLE DO GLOBALNEGO CSS (jeśli jeszcze ich nie masz) ---
/*
.glass-box-flat {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.15);
    padding: 0;
    overflow: hidden;
}
.input-glass {
    width: 100%;
    padding: 0.8rem 1rem;
    border-radius: 8px;
    font-size: 1rem;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: white;
    transition: all 0.2s ease;
}
.input-glass::placeholder {
    color: rgba(255, 255, 255, 0.5);
}
.input-glass:focus {
    outline: none;
    border-color: rgba(255, 255, 255, 0.8);
    background: rgba(255, 255, 255, 0.2);
}
.form-radio {
    width: 18px;
    height: 18px;
    accent-color: white;
    cursor: pointer;
}
.setting-option {
    display: flex;
    align-items: center;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 12px;
    border: 2px solid rgba(255, 255, 255, 0.1);
    cursor: pointer;
    transition: all 0.2s ease;
}
.setting-option:hover {
    background: rgba(255, 255, 255, 0.1);
}
.setting-option.selected {
    background: rgba(255, 255, 255, 0.15);
    border-color: rgba(255, 255, 255, 0.4);
}
.btn-primary-solid { ... }
.btn-glass { ... }
*/

export default EditGroupModal;