import axios from 'axios';
import {
    SocialDashboard,
    Friendship,
    User,
    StudyGroup,
    PrivateMessage,
    GroupMessage,
    CreateGroupRequest,
    JoinGroupRequest,
    UpdateGroupRequest
} from '../types/social';

const api = axios.create({
    baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add auth token to requests
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Response interceptor for error handling
api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error);
        if (error.response?.status === 401) {
            // Token expired or invalid
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

// Social Dashboard API
export const getSocialDashboard = async (): Promise<SocialDashboard> => {
    const response = await api.get('/api/social/dashboard');
    return response.data;
};

// Friendship API
export const getMyFriendships = async (): Promise<Friendship[]> => {
    const response = await api.get('/api/friendships/my');
    return response.data;
};

export const sendFriendRequest = async (receiverEmail: string): Promise<Friendship> => {
    const response = await api.post('/api/friendships/request', { receiverEmail });
    return response.data;
};

export const acceptFriendRequest = async (friendshipId: number): Promise<Friendship> => {
    const response = await api.put(`/api/friendships/${friendshipId}/accept`);
    return response.data;
};

export const declineFriendRequest = async (friendshipId: number): Promise<void> => {
    await api.put(`/api/friendships/${friendshipId}/decline`);
};

export const removeFriend = async (friendshipId: number): Promise<void> => {
    await api.delete(`/api/friendships/${friendshipId}`);
};

// Social Search API
export const searchSocial = async (searchTerm: string, page: number = 0, size: number = 20): Promise<{
    content: User[];
    totalElements: number;
    totalPages: number;
    last: boolean;
}> => {
    const response = await api.get('/api/social/search', {
        params: {
            searchTerm,
            page,
            size,
            sort: 'createdAt,desc'
        }
    });
    return response.data;
};

// Groups API - mock implementations (dodaj gdy backend będzie miał groups)
export const getMyGroups = async (): Promise<StudyGroup[]> => {
    // TODO: Zaimplementuj gdy backend będzie miał /api/groups/my
    return [];
};

export const createGroup = async (request: CreateGroupRequest): Promise<StudyGroup> => {
    // TODO: Zaimplementuj gdy backend będzie miał /api/groups
    throw new Error('Groups not implemented yet');
};

export const joinGroup = async (request: JoinGroupRequest): Promise<StudyGroup> => {
    // TODO: Zaimplementuj gdy backend będzie miał /api/groups/join
    throw new Error('Groups not implemented yet');
};

export const leaveGroup = async (groupId: number): Promise<void> => {
    // TODO: Zaimplementuj gdy backend będzie miał /api/groups/{id}/leave
    throw new Error('Groups not implemented yet');
};

export const updateGroup = async (groupId: number, request: UpdateGroupRequest): Promise<StudyGroup> => {
    // TODO: Zaimplementuj gdy backend będzie miał /api/groups/{id}
    throw new Error('Groups not implemented yet');
};

export const regenerateInviteCode = async (groupId: number): Promise<StudyGroup> => {
    // TODO: Zaimplementuj gdy backend będzie miał /api/groups/{id}/regenerate-code
    throw new Error('Groups not implemented yet');
};

// Messages API - mock implementations (dodaj gdy backend będzie miał messages)
export const getPrivateMessages = async (conversationId: number): Promise<PrivateMessage[]> => {
    // TODO: Zaimplementuj gdy backend będzie miał /api/messages/private/{id}
    return [];
};

export const sendPrivateMessage = async (receiverId: number, content: string): Promise<PrivateMessage> => {
    // TODO: Zaimplementuj gdy backend będzie miał /api/messages/private
    throw new Error('Messages not implemented yet');
};

export const getGroupMessages = async (groupId: number): Promise<GroupMessage[]> => {
    // TODO: Zaimplementuj gdy backend będzie miał /api/messages/group/{id}
    return [];
};

export const sendGroupMessage = async (groupId: number, content: string): Promise<GroupMessage> => {
    // TODO: Zaimplementuj gdy backend będzie miał /api/messages/group
    throw new Error('Messages not implemented yet');
};

// Stats API (dodatkowy endpoint)
export const getSocialStats = async (): Promise<any> => {
    const response = await api.get('/api/social/stats');
    return response.data;
};

// Export all functions
export default {
    getSocialDashboard,
    getMyFriendships,
    sendFriendRequest,
    acceptFriendRequest,
    declineFriendRequest,
    removeFriend,
    searchSocial,
    getSocialStats,
    getMyGroups,
    createGroup,
    joinGroup,
    leaveGroup,
    updateGroup,
    regenerateInviteCode,
    getPrivateMessages,
    sendPrivateMessage,
    getGroupMessages,
    sendGroupMessage
};