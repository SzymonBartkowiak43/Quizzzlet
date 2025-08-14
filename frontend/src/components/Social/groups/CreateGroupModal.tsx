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

        // Clear error for this field
        if (errors[field]) {
            setErrors(prev => ({
                ...prev,
                [field]: ''
            }));
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-lg max-h-[90vh] overflow-y-auto">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-gray-200">
                    <h2 className="text-xl font-semibold text-gray-900">Utwórz nową grupę</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-600 transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                {/* Content */}
                <form onSubmit={handleSubmit} className="p-6 space-y-6">
                    {/* Group Name */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Nazwa grupy *
                        </label>
                        <input
                            type="text"
                            value={formData.name}
                            onChange={(e) => handleInputChange('name', e.target.value)}
                            placeholder="np. Angielski dla początkujących"
                            className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                                errors.name ? 'border-red-500' : 'border-gray-300'
                            }`}
                            maxLength={100}
                        />
                        {errors.name && (
                            <p className="text-red-500 text-sm mt-1">{errors.name}</p>
                        )}
                        <p className="text-gray-500 text-sm mt-1">
                            {formData.name.length}/100 znaków
                        </p>
                    </div>

                    {/* Description */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Opis grupy *
                        </label>
                        <textarea
                            value={formData.description}
                            onChange={(e) => handleInputChange('description', e.target.value)}
                            placeholder="Opisz czego dotyczy ta grupa i kto może dołączyć..."
                            rows={4}
                            className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none ${
                                errors.description ? 'border-red-500' : 'border-gray-300'
                            }`}
                            maxLength={500}
                        />
                        {errors.description && (
                            <p className="text-red-500 text-sm mt-1">{errors.description}</p>
                        )}
                        <p className="text-gray-500 text-sm mt-1">
                            {formData.description.length}/500 znaków
                        </p>
                    </div>

                    {/* Privacy Settings */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-3">
                            Ustawienia prywatności
                        </label>
                        <div className="space-y-3">
                            <label className="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50 transition-colors">
                                <input
                                    type="radio"
                                    name="privacy"
                                    checked={!formData.isPrivate}
                                    onChange={() => handleInputChange('isPrivate', false)}
                                    className="text-blue-600 focus:ring-blue-500"
                                />
                                <div className="ml-3 flex-1">
                                    <div className="flex items-center gap-2">
                                        <Globe className="h-5 w-5 text-green-600" />
                                        <span className="font-medium text-gray-900">Grupa publiczna</span>
                                    </div>
                                    <p className="text-sm text-gray-600 mt-1">
                                        Każdy może znaleźć i dołączyć do tej grupy
                                    </p>
                                </div>
                            </label>

                            <label className="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50 transition-colors">
                                <input
                                    type="radio"
                                    name="privacy"
                                    checked={formData.isPrivate}
                                    onChange={() => handleInputChange('isPrivate', true)}
                                    className="text-blue-600 focus:ring-blue-500"
                                />
                                <div className="ml-3 flex-1">
                                    <div className="flex items-center gap-2">
                                        <Lock className="h-5 w-5 text-orange-600" />
                                        <span className="font-medium text-gray-900">Grupa prywatna</span>
                                    </div>
                                    <p className="text-sm text-gray-600 mt-1">
                                        Tylko osoby z kodem zaproszenia mogą dołączyć
                                    </p>
                                </div>
                            </label>
                        </div>
                    </div>

                    {/* Max Members */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
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
                                className={`w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                                    errors.maxMembers ? 'border-red-500' : 'border-gray-300'
                                }`}
                            />
                        </div>
                        {errors.maxMembers && (
                            <p className="text-red-500 text-sm mt-1">{errors.maxMembers}</p>
                        )}
                        <p className="text-gray-500 text-sm mt-1">
                            Recommended: 10-50 członków dla optymalnej komunikacji
                        </p>
                    </div>

                    {/* Preview */}
                    <div className="bg-gray-50 rounded-lg p-4">
                        <h3 className="font-medium text-gray-900 mb-2">Podgląd grupy</h3>
                        <div className="flex items-start gap-3">
                            <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                                <Hash className="h-6 w-6 text-white" />
                            </div>
                            <div className="flex-1">
                                <div className="flex items-center gap-2 mb-1">
                                    <h4 className="font-semibold text-gray-900">
                                        {formData.name || 'Nazwa grupy'}
                                    </h4>
                                    {formData.isPrivate ? (
                                        <Lock className="h-4 w-4 text-orange-600" />
                                    ) : (
                                        <Globe className="h-4 w-4 text-green-600" />
                                    )}
                                </div>
                                <p className="text-sm text-gray-600">
                                    {formData.description || 'Opis grupy...'}
                                </p>
                                <div className="flex items-center gap-1 text-xs text-gray-500 mt-1">
                                    <Users className="h-3 w-3" />
                                    <span>0/{formData.maxMembers} członków</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>

                {/* Footer */}
                <div className="flex justify-end gap-3 p-6 border-t border-gray-200">
                    <button
                        type="button"
                        onClick={onClose}
                        className="bg-gray-100 text-gray-700 px-6 py-2 rounded-lg hover:bg-gray-200 transition-colors"
                    >
                        Anuluj
                    </button>
                    <button
                        type="submit"
                        onClick={handleSubmit}
                        disabled={creating || !formData.name.trim() || !formData.description.trim()}
                        className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                    >
                        {creating ? (
                            <>
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                                Tworzenie...
                            </>
                        ) : (
                            <>
                                {/*<Plus className="h-4 w-4" />*/}
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