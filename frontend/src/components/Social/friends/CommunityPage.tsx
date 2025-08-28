import React, { useEffect, useState } from 'react';
import socialApi from '../../../services/socialApi';
import { toast } from 'react-toastify';
import './CommunityPage.css';

interface User {
    id: number;
    name: string;
    email: string;
}

// Zastąp to swoim sposobem pobierania ID aktualnego użytkownika
const getCurrentUserId = () => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    return user.id;
};

const CommunityPage: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState('');
    const [sendingId, setSendingId] = useState<number | null>(null);

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

    return (
        <div className="community-page">
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
    );
};

export default CommunityPage;