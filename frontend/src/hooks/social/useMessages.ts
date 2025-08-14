import { useState, useEffect, useCallback } from 'react';
import { socialApi } from '../../services/social/socialApi';
import {
    MessagingInfo,
    PrivateMessage,
    GroupMessage,
    SendMessageRequest,
    ShareWordSetRequest
} from '../../types/social';
import { toast } from 'react-toastify';

interface UseMessagesReturn {
    messagingInfo: MessagingInfo | null;
    conversations: Record<number, PrivateMessage[]>;
    loading: boolean;
    error: string | null;
    sendPrivateMessage: (recipientId: number, content: string) => Promise<void>;
    sendGroupMessage: (groupId: number, content: string) => Promise<void>;
    shareWordSetPrivately: (recipientId: number, wordSetId: number, message: string) => Promise<void>;
    shareWordSetInGroup: (groupId: number, wordSetId: number, message: string) => Promise<void>;
    getConversation: (userId: number) => Promise<void>;
    markMessagesAsRead: (userId: number) => Promise<void>;
    deletePrivateMessage: (messageId: number) => Promise<void>;
    deleteGroupMessage: (messageId: number) => Promise<void>;
    refreshMessages: () => Promise<void>;
}

export const useMessages = (): UseMessagesReturn => {
    const [messagingInfo, setMessagingInfo] = useState<MessagingInfo | null>(null);
    const [conversations, setConversations] = useState<Record<number, PrivateMessage[]>>({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const loadMessages = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await socialApi.getMyMessages();
            setMessagingInfo(data);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Błąd podczas ładowania wiadomości');
            toast.error('Nie udało się załadować wiadomości');
        } finally {
            setLoading(false);
        }
    }, []);

    const sendPrivateMessage = useCallback(async (recipientId: number, content: string) => {
        try {
            const request: SendMessageRequest = { recipientId, content };
            const response = await socialApi.sendPrivateMessage(request);
            toast.success(response.message || 'Wiadomość została wysłana');

            // Update conversation
            await getConversation(recipientId);
            await loadMessages(); // Refresh messaging info
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się wysłać wiadomości');
        }
    }, [loadMessages]);

    const sendGroupMessage = useCallback(async (groupId: number, content: string) => {
        try {
            const request: SendMessageRequest = { groupId, content };
            const response = await socialApi.sendGroupMessage(request);
            toast.success(response.message || 'Wiadomość została wysłana do grupy');
            await loadMessages();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się wysłać wiadomości do grupy');
        }
    }, [loadMessages]);

    const shareWordSetPrivately = useCallback(async (recipientId: number, wordSetId: number, message: string) => {
        try {
            const request: ShareWordSetRequest = { recipientId, wordSetId, message };
            const response = await socialApi.shareWordSetPrivately(request);
            toast.success(response.message || 'Zestaw słówek został udostępniony');
            await getConversation(recipientId);
            await loadMessages();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się udostępnić zestawu słówek');
        }
    }, [loadMessages]);

    const shareWordSetInGroup = useCallback(async (groupId: number, wordSetId: number, message: string) => {
        try {
            const request: ShareWordSetRequest = { groupId, wordSetId, message };
            const response = await socialApi.shareWordSetInGroup(request);
            toast.success(response.message || 'Zestaw słówek został udostępniony w grupie');
            await loadMessages();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się udostępnić zestawu słówek w grupie');
        }
    }, [loadMessages]);

    const getConversation = useCallback(async (userId: number) => {
        try {
            const data = await socialApi.getConversation(userId);
            setConversations(prev => ({
                ...prev,
                [userId]: data.messages
            }));
        } catch (err: any) {
            toast.error('Nie udało się załadować konwersacji');
        }
    }, []);

    const markMessagesAsRead = useCallback(async (userId: number) => {
        try {
            await socialApi.markMessagesAsRead(userId);
            await loadMessages();
            await getConversation(userId);
        } catch (err: any) {
            toast.error('Nie udało się oznaczyć wiadomości jako przeczytane');
        }
    }, [loadMessages, getConversation]);

    const deletePrivateMessage = useCallback(async (messageId: number) => {
        try {
            const response = await socialApi.deletePrivateMessage(messageId);
            toast.success(response.message || 'Wiadomość została usunięta');
            await loadMessages();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się usunąć wiadomości');
        }
    }, [loadMessages]);

    const deleteGroupMessage = useCallback(async (messageId: number) => {
        try {
            const response = await socialApi.deleteGroupMessage(messageId);
            toast.success(response.message || 'Wiadomość została usunięta');
            await loadMessages();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się usunąć wiadomości');
        }
    }, [loadMessages]);

    const refreshMessages = useCallback(async () => {
        await loadMessages();
    }, [loadMessages]);

    useEffect(() => {
        loadMessages();
    }, [loadMessages]);

    return {
        messagingInfo,
        conversations,
        loading,
        error,
        sendPrivateMessage,
        sendGroupMessage,
        shareWordSetPrivately,
        shareWordSetInGroup,
        getConversation,
        markMessagesAsRead,
        deletePrivateMessage,
        deleteGroupMessage,
        refreshMessages
    };
};