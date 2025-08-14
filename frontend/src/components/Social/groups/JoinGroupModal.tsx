import React, { useState } from 'react';
import { X, Hash, Users, Key } from 'lucide-react';

interface JoinGroupModalProps {
    onClose: () => void;
    onJoinByCode: (inviteCode: string) => Promise<void>;
}

const JoinGroupModal: React.FC<JoinGroupModalProps> = ({
                                                           onClose,
                                                           onJoinByCode
                                                       }) => {
    const [inviteCode, setInviteCode] = useState('');
    const [joining, setJoining] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!inviteCode.trim()) return;

        setJoining(true);
        setError('');

        try {
            await onJoinByCode(inviteCode.trim().toUpperCase());
            onClose();
        } catch (err: any) {
            setError(err.response?.data?.message || 'Nieprawidłowy kod zaproszenia');
        } finally {
            setJoining(false);
        }
    };

    const handleCodeChange = (value: string) => {
        // Format code as user types (uppercase, max 8 chars)
        const formatted = value.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 8);
        setInviteCode(formatted);
        if (error) setError('');
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-md">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-gray-200">
                    <h2 className="text-xl font-semibold text-gray-900">Dołącz do grupy</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-600 transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                {/* Content */}
                <form onSubmit={handleSubmit} className="p-6 space-y-6">
                    {/* Instructions */}
                    <div className="text-center">
                        <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                            <Key className="h-8 w-8 text-blue-600" />
                        </div>
                        <h3 className="text-lg font-medium text-gray-900 mb-2">
                            Wprowadź kod zaproszenia
                        </h3>
                        <p className="text-sm text-gray-600">
                            Jeśli masz kod zaproszenia do prywatnej grupy, wprowadź go poniżej
                        </p>
                    </div>

                    {/* Invite Code Input */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Kod zaproszenia
                        </label>
                        <div className="relative">
                            <Hash className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                            <input
                                type="text"
                                value={inviteCode}
                                onChange={(e) => handleCodeChange(e.target.value)}
                                placeholder="ABCD1234"
                                className={`w-full pl-10 pr-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-center font-mono text-lg tracking-widest ${
                                    error ? 'border-red-500' : 'border-gray-300'
                                }`}
                                maxLength={8}
                                style={{ letterSpacing: '0.2em' }}
                            />
                        </div>
                        {error && (
                            <p className="text-red-500 text-sm mt-2 flex items-center gap-1">
                                <X className="h-4 w-4" />
                                {error}
                            </p>
                        )}
                        <p className="text-gray-500 text-sm mt-2">
                            Kod składa się z 8 liter i cyfr
                        </p>
                    </div>

                    {/* Example codes for demo */}
                    <div className="bg-blue-50 rounded-lg p-4">
                        <h4 className="font-medium text-blue-900 mb-2">💡 Przykładowe kody do testowania:</h4>
                        <div className="space-y-2">
                            <button
                                type="button"
                                onClick={() => handleCodeChange('ENGL2024')}
                                className="block w-full text-left text-sm bg-white rounded px-3 py-2 hover:bg-blue-100 transition-colors font-mono"
                            >
                                ENGL2024 - Angielski dla początkujących
                            </button>
                            <button
                                type="button"
                                onClick={() => handleCodeChange('ADVENG01')}
                                className="block w-full text-left text-sm bg-white rounded px-3 py-2 hover:bg-blue-100 transition-colors font-mono"
                            >
                                ADVENG01 - Advanced English Club
                            </button>
                            <button
                                type="button"
                                onClick={() => handleCodeChange('CONV2024')}
                                className="block w-full text-left text-sm bg-white rounded px-3 py-2 hover:bg-blue-100 transition-colors font-mono"
                            >
                                CONV2024 - Konwersacje po angielsku
                            </button>
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
                        disabled={joining || !inviteCode.trim() || inviteCode.length !== 8}
                        className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                    >
                        {joining ? (
                            <>
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                                Dołączanie...
                            </>
                        ) : (
                            <>
                                <Users className="h-4 w-4" />
                                Dołącz do grupy
                            </>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default JoinGroupModal;