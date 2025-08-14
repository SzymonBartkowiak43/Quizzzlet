import React from 'react';
import { MessagingInfo, FriendshipInfo, GroupInfo } from '../../../types/social';
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
    friendshipInfo: FriendshipInfo | null;
    groupInfo: GroupInfo | null;
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
    // Filter conversations based on search term
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

    // Get unread count for specific user
    const getUnreadCount = (userId: number) => {
        return messagingInfo.unreadMessages.filter(msg =>
            msg.sender.id === userId || msg.recipient.id === userId
        ).length;
    };

    // Group conversations by user
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
            <div className="bg-white rounded-lg shadow p-6">
                <div className="text-center py-12">
                    <MessageCircle className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                    <h3 className="text-lg font-medium text-gray-900 mb-2">
                        {searchTerm ? 'Brak wyników wyszukiwania' : 'Brak konwersacji'}
                    </h3>
                    <p className="text-gray-500 mb-6">
                        {searchTerm
                            ? `Nie znaleziono konwersacji pasujących do "${searchTerm}"`
                            : 'Rozpocznij konwersację z przyjaciółmi lub w grupach!'
                        }
                    </p>
                    {!searchTerm && (
                        <button className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors">
                            Rozpocznij pierwszą konwersację
                        </button>
                    )}
                </div>
            </div>
        );
    }

    return (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Private Conversations */}
            {conversationsList.length > 0 && (
                <div className="bg-white rounded-lg shadow">
                    <div className="p-6 border-b border-gray-200">
                        <div className="flex items-center gap-2">
                            <User className="h-5 w-5 text-gray-600" />
                            <h2 className="text-lg font-semibold text-gray-900">
                                Konwersacje prywatne ({conversationsList.length})
                            </h2>
                        </div>
                    </div>

                    <div className="divide-y divide-gray-200">
                        {conversationsList.map((conversation) => {
                            const unreadCount = getUnreadCount(conversation.otherUser.id);
                            const isUnread = !conversation.isRead && conversation.recipient.name === 'SzymonBartkowiak43';

                            return (
                                <button
                                    key={conversation.otherUser.id}
                                    onClick={() => onOpenPrivateChat(conversation.otherUser.id, conversation.otherUser.name)}
                                    className="w-full p-4 hover:bg-gray-50 transition-colors text-left"
                                >
                                    <div className="flex items-center gap-4">
                                        <div className="relative">
                                            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                        <span className="text-lg font-semibold text-blue-600">
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
                                                <h3 className={`font-medium ${isUnread ? 'text-gray-900 font-semibold' : 'text-gray-900'}`}>
                                                    {conversation.otherUser.name}
                                                </h3>
                                                <div className="flex items-center gap-1 text-xs text-gray-500">
                                                    <Clock className="h-3 w-3" />
                                                    <span>{formatTime(conversation.createdAt)}</span>
                                                </div>
                                            </div>

                                            <p className={`text-sm ${isUnread ? 'text-gray-700 font-medium' : 'text-gray-600'}`}>
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
                        <span className="text-xs text-gray-500">
                          {conversation.otherUser.email}
                        </span>
                                                {isUnread && (
                                                    <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
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

            {/* Group Conversations */}
            {filteredGroups.length > 0 && (
                <div className="bg-white rounded-lg shadow">
                    <div className="p-6 border-b border-gray-200">
                        <div className="flex items-center gap-2">
                            <Users className="h-5 w-5 text-gray-600" />
                            <h2 className="text-lg font-semibold text-gray-900">
                                Grupy ({filteredGroups.length})
                            </h2>
                        </div>
                    </div>

                    <div className="divide-y divide-gray-200">
                        {filteredGroups.map((group) => (
                            <button
                                key={group.id}
                                onClick={() => onOpenGroupChat(group.id, group.name)}
                                className="w-full p-4 hover:bg-gray-50 transition-colors text-left"
                            >
                                <div className="flex items-center gap-4">
                                    <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                                        <Hash className="h-6 w-6 text-green-600" />
                                    </div>

                                    <div className="flex-1 min-w-0">
                                        <div className="flex items-center justify-between mb-1">
                                            <h3 className="font-medium text-gray-900">
                                                {group.name}
                                            </h3>
                                            <div className="flex items-center gap-1 text-xs text-gray-500">
                                                <Users className="h-3 w-3" />
                                                <span>{group.memberCount}</span>
                                            </div>
                                        </div>

                                        <p className="text-sm text-gray-600">
                                            {truncateMessage(group.description)}
                                        </p>

                                        <div className="flex items-center justify-between mt-1">
                      <span className="text-xs text-gray-500">
                        {group.isPrivate ? 'Prywatna' : 'Publiczna'}
                      </span>
                                            <span className="text-xs text-gray-500">
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