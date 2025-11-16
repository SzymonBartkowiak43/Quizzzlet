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
import ConversationsList from './ConversationsList';
import './MessagesPage.css';

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

    const mockGroupInfo = messagingInfo?.activeGroups || [];


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
            <div className="messages-page">
                <div className="loading-container">
                    <LoadingSpinner size="lg" color="white" />
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="messages-page">
                <div className="error-container">
                    <div className="error-message max-w-md mx-auto">
                        <h3 className="font-medium mb-2">Błąd ładowania</h3>
                        <p className="mb-4">{error}</p>
                        <button
                            onClick={handleRefresh}
                            className="btn-glass-danger"
                        >
                            Spróbuj ponownie
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    if (!messagingInfo) return <div className="messages-page"><div className="loading-container">Brak danych</div></div>;

    return (
        <div className="messages-page">
            {viewMode === 'conversations' && (
                <>
                    {/* Header */}
                    <div className="page-header">
                        <div>
                            <h1>Wiadomości</h1>
                            <p className="page-subtitle">
                                Zarządzaj konwersacjami prywatnymi i grupowymi
                            </p>
                        </div>

                        <div className="flex gap-3">
                            <button
                                className="btn-primary-solid"
                            >
                                <Plus className="h-4 w-4" />
                                Nowa wiadomość
                            </button>
                            <button
                                onClick={handleRefresh}
                                disabled={refreshing}
                                className="btn-glass"
                            >
                                <RefreshCw className={`h-4 w-4 ${refreshing ? 'animate-spin' : ''}`} />
                                Odśwież
                            </button>
                        </div>
                    </div>

                    {/* Wyszukiwarka */}
                    <div className="mb-6">
                        <div className="relative">
                            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                            <input
                                type="text"
                                placeholder="Szukaj konwersacji lub grup..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="search-input"
                            />
                        </div>
                    </div>

                    <ConversationsList
                        messagingInfo={messagingInfo}
                        friendshipInfo={friendshipInfo || { friends: [], pendingRequests: [], sentRequests: [] }}
                        groupInfo={mockGroupInfo}
                        searchTerm={searchTerm}
                        onOpenPrivateChat={openPrivateChat}
                        onOpenGroupChat={openGroupChat}
                    />
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