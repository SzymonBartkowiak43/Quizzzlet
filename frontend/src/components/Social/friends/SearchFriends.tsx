import React, { useState, useEffect } from 'react';
import { User } from '../../../types/social';
import { X, Search, UserPlus, Mail, Calendar, MapPin, Users, Filter, Check } from 'lucide-react';
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
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="glass-box-flat w-full max-w-4xl max-h-[90vh] flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-white/20">
                    <div className="flex items-center gap-3">
                        <Search className="h-6 w-6 text-blue-300" />
                        <div>
                            <h2 className="text-xl font-semibold text-white">Wyszukaj przyjaciół</h2>
                            <p className="text-sm text-gray-300">Znajdź nowych przyjaciół do nauki języka</p>
                        </div>
                    </div>
                    <button
                        onClick={onClose}
                        className="text-gray-300 hover:text-white transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                {/* Search */}
                <div className="p-6 border-b border-white/20">
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            placeholder="Wyszukaj po imieniu lub emailu..."
                            className="input-glass w-full pl-10 pr-4 py-3"
                        />
                    </div>

                    <div className="flex items-center justify-between mt-4">
                        <span className="text-sm text-gray-300">
                          {totalResults > 0 ? `Znaleziono: ${totalResults} użytkowników` : ''}
                        </span>
                    </div>
                </div>

                {/* Results */}
                <div className="flex-1 overflow-y-auto p-6">
                    {loading && searchResults.length === 0 ? (
                        <div className="text-center py-12">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white mx-auto mb-4"></div>
                            <p className="text-gray-300">Wyszukiwanie użytkowników...</p>
                        </div>
                    ) : searchResults.length === 0 ? (
                        <div className="text-center py-12">
                            <Users className="h-16 w-16 text-gray-500 mx-auto mb-4" />
                            <h3 className="text-lg font-medium text-white mb-2">
                                {searchTerm ? 'Brak wyników' : 'Rozpocznij wyszukiwanie'}
                            </h3>
                            <p className="text-gray-300">
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
                                            className="bg-white/10 border border-white/20 rounded-lg p-5 hover:border-white/40 hover:bg-white/20 transition-all duration-200"
                                        >
                                            <div className="flex items-start justify-between mb-4">
                                                <div className="flex items-center gap-3">
                                                    <div className="w-14 h-14 bg-gradient-to-br from-blue-500/50 to-purple-600/50 rounded-full flex items-center justify-center">
                                                        <span className="text-xl font-semibold text-white">
                                                          {user.name?.charAt(0)?.toUpperCase() || user.email.charAt(0).toUpperCase()}
                                                        </span>
                                                    </div>
                                                    <div>
                                                        <h3 className="font-semibold text-white mb-1">
                                                            {user.name || user.email.split('@')[0]}
                                                        </h3>
                                                        <div className="flex items-center gap-1 text-sm text-gray-300">
                                                            <Mail className="h-3 w-3" />
                                                            <span>{user.email}</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            {/* User Details */}
                                            <div className="space-y-3 mb-4">
                                                <div className="flex items-center gap-1 text-xs text-gray-400">
                                                    <Calendar className="h-3 w-3" />
                                                    <span>{formatDate(user.createdAt)}</span>
                                                </div>
                                            </div>

                                            {/* Action Button */}
                                            <div className="flex justify-end">
                                                {isRequestSent ? (
                                                    <div className="bg-green-500/30 text-green-200 px-4 py-2 rounded-md text-sm font-medium flex items-center gap-2">
                                                        <Check className="h-4 w-4" /> Zaproszenie wysłane
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
                                        className="btn-glass"
                                    >
                                        {loading ? 'Ładowanie...' : 'Pokaż więcej'}
                                    </button>
                                </div>
                            )}
                        </>
                    )}
                </div>

                {/* Footer */}
                <div className="p-6 border-t border-white/20 bg-black/20">
                    <div className="flex items-center justify-between">
                        <div className="text-sm text-gray-300">
                            💡 Wskazówka: Wyszukuj według imienia lub adresu email
                        </div>
                        <button
                            onClick={onClose}
                            className="btn-glass"
                        >
                            Zamknij
                        </button>
                    </div>
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
.btn-glass {
    background: rgba(255, 255, 255, 0.1);
    color: white;
    border: 1px solid rgba(255, 255, 255, 0.2);
    padding: 0.8rem 1.5rem;
    border-radius: 50px;
    font-weight: 600;
    font-size: 0.9rem;
    cursor: pointer;
    transition: all 0.3s ease;
}
.btn-glass:hover {
    background: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.4);
}
*/

export default SearchFriends;