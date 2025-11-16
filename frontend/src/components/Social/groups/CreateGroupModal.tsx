import React, { useState } from 'react';
import { CreateGroupRequest, StudyGroup } from '../../../types/social';
import { X, Users, Globe, Lock, Hash } from 'lucide-react';

interface CreateGroupModalProps {
    onClose: () => void;
    onCreate: (request: CreateGroupRequest) => Promise<StudyGroup | null>;
}

const CreateGroupModal: React.FC<CreateGroupModalProps> = ({
                                                               onClose,
                                                               onCreate
                                                           }) => {
    const [formData, setFormData] = useState<CreateGroupRequest>({
        name: '',
        description: '',
        isPrivate: false,
        maxMembers: 25
    });
    const [creating, setCreating] = useState(false);
    const [errors, setErrors] = useState<Record<string, string>>({});

    const validateForm = () => {
        const newErrors: Record<string, string> = {};
        if (!formData.name.trim()) {
            newErrors.name = 'Nazwa grupy jest wymagana';
        } else if (formData.name.length < 3) {
            newErrors.name = 'Nazwa musi mieć co najmniej 3 znaki';
        } else if (formData.name.length > 100) {
            newErrors.name = 'Nazwa nie może przekraczać 100 znaków';
        }
        if (!formData.description.trim()) {
            newErrors.description = 'Opis grupy jest wymagany';
        } else if (formData.description.length > 500) {
            newErrors.description = 'Opis nie może przekraczać 500 znaków';
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
        setCreating(true);
        try {
            const result = await onCreate(formData);
            if (result) {
                onClose();
            }
        } finally {
            setCreating(false);
        }
    };

    const handleInputChange = (field: keyof CreateGroupRequest, value: any) => {
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

    return (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="glass-box-flat w-full max-w-lg max-h-[90vh] flex flex-col">
                <div className="flex items-center justify-between p-6 border-b border-white/20">
                    <h2 className="text-xl font-semibold text-white">Utwórz nową grupę</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-300 hover:text-white transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-6 overflow-y-auto">
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">
                            Nazwa grupy *
                        </label>
                        <input
                            type="text"
                            value={formData.name}
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
                        <p className="text-gray-400 text-sm mt-1">
                            {formData.name.length}/100 znaków
                        </p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">
                            Opis grupy *
                        </label>
                        <textarea
                            value={formData.description}
                            onChange={(e) => handleInputChange('description', e.target.value)}
                            placeholder="Opisz czego dotyczy ta grupa i kto może dołączyć..."
                            rows={4}
                            className={`input-glass resize-none ${
                                errors.description ? 'border-red-500/50' : 'border-white/30'
                            }`}
                            maxLength={500}
                        />
                        {errors.description && (
                            <p className="text-red-400 text-sm mt-1">{errors.description}</p>
                        )}
                        <p className="text-gray-400 text-sm mt-1">
                            {formData.description.length}/500 znaków
                        </p>
                    </div>

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
                                    checked={formData.isPrivate}
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

                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">
                            Maksymalna liczba członków
                        </label>
                        <div className="relative">
                            <Users className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                            <input
                                type="number"
                                value={formData.maxMembers}
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
                        <p className="text-gray-400 text-sm mt-1">
                            Zalecane: 10-50 członków
                        </p>
                    </div>

                    <div className="bg-black/20 rounded-lg p-4">
                        <h3 className="font-medium text-white mb-2">Podgląd grupy</h3>
                        <div className="flex items-start gap-3">
                            <div className="w-12 h-12 bg-gradient-to-br from-blue-500/50 to-purple-600/50 rounded-lg flex items-center justify-center">
                                <Hash className="h-6 w-6 text-white" />
                            </div>
                            <div className="flex-1">
                                <div className="flex items-center gap-2 mb-1">
                                    <h4 className="font-semibold text-white">
                                        {formData.name || 'Nazwa grupy'}
                                    </h4>
                                    {formData.isPrivate ? (
                                        <Lock className="h-4 w-4 text-orange-300" />
                                    ) : (
                                        <Globe className="h-4 w-4 text-green-300" />
                                    )}
                                </div>
                                <p className="text-sm text-gray-300">
                                    {formData.description || 'Opis grupy...'}
                                </p>
                                <div className="flex items-center gap-1 text-xs text-gray-400 mt-1">
                                    <Users className="h-3 w-3" />
                                    <span>0/{formData.maxMembers} członków</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>

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
                        disabled={creating || !formData.name.trim() || !formData.description.trim()}
                        className="btn-primary-solid flex items-center gap-2"
                    >
                        {creating ? (
                            <>
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                                Tworzenie...
                            </>
                        ) : (
                            <>
                                Utwórz grupę
                            </>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CreateGroupModal;