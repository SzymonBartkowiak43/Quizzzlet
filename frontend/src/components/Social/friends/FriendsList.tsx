import React, { useState } from 'react';
import { DisplayUser } from '../../../types/social';
import {
    MessageCircle,
    MoreVertical,
    UserMinus,
    Ban,
    Mail,
    Calendar,
    Users
} from 'lucide-react';

interface FriendsListProps {
    friends: DisplayUser[];
    onRemoveFriend: (friendId: number) => Promise<void>;
    onBlockUser: (userId: number) => Promise<void>;
    searchTerm?: string;
}

const FriendsList: React.FC<FriendsListProps> = ({
                                                     friends,
                                                     onRemoveFriend,
                                                     onBlockUser,
                                                     searchTerm
                                                 }) => {
    const [showDropdown, setShowDropdown] = useState<number | null>(null);
    const [loading, setLoading] = useState<number | null>(null);

    const handleAction = async (action: () => Promise<void>, friendId: number) => {
        setLoading(friendId);
        try {
            await action();
            setShowDropdown(null);
        } finally {
            setLoading(null);
        }
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('pl-PL');
    };

    const highlightText = (text: string, highlight: string) => {
        if (!highlight) return text;

        const parts = text.split(new RegExp(`(${highlight})`, 'gi'));
        return parts.map((part, index) =>
            part.toLowerCase() === highlight.toLowerCase() ? (
                <mark key={index} className="bg-yellow-200 px-1 rounded">{part}</mark>
            ) : part
        );
    };

    if (friends.length === 0) {
        return (
            <div className="text-center py-12">
                <Users className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">
                    {searchTerm ? 'Brak wyników wyszukiwania' : 'Nie masz jeszcze przyjaciół'}
                </h3>
                <p className="text-gray-500 mb-6">
                    {searchTerm
                        ? `Nie znaleziono przyjaciół pasujących do "${searchTerm}"`
                        : 'Zacznij dodawać przyjaciół, aby rozwijać swoją sieć kontaktów!'
                    }
                </p>
                {!searchTerm && (
                    <button className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors">
                        Znajdź przyjaciół
                    </button>
                )}
            </div>
        );
    }

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-gray-900">
                    Twoi przyjaciele ({friends.length})
                </h3>
                <div className="text-sm text-gray-500">
                    {searchTerm && `Wyniki dla: "${searchTerm}"`}
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {friends.map(friend => (
                    <div key={friend.id} className="bg-gray-50 rounded-lg p-4 hover:bg-gray-100 transition-colors">
                        <div className="flex items-start justify-between mb-3">
                            <div className="flex items-center gap-3">
                                <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                  <span className="text-lg font-semibold text-blue-600">
                    {friend.name.charAt(0).toUpperCase()}
                  </span>
                                </div>
                                <div className="flex-1">
                                    <h4 className="font-semibold text-gray-900">
                                        {highlightText(friend.name, searchTerm || '')}
                                    </h4>
                                    <p className="text-sm text-gray-600">
                                        {highlightText(friend.email, searchTerm || '')}
                                    </p>
                                </div>
                            </div>

                            <div className="relative">
                                <button
                                    onClick={() => setShowDropdown(showDropdown === friend.id ? null : friend.id)}
                                    className="p-1 hover:bg-gray-200 rounded-full transition-colors"
                                    disabled={loading === friend.id}
                                >
                                    <MoreVertical className="h-4 w-4 text-gray-500" />
                                </button>

                                {showDropdown === friend.id && (
                                    <div className="absolute right-0 top-8 bg-white border border-gray-200 rounded-lg shadow-lg z-10 min-w-[160px]">
                                        <button
                                            onClick={() => {/* Navigate to messages */}}
                                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 flex items-center gap-2"
                                        >
                                            <MessageCircle className="h-4 w-4" />
                                            Wyślij wiadomość
                                        </button>
                                        <button
                                            onClick={() => handleAction(() => onRemoveFriend(friend.id), friend.id)}
                                            className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                                            disabled={loading === friend.id}
                                        >
                                            <UserMinus className="h-4 w-4" />
                                            Usuń z przyjaciół
                                        </button>
                                        <button
                                            onClick={() => handleAction(() => onBlockUser(friend.id), friend.id)}
                                            className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                                            disabled={loading === friend.id}
                                        >
                                            <Ban className="h-4 w-4" />
                                            Zablokuj użytkownika
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="flex items-center gap-4 text-xs text-gray-500 mb-3">
                            <div className="flex items-center gap-1">
                                <Mail className="h-3 w-3" />
                                <span>Aktywny</span>
                            </div>
                        </div>

                        <div className="flex gap-2">
                            <button className="flex-1 bg-blue-600 text-white py-2 px-3 rounded-md text-sm hover:bg-blue-700 transition-colors flex items-center justify-center gap-1">
                                <MessageCircle className="h-3 w-3" />
                                Wiadomość
                            </button>
                        </div>

                        {loading === friend.id && (
                            <div className="absolute inset-0 bg-white bg-opacity-75 rounded-lg flex items-center justify-center">
                                <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default FriendsList;