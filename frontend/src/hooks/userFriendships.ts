import {useState, useEffect, useMemo} from 'react';
import { Friendship } from '../types/social';
import socialApi from '../services/socialApi';

type DisplayUser = {
    id: number;
    name: string;
    email: string;
};

export const useFriendships = () => {
    const [friendships, setFriendships] = useState<Friendship[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchFriendships = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await socialApi.getMyFriendships();
            setFriendships(data);
        } catch (err) {
            console.error('Failed to fetch friendships:', err);
            setError('Nie udało się załadować przyjaciół');
        } finally {
            setLoading(false);
        }
    };

    // Original function for email-based requests
    const sendFriendRequestByEmail = async (receiverEmail: string): Promise<void> => {
        try {
            const newFriendship = await socialApi.sendFriendRequest(receiverEmail);
            setFriendships(prev => [...prev, newFriendship]);
        } catch (error) {
            console.error('Failed to send friend request:', error);
            throw error;
        }
    };

    // New function for user ID-based requests (what your components expect)
    const sendFriendRequestByUserId = async (userId: number): Promise<void> => {
        try {
            // You'll need to implement this in your API or convert userId to email
            // Option 1: If your API supports userId
            // const newFriendship = await socialApi.sendFriendRequestByUserId(userId);

            // Option 2: Convert userId to email (you'll need a helper function)
            // const userEmail = await socialApi.getUserEmailById(userId);
            // const newFriendship = await socialApi.sendFriendRequest(userEmail);

            // For now, throwing an error until you implement the conversion
            throw new Error('Send friend request by userId not implemented yet');
        } catch (error) {
            console.error('Failed to send friend request:', error);
            throw error;
        }
    };

    const acceptFriendRequest = async (friendshipId: number): Promise<void> => {
        try {
            const updatedFriendship = await socialApi.acceptFriendRequest(friendshipId);
            setFriendships(prev =>
                prev.map(f => f.id === friendshipId ? updatedFriendship : f)
            );
        } catch (error) {
            console.error('Failed to accept friend request:', error);
            throw error;
        }
    };

    const declineFriendRequest = async (friendshipId: number): Promise<void> => {
        try {
            await socialApi.declineFriendRequest(friendshipId);
            setFriendships(prev => prev.filter(f => f.id !== friendshipId));
        } catch (error) {
            console.error('Failed to decline friend request:', error);
            throw error;
        }
    };

    const removeFriend = async (friendshipId: number): Promise<void> => {
        try {
            await socialApi.removeFriend(friendshipId);
            setFriendships(prev => prev.filter(f => f.id !== friendshipId));
        } catch (error) {
            console.error('Failed to remove friend:', error);
            throw error;
        }
    };

    const blockUser = async (userId: number): Promise<void> => {
        try {
            console.log('Block user not implemented yet');
            throw new Error('Block user not implemented yet');
        } catch (error) {
            console.error('Failed to block user:', error);
            throw error;
        }
    };

    const extractUserFromFriendship = (friendship: Friendship): DisplayUser => {
        return {
            id: friendship.requester.id || friendship.addressee.id,
            name: friendship.requester.name || friendship.addressee.name,
            email: friendship.requester.email || friendship.addressee.email
        };
    };

    const friendshipInfo = useMemo(() => {
        const activeFriendships = friendships.filter(f => f.status === 'ACCEPTED'); // Update to match your status values
        const pendingRequests = friendships.filter(f => f.status === 'PENDING'); // Update to match your status values
        const sentRequests = friendships.filter(f => f.status === 'PENDING'); // Update logic for sent vs received

        return {
            // Raw friendship arrays
            activeFriendships,
            pendingRequests,
            sentRequests,
            allFriendships: friendships,

            // User arrays for components
            friends: activeFriendships.map(extractUserFromFriendship),
            pendingRequestUsers: pendingRequests.map(extractUserFromFriendship),
            sentRequestUsers: sentRequests.map(extractUserFromFriendship),

            // Counts
            totalFriends: activeFriendships.length,
            pendingRequestsCount: pendingRequests.length,
            sentRequestsCount: sentRequests.length,
        };
    }, [friendships]);

    return {
        friendships,
        friendshipInfo,
        loading,
        error,
        refetch: fetchFriendships,
        refreshFriendships: fetchFriendships,
        sendFriendRequest: sendFriendRequestByUserId, // Use the userId version for components
        sendFriendRequestByEmail, // Keep the email version available
        acceptFriendRequest,
        declineFriendRequest,
        removeFriend,
        blockUser
    };
};