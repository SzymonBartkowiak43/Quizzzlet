import React from 'react';
import { MessagingInfo, Friendship, GroupInfo } from '../../../types/social';
import {
    MessageCircle,
    Users,
    Clock,
    User,
    Mail,
    Hash
} from 'lucide-react';

interface ConversationsListProps {
    messagingInfo: MessagingInfo;
    friendshipInfo: any;
    groupInfo: any;
    searchTerm: string;
    onOpenPrivateChat: (userId: number, userName: string) => void;
    onOpenGroupChat: (groupId: number, groupName: string) => void;
}

const ConversationsList: React.FC<ConversationsListProps> = ({
                                                                 messagingInfo,
                                                                 friendshipInfo,
                                                                 groupInfo,
                                                                 searchTerm,
                                                                 onOpenPrivateChat,
                                                                 onOpenGroupChat
                                                             }) => {
    const filteredConversations = messagingInfo.conversations.filter(message =>
        message.sender.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        message.recipient.name.toLowerCase().includes(searchTerm.toLowerCase())
    );
    const filteredGroups = messagingInfo.activeGroups.filter(group =>
        group.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const formatTime = (dateString: string) => {
        const date = new Date(dateString);
        const now = new Date();
        const diffTime = Math.abs(now.getTime() - date.getTime());
        const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays === 0) {
            return date.toLocaleTimeString('pl-PL', {
                hour: '2-digit',
                minute: '2-digit'
            });
        } else if (diffDays === 1) {
            return 'Wczoraj';
        } else if (diffDays < 7) {
            return `${diffDays} dni temu`;
        }
        return date.toLocaleDateString('pl-PL');
    };

    const truncateMessage = (text: string, maxLength: number = 60) => {
        return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
    };

    const getUnreadCount = (userId: number) => {
        return messagingInfo.unreadMessages.filter(msg =>
            msg.sender.id === userId || msg.recipient.id === userId
        ).length;
    };
    const groupedConversations = filteredConversations.reduce((acc, message) => {
        const otherUser = message.sender.name === 'SzymonBartkowiak43' ? message.recipient : message.sender;
        const key = otherUser.id;
        if (!acc[key] || new Date(message.createdAt) > new Date(acc[key].createdAt)) {
            acc[key] = { ...message, otherUser };
        }
        return acc;
    }, {} as Record<number, any>);
    const conversationsList = Object.values(groupedConversations);

    if (conversationsList.length === 0 && filteredGroups.length === 0) {
        return (
            <div className="glass-box">
                <div className="text-center py-12">
                    <MessageCircle className="h-16 w-16 text-gray-400 mx-auto mb-4" />
                    <h3 className="text-lg font-medium text-white mb-2">
                        {searchTerm ? 'Brak wyników wyszukiwania' : 'Brak konwersacji'}
                    </h3>
                    <p className="text-gray-300 mb-6">
                        {searchTerm
                            ? `Nie znaleziono konwersacji pasujących do "${searchTerm}"`
                            : 'Rozpocznij konwersację z przyjaciółmi lub w grupach!'
                        }
                    </p>
                    {!searchTerm && (
                        <button className="btn-primary-solid">
                            Rozpocznij pierwszą konwersację
                        </button>
                    )}
                </div>
            </div>
        );
    }

    return (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Konwersacje prywatne */}
            {conversationsList.length > 0 && (
                <div className="glass-box-flat">
                    <div className="p-6 border-b border-white/20">
                        <div className="flex items-center gap-2">
                            <User className="h-5 w-5 text-gray-300" />
                            <h2 className="box-title mb-0">
                                Konwersacje prywatne ({conversationsList.length})
                            </h2>
                        </div>
                    </div>

                    <div className="divide-y divide-white/10">
                        {conversationsList.map((conversation) => {
                            const unreadCount = getUnreadCount(conversation.otherUser.id);
                            const isUnread = !conversation.isRead && conversation.recipient.name === 'SzymonBartkowiak43';

                            return (
                                <button
                                    key={conversation.otherUser.id}
                                    onClick={() => onOpenPrivateChat(conversation.otherUser.id, conversation.otherUser.name)}
                                    className="w-full p-4 hover:bg-white/10 transition-colors text-left"
                                >
                                    <div className="flex items-center gap-4">
                                        <div className="relative">
                                            <div className="w-12 h-12 bg-blue-500/30 rounded-full flex items-center justify-center">
                                                <span className="text-lg font-semibold text-blue-100">
                                                  {conversation.otherUser.name.charAt(0).toUpperCase()}
                                                </span>
                                            </div>
                                            {unreadCount > 0 && (
                                                <div className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 rounded-full flex items-center justify-center">
                                                  <span className="text-xs text-white font-medium">
                                                    {unreadCount > 9 ? '9+' : unreadCount}
                                                  </span>
                                                </div>
                                            )}
                                        </div>

                                        <div className="flex-1 min-w-0">
                                            <div className="flex items-center justify-between mb-1">
                                                <h3 className={`font-medium ${isUnread ? 'text-white font-semibold' : 'text-gray-200'}`}>
                                                    {conversation.otherUser.name}
                                                </h3>
                                                <div className="flex items-center gap-1 text-xs text-gray-400">
                                                    <Clock className="h-3 w-3" />
                                                    <span>{formatTime(conversation.createdAt)}</span>
                                                </div>
                                            </div>

                                            <p className={`text-sm ${isUnread ? 'text-white' : 'text-gray-300'}`}>
                                                {conversation.sharedWordSet ? (
                                                    <span className="flex items-center gap-1">
                                                        <Mail className="h-3 w-3" />
                                                        Udostępnił zestaw słówek
                                                    </span>
                                                ) : (
                                                    truncateMessage(conversation.content)
                                                )}
                                            </p>

                                            <div className="flex items-center justify-between mt-1">
                                                <span className="text-xs text-gray-400">
                                                  {conversation.otherUser.email}
                                                </span>
                                                {isUnread && (
                                                    <div className="w-2 h-2 bg-blue-400 rounded-full"></div>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                </button>
                            );
                        })}
                    </div>
                </div>
            )}

            {/* Konwersacje grupowe */}
            {filteredGroups.length > 0 && (
                <div className="glass-box-flat">
                    <div className="p-6 border-b border-white/20">
                        <div className="flex items-center gap-2">
                            <Users className="h-5 w-5 text-gray-300" />
                            <h2 className="box-title mb-0">
                                Grupy ({filteredGroups.length})
                            </h2>
                        </div>
                    </div>

                    <div className="divide-y divide-white/10">
                        {filteredGroups.map((group) => (
                            <button
                                key={group.id}
                                onClick={() => onOpenGroupChat(group.id, group.name)}
                                className="w-full p-4 hover:bg-white/10 transition-colors text-left"
                            >
                                <div className="flex items-center gap-4">
                                    <div className="w-12 h-12 bg-green-500/30 rounded-full flex items-center justify-center">
                                        <Hash className="h-6 w-6 text-green-200" />
                                    </div>

                                    <div className="flex-1 min-w-0">
                                        <div className="flex items-center justify-between mb-1">
                                            <h3 className="font-medium text-white">
                                                {group.name}
                                            </h3>
                                            <div className="flex items-center gap-1 text-xs text-gray-400">
                                                <Users className="h-3 w-3" />
                                                <span>{group.memberCount}</span>
                                            </div>
                                        </div>

                                        <p className="text-sm text-gray-300">
                                            {truncateMessage(group.description)}
                                        </p>

                                        <div className="flex items-center justify-between mt-1">
                                          <span className="text-xs text-gray-400">
                                            {group.isPrivate ? 'Prywatna' : 'Publiczna'}
                                          </span>
                                            <span className="text-xs text-gray-400">
                                            Ostatnia aktywność: dziś
                                          </span>
                                        </div>
                                    </div>
                                </div>
                            </button>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default ConversationsList;