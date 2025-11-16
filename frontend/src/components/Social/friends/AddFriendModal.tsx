import React, { useState } from 'react';
import { X, Search, UserPlus, Mail, Users } from 'lucide-react';

interface AddFriendModalProps {
    onClose: () => void;
    onSendRequest: (userId: number) => Promise<void>;
}

const AddFriendModal: React.FC<AddFriendModalProps> = ({
                                                           onClose,
                                                           onSendRequest
                                                       }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState<any[]>([]);
    const [loading, setLoading] = useState(false);
    const [sending, setSending] = useState<number | null>(null);

    const handleSearch = async () => {
        if (!searchTerm.trim()) return;

        setLoading(true);
        try {
            const mockResults = [
                { id: 4, name: 'Jan Kowalski', email: 'jan@example.com' },
                { id: 5, name: 'Maria Nowak', email: 'maria@example.com' }
            ].filter(user =>
                user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                user.email.toLowerCase().includes(searchTerm.toLowerCase())
            );

            setSearchResults(mockResults);
        } catch (error) {
            console.error('Search error:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSendRequest = async (userId: number) => {
        setSending(userId);
        try {
            await onSendRequest(userId);
            setSearchResults(prev => prev.filter(user => user.id !== userId));
        } finally {
            setSending(null);
        }
    };

    return (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="glass-box-flat w-full max-w-md flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-white/20">
                    <h2 className="text-lg font-semibold text-white">Dodaj przyjaciela</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-300 hover:text-white transition-colors"
                    >
                        <X className="h-5 w-5" />
                    </button>
                </div>

                <div className="p-6 space-y-6 overflow-y-auto">
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">
                            Wyszukaj użytkownika
                        </label>
                        <div className="flex gap-2">
                            <div className="relative flex-1">
                                <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                                <input
                                    type="text"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    placeholder="Wprowadź email lub nazwę"
                                    className="input-glass w-full pl-10"
                                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                />
                            </div>
                            <button
                                onClick={handleSearch}
                                disabled={loading || !searchTerm.trim()}
                                className="btn-primary-solid flex items-center gap-2"
                            >
                                <Search className="h-4 w-4" />
                                {loading ? 'Szukam...' : 'Szukaj'}
                            </button>
                        </div>
                    </div>

                    {searchResults.length > 0 && (
                        <div className="space-y-3">
                            <h3 className="text-sm font-medium text-gray-200 mb-3">
                                Wyniki wyszukiwania ({searchResults.length})
                            </h3>
                            {searchResults.map(user => (
                                <div
                                    key={user.id}
                                    className="flex items-center justify-between p-3 bg-white/10 rounded-lg"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-blue-500/30 rounded-full flex items-center justify-center">
                                          <span className="text-sm font-semibold text-blue-100">
                                            {user.name.charAt(0).toUpperCase()}
                                          </span>
                                        </div>
                                        <div>
                                            <p className="font-medium text-white">{user.name}</p>
                                            <p className="text-sm text-gray-300">{user.email}</p>
                                        </div>
                                    </div>
                                    <button
                                        onClick={() => handleSendRequest(user.id)}
                                        disabled={sending === user.id}
                                        className="bg-green-600 text-white px-3 py-1 rounded-md hover:bg-green-700 transition-colors disabled:opacity-50 flex items-center gap-1 text-sm"
                                    >
                                        <UserPlus className="h-3 w-3" />
                                        {sending === user.id ? 'Wysyłanie...' : 'Dodaj'}
                                    </button>
                                </div>
                            ))}
                        </div>
                    )}

                    {searchTerm && searchResults.length === 0 && !loading && (
                        <div className="text-center py-6">
                            <Search className="h-12 w-12 text-gray-500 mx-auto mb-3" />
                            <p className="text-gray-300">
                                Nie znaleziono użytkowników dla "{searchTerm}"
                            </p>
                        </div>
                    )}

                    <div className="mt-6 p-4 bg-blue-500/20 rounded-lg border border-blue-500/30">
                        <h4 className="font-medium text-blue-100 mb-2">💡 Wskazówka</h4>
                        <p className="text-sm text-blue-200">
                            Możesz wyszukiwać użytkowników po adresie email lub nazwie użytkownika.
                        </p>
                    </div>
                </div>

                <div className="flex justify-end gap-3 p-6 border-t border-white/20">
                    <button
                        onClick={onClose}
                        className="btn-glass"
                    >
                        Anuluj
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AddFriendModal;