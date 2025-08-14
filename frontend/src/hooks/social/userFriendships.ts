import { useState, useEffect, useCallback } from 'react';
import { socialApi } from '../../services/social/socialApi';
import {
    FriendshipInfo,
    FriendRequestDto,
    Friendship,
    User
} from '../../types/social';
import { toast } from 'react-toastify';

interface UseFriendshipsReturn {
    friendshipInfo: FriendshipInfo | null;
    loading: boolean;
    error: string | null;
    sendFriendRequest: (addresseeId: number) => Promise<void>;
    acceptFriendRequest: (friendshipId: number) => Promise<void>;
    declineFriendRequest: (friendshipId: number) => Promise<void>;
    removeFriend: (friendId: number) => Promise<void>;
    blockUser: (userId: number) => Promise<void>;
    checkFriendshipStatus: (userId: number) => Promise<any>;
    refreshFriendships: () => Promise<void>;
}

export const useFriendships = (): UseFriendshipsReturn => {
    const [friendshipInfo, setFriendshipInfo] = useState<FriendshipInfo | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const loadFriendships = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await socialApi.getMyFriendships();
            setFriendshipInfo(data);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Błąd podczas ładowania przyjaciół');
            toast.error('Nie udało się załadować listy przyjaciół');
        } finally {
            setLoading(false);
        }
    }, []);

    const sendFriendRequest = useCallback(async (addresseeId: number) => {
        try {
            const request: FriendRequestDto = { addresseeId };
            const response = await socialApi.sendFriendRequest(request);
            toast.success(response.message || 'Zaproszenie zostało wysłane');
            await loadFriendships(); // Refresh data
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się wysłać zaproszenia');
        }
    }, [loadFriendships]);

    const acceptFriendRequest = useCallback(async (friendshipId: number) => {
        try {
            const response = await socialApi.acceptFriendRequest(friendshipId);
            toast.success(response.message || 'Zaproszenie zostało zaakceptowane');
            await loadFriendships();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się zaakceptować zaproszenia');
        }
    }, [loadFriendships]);

    const declineFriendRequest = useCallback(async (friendshipId: number) => {
        try {
            const response = await socialApi.declineFriendRequest(friendshipId);
            toast.success(response.message || 'Zaproszenie zostało odrzucone');
            await loadFriendships();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się odrzucić zaproszenia');
        }
    }, [loadFriendships]);

    const removeFriend = useCallback(async (friendId: number) => {
        try {
            const response = await socialApi.removeFriend(friendId);
            toast.success(response.message || 'Przyjaźń została usunięta');
            await loadFriendships();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się usunąć przyjaźni');
        }
    }, [loadFriendships]);

    const blockUser = useCallback(async (userId: number) => {
        try {
            const response = await socialApi.blockUser(userId);
            toast.success(response.message || 'Użytkownik został zablokowany');
            await loadFriendships();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się zablokować użytkownika');
        }
    }, [loadFriendships]);

    const checkFriendshipStatus = useCallback(async (userId: number) => {
        try {
            return await socialApi.checkFriendshipStatus(userId);
        } catch (err: any) {
            toast.error('Nie udało się sprawdzić statusu przyjaźni');
            return null;
        }
    }, []);

    const refreshFriendships = useCallback(async () => {
        await loadFriendships();
    }, [loadFriendships]);

    useEffect(() => {
        loadFriendships();
    }, [loadFriendships]);

    return {
        friendshipInfo,
        loading,
        error,
        sendFriendRequest,
        acceptFriendRequest,
        declineFriendRequest,
        removeFriend,
        blockUser,
        checkFriendshipStatus,
        refreshFriendships
    };
};