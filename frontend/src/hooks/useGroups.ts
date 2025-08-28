import { useState, useEffect, useCallback } from 'react';
import socialApi from '../services/socialApi';
import {
    GroupInfo,
    StudyGroup,
    CreateGroupRequest,
    UpdateGroupRequest
} from '../types/social';
import { toast } from 'react-toastify';

interface UseGroupsReturn {
    groupInfo: GroupInfo | null;
    selectedGroup: StudyGroup | null;
    loading: boolean;
    error: string | null;
    createGroup: (request: CreateGroupRequest) => Promise<StudyGroup | null>;
    joinByInviteCode: (inviteCode: string) => Promise<void>;
    leaveGroup: (groupId: number) => Promise<void>;
    updateGroup: (groupId: number, request: UpdateGroupRequest) => Promise<void>;
    getGroupDetails: (groupId: number) => Promise<void>;
    regenerateInviteCode: (groupId: number) => Promise<string | null>;
    refreshGroups: () => Promise<void>;
}

export const useGroups = (): UseGroupsReturn => {
    const [groupInfo, setGroupInfo] = useState<GroupInfo | null>(null);
    const [selectedGroup, setSelectedGroup] = useState<StudyGroup | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const loadGroups = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);

            // Używamy dashboard API żeby dostać groupInfo
            const dashboard = await socialApi.getSocialDashboard();
            setGroupInfo(dashboard.groupInfo);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Błąd podczas ładowania grup');
            console.error('Failed to load groups:', err);
        } finally {
            setLoading(false);
        }
    }, []);

    const createGroup = useCallback(async (request: CreateGroupRequest): Promise<StudyGroup | null> => {
        try {
            const newGroup = await socialApi.createGroup(request);
            toast.success('Grupa została utworzona');
            await loadGroups();
            return newGroup;
        } catch (err: any) {
            toast.error('Nie udało się utworzyć grupy');
            console.error('Failed to create group:', err);
            return null;
        }
    }, [loadGroups]);

    const joinByInviteCode = useCallback(async (inviteCode: string) => {
        try {
            await socialApi.joinGroup({ inviteCode });
            toast.success('Dołączyłeś do grupy');
            await loadGroups();
        } catch (err: any) {
            toast.error('Nieprawidłowy kod zaproszenia');
            console.error('Failed to join group:', err);
        }
    }, [loadGroups]);

    const leaveGroup = useCallback(async (groupId: number) => {
        try {
            await socialApi.leaveGroup(groupId);
            toast.success('Opuściłeś grupę');
            await loadGroups();
            if (selectedGroup?.id === groupId) {
                setSelectedGroup(null);
            }
        } catch (err: any) {
            toast.error('Nie udało się opuścić grupy');
            console.error('Failed to leave group:', err);
        }
    }, [loadGroups, selectedGroup]);

    const updateGroup = useCallback(async (groupId: number, request: UpdateGroupRequest) => {
        try {
            const updatedGroup = await socialApi.updateGroup(groupId, request);
            toast.success('Grupa została zaktualizowana');
            await loadGroups();
            if (selectedGroup?.id === groupId) {
                setSelectedGroup(updatedGroup);
            }
        } catch (err: any) {
            toast.error('Nie udało się zaktualizować grupy');
            console.error('Failed to update group:', err);
        }
    }, [loadGroups, selectedGroup]);

    const getGroupDetails = useCallback(async (groupId: number) => {
        try {
            // Na razie używamy aktualnych grup z groupInfo
            if (groupInfo?.activeGroups) {
                const group = groupInfo.activeGroups.find(g => g.id === groupId);
                if (group) {
                    setSelectedGroup(group);
                }
            }
        } catch (err: any) {
            toast.error('Nie udało się załadować szczegółów grupy');
            console.error('Failed to load group details:', err);
        }
    }, [groupInfo]);

    const regenerateInviteCode = useCallback(async (groupId: number): Promise<string | null> => {
        try {
            const updatedGroup = await socialApi.regenerateInviteCode(groupId);
            toast.success('Kod został wygenerowany ponownie');
            await loadGroups();
            if (selectedGroup?.id === groupId) {
                setSelectedGroup(updatedGroup);
            }
            return updatedGroup.inviteCode;
        } catch (err: any) {
            toast.error('Nie udało się wygenerować kodu');
            console.error('Failed to regenerate invite code:', err);
            return null;
        }
    }, [loadGroups, selectedGroup]);

    const refreshGroups = useCallback(async () => {
        await loadGroups();
    }, [loadGroups]);

    useEffect(() => {
        loadGroups();
    }, [loadGroups]);

    return {
        groupInfo,
        selectedGroup,
        loading,
        error,
        createGroup,
        joinByInviteCode,
        leaveGroup,
        updateGroup,
        getGroupDetails,
        regenerateInviteCode,
        refreshGroups
    };
};