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
                    <h2 className="text-xl font-semibold text-gray-900">Edytuj grupę</h2>
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
                            value={formData.name || ''}
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
                    </div>

                    {/* Description */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Opis grupy *
                        </label>
                        <textarea
                            value={formData.description || ''}
                            onChange={(e) => handleInputChange('description', e.target.value)}
                            placeholder="Opisz czego dotyczy ta grupa..."
                            rows={4}
                            className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none ${
                                errors.description ? 'border-red-500' : 'border-gray-300'
                            }`}
                            maxLength={500}
                        />
                        {errors.description && (
                            <p className="text-red-500 text-sm mt-1">{errors.description}</p>
                        )}
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
                                value={formData.maxMembers || 25}
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
                        disabled={saving || !formData.name?.trim() || !formData.description?.trim()}
                        className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                    >
                        {saving ? (
                            <>
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
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

export default EditGroupModal;