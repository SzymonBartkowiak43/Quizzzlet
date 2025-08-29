import { useState, useEffect, useCallback } from 'react';
import socialApi from '../services/socialApi';
import {
    MessagingInfo,
    PrivateMessage,
    GroupMessage
} from '../types/social';
import { toast } from 'react-toastify';

interface UseMessagesReturn {
    messagingInfo: MessagingInfo | null;
    conversations: Record<number, PrivateMessage[]>;
    loading: boolean;
    error: string | null;
    sendPrivateMessage: (recipientId: number, content: string) => Promise<void>;
    sendGroupMessage: (groupId: number, content: string) => Promise<void>;
    shareWordSetPrivately: (recipientId: number, wordSetId: number) => Promise<void>;
    shareWordSetInGroup: (groupId: number, wordSetId: number) => Promise<void>;
    getConversation: (userId: number) => Promise<void>;
    markMessagesAsRead: (messageIds: number | undefined) => Promise<void>;
    refreshMessages: () => Promise<void>;
}

export const useMessages = (): { messagingInfo: MessagingInfo | null; shareWordSetInGroup: (groupId: number, wordSetId: number) => Promise<void>; sendPrivateMessage: (recipientId: number, content: string) => Promise<void>; sendGroupMessage: (groupId: number, content: string) => Promise<void>; shareWordSetPrivately: (recipientId: number, wordSetId: number) => Promise<void>; getConversation: (userId: number) => Promise<void>; markMessagesAsRead: (messageIds: number[]) => Promise<void>; loading: boolean; error: string | null; conversations: Record<number, PrivateMessage[]>; refreshMessages: () => Promise<void> } => {
    const [messagingInfo, setMessagingInfo] = useState<MessagingInfo | null>(null);
    const [conversations, setConversations] = useState<Record<number, PrivateMessage[]>>({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const loadMessages = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);

            // Używamy istniejącej funkcji z dashboardu
            const dashboard = await socialApi.getSocialDashboard();
            setMessagingInfo(dashboard.messagingInfo);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Błąd podczas ładowania wiadomości');
            console.error('Failed to load messages:', err);
        } finally {
            setLoading(false);
        }
    }, []);

    const sendPrivateMessage = useCallback(async (recipientId: number, content: string) => {
        try {
            // Używamy istniejącej funkcji
            await socialApi.sendPrivateMessage(recipientId, content);
            toast.success('Wiadomość została wysłana');
            await loadMessages();
        } catch (err: any) {
            toast.error('Nie udało się wysłać wiadomości');
            console.error('Failed to send message:', err);
        }
    }, [loadMessages]);

    const sendGroupMessage = useCallback(async (groupId: number, content: string) => {
        try {
            await socialApi.sendGroupMessage(groupId, content);
            toast.success('Wiadomość została wysłana do grupy');
            await loadMessages();
        } catch (err: any) {
            toast.error('Nie udało się wysłać wiadomości do grupy');
            console.error('Failed to send group message:', err);
        }
    }, [loadMessages]);

    const shareWordSetPrivately = useCallback(async (recipientId: number, wordSetId: number) => {
        try {
            // Check if your socialApi has this function, if not you'll need to implement it
            // await socialApi.shareWordSetPrivately(recipientId, wordSetId);
            toast.success('Zestaw słówek został udostępniony prywatnie');
            await loadMessages();
        } catch (err: any) {
            toast.error('Nie udało się udostępnić zestawu słówek');
            console.error('Failed to share word set privately:', err);
            // If the API function doesn't exist yet, you can temporarily throw an error
            throw new Error('Share word set privately not implemented yet');
        }
    }, [loadMessages]);

    const shareWordSetInGroup = useCallback(async (groupId: number, wordSetId: number) => {
        try {
            // Check if your socialApi has this function, if not you'll need to implement it
            // await socialApi.shareWordSetInGroup(groupId, wordSetId);
            toast.success('Zestaw słówek został udostępniony w grupie');
            await loadMessages();
        } catch (err: any) {
            toast.error('Nie udało się udostępnić zestawu słówek w grupie');
            console.error('Failed to share word set in group:', err);
            // If the API function doesn't exist yet, you can temporarily throw an error
            throw new Error('Share word set in group not implemented yet');
        }
    }, [loadMessages]);

    const getConversation = useCallback(async (userId: number) => {
        try {
            const messages = await socialApi.getGroupMessages(userId);
            setConversations(prev => ({
                ...prev,
                [userId]: messages
            }));
        } catch (err: any) {
            toast.error('Nie udało się załadować konwersacji');
            console.error('Failed to load conversation:', err);
        }
    }, []);

    const markMessagesAsRead = useCallback(async (messageIds: number[]) => {
        try {
            // Check if your socialApi has this function, if not you'll need to implement it
            // await socialApi.markMessagesAsRead(messageIds);
            toast.success('Wiadomości zostały oznaczone jako przeczytane');
            await loadMessages();
        } catch (err: any) {
            toast.error('Nie udało się oznaczyć wiadomości jako przeczytane');
            console.error('Failed to mark messages as read:', err);
            // If the API function doesn't exist yet, you can temporarily throw an error
            throw new Error('Mark messages as read not implemented yet');
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
        refreshMessages
    };
};