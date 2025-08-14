import React, { useState } from 'react';
import { Friendship } from '../../../types/social';
import { Check, X, User, Clock, Mail } from 'lucide-react';

interface PendingRequestsProps {
    requests: Friendship[];
    onAccept: (friendshipId: number) => Promise<void>;
    onDecline: (friendshipId: number) => Promise<void>;
}

const PendingRequests: React.FC<PendingRequestsProps> = ({
                                                             requests,
                                                             onAccept,
                                                             onDecline
                                                         }) => {
    const [loading, setLoading] = useState<number | null>(null);

    const handleAction = async (action: () => Promise<void>, requestId: number) => {
        setLoading(requestId);
        try {
            await action();
        } finally {
            setLoading(null);
        }
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        const now = new Date();
        const diffTime = Math.abs(now.getTime() - date.getTime());
        const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays === 0) return 'Dzisiaj';
        if (diffDays === 1) return 'Wczoraj';
        if (diffDays < 7) return `${diffDays} dni temu`;
        return date.toLocaleDateString('pl-PL');
    };

    if (requests.length === 0) {
        return (
            <div className="text-center py-12">
                <User className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">
                    Brak oczekujących zaproszeń
                </h3>
                <p className="text-gray-500">
                    Wszystkie zaproszenia do przyjaźni zostały już przetworzone.
                </p>
            </div>
        );
    }

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-gray-900">
                    Oczekujące zaproszenia ({requests.length})
                </h3>
                <div className="text-sm text-gray-500">
                    Wymagają twojej decyzji
                </div>
            </div>

            <div className="space-y-3">
                {requests.map(request => (
                    <div
                        key={request.id}
                        className="bg-white border border-gray-200 rounded-lg p-4 hover:border-blue-300 transition-all duration-200 relative"
                    >
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                  <span className="text-lg font-semibold text-blue-600">
                    {request.requester.name.charAt(0).toUpperCase()}
                  </span>
                                </div>

                                <div className="flex-1">
                                    <h4 className="font-semibold text-gray-900 mb-1">
                                        {request.requester.name}
                                    </h4>
                                    <div className="flex items-center gap-3 text-sm text-gray-600">
                                        <div className="flex items-center gap-1">
                                            <Mail className="h-3 w-3" />
                                            <span>{request.requester.email}</span>
                                        </div>
                                        <div className="flex items-center gap-1">
                                            <Clock className="h-3 w-3" />
                                            <span>{formatDate(request.createdAt)}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="flex gap-2">
                                <button
                                    onClick={() => handleAction(() => onAccept(request.id), request.id)}
                                    disabled={loading === request.id}
                                    className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition-colors disabled:opacity-50 flex items-center gap-2"
                                >
                                    <Check className="h-4 w-4" />
                                    Akceptuj
                                </button>
                                <button
                                    onClick={() => handleAction(() => onDecline(request.id), request.id)}
                                    disabled={loading === request.id}
                                    className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors disabled:opacity-50 flex items-center gap-2"
                                >
                                    <X className="h-4 w-4" />
                                    Odrzuć
                                </button>
                            </div>
                        </div>

                        {loading === request.id && (
                            <div className="absolute inset-0 bg-white bg-opacity-75 rounded-lg flex items-center justify-center">
                                <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <div className="mt-6 p-4 bg-blue-50 rounded-lg">
                <h4 className="font-medium text-blue-900 mb-2">💡 Wskazówka</h4>
                <p className="text-sm text-blue-800">
                    Zaakceptowanie zaproszenia pozwoli wam na wymianę wiadomości i udostępnianie zestawów słówek.
                    Możesz zawsze usunąć przyjaciela z listy później.
                </p>
            </div>
        </div>
    );
};

export default PendingRequests;