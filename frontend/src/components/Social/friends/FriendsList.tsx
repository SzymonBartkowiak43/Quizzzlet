import React from 'react';
import { DisplayUser } from '../../../types/social';
import './FriendsList.css';
import { MessageCircle, Trash2, XCircle } from 'lucide-react';

interface FriendsListProps {
    friends: DisplayUser[];
    onRemoveFriend: (friendId: number) => void;
    onBlockUser: (userId: number) => void;
    onOpenChat: (friend: DisplayUser) => void;
    searchTerm?: string;
    processingId?: number | null;
}

const FriendsList: React.FC<FriendsListProps> = ({
                                                     friends,
                                                     onRemoveFriend,
                                                     onBlockUser,
                                                     onOpenChat,
                                                     searchTerm,
                                                     processingId
                                                 }) => {
    if (friends.length === 0) {
        return (
            <div className="friendslist-empty">
                {searchTerm
                    ? <>Brak wyników dla "<b>{searchTerm}</b>"</>
                    : "Nie masz jeszcze przyjaciół."}
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
                            className="btn-glass"
                            onClick={() => onOpenChat(friend)}
                        >
                            <MessageCircle size={16} />
                            Chat
                        </button>
                        <button
                            className="btn-glass-danger"
                            onClick={() => onRemoveFriend(friend.id)}
                            disabled={processingId === friend.id}
                        >
                            <Trash2 size={16} />
                            Usuń
                        </button>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default FriendsList;