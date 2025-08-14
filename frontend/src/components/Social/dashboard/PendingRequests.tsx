import React, { useState } from 'react';
import { Friendship } from '../../../types/social';
import { Check, X, User } from 'lucide-react';

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

    const handleAccept = async (friendshipId: number) => {
        setLoading(friendshipId);
        try {
            await onAccept(friendshipId);
        } finally {
            setLoading(null);
        }
    };

    const handleDecline = async (friendshipId: number) => {
        setLoading(friendshipId);
        try {
            await onDecline(friendshipId);
        } finally {
            setLoading(null);
        }
    };

    return (
        <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Zaproszenia do przyjaźni ({requests.length})
            </h3>
            <div className="space-y-3">
                {requests.map(request => (
                    <div key={request.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center">
                                <User className="h-5 w-5 text-gray-600" />
                            </div>
                            <div>
                                <p className="font-medium text-gray-900">{request.requester.name}</p>
                                <p className="text-sm text-gray-500">{request.requester.email}</p>
                            </div>
                        </div>
                        <div className="flex gap-2">
                            <button
                                onClick={() => handleAccept(request.id)}
                                disabled={loading === request.id}
                                className="bg-green-600 text-white p-2 rounded-md hover:bg-green-700 transition-colors disabled:opacity-50"
                            >
                                <Check className="h-4 w-4" />
                            </button>
                            <button
                                onClick={() => handleDecline(request.id)}
                                disabled={loading === request.id}
                                className="bg-red-600 text-white p-2 rounded-md hover:bg-red-700 transition-colors disabled:opacity-50"
                            >
                                <X className="h-4 w-4" />
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default PendingRequests;