import React from 'react';
import { DisplayUser } from '../../../types/social';
import './FriendsList.css';

interface FriendsListProps {
    friends: DisplayUser[];
    onRemoveFriend: (friendId: number) => Promise<void>;
    onBlockUser: (userId: number) => Promise<void>;
    searchTerm?: string;
}

const FriendsList: React.FC<FriendsListProps> = ({
                                                     friends,
                                                     onRemoveFriend,
                                                     onBlockUser,
                                                     searchTerm
                                                 }) => {
    if (friends.length === 0) {
        return (
            <div className="friendslist-empty">
                <span>
                    {searchTerm
                        ? <>Brak wyników dla "<b>{searchTerm}</b>"</>
                        : "Nie masz jeszcze przyjaciół."}
                </span>
            </div>
        );
    }

    return (
        <div className="friendslist-grid">
            {friends.map(friend => (
                <div key={friend.id} className="friendslist-card">
                    <div className="friendslist-avatar">
                        {friend.name.charAt(0).toUpperCase()}
                    </div>
                    <div className="friendslist-info">
                        <div className="friendslist-name">{friend.name}</div>
                        <div className="friendslist-email">{friend.email}</div>
                    </div>
                    <div className="friendslist-actions">
                        <button
                            className="friendslist-btn remove"
                            onClick={() => onRemoveFriend(friend.id)}
                        >
                            Usuń
                        </button>
                        <button
                            className="friendslist-btn block"
                            onClick={() => onBlockUser(friend.id)}
                        >
                            Zablokuj
                        </button>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default FriendsList;