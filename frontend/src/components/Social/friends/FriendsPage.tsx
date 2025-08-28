import React, { useState } from 'react';
import { useFriendships } from '../../../hooks/userFriendships';
import FriendsList from './FriendsList';
import ChatPanel from './ChatPanel';
import './FriendsPage.css';

const FriendsPage: React.FC = () => {
    const { friendshipInfo, loading, error, removeFriend, blockUser } = useFriendships();
    const [searchTerm, setSearchTerm] = useState('');
    const [activeChatFriend, setActiveChatFriend] = useState<null | {id: number, name: string, email: string}>(null);

    if (loading) return (
        <div className="friends-loader">
            <div className="spinner"></div>
            Ładuję...
        </div>
    );
    if (error) return (
        <div className="friends-error">
            <span>Błąd: {error}</span>
        </div>
    );
    if (!friendshipInfo) return (
        <div className="friends-empty">
            <span>Brak danych o znajomych.</span>
        </div>
    );

    const filteredFriends = friendshipInfo.friends.filter(friend =>
        friend.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        friend.email?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="friends-page">
            <div className="friends-header">
                <h2>
                    Twoi przyjaciele <span className="friends-count">({friendshipInfo.friendsCount})</span>
                </h2>
                <input
                    type="text"
                    className="friends-search"
                    placeholder="Szukaj imienia lub emaila..."
                    value={searchTerm}
                    onChange={e => setSearchTerm(e.target.value)}
                />
            </div>
            <FriendsList
                friends={searchTerm ? filteredFriends : friendshipInfo.friends}
                onRemoveFriend={removeFriend}
                onBlockUser={blockUser}
                onOpenChat={setActiveChatFriend}
                searchTerm={searchTerm}
            />
            {activeChatFriend &&
                <ChatPanel
                    friend={activeChatFriend}
                    onClose={() => setActiveChatFriend(null)}
                />
            }
        </div>
    );
};

export default FriendsPage;