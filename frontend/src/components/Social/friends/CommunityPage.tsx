import React, { useEffect, useState } from 'react';
import socialApi from '../../../services/socialApi';
import { toast } from 'react-toastify';
import './CommunityPage.css';

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
            } catch (e) {
                setPendingRequests([]);
            } finally {
                setRequestsLoading(false);
            }
        };
        fetchRequests();
    }, []);

    const filtered = users.filter(u =>
        u.name.toLowerCase().includes(search.toLowerCase()) ||
        u.email.toLowerCase().includes(search.toLowerCase())
    );

    const handleSendRequest = async (userId: number) => {
        setSendingId(userId);
        try {
            await socialApi.sendFriendRequest(userId);
            toast.success('Zaproszenie wysłane!');
        } catch (err) {
            toast.error('Błąd przy wysyłaniu zaproszenia');
        } finally {
            setSendingId(null);
        }
    };

    const handleAccept = async (friendshipId: number) => {
        setActionId(friendshipId);
        try {
            await socialApi.acceptFriendRequest(friendshipId);
            toast.success('Zaproszenie zaakceptowane!');
            setPendingRequests(prev => prev.filter(req => req.id !== friendshipId));
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
        <div className="community-page community-layout">
            <div className="community-left">
                <h2>Wszyscy użytkownicy</h2>
                <input
                    type="text"
                    placeholder="Szukaj..."
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                    className="community-search"
                />
                {loading ? (
                    <div className="community-loader">Ładuję...</div>
                ) : filtered.length === 0 ? (
                    <div className="community-empty">Brak użytkowników</div>
                ) : (
                    <div className="community-list">
                        {filtered.map(u => (
                            <div key={u.id} className="community-user">
                                <div className="community-avatar">{u.name.charAt(0).toUpperCase()}</div>
                                <div>
                                    <div className="community-name">{u.name}</div>
                                    <div className="community-email">{u.email}</div>
                                </div>
                                {u.id !== currentUserId && (
                                    <button
                                        className="community-btn"
                                        onClick={() => handleSendRequest(u.id)}
                                        disabled={sendingId === u.id}
                                    >
                                        Wyślij zaproszenie
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
            <div className="community-right">
                <h3>Zaproszenia do przyjaźni</h3>
                {requestsLoading ? (
                    <div className="community-loader">Ładuję zaproszenia...</div>
                ) : pendingRequests.length === 0 ? (
                    <div className="community-empty">Brak nowych zaproszeń</div>
                ) : (
                    <div className="pending-list">
                        {pendingRequests.map(req => (
                            <div className="pending-item">
                                <div className="community-avatar">{req.requesterName.charAt(0).toUpperCase()}</div>
                                <div className="pending-info">
                                    <span className="pending-name">{req.requesterName}</span>
                                    <span className="pending-email">{req.requesterEmail}</span>
                                </div>
                                <button
                                    onClick={() => handleAccept(req.id)}
                                    disabled={actionId === req.id}
                                    className="pending-btn accept"
                                >
                                    Akceptuj
                                </button>
                                <button
                                    onClick={() => handleDecline(req.id)}
                                    disabled={actionId === req.id}
                                    className="pending-btn decline"
                                >
                                    Odrzuć
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default CommunityPage;