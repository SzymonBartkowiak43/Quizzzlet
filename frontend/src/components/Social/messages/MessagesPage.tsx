import React, { useState, useEffect } from 'react';
import { useMessages } from '../../../hooks/useMessages';
import { useFriendships } from '../../../hooks/userFriendships';
import {
    MessageCircle,
    Users,
    Plus,
    Search,
    Filter,
    RefreshCw,
    Send,
    PaperclipIcon
} from 'lucide-react';
import LoadingSpinner from '../../Shared/LoadingSpinner';
import ChatWindow from './ChatWindow';
import GroupChatWindow from './GroupChatWindow';


type ChatType = 'private' | 'group';
type ViewMode = 'conversations' | 'chat';

interface ActiveChat {
    type: ChatType;
    id: number;
    name: string;
    isGroup: boolean;
}

const MessagesPage: React.FC = () => {
    const {
        messagingInfo,
        conversations,
        loading,
        error,
        sendPrivateMessage,
        sendGroupMessage,
        getConversation,
        refreshMessages
    } = useMessages();

    const { friendshipInfo } = useFriendships();

    const [viewMode, setViewMode] = useState<ViewMode>('conversations');
    const [activeChat, setActiveChat] = useState<ActiveChat | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [refreshing, setRefreshing] = useState(false);

    const handleRefresh = async () => {
        setRefreshing(true);
        await refreshMessages();
        setRefreshing(false);
    };

    const openPrivateChat = async (userId: number, userName: string) => {
        setActiveChat({
            type: 'private',
            id: userId,
            name: userName,
            isGroup: false
        });
        setViewMode('chat');
        await getConversation(userId);
    };

    const openGroupChat = (groupId: number, groupName: string) => {
        setActiveChat({
            type: 'group',
            id: groupId,
            name: groupName,
            isGroup: true
        });
        setViewMode('chat');
    };

    const backToConversations = () => {
        setViewMode('conversations');
        setActiveChat(null);
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-[400px]">
                <LoadingSpinner size="lg" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="text-center py-8">
                <div className="bg-red-50 border border-red-200 rounded-lg p-6 max-w-md mx-auto">
                    <h3 className="text-red-800 font-medium mb-2">Błąd ładowania</h3>
                    <p className="text-red-600 mb-4">{error}</p>
                    <button
                        onClick={handleRefresh}
                        className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors"
                    >
                        Spróbuj ponownie
                    </button>
                </div>
            </div>
        );
    }

    if (!messagingInfo) return null;

    return (
        <div className="max-w-7xl mx-auto p-6">
            {viewMode === 'conversations' && (
                <>
                    {/* Header */}
                    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900">Wiadomości</h1>
                            <p className="text-gray-600 mt-1">
                                Zarządzaj konwersacjami prywatnymi i grupowymi
                            </p>
                        </div>

                        <div className="flex gap-3">
                            <div className="relative">
                                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                                <input
                                    type="text"
                                    placeholder="Szukaj konwersacji..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                />
                            </div>

                            <button
                                // onClick={() => setShowNewMessage(true)}
                                className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                            >
                                <Plus className="h-4 w-4" />
                                Nowa wiadomość
                            </button>

                            <button
                                // onClick={() => setShowShareWordSet(true)}
                                className="flex items-center gap-2 bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors"
                            >
                                <PaperclipIcon className="h-4 w-4" />
                                Udostępnij słówka
                            </button>

                            <button
                                onClick={handleRefresh}
                                disabled={refreshing}
                                className="flex items-center gap-2 bg-white border border-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50"
                            >
                                <RefreshCw className={`h-4 w-4 ${refreshing ? 'animate-spin' : ''}`} />
                                Odśwież
                            </button>
                        </div>
                    </div>

                    {/* Stats Cards */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
                        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm font-medium text-gray-600 mb-1">Wszystkie konwersacje</p>
                                    <p className="text-3xl font-bold text-blue-900">{messagingInfo.conversationsCount}</p>
                                </div>
                                <MessageCircle className="h-8 w-8 text-blue-600" />
                            </div>
                        </div>

                        <div className="bg-orange-50 border border-orange-200 rounded-lg p-6">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm font-medium text-gray-600 mb-1">Nieprzeczytane</p>
                                    <p className="text-3xl font-bold text-orange-900">{messagingInfo.unreadCount}</p>
                                    {messagingInfo.unreadCount > 0 && (
                                        <p className="text-xs text-orange-600 mt-1">Wymagają uwagi</p>
                                    )}
                                </div>
                                <div className="relative">
                                    <MessageCircle className="h-8 w-8 text-orange-600" />
                                    {messagingInfo.unreadCount > 0 && (
                                        <div className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full animate-pulse"></div>
                                    )}
                                </div>
                            </div>
                        </div>

                        <div className="bg-green-50 border border-green-200 rounded-lg p-6">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm font-medium text-gray-600 mb-1">Aktywne grupy</p>
                                    <p className="text-3xl font-bold text-green-900">{messagingInfo.activeGroups.length}</p>
                                </div>
                                <Users className="h-8 w-8 text-green-600" />
                            </div>
                        </div>
                    </div>

                     Conversations List
                </>
            )}

            {viewMode === 'chat' && activeChat && (
                <>
                    {activeChat.type === 'private' ? (
                        <ChatWindow
                            userId={activeChat.id}
                            userName={activeChat.name}
                            messages={conversations[activeChat.id] || []}
                            onSendMessage={sendPrivateMessage}
                            onBack={backToConversations}
                            // onMarkAsRead={() => markMessagesAsRead(activeChat?.id)}
                        />
                    ) : (
                        <GroupChatWindow
                            groupId={activeChat.id}
                            groupName={activeChat.name}
                            onSendMessage={sendGroupMessage}
                            onBack={backToConversations}
                        />
                    )}
                </>
            )}
        </div>
    );
};

export default MessagesPage;