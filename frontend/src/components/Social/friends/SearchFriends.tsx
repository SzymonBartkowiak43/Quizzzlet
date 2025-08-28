import React, { useState, useEffect } from 'react';
import { User } from '../../../types/social';
import { X, Search, UserPlus, Mail, Calendar, MapPin, Users, Filter } from 'lucide-react';
import { searchSocial, sendFriendRequest } from '../../../services/socialApi';

interface SearchFriendsProps {
    onClose: () => void;
}

const SearchFriends: React.FC<SearchFriendsProps> = ({ onClose }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState<User[]>([]);
    const [loading, setLoading] = useState(false);
    const [sending, setSending] = useState<number | null>(null);
    const [sentRequests, setSentRequests] = useState<Set<number>>(new Set());
    const [totalResults, setTotalResults] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);

    const performSearch = async (page: number = 0, reset: boolean = true) => {
        if (!searchTerm.trim()) {
            setSearchResults([]);
            setTotalResults(0);
            return;
        }

        setLoading(true);
        try {
            const response = await searchSocial(searchTerm, page, 20);

            if (reset) {
                setSearchResults(response.content);
            } else {
                setSearchResults(prev => [...prev, ...response.content]);
            }

            setTotalResults(response.totalElements);
            setCurrentPage(page);
        } catch (error) {
            console.error('Search error:', error);
            if (reset) {
                setSearchResults([]);
                setTotalResults(0);
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const timeoutId = setTimeout(() => {
            performSearch(0, true);
        }, 500); // Debounce search

        return () => clearTimeout(timeoutId);
    }, [searchTerm]);

    const handleSendRequest = async (userEmail: string, userId: number) => {
        setSending(userId);
        try {
            await sendFriendRequest(userId);
            setSentRequests(prev => new Set(prev).add(userId));
        } catch (error) {
            console.error('Failed to send friend request:', error);
        } finally {
            setSending(null);
        }
    };

    const loadMore = () => {
        performSearch(currentPage + 1, false);
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        const now = new Date();
        const diffTime = Math.abs(now.getTime() - date.getTime());
        const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays === 0) return 'Dołączył dzisiaj';
        if (diffDays === 1) return 'Dołączył wczoraj';
        if (diffDays < 30) return `Dołączył ${diffDays} dni temu`;
        if (diffDays < 365) return `Dołączył ${Math.floor(diffDays / 30)} miesięcy temu`;
        return `Dołączył ${Math.floor(diffDays / 365)} lat temu`;
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-gray-200">
                    <div className="flex items-center gap-3">
                        <Search className="h-6 w-6 text-blue-600" />
                        <div>
                            <h2 className="text-xl font-semibold text-gray-900">Wyszukaj przyjaciół</h2>
                            <p className="text-sm text-gray-600">Znajdź nowych przyjaciół do nauki języka</p>
                        </div>
                    </div>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-600 transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                {/* Search */}
                <div className="p-6 border-b border-gray-200">
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            placeholder="Wyszukaj po imieniu lub emailu..."
                            className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        />
                    </div>

                    <div className="flex items-center justify-between mt-4">
            <span className="text-sm text-gray-500">
              {totalResults > 0 ? `Znaleziono: ${totalResults} użytkowników` : ''}
            </span>
                    </div>
                </div>

                {/* Results */}
                <div className="flex-1 overflow-y-auto p-6">
                    {loading && searchResults.length === 0 ? (
                        <div className="text-center py-12">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto mb-4"></div>
                            <p className="text-gray-500">Wyszukiwanie użytkowników...</p>
                        </div>
                    ) : searchResults.length === 0 ? (
                        <div className="text-center py-12">
                            <Users className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                            <h3 className="text-lg font-medium text-gray-900 mb-2">
                                {searchTerm ? 'Brak wyników' : 'Rozpocznij wyszukiwanie'}
                            </h3>
                            <p className="text-gray-500">
                                {searchTerm
                                    ? `Nie znaleziono użytkowników pasujących do "${searchTerm}"`
                                    : 'Wprowadź frazę, aby wyszukać nowych przyjaciół'
                                }
                            </p>
                        </div>
                    ) : (
                        <>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                {searchResults.map(user => {
                                    const isRequestSent = sentRequests.has(user.id);
                                    const isLoading = sending === user.id;

                                    return (
                                        <div
                                            key={user.id}
                                            className="bg-white border border-gray-200 rounded-lg p-5 hover:border-blue-300 hover:shadow-md transition-all duration-200"
                                        >
                                            <div className="flex items-start justify-between mb-4">
                                                <div className="flex items-center gap-3">
                                                    <div className="w-14 h-14 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
                            <span className="text-xl font-semibold text-white">
                              {user.name?.charAt(0)?.toUpperCase() || user.email.charAt(0).toUpperCase()}
                            </span>
                                                    </div>
                                                    <div>
                                                        <h3 className="font-semibold text-gray-900 mb-1">
                                                            {user.name || user.email.split('@')[0]}
                                                        </h3>
                                                        <div className="flex items-center gap-1 text-sm text-gray-600">
                                                            <Mail className="h-3 w-3" />
                                                            <span>{user.email}</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            {/* User Details */}
                                            <div className="space-y-3 mb-4">
                                                {/* Join Date */}
                                                <div className="flex items-center gap-1 text-xs text-gray-500">
                                                    <Calendar className="h-3 w-3" />
                                                    <span>{formatDate(user.createdAt)}</span>
                                                </div>
                                            </div>

                                            {/* Action Button */}
                                            <div className="flex justify-end">
                                                {isRequestSent ? (
                                                    <div className="bg-green-100 text-green-700 px-4 py-2 rounded-md text-sm font-medium">
                                                        ✓ Zaproszenie wysłane
                                                    </div>
                                                ) : (
                                                    <button
                                                        onClick={() => handleSendRequest(user.email, user.id)}
                                                        disabled={isLoading}
                                                        className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50 flex items-center gap-2 text-sm"
                                                    >
                                                        {isLoading ? (
                                                            <>
                                                                <div className="animate-spin rounded-full h-3 w-3 border-b-2 border-white"></div>
                                                                Wysyłanie...
                                                            </>
                                                        ) : (
                                                            <>
                                                                <UserPlus className="h-3 w-3" />
                                                                Dodaj do przyjaciół
                                                            </>
                                                        )}
                                                    </button>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>

                            {/* Load More */}
                            {searchResults.length < totalResults && (
                                <div className="text-center mt-6">
                                    <button
                                        onClick={loadMore}
                                        disabled={loading}
                                        className="bg-gray-100 text-gray-700 px-6 py-2 rounded-lg hover:bg-gray-200 transition-colors disabled:opacity-50"
                                    >
                                        {loading ? 'Ładowanie...' : 'Pokaż więcej'}
                                    </button>
                                </div>
                            )}
                        </>
                    )}
                </div>

                {/* Footer */}
                <div className="p-6 border-t border-gray-200 bg-gray-50">
                    <div className="flex items-center justify-between">
                        <div className="text-sm text-gray-600">
                            💡 Wskazówka: Wyszukuj według imienia lub adresu email
                        </div>
                        <button
                            onClick={onClose}
                            className="bg-gray-200 text-gray-800 px-6 py-2 rounded-lg hover:bg-gray-300 transition-colors"
                        >
                            Zamknij
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SearchFriends;