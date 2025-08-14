import axios from 'axios';
import {
    FriendshipInfo,
    GroupInfo,
    MessagingInfo,
    SocialDashboard,
    StudyGroup,
    GroupMember,
    PrivateMessage,
    GroupMessage,
    Friendship,
    CreateGroupRequest,
    UpdateGroupRequest,
    SendMessageRequest,
    ShareWordSetRequest,
    FriendRequestDto,
    ApiResponse
} from '../../types/social';

const API_BASE = '/api';

class SocialApiService {
    // ========== FRIENDSHIP ENDPOINTS ==========

    async getMyFriendships(): Promise<FriendshipInfo> {
        const response = await axios.get(`${API_BASE}/friendships/my`);
        return response.data;
    }

    async sendFriendRequest(request: FriendRequestDto): Promise<ApiResponse<Friendship>> {
        const response = await axios.post(`${API_BASE}/friendships/send-request`, request);
        return response.data;
    }

    async acceptFriendRequest(friendshipId: number): Promise<ApiResponse<Friendship>> {
        const response = await axios.post(`${API_BASE}/friendships/${friendshipId}/accept`);
        return response.data;
    }

    async declineFriendRequest(friendshipId: number): Promise<ApiResponse<string>> {
        const response = await axios.post(`${API_BASE}/friendships/${friendshipId}/decline`);
        return response.data;
    }

    async removeFriend(friendId: number): Promise<ApiResponse<string>> {
        const response = await axios.delete(`${API_BASE}/friendships/${friendId}`);
        return response.data;
    }

    async blockUser(userId: number): Promise<ApiResponse<string>> {
        const response = await axios.post(`${API_BASE}/friendships/${userId}/block`);
        return response.data;
    }

    async checkFriendshipStatus(userId: number): Promise<any> {
        const response = await axios.get(`${API_BASE}/friendships/status/${userId}`);
        return response.data;
    }

    // ========== GROUP ENDPOINTS ==========

    async getMyGroups(): Promise<GroupInfo> {
        const response = await axios.get(`${API_BASE}/groups/my`);
        return response.data;
    }

    async createGroup(request: CreateGroupRequest): Promise<ApiResponse<StudyGroup>> {
        const response = await axios.post(`${API_BASE}/groups`, request);
        return response.data;
    }

    async joinGroup(groupId: number): Promise<ApiResponse<GroupMember>> {
        const response = await axios.post(`${API_BASE}/groups/${groupId}/join`);
        return response.data;
    }

    async joinByInviteCode(inviteCode: string): Promise<ApiResponse<GroupMember>> {
        const response = await axios.post(`${API_BASE}/groups/join-by-code`, { inviteCode });
        return response.data;
    }

    async leaveGroup(groupId: number): Promise<ApiResponse<string>> {
        const response = await axios.post(`${API_BASE}/groups/${groupId}/leave`);
        return response.data;
    }

    async getGroupDetails(groupId: number): Promise<any> {
        const response = await axios.get(`${API_BASE}/groups/${groupId}`);
        return response.data;
    }

    async updateGroup(groupId: number, request: UpdateGroupRequest): Promise<ApiResponse<StudyGroup>> {
        const response = await axios.put(`${API_BASE}/groups/${groupId}`, request);
        return response.data;
    }

    async deleteGroup(groupId: number): Promise<ApiResponse<string>> {
        const response = await axios.delete(`${API_BASE}/groups/${groupId}`);
        return response.data;
    }

    async regenerateInviteCode(groupId: number): Promise<ApiResponse<string>> {
        const response = await axios.post(`${API_BASE}/groups/${groupId}/regenerate-code`);
        return response.data;
    }

    async removeMember(groupId: number, memberId: number): Promise<ApiResponse<string>> {
        const response = await axios.delete(`${API_BASE}/groups/${groupId}/members/${memberId}`);
        return response.data;
    }

    async changeRole(groupId: number, memberId: number, role: string): Promise<ApiResponse<GroupMember>> {
        const response = await axios.put(`${API_BASE}/groups/${groupId}/members/${memberId}/role`, { role });
        return response.data;
    }

    async searchGroups(searchTerm: string, page = 0, size = 20): Promise<any> {
        const response = await axios.get(`${API_BASE}/groups/search`, {
            params: { searchTerm, page, size }
        });
        return response.data;
    }

    // ========== MESSAGE ENDPOINTS ==========

    async getMyMessages(): Promise<MessagingInfo> {
        const response = await axios.get(`${API_BASE}/messages/my`);
        return response.data;
    }

    async sendPrivateMessage(request: SendMessageRequest): Promise<ApiResponse<PrivateMessage>> {
        const response = await axios.post(`${API_BASE}/messages/private`, request);
        return response.data;
    }

    async sendGroupMessage(request: SendMessageRequest): Promise<ApiResponse<GroupMessage>> {
        const response = await axios.post(`${API_BASE}/messages/group`, request);
        return response.data;
    }

    async shareWordSetPrivately(request: ShareWordSetRequest): Promise<ApiResponse<PrivateMessage>> {
        const response = await axios.post(`${API_BASE}/messages/private/share-wordset`, request);
        return response.data;
    }

    async shareWordSetInGroup(request: ShareWordSetRequest): Promise<ApiResponse<GroupMessage>> {
        const response = await axios.post(`${API_BASE}/messages/group/share-wordset`, request);
        return response.data;
    }

    async getConversation(userId: number): Promise<{ messages: PrivateMessage[] }> {
        const response = await axios.get(`${API_BASE}/messages/private/conversation/${userId}`);
        return response.data;
    }

    async markMessagesAsRead(userId: number): Promise<ApiResponse<number>> {
        const response = await axios.post(`${API_BASE}/messages/private/conversation/${userId}/mark-read`);
        return response.data;
    }

    async deletePrivateMessage(messageId: number): Promise<ApiResponse<string>> {
        const response = await axios.delete(`${API_BASE}/messages/private/${messageId}`);
        return response.data;
    }

    async deleteGroupMessage(messageId: number): Promise<ApiResponse<string>> {
        const response = await axios.delete(`${API_BASE}/messages/group/${messageId}`);
        return response.data;
    }

    // ========== DASHBOARD ENDPOINTS ==========

    async getSocialDashboard(): Promise<SocialDashboard> {
        const response = await axios.get(`${API_BASE}/social/dashboard`);
        return response.data;
    }

    async getSocialStats(): Promise<any> {
        const response = await axios.get(`${API_BASE}/social/stats`);
        return response.data;
    }

    async searchSocial(searchTerm?: string, page = 0, size = 20): Promise<any> {
        const response = await axios.get(`${API_BASE}/social/search`, {
            params: { searchTerm, page, size }
        });
        return response.data;
    }
}

export const socialApi = new SocialApiService();