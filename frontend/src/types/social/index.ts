// ========== ENUMS ==========
export enum FriendshipStatus {
    PENDING = 'PENDING',
    ACCEPTED = 'ACCEPTED',
    BLOCKED = 'BLOCKED'
}

export enum GroupRole {
    ADMIN = 'ADMIN',
    MODERATOR = 'MODERATOR',
    MEMBER = 'MEMBER'
}

// ========== INTERFACES ==========
export interface User {
    id: number;
    email: string;
    name: string;
    createdAt: string;
    updatedAt: string;
}

export interface Friendship {
    id: number;
    requester: User;
    addressee: User;
    status: FriendshipStatus;
    createdAt: string;
    updatedAt: string;
}

export interface StudyGroup {
    id: number;
    name: string;
    description: string;
    creator: User;
    isPrivate: boolean;
    maxMembers: number;
    memberCount: number;
    inviteCode: string;
    createdAt: string;
    updatedAt: string;
    members?: GroupMember[];
    messages?: GroupMessage[];
}

export interface GroupMember {
    id: number;
    group: StudyGroup;
    user: User;
    role: GroupRole;
    joinedAt: string;
}

export interface PrivateMessage {
    id: number;
    sender: User;
    recipient: User;
    content: string;
    isRead: boolean;
    sharedWordSet?: any;
    createdAt: string;
}

export interface GroupMessage {
    id: number;
    group: StudyGroup;
    sender: User;
    content: string;
    sharedWordSet?: any;
    createdAt: string;
}

// ========== REQUEST TYPES ==========
export interface CreateGroupRequest {
    name: string;
    description: string;
    isPrivate: boolean;
    maxMembers?: number;
}

export interface UpdateGroupRequest {
    name?: string;
    description?: string;
    isPrivate?: boolean;
    maxMembers?: number;
}

export interface SendMessageRequest {
    recipientId?: number;
    groupId?: number;
    content: string;
}

export interface ShareWordSetRequest {
    recipientId?: number;
    groupId?: number;
    wordSetId: number;
    message: string;
}

export interface FriendRequestDto {
    addresseeId: number;
}

export type DisplayUser = {
    id: number;
    email: string;
    name: string;
};

export type FriendshipInfo = {
    friendsCount: number;
    friends: DisplayUser[];
    pendingRequestsCount: number;
    pendingRequests: any[];
    sentRequestsCount: number;
    sentRequests: any[];
    suggestedFriends: DisplayUser[];
};

// ========== RESPONSE TYPES ==========
export interface GroupInfo {
    memberGroups: StudyGroup[];
    memberGroupsCount: number;
    createdGroups: StudyGroup[];
    createdGroupsCount: number;
    activeGroups: StudyGroup[];
}

export interface MessagingInfo {
    conversations: PrivateMessage[];
    conversationsCount: number;
    unreadMessages: PrivateMessage[];
    unreadCount: number;
    activeGroups: StudyGroup[];
}

export interface SocialDashboard {
    friendshipInfo: FriendshipInfo;
    groupInfo: GroupInfo;
    messagingInfo: MessagingInfo;
    stats: {
        totalFriends: number;
        totalGroups: number;
        unreadMessages: number;
        pendingFriendRequests: number;
    };
    quickActions: {
        canCreateGroup: boolean;
        hasUnreadMessages: boolean;
        hasPendingRequests: boolean;
    };
}

export interface ApiResponse<T> {
    message?: string;
    data?: T;
    [key: string]: any;
}

export interface JoinGroupRequest {
    inviteCode: string;
}