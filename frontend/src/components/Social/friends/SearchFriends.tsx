import React, { useState, useEffect } from 'react';
import { User } from '../../../types/social';
import { X, Search, UserPlus, Mail, Calendar, MapPin, Users, Filter } from 'lucide-react';

interface SearchFriendsProps {
    onClose: () => void;
}

const SearchFriends: React.FC<SearchFriendsProps> = ({ onClose }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState<User[]>([]);
    const [loading, setLoading] = useState(false);
    const [sending, setSending] = useState<number | null>(null);
    const [sentRequests, setSentRequests] = useState<Set<number>>(new Set());
    const [filters, setFilters] = useState({
        location: '',
        interests: '',
        experience: 'all'
    });
    const [showAdvanced, setShowAdvanced] = useState(false);

    // Mock search results - w prawdziwej aplikacji to by było API call
    const mockResults: (User & { location?: string; interests?: string[]; experience?: string })[] = [
        {
            id: 10,
            name: 'Maria Wiśniewska',
            email: 'maria.w@example.com',
            createdAt: '2024-07-20T10:00:00Z',
            updatedAt: '2024-08-14T16:15:20Z',
            location: 'Warszawa',
            interests: ['Business English', 'Travel'],
            experience: 'Intermediate'
        },
        {
            id: 11,
            name: 'Tomasz Kowalczyk',
            email: 'tomasz.k@example.com',
            createdAt: '2024-08-01T14:30:00Z',
            updatedAt: '2024-08-14T16:15:20Z',
            location: 'Kraków',
            interests: ['Conversation', 'Grammar'],
            experience: 'Advanced'
        },
        {
            id: 12,
            name: 'Anna Lewandowska',
            email: 'anna.l@example.com',
            createdAt: '2024-06-15T09:15:00Z',
            updatedAt: '2024-08-14T16:15:20Z',
            location: 'Gdańsk',
            interests: ['IELTS', 'Academic English'],
            experience: 'Beginner'
        },
        {
            id: 13,
            name: 'Piotr Szymański',
            email: 'piotr.s@example.com',
            createdAt: '2024-05-10T11:45:00Z',
            updatedAt: '2024-08-14T16:15:20Z',
            location: 'Wrocław',
            interests: ['Speaking', 'Pronunciation'],
            experience: 'Intermediate'
        },
        {
            id: 14,
            name: 'Katarzyna Nowak',
            email: 'katarzyna.n@example.com',
            createdAt: '2024-08-05T16:20:00Z',
            updatedAt: '2024-08-14T16:15:20Z',
            location: 'Poznań',
            interests: ['Business English', 'Writing'],
            experience: 'Advanced'
        }
    ];

    const performSearch = async () => {
        if (!searchTerm.trim()) {
            setSearchResults([]);
            return;
        }

        setLoading(true);
        try {
            // Simulate API call delay
            await new Promise(resolve => setTimeout(resolve, 800));

            let filtered = mockResults.filter(user =>
                user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
                user.location?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                user.interests?.some(interest =>
                    interest.toLowerCase().includes(searchTerm.toLowerCase())
                )
            );

            // Apply advanced filters
            if (filters.location) {
                filtered = filtered.filter(user =>
                    user.location?.toLowerCase().includes(filters.location.toLowerCase())
                );
            }

            if (filters.interests) {
                filtered = filtered.filter(user =>
                    user.interests?.some(interest =>
                        interest.toLowerCase().includes(filters.interests.toLowerCase())
                    )
                );
            }

            if (filters.experience !== 'all') {
                filtered = filtered.filter(user =>
                    user.experience?.toLowerCase() === filters.experience.toLowerCase()
                );
            }

            setSearchResults(filtered);
        } catch (error) {
            console.error('Search error:', error);
            setSearchResults([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const timeoutId = setTimeout(() => {
            performSearch();
        }, 300); // Debounce search

        return () => clearTimeout(timeoutId);
    }, [searchTerm, filters]);

    const handleSendRequest = async (userId: number) => {
        setSending(userId);
        try {
            // Mock API call
            await new Promise(resolve => setTimeout(resolve, 1000));

            setSentRequests(prev => new Set(prev).add(userId));

            // Remove user from results after sending request (optional)
            // setSearchResults(prev => prev.filter(user => user.id !== userId));
        } catch (error) {
            console.error('Failed to send friend request:', error);
        } finally {
            setSending(null);
        }
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

    const getExperienceColor = (experience?: string) => {
        switch (experience?.toLowerCase()) {
            case 'beginner': return 'bg-green-100 text-green-600';
            case 'intermediate': return 'bg-yellow-100 text-yellow-600';
            case 'advanced': return 'bg-red-100 text-red-600';
            default: return 'bg-gray-100 text-gray-600';
        }
    };

    const clearFilters = () => {
        setFilters({ location: '', interests: '', experience: 'all' });
        setSearchTerm('');
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

                {/* Search & Filters */}
                <div className="p-6 border-b border-gray-200 space-y-4">
                    {/* Main Search */}
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            placeholder="Wyszukaj po imieniu, emailu, lokalizacji lub zainteresowaniach..."
                            className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        />
                    </div>

                    {/* Advanced Filters Toggle */}
                    <div className="flex items-center justify-between">
                        <button
                            onClick={() => setShowAdvanced(!showAdvanced)}
                            className="flex items-center gap-2 text-sm text-blue-600 hover:text-blue-700 transition-colors"
                        >
                            <Filter className="h-4 w-4" />
                            {showAdvanced ? 'Ukryj filtry' : 'Pokaż filtry zaawansowane'}
                        </button>

                        <div className="flex items-center gap-4">
              <span className="text-sm text-gray-500">
                Znaleziono: {searchResults.length} użytkowników
              </span>
                            {(filters.location || filters.interests || filters.experience !== 'all') && (
                                <button
                                    onClick={clearFilters}
                                    className="text-sm text-red-600 hover:text-red-700"
                                >
                                    Wyczyść filtry
                                </button>
                            )}
                        </div>
                    </div>

                    {/* Advanced Filters */}
                    {showAdvanced && (
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 p-4 bg-gray-50 rounded-lg">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Lokalizacja
                                </label>
                                <input
                                    type="text"
                                    value={filters.location}
                                    onChange={(e) => setFilters(prev => ({ ...prev, location: e.target.value }))}
                                    placeholder="np. Warszawa"
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Zainteresowania
                                </label>
                                <input
                                    type="text"
                                    value={filters.interests}
                                    onChange={(e) => setFilters(prev => ({ ...prev, interests: e.target.value }))}
                                    placeholder="np. Business English"
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Poziom zaawansowania
                                </label>
                                <select
                                    value={filters.experience}
                                    onChange={(e) => setFilters(prev => ({ ...prev, experience: e.target.value }))}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                                >
                                    <option value="all">Wszystkie poziomy</option>
                                    <option value="beginner">Początkujący</option>
                                    <option value="intermediate">Średniozaawansowany</option>
                                    <option value="advanced">Zaawansowany</option>
                                </select>
                            </div>
                        </div>
                    )}
                </div>

                {/* Results */}
                <div className="flex-1 overflow-y-auto p-6">
                    {loading ? (
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
                            {user.name.charAt(0).toUpperCase()}
                          </span>
                                                </div>
                                                <div>
                                                    <h3 className="font-semibold text-gray-900 mb-1">{user.name}</h3>
                                                    <div className="flex items-center gap-1 text-sm text-gray-600 mb-1">
                                                        <Mail className="h-3 w-3" />
                                                        <span>{user.email}</span>
                                                    </div>
                                                    {user.createdAt && (
                                                        <div className="flex items-center gap-1 text-sm text-gray-500">
                                                            <MapPin className="h-3 w-3" />
                                                            <span>{user.createdAt}</span>
                                                        </div>
                                                    )}
                                                </div>
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
                                                    onClick={() => handleSendRequest(user.id)}
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
                    )}
                </div>

                {/* Footer */}
                <div className="p-6 border-t border-gray-200 bg-gray-50">
                    <div className="flex items-center justify-between">
                        <div className="text-sm text-gray-600">
                            💡 Wskazówka: Używaj filtrów, aby znaleźć osoby o podobnych zainteresowaniach językowych
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