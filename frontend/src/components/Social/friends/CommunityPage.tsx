import React, { useEffect, useState } from 'react';
import socialApi from '../../../services/socialApi';
import { toast } from 'react-toastify';
import './CommunityPage.css';
import LoadingSpinner from '../../Shared/LoadingSpinner';
import { UserPlus, Check, X } from 'lucide-react';


interface User {
    id: number;
    name: string;
    email: string;
}
interface FriendshipRequest {
    id: number;
    requesterId: number;
    requesterName: string;
    requesterEmail: string;
    addresseeId: number;
    addresseeName: string;
    addresseeEmail: string;
    status: string;
}

const getCurrentUserId = () => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    return user.id;
};

const CommunityPage: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState('');
    const [sendingId, setSendingId] = useState<number | null>(null);
    const [pendingRequests, setPendingRequests] = useState<FriendshipRequest[]>([]);
    const [requestsLoading, setRequestsLoading] = useState(true);
    const [actionId, setActionId] = useState<number | null>(null);

    const [friends, setFriends] = useState<User[]>([]);

    const currentUserId = getCurrentUserId();

    useEffect(() => {
        const fetchUsers = async () => {
            setLoading(true);
            try {
                const data = await socialApi.getAllUsers();
                setUsers(data);
            } catch (e) {
                setUsers([]);
            } finally {
                setLoading(false);
            }
        };
        fetchUsers();
    }, []);

    useEffect(() => {
        const fetchRequests = async () => {
            setRequestsLoading(true);
            try {
                const data = await socialApi.getMyFriendships();
                setPendingRequests(data.pendingRequests || []);

                setFriends(data.friends || []);

            } catch (e) {
                setPendingRequests([]);
                setFriends([]);
            } finally {
                setRequestsLoading(false);
            }
        };
        fetchRequests();
    }, []);

    const friendIds = new Set(friends.map(f => f.id));

    const filtered = users.filter(u =>
        (u.name.toLowerCase().includes(search.toLowerCase()) ||
            u.email.toLowerCase().includes(search.toLowerCase())) &&

        u.id !== currentUserId &&

        !friendIds.has(u.id)
    );


    const handleSendRequest = async (userId: number) => {
        setSendingId(userId);
        try {
            await socialApi.sendFriendRequest(userId);
            toast.success('Zaproszenie wysłane!');
        } catch (err) {
            toast.error('Już wysłałeś zaproszenie do tego użytkownika.');
        } finally {
            setSendingId(null);
        }
    };

    const handleAccept = async (friendshipId: number) => {
        setActionId(friendshipId);
        try {
            const acceptedRequest = pendingRequests.find(req => req.id === friendshipId);
            await socialApi.acceptFriendRequest(friendshipId);
            toast.success('Zaproszenie zaakceptowane!');
            setPendingRequests(prev => prev.filter(req => req.id !== friendshipId));

            if (acceptedRequest) {
                setFriends(prev => [...prev, {
                    id: acceptedRequest.requesterId,
                    name: acceptedRequest.requesterName,
                    email: acceptedRequest.requesterEmail
                }]);
            }
        } catch (err) {
            toast.error('Błąd przy akceptacji zaproszenia');
        } finally {
            setActionId(null);
        }
    };

    const handleDecline = async (friendshipId: number) => {
        setActionId(friendshipId);
        try {
            await socialApi.declineFriendRequest(friendshipId);
            toast.info('Zaproszenie odrzucone.');
            setPendingRequests(prev => prev.filter(req => req.id !== friendshipId));
        } catch (err) {
            toast.error('Błąd przy odrzuceniu zaproszenia');
        } finally {
            setActionId(null);
        }
    };

    return (
        <div className="community-page">
            <div className="community-layout">
                {/* Lewa kolumna */}
                <div className="community-left glass-box">
                    <h2 className="box-title">Wszyscy użytkownicy</h2>
                    <input
                        type="text"
                        placeholder="Szukaj..."
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        className="search-input"
                    />
                    {loading ? (
                        <div className="loading-container">
                            <LoadingSpinner color="white" />
                        </div>
                    ) : filtered.length === 0 ? (
                        <div className="empty-state">Brak użytkowników</div>
                    ) : (
                        <div className="community-list">
                            {/* <<< 6. PRZYWRÓCENIE STAREGO JSX (BEZ WARUNKU) >>> */}
                            {filtered.map(u => (
                                <div key={u.id} className="community-user">
                                    <div className="community-avatar">{u.name.charAt(0).toUpperCase()}</div>
                                    <div>
                                        <div className="community-name">{u.name}</div>
                                        <div className="community-email">{u.email}</div>
                                    </div>
                                    <button
                                        className="btn-glass"
                                        onClick={() => handleSendRequest(u.id)}
                                        disabled={sendingId === u.id}
                                    >
                                        <UserPlus size={16} />
                                        {sendingId === u.id ? 'Wysyłanie...' : 'Dodaj'}
                                    </button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                <div className="community-right glass-box">
                    <h3 className="box-title">Zaproszenia do przyjaźni</h3>
                    {requestsLoading ? (
                        <div className="loading-container">
                            <LoadingSpinner color="white" />
                        </div>
                    ) : pendingRequests.length === 0 ? (
                        <div className="empty-state">Brak nowych zaproszeń</div>
                    ) : (
                        <div className="pending-list">
                            {pendingRequests.map(req => (
                                <div className="pending-item" key={req.id}>
                                    <div className="community-avatar">{req.requesterName.charAt(0).toUpperCase()}</div>
                                    <div className="pending-info">
                                        <span className="pending-name">{req.requesterName}</span>
                                        <span className="pending-email">{req.requesterEmail}</span>
                                    </div>
                                    <div className="pending-actions">
                                        <button
                                            onClick={() => handleAccept(req.id)}
                                            disabled={actionId === req.id}
                                            className="btn-icon btn-accept"
                                        >
                                            <Check size={16} />
                                        </button>
                                        <button
                                            onClick={() => handleDecline(req.id)}
                                            disabled={actionId === req.id}
                                            className="btn-icon btn-decline"
                                        >
                                            <X size={16} />
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default CommunityPage;