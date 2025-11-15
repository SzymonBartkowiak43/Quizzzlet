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
            <div className="text-center py-12 glass-box">
                <User className="h-16 w-16 text-gray-400 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-white mb-2">
                    Brak oczekujących zaproszeń
                </h3>
                <p className="text-gray-300">
                    Wszystkie zaproszenia do przyjaźni zostały już przetworzone.
                </p>
            </div>
        );
    }

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-white">
                    Oczekujące zaproszenia ({requests.length})
                </h3>
                <div className="text-sm text-gray-300">
                    Wymagają twojej decyzji
                </div>
            </div>

            <div className="space-y-3">
                {requests.map(request => (
                    <div
                        key={request.id}
                        className="bg-white/10 border border-white/20 rounded-lg p-4 hover:border-white/40 transition-all duration-200 relative"
                    >
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                <div className="w-12 h-12 bg-blue-500/30 rounded-full flex items-center justify-center">
                                  <span className="text-lg font-semibold text-blue-100">
                                    {request.requester.name.charAt(0).toUpperCase()}
                                  </span>
                                </div>

                                <div className="flex-1">
                                    <h4 className="font-semibold text-white mb-1">
                                        {request.requester.name}
                                    </h4>
                                    <div className="flex items-center gap-3 text-sm text-gray-300">
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
                            <div className="absolute inset-0 bg-black/50 backdrop-blur-sm rounded-lg flex items-center justify-center">
                                <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-white"></div>
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <div className="mt-6 p-4 bg-blue-500/20 rounded-lg border border-blue-500/30">
                <h4 className="font-medium text-blue-100 mb-2">💡 Wskazówka</h4>
                <p className="text-sm text-blue-200">
                    Zaakceptowanie zaproszenia pozwoli wam na wymianę wiadomości i udostępnianie zestawów słówek.
                </p>
            </div>
        </div>
    );
};

// --- DODAJ TE STYLE DO GLOBALNEGO CSS (jeśli jeszcze ich nie masz) ---
/*
.glass-box {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.15);
    padding: 1.5rem;
}
*/

export default PendingRequests;