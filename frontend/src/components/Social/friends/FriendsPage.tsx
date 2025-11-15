import React, { useState } from 'react';
import { useFriendships } from '../../../hooks/userFriendships';
import FriendsList from './FriendsList';
import ChatPanel from './ChatPanel';
import './FriendsPage.css';
import LoadingSpinner from '../../Shared/LoadingSpinner'; // Zakładam, że masz ten komponent

const FriendsPage: React.FC = () => {
    const { friendshipInfo, loading, error, removeFriend, blockUser } = useFriendships();
    const [searchTerm, setSearchTerm] = useState('');
    const [activeChatFriend, setActiveChatFriend] = useState<null | {id: number, name: string, email: string}>(null);

    if (loading) return (
        <div className="friends-page">
            <div className="loading-container">
                <LoadingSpinner size="lg" color="white" />
                Ładuję...
            </div>
        </div>
    );
    if (error) return (
        <div className="friends-page">
            <div className="error-container">
                <div className="error-message">
                    <span>Błąd: {error}</span>
                </div>
            </div>
        </div>
    );
    if (!friendshipInfo) return (
        <div className="friends-page">
            <div className="empty-state">
                <span>Brak danych o znajomych.</span>
            </div>
        </div>
    );

    const filteredFriends = friendshipInfo.friends.filter(friend =>
        friend.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        friend.email?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="friends-page">
            <div className="friends-content-wrapper">
                <div className="friends-header">
                    <h2>
                        Twoi przyjaciele <span className="friends-count">({friendshipInfo.friendsCount})</span>
                    </h2>
                    <input
                        type="text"
                        className="search-input"
                        placeholder="Szukaj imienia lub emaila..."
                        value={searchTerm}
                        onChange={e => setSearchTerm(e.target.value)}
                    />
                </div>

                {/* FriendsList będzie wymagał osobnego refaktoru CSS */}
                <FriendsList
                    friends={searchTerm ? filteredFriends : friendshipInfo.friends}
                    onRemoveFriend={removeFriend}
                    onBlockUser={blockUser}
                    onOpenChat={setActiveChatFriend}
                    searchTerm={searchTerm}
                />
            </div>

            {/* ChatPanel (modal) również będzie wymagał refaktoru CSS */}
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