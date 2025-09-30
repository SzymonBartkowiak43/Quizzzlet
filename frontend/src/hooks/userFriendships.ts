import { useState, useEffect } from 'react';
import { FriendshipInfo, DisplayUser } from '../types/social';
import socialApi from '../services/socialApi';

export const useFriendships = () => {
    const [friendshipInfo, setFriendshipInfo] = useState<FriendshipInfo | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchFriendships = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await socialApi.getMyFriendships();
            setFriendshipInfo(data);
        } catch (err) {
            console.error('Failed to fetch friendships:', err);
            setError('Nie udało się załadować przyjaciół');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchFriendships();
    }, []);

    const removeFriend = async (userId: number): Promise<void> => {
        try {
            await socialApi.removeFriend(userId);
            await fetchFriendships();
        } catch (error) {
            console.error('Failed to remove friend:', error);
            throw error;
        }
    };

    const blockUser = async (userId: number): Promise<void> => {
        try {
            throw new Error('Block user not implemented yet');
        } catch (error) {
            console.error('Failed to block user:', error);
            throw error;
        }
    };


    return {
        friendshipInfo,
        loading,
        error,
        refetch: fetchFriendships,
        removeFriend,
        blockUser,
    };
};