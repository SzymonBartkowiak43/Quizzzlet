import React from 'react';
import { Friendship } from '../../../types/social';
import { Clock, Mail, Send, User } from 'lucide-react';

interface SentRequestsProps {
    requests: Friendship[];
}

const SentRequests: React.FC<SentRequestsProps> = ({ requests }) => {
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
                <Send className="h-16 w-16 text-gray-400 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-white mb-2">
                    Brak wysłanych zaproszeń
                </h3>
                <p className="text-gray-300">
                    Nie wysłałeś jeszcze żadnych zaproszeń do przyjaźni.
                </p>
            </div>
        );
    }

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-white">
                    Wysłane zaproszenia ({requests.length})
                </h3>
                <div className="text-sm text-gray-300">
                    Oczekują na odpowiedź
                </div>
            </div>

            <div className="space-y-3">
                {requests.map(request => (
                    <div
                        key={request.id}
                        className="bg-white/10 border border-white/20 rounded-lg p-4 hover:border-orange-300/50 transition-colors"
                    >
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                <div className="w-12 h-12 bg-orange-500/30 rounded-full flex items-center justify-center">
                                  <span className="text-lg font-semibold text-orange-100">
                                    {request.addressee.name.charAt(0).toUpperCase()}
                                  </span>
                                </div>

                                <div className="flex-1">
                                    <h4 className="font-semibold text-white mb-1">
                                        {request.addressee.name}
                                    </h4>
                                    <div className="flex items-center gap-3 text-sm text-gray-300">
                                        <div className="flex items-center gap-1">
                                            <Mail className="h-3 w-3" />
                                            <span>{request.addressee.email}</span>
                                        </div>
                                        <div className="flex items-center gap-1">
                                            <Clock className="h-3 w-3" />
                                            <span>Wysłano {formatDate(request.createdAt)}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="flex items-center gap-2">
                                <div className="bg-orange-500/30 text-orange-200 px-3 py-1 rounded-full text-sm font-medium">
                                    <div className="flex items-center gap-1">
                                        <div className="w-2 h-2 bg-orange-400 rounded-full animate-pulse"></div>
                                        Oczekuje
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            <div className="mt-6 p-4 bg-orange-500/20 rounded-lg border border-orange-500/30">
                <h4 className="font-medium text-orange-100 mb-2">⏳ Informacja</h4>
                <p className="text-sm text-orange-200">
                    Wysłane zaproszenia oczekują na odpowiedź od innych użytkowników.
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

export default SentRequests;