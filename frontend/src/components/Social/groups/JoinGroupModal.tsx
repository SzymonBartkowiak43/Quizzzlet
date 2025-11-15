import React, { useState } from 'react';
import { X, Hash, Users, Key } from 'lucide-react';
import './GroupDetails.css';

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
        const formatted = value.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 8);
        setInviteCode(formatted);
        if (error) setError('');
    };

    return (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="glass-box-flat w-full max-w-md flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-white/20">
                    <h2 className="text-xl font-semibold text-white">Dołącz do grupy</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-300 hover:text-white transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                {/* Content */}
                <form onSubmit={handleSubmit} className="p-6 space-y-6 overflow-y-auto">
                    {/* Instrukcje */}
                    <div className="text-center">
                        <div className="w-16 h-16 bg-blue-500/30 rounded-full flex items-center justify-center mx-auto mb-4">
                            <Key className="h-8 w-8 text-blue-200" />
                        </div>
                        <h3 className="text-lg font-medium text-white mb-2">
                            Wprowadź kod zaproszenia
                        </h3>
                        <p className="text-sm text-gray-300">
                            Jeśli masz kod zaproszenia do prywatnej grupy, wprowadź go poniżej
                        </p>
                    </div>

                    {/* Input kodu */}
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">
                            Kod zaproszenia
                        </label>
                        <div className="relative">
                            <Hash className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                            <input
                                type="text"
                                value={inviteCode}
                                onChange={(e) => handleCodeChange(e.target.value)}
                                placeholder="ABCD1234"
                                className={`w-full pl-10 pr-4 py-3 border rounded-lg focus:ring-2 focus:ring-white/50 focus:border-transparent text-center font-mono text-lg tracking-widest bg-white/10 text-white placeholder-gray-400 ${
                                    error ? 'border-red-500/50' : 'border-white/30'
                                }`}
                                maxLength={8}
                                style={{ letterSpacing: '0.2em' }}
                            />
                        </div>
                        {error && (
                            <p className="text-red-400 text-sm mt-2 flex items-center gap-1">
                                <X className="h-4 w-4" />
                                {error}
                            </p>
                        )}
                        <p className="text-gray-400 text-sm mt-2">
                            Kod składa się z 8 liter i cyfr
                        </p>
                    </div>

                    {/* Przykładowe kody */}
                    <div className="bg-black/20 rounded-lg p-4">
                        <h4 className="font-medium text-white mb-2">💡 Przykładowe kody do testowania:</h4>
                        <div className="space-y-2">
                            <button
                                type="button"
                                onClick={() => handleCodeChange('ENGL2024')}
                                className="block w-full text-left text-sm bg-white/10 rounded px-3 py-2 hover:bg-white/20 transition-colors font-mono text-gray-200"
                            >
                                ENGL2024 - Angielski dla początkujących
                            </button>
                            <button
                                type="button"
                                onClick={() => handleCodeChange('ADVENG01')}
                                className="block w-full text-left text-sm bg-white/10 rounded px-3 py-2 hover:bg-white/20 transition-colors font-mono text-gray-200"
                            >
                                ADVENG01 - Advanced English Club
                            </button>
                            <button
                                type="button"
                                onClick={() => handleCodeChange('CONV2024')}
                                className="block w-full text-left text-sm bg-white/10 rounded px-3 py-2 hover:bg-white/20 transition-colors font-mono text-gray-200"
                            >
                                CONV2024 - Konwersacje po angielsku
                            </button>
                        </div>
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
                        disabled={joining || !inviteCode.trim() || inviteCode.length !== 8}
                        className="btn-primary-solid flex items-center gap-2"
                    >
                        {joining ? (
                            <>
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
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