import React, { useState } from 'react';
import { User } from '../../../types/social';
import { UserPlus, Users, Mail, Calendar } from 'lucide-react';

interface SuggestedFriendsProps {
    suggestedFriends: User[];
    onSendRequest: (userId: number) => Promise<void>;
}

const SuggestedFriends: React.FC<SuggestedFriendsProps> = ({
                                                               suggestedFriends,
                                                               onSendRequest
                                                           }) => {
    const [loading, setLoading] = useState<number | null>(null);
    const [sentRequests, setSentRequests] = useState<Set<number>>(new Set());

    const handleSendRequest = async (userId: number) => {
        setLoading(userId);
        try {
            await onSendRequest(userId);
            setSentRequests(prev => new Set(prev).add(userId));
        } finally {
            setLoading(null);
        }
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('pl-PL');
    };

    if (suggestedFriends.length === 0) {
        return (
            <div className="text-center py-12">
                <Users className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">
                    Brak sugerowanych przyjaciół
                </h3>
                <p className="text-gray-500 mb-6">
                    W tej chwili nie mamy dla ciebie sugestii nowych przyjaciół.
                    Sprawdź ponownie później!
                </p>
                <button className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors">
                    Przeszukaj użytkowników
                </button>
            </div>
        );
    }

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-gray-900">
                    Sugerowani przyjaciele ({suggestedFriends.length})
                </h3>
                <div className="text-sm text-gray-500">
                    Użytkownicy, których możesz znać
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {suggestedFriends.map(user => {
                    const isRequestSent = sentRequests.has(user.id);
                    const isLoading = loading === user.id;

                    return (
                        <div
                            key={user.id}
                            className="bg-white border border-gray-200 rounded-lg p-4 hover:border-green-300 transition-all duration-200 relative"
                        >
                            <div className="text-center mb-4">
                                <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
                  <span className="text-xl font-semibold text-green-600">
                    {user.name.charAt(0).toUpperCase()}
                  </span>
                                </div>

                                <h4 className="font-semibold text-gray-900 mb-1">
                                    {user.name}
                                </h4>

                                <div className="flex items-center justify-center gap-1 text-sm text-gray-600 mb-2">
                                    <Mail className="h-3 w-3" />
                                    <span>{user.email}</span>
                                </div>

                                <div className="flex items-center justify-center gap-1 text-xs text-gray-500">
                                    <Calendar className="h-3 w-3" />
                                    <span>Dołączył {formatDate(user.createdAt)}</span>
                                </div>
                            </div>

                            <div className="space-y-2">
                                {isRequestSent ? (
                                    <div className="bg-gray-100 text-gray-600 py-2 px-4 rounded-md text-center text-sm font-medium">
                                        ✓ Zaproszenie wysłane
                                    </div>
                                ) : (
                                    <button
                                        onClick={() => handleSendRequest(user.id)}
                                        disabled={isLoading}
                                        className="w-full bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700 transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
                                    >
                                        <UserPlus className="h-4 w-4" />
                                        {isLoading ? 'Wysyłanie...' : 'Dodaj do przyjaciół'}
                                    </button>
                                )}
                            </div>

                            {/* Reason for suggestion (mock) */}
                            <div className="mt-3 pt-3 border-t border-gray-100">
                                <p className="text-xs text-gray-500 text-center">
                                    💡 Sugerowane na podstawie aktywności
                                </p>
                            </div>

                            {isLoading && (
                                <div className="absolute inset-0 bg-white bg-opacity-75 rounded-lg flex items-center justify-center">
                                    <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-green-600"></div>
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>

            <div className="mt-6 p-4 bg-green-50 rounded-lg">
                <h4 className="font-medium text-green-900 mb-2">🎯 Jak działają sugestie?</h4>
                <p className="text-sm text-green-800">
                    Sugerowani przyjaciele to użytkownicy z podobnymi zainteresowaniami, którzy uczą się tego samego języka
                    lub należą do podobnych grup nauki. Dodaj ich do znajomych, aby wspólnie rozwijać swoje umiejętności!
                </p>
            </div>
        </div>
    );
};

export default SuggestedFriends;