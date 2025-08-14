import React, { useState } from 'react';
import { X, Search, UserPlus, Mail } from 'lucide-react';

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
            // Mock search results - w prawdziwej aplikacji to by było API call
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
            // Remove user from results after sending request
            setSearchResults(prev => prev.filter(user => user.id !== userId));
        } finally {
            setSending(null);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-md">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-gray-900">Dodaj przyjaciela</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-600 transition-colors"
                    >
                        <X className="h-5 w-5" />
                    </button>
                </div>

                {/* Content */}
                <div className="p-6">
                    {/* Search Input */}
                    <div className="mb-6">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
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
                                    className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                />
                            </div>
                            <button
                                onClick={handleSearch}
                                disabled={loading || !searchTerm.trim()}
                                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 flex items-center gap-2"
                            >
                                <Search className="h-4 w-4" />
                                {loading ? 'Szukam...' : 'Szukaj'}
                            </button>
                        </div>
                    </div>

                    {/* Search Results */}
                    {searchResults.length > 0 && (
                        <div className="space-y-3">
                            <h3 className="text-sm font-medium text-gray-700 mb-3">
                                Wyniki wyszukiwania ({searchResults.length})
                            </h3>
                            {searchResults.map(user => (
                                <div
                                    key={user.id}
                                    className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                      <span className="text-sm font-semibold text-blue-600">
                        {user.name.charAt(0).toUpperCase()}
                      </span>
                                        </div>
                                        <div>
                                            <p className="font-medium text-gray-900">{user.name}</p>
                                            <p className="text-sm text-gray-500">{user.email}</p>
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

                    {/* No Results */}
                    {searchTerm && searchResults.length === 0 && !loading && (
                        <div className="text-center py-6">
                            <Search className="h-12 w-12 text-gray-300 mx-auto mb-3" />
                            <p className="text-gray-500">
                                Nie znaleziono użytkowników dla "{searchTerm}"
                            </p>
                        </div>
                    )}

                    {/* Instructions */}
                    <div className="mt-6 p-4 bg-blue-50 rounded-lg">
                        <h4 className="font-medium text-blue-900 mb-2">💡 Wskazówka</h4>
                        <p className="text-sm text-blue-800">
                            Możesz wyszukiwać użytkowników po adresie email lub nazwie użytkownika.
                            Po wysłaniu zaproszenia, użytkownik otrzyma powiadomienie i będzie mógł je zaakceptować.
                        </p>
                    </div>
                </div>

                {/* Footer */}
                <div className="flex justify-end gap-3 p-6 border-t border-gray-200">
                    <button
                        onClick={onClose}
                        className="bg-gray-100 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-200 transition-colors"
                    >
                        Anuluj
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AddFriendModal;