import React, { useState, useEffect } from 'react';
import { StudyGroup } from '../../../types/social';
import { X, Search, Users, Globe, Lock, Hash, UserPlus, Filter } from 'lucide-react';

interface GroupSearchProps {
    onClose: () => void;
    onJoinGroup: (groupId: number) => Promise<void>;
    onSearch: (searchTerm: string) => Promise<any>;
}

const GroupSearch: React.FC<GroupSearchProps> = ({
                                                     onClose,
                                                     onJoinGroup,
                                                     onSearch
                                                 }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState<StudyGroup[]>([]);
    const [loading, setLoading] = useState(false);
    const [joining, setJoining] = useState<number | null>(null);
    const [filters, setFilters] = useState({
        type: 'all', // 'all', 'public', 'private'
        size: 'all', // 'all', 'small', 'medium', 'large'
        activity: 'all' // 'all', 'active', 'new'
    });
    const [showFilters, setShowFilters] = useState(false);

    // Mock search results
    const mockResults: StudyGroup[] = [
        {
            id: 4,
            name: 'Advanced Business English',
            description: 'Grupa dla zaawansowanych uczniów języka angielskiego, skupiających się na komunikacji biznesowej i negocjacjach.',
            creator: { id: 4, name: 'John Smith', email: 'john@example.com', createdAt: '', updatedAt: '' },
            isPrivate: false,
            maxMembers: 30,
            memberCount: 18,
            inviteCode: 'BIZ2024A',
            createdAt: '2024-08-01T10:00:00Z',
            updatedAt: '2024-08-14T12:00:00Z'
        },
        {
            id: 5,
            name: 'Casual English Conversations',
            description: 'Swobodne rozmowy po angielsku na codzienne tematy. Idealne dla osób chcących poprawić płynność wypowiedzi.',
            creator: { id: 5, name: 'Emma Wilson', email: 'emma@example.com', createdAt: '', updatedAt: '' },
            isPrivate: false,
            maxMembers: 50,
            memberCount: 35,
            inviteCode: 'CASUAL01',
            createdAt: '2024-07-15T14:30:00Z',
            updatedAt: '2024-08-14T11:30:00Z'
        },
        {
            id: 6,
            name: 'IELTS Preparation Group',
            description: 'Grupa przygotowawcza do egzaminu IELTS. Wspólne ćwiczenia, materiały i wsparcie w przygotowaniach.',
            creator: { id: 6, name: 'Michael Brown', email: 'michael@example.com', createdAt: '', updatedAt: '' },
            isPrivate: true,
            maxMembers: 15,
            memberCount: 12,
            inviteCode: 'IELTS2024',
            createdAt: '2024-06-20T09:15:00Z',
            updatedAt: '2024-08-13T16:45:00Z'
        }
    ];

    const performSearch = async () => {
        if (!searchTerm.trim()) {
            setSearchResults(mockResults);
            return;
        }

        setLoading(true);
        try {
            // Simulate API call
            await new Promise(resolve => setTimeout(resolve, 500));

            const filtered = mockResults.filter(group =>
                group.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                group.description.toLowerCase().includes(searchTerm.toLowerCase())
            );

            setSearchResults(filtered);
        } catch (error) {
            console.error('Search error:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        performSearch();
    }, [searchTerm]);

    const handleJoinGroup = async (groupId: number) => {
        setJoining(groupId);
        try {
            await onJoinGroup(groupId);
            // Remove group from results after joining
            setSearchResults(prev => prev.filter(group => group.id !== groupId));
        } finally {
            setJoining(null);
        }
    };

    const applyFilters = (groups: StudyGroup[]) => {
        return groups.filter(group => {
            // Type filter
            if (filters.type === 'public' && group.isPrivate) return false;
            if (filters.type === 'private' && !group.isPrivate) return false;

            // Size filter
            if (filters.size === 'small' && group.maxMembers > 20) return false;
            if (filters.size === 'medium' && (group.maxMembers <= 20 || group.maxMembers > 50)) return false;
            if (filters.size === 'large' && group.maxMembers <= 50) return false;

            return true;
        });
    };

    const filteredResults = applyFilters(searchResults);

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('pl-PL');
    };

    const getGroupSize = (maxMembers: number) => {
        if (maxMembers <= 20) return 'Mała';
        if (maxMembers <= 50) return 'Średnia';
        return 'Duża';
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-4xl max-h-[90vh] flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-gray-200">
                    <h2 className="text-xl font-semibold text-gray-900">Wyszukaj grupy</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-600 transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                {/* Search & Filters */}
                <div className="p-6 border-b border-gray-200 space-y-4">
                    {/* Search Input */}
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            placeholder="Wyszukaj grupy po nazwie lub opisie..."
                            className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        />
                    </div>

                    {/* Filters Toggle */}
                    <div className="flex items-center justify-between">
                        <button
                            onClick={() => setShowFilters(!showFilters)}
                            className="flex items-center gap-2 text-sm text-gray-600 hover:text-gray-800 transition-colors"
                        >
                            <Filter className="h-4 w-4" />
                            {showFilters ? 'Ukryj filtry' : 'Pokaż filtry'}
                        </button>

                        <div className="text-sm text-gray-500">
                            Znaleziono: {filteredResults.length} grup
                        </div>
                    </div>

                    {/* Filters */}
                    {showFilters && (
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 p-4 bg-gray-50 rounded-lg">
                            {/* Type Filter */}
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Typ grupy</label>
                                <select
                                    value={filters.type}
                                    onChange={(e) => setFilters(prev => ({ ...prev, type: e.target.value }))}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                                >
                                    <option value="all">Wszystkie</option>
                                    <option value="public">Tylko publiczne</option>
                                    <option value="private">Tylko prywatne</option>
                                </select>
                            </div>

                            {/* Size Filter */}
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Rozmiar grupy</label>
                                <select
                                    value={filters.size}
                                    onChange={(e) => setFilters(prev => ({ ...prev, size: e.target.value }))}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                                >
                                    <option value="all">Dowolny rozmiar</option>
                                    <option value="small">Małe (≤20 osób)</option>
                                    <option value="medium">Średnie (21-50 osób)</option>
                                    <option value="large">Duże (50 osób)</option>
                                </select>
                            </div>

                            {/* Activity Filter */}
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Aktywność</label>
                                <select
                                    value={filters.activity}
                                    onChange={(e) => setFilters(prev => ({ ...prev, activity: e.target.value }))}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                                >
                                    <option value="all">Wszystkie</option>
                                    <option value="active">Aktywne</option>
                                    <option value="new">Nowe</option>
                                </select>
                            </div>
                        </div>
                    )}
                </div>

                {/* Results */}
                <div className="flex-1 overflow-y-auto p-6">
                    {loading ? (
                        <div className="text-center py-8">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto mb-4"></div>
                            <p className="text-gray-500">Wyszukiwanie grup...</p>
                        </div>
                    ) : filteredResults.length === 0 ? (
                        <div className="text-center py-12">
                            <Search className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                            <h3 className="text-lg font-medium text-gray-900 mb-2">
                                {searchTerm ? 'Brak wyników' : 'Rozpocznij wyszukiwanie'}
                            </h3>
                            <p className="text-gray-500">
                                {searchTerm
                                    ? `Nie znaleziono grup pasujących do "${searchTerm}"`
                                    : 'Wprowadź frazę, aby wyszukać dostępne grupy'
                                }
                            </p>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {filteredResults.map(group => (
                                <div key={group.id} className="border border-gray-200 rounded-lg p-5 hover:border-blue-300 hover:shadow-md transition-all duration-200">
                                    <div className="flex items-start justify-between mb-3">
                                        <div className="flex items-center gap-3">
                                            <div className="w-12 h-12 bg-gradient-to-br from-green-500 to-blue-600 rounded-lg flex items-center justify-center">
                                                <Hash className="h-6 w-6 text-white" />
                                            </div>
                                            <div>
                                                <h3 className="font-semibold text-gray-900 mb-1">{group.name}</h3>
                                                <div className="flex items-center gap-2">
                                                    {group.isPrivate ? (
                                                        <div className="flex items-center gap-1 text-orange-600">
                                                            <Lock className="h-3 w-3" />
                                                            <span className="text-xs">Prywatna</span>
                                                        </div>
                                                    ) : (
                                                        <div className="flex items-center gap-1 text-green-600">
                                                            <Globe className="h-3 w-3" />
                                                            <span className="text-xs">Publiczna</span>
                                                        </div>
                                                    )}
                                                    <span className="text-gray-400">•</span>
                                                    <span className="text-xs text-gray-500">{getGroupSize(group.maxMembers)}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <p className="text-sm text-gray-600 mb-4 line-clamp-3">
                                        {group.description}
                                    </p>

                                    <div className="flex items-center justify-between mb-4">
                                        <div className="flex items-center gap-4 text-sm text-gray-500">
                                            <div className="flex items-center gap-1">
                                                <Users className="h-3 w-3" />
                                                <span>{group.memberCount}/{group.maxMembers}</span>
                                            </div>
                                            <span>Utworzono {formatDate(group.createdAt)}</span>
                                        </div>
                                    </div>

                                    <div className="flex items-center justify-between">
                                        <div className="text-sm text-gray-500">
                                            Założyciel: <span className="text-gray-700">{group.creator.name}</span>
                                        </div>

                                        <button
                                            onClick={() => handleJoinGroup(group.id)}
                                            disabled={joining === group.id || group.memberCount >= group.maxMembers}
                                            className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed text-sm"
                                        >
                                            {joining === group.id ? (
                                                <>
                                                    <div className="animate-spin rounded-full h-3 w-3 border-b-2 border-white"></div>
                                                    Dołączanie...
                                                </>
                                            ) : group.memberCount >= group.maxMembers ? (
                                                'Pełna'
                                            ) : (
                                                <>
                                                    <UserPlus className="h-3 w-3" />
                                                    Dołącz
                                                </>
                                            )}
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                {/* Footer */}
                <div className="p-6 border-t border-gray-200 bg-gray-50">
                    <div className="flex items-center justify-between">
                        <div className="text-sm text-gray-600">
                            💡 Nie możesz znaleźć odpowiedniej grupy?
                            <button className="text-blue-600 hover:text-blue-700 ml-1 font-medium">
                                Utwórz własną!
                            </button>
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

export default GroupSearch;