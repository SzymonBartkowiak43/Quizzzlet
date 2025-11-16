import React, { useState } from 'react';
import { User, StudyGroup} from '../../../types/social';
import { X, Send, Users, Hash, Search } from 'lucide-react';

interface NewMessageModalProps {
    friends: User[];
    groups: any[];
    onClose: () => void;
    onSendPrivate: (userId: number, message: string) => Promise<void>;
    onSendGroup: (groupId: number, message: string) => Promise<void>;
}

type RecipientType = 'friend' | 'group';

const NewMessageModal: React.FC<NewMessageModalProps> = ({
                                                             friends,
                                                             groups,
                                                             onClose,
                                                             onSendPrivate,
                                                             onSendGroup
                                                         }) => {
    const [recipientType, setRecipientType] = useState<RecipientType>('friend');
    const [selectedRecipient, setSelectedRecipient] = useState<number | null>(null);
    const [message, setMessage] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [sending, setSending] = useState(false);

    const filteredFriends = friends.filter(friend =>
        friend.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        friend.email.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const filteredGroups = groups.filter(group =>
        group.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleSend = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedRecipient || !message.trim() || sending) return;

        setSending(true);
        try {
            if (recipientType === 'friend') {
                await onSendPrivate(selectedRecipient, message.trim());
            } else {
                await onSendGroup(selectedRecipient, message.trim());
            }
            onClose();
        } finally {
            setSending(false);
        }
    };

    const getRecipientName = () => {
        if (recipientType === 'friend') {
            const friend = friends.find(f => f.id === selectedRecipient);
            return friend?.name;
        } else {
            const group = groups.find(g => g.id === selectedRecipient);
            return group?.name;
        }
    };

    return (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="glass-box-flat w-full max-w-2xl max-h-[80vh] flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-white/20">
                    <h2 className="text-lg font-semibold text-white">Nowa wiadomość</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-300 hover:text-white transition-colors"
                    >
                        <X className="h-5 w-5" />
                    </button>
                </div>

                {/* Content */}
                <div className="flex-1 overflow-hidden flex flex-col">
                    {/* Taby wyboru */}
                    <div className="flex border-b border-white/20">
                        <button
                            onClick={() => {
                                setRecipientType('friend');
                                setSelectedRecipient(null);
                                setSearchTerm('');
                            }}
                            className={`flex-1 flex items-center justify-center gap-2 py-3 px-4 text-sm font-medium transition-colors ${
                                recipientType === 'friend'
                                    ? 'border-b-2 border-white text-white'
                                    : 'text-gray-400 hover:text-gray-200'
                            }`}
                        >
                            <Users className="h-4 w-4" />
                            Przyjaciele ({friends.length})
                        </button>
                        <button
                            onClick={() => {
                                setRecipientType('group');
                                setSelectedRecipient(null);
                                setSearchTerm('');
                            }}
                            className={`flex-1 flex items-center justify-center gap-2 py-3 px-4 text-sm font-medium transition-colors ${
                                recipientType === 'group'
                                    ? 'border-b-2 border-white text-white'
                                    : 'text-gray-400 hover:text-gray-200'
                            }`}
                        >
                            <Hash className="h-4 w-4" />
                            Grupy ({groups.length})
                        </button>
                    </div>

                    {/* Wyszukiwarka */}
                    <div className="p-4 border-b border-white/20">
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                            <input
                                type="text"
                                placeholder={`Szukaj ${recipientType === 'friend' ? 'przyjaciół' : 'grup'}...`}
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="w-full pl-10 pr-4 py-2 bg-white/10 border border-white/30 rounded-lg text-white placeholder-gray-400 focus:ring-2 focus:ring-white/50 focus:border-transparent"
                            />
                        </div>
                    </div>

                    {/* Lista odbiorców */}
                    <div className="flex-1 overflow-y-auto p-4">
                        {recipientType === 'friend' ? (
                            <div className="space-y-2">
                                {filteredFriends.length === 0 ? (
                                    <div className="text-center py-8 text-gray-400">
                                        {searchTerm ? 'Brak wyników wyszukiwania' : 'Brak przyjaciół'}
                                    </div>
                                ) : (
                                    filteredFriends.map(friend => (
                                        <button
                                            key={friend.id}
                                            onClick={() => setSelectedRecipient(friend.id)}
                                            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-colors text-left ${
                                                selectedRecipient === friend.id
                                                    ? 'bg-white/20 border-2 border-white/30'
                                                    : 'hover:bg-white/10 border-2 border-transparent'
                                            }`}
                                        >
                                            <div className="w-10 h-10 bg-blue-500/30 rounded-full flex items-center justify-center">
                                                <span className="text-sm font-semibold text-blue-100">
                                                  {friend.name.charAt(0).toUpperCase()}
                                                </span>
                                            </div>
                                            <div>
                                                <p className="font-medium text-white">{friend.name}</p>
                                                <p className="text-sm text-gray-300">{friend.email}</p>
                                            </div>
                                        </button>
                                    ))
                                )}
                            </div>
                        ) : (
                            <div className="space-y-2">
                                {filteredGroups.length === 0 ? (
                                    <div className="text-center py-8 text-gray-400">
                                        {searchTerm ? 'Brak wyników wyszukiwania' : 'Brak grup'}
                                    </div>
                                ) : (
                                    filteredGroups.map(group => (
                                        <button
                                            key={group.id}
                                            onClick={() => setSelectedRecipient(group.id)}
                                            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-colors text-left ${
                                                selectedRecipient === group.id
                                                    ? 'bg-green-500/20 border-2 border-green-500/30'
                                                    : 'hover:bg-white/10 border-2 border-transparent'
                                            }`}
                                        >
                                            <div className="w-10 h-10 bg-green-500/30 rounded-full flex items-center justify-center">
                                                <Hash className="h-5 w-5 text-green-200" />
                                            </div>
                                            <div>
                                                <p className="font-medium text-white">{group.name}</p>
                                                <p className="text-sm text-gray-300">
                                                    {group.memberCount} członków • {group.isPrivate ? 'Prywatna' : 'Publiczna'}
                                                </p>
                                            </div>
                                        </button>
                                    ))
                                )}
                            </div>
                        )}
                    </div>

                    {selectedRecipient && (
                        <form onSubmit={handleSend} className="p-4 border-t border-white/20">
                            <div className="mb-3">
                                <p className="text-sm text-gray-300">
                                    Do: <span className="font-medium text-white">{getRecipientName()}</span>
                                </p>
                            </div>
                            <div className="flex gap-3">
                                <textarea
                                    value={message}
                                    onChange={(e) => setMessage(e.target.value)}
                                    placeholder="Napisz swoją wiadomość..."
                                    rows={3}
                                    className="flex-1 px-3 py-2 bg-white/10 border border-white/30 rounded-lg text-white placeholder-gray-400 focus:ring-2 focus:ring-white/50 focus:border-transparent resize-none"
                                    disabled={sending}
                                />
                                <button
                                    type="submit"
                                    disabled={!message.trim() || sending}
                                    className="btn-primary-solid"
                                >
                                    {sending ? (
                                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                                    ) : (
                                        <Send className="h-4 w-4" />
                                    )}
                                    Wyślij
                                </button>
                            </div>
                        </form>
                    )}
                </div>
            </div>
        </div>
    );
};

export default NewMessageModal;