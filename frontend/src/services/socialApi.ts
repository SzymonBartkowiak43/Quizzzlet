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
    UpdateGroupRequest, FriendshipInfo
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
export const getMyFriendships = async (): Promise<FriendshipInfo> => {
    const response = await api.get('/api/friendships/my');
    return response.data;
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


// Stats API (dodatkowy endpoint)
export const getSocialStats = async (): Promise<any> => {
    const response = await api.get('/api/social/stats');
    return response.data;
};

export const getConversation = async (userId: number) => {
    const res = await api.get(`/api/messages/private/conversation/${userId}`);
    return res.data;
};

export const sendPrivateMessage = async (recipientId: number, content: string) => {
    const res = await api.post(`/api/messages/private`, { recipientId, content });
    return res.data;
};

export const getAllUsers = async (): Promise<User[]> => {
    const response = await api.get('/api/users');
    return response.data;
};

export const sendFriendRequest = async (addresseeId: number) => {
    const response = await api.post('/api/friendships/send-request', { addresseeId });
    return response.data;
};

// Akceptowanie zaproszenia
export const acceptFriendRequest = async (friendshipId: number) => {
    const response = await api.post(`/api/friendships/${friendshipId}/accept`);
    return response.data;
};

// Odrzucanie zaproszenia
export const declineFriendRequest = async (friendshipId: number) => {
    const response = await api.post(`/api/friendships/${friendshipId}/decline`);
    return response.data;
};

// Sprawdzanie statusu znajomości (np. czy już wysłałeś zaproszenie, czy już jesteście znajomymi)
export const checkFriendshipStatus = async (userId: number) => {
    const response = await api.get(`/api/friendships/status/${userId}`);
    return response.data;
};

export const getMyGroups = async () => {
    const response = await api.get('/api/messages/groups/my');
    return response.data;
};
export const createGroup = async (name: string, memberIds: number[]) => {
    const response = await api.post('/api/messages/groups', { name, memberIds });
    return response.data;
};
export const getGroupMessages = async (groupId: number) => {
    const response = await api.get(`/api/messages/groups/${groupId}/messages`);
    return response.data;
};
export const sendGroupMessage = async (groupId: number, content: string) => {
    const response = await api.post('/api/messages/group', { groupId, content });
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
    sendPrivateMessage,
    getGroupMessages,
    sendGroupMessage,
    getConversation,
    getAllUsers,
    checkFriendshipStatus
};