import React, { useState } from 'react';
import { useFriendships } from '../../../hooks/userFriendships';
import FriendsList from './FriendsList';

const FriendsPage: React.FC = () => {
    const { friendshipInfo, loading, error, removeFriend, blockUser } = useFriendships();
    const [searchTerm, setSearchTerm] = useState('');

    if (loading) return <div>Ładuję...</div>;
    if (error) return <div>Błąd: {error}</div>;
    if (!friendshipInfo) return <div>Brak danych o znajomych.</div>;

    const filteredFriends = friendshipInfo.friends.filter(friend =>
        friend.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        friend.email?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div>
            <h2>Twoi przyjaciele ({friendshipInfo.friendsCount})</h2>
            <input
                type="text"
                placeholder="Szukaj..."
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
            />
            <FriendsList
                friends={searchTerm ? filteredFriends : friendshipInfo.friends}
                onRemoveFriend={removeFriend}
                onBlockUser={blockUser}
                searchTerm={searchTerm}
            />
        </div>
    );
};

export default FriendsPage;