import { useState, useEffect, useCallback } from 'react';
import { socialApi } from '../../services/social/socialApi';
import {
    GroupInfo,
    StudyGroup,
    CreateGroupRequest,
    UpdateGroupRequest,
    GroupMember
} from '../../types/social';
import { toast } from 'react-toastify';

interface UseGroupsReturn {
    groupInfo: GroupInfo | null;
    selectedGroup: StudyGroup | null;
    loading: boolean;
    error: string | null;
    createGroup: (request: CreateGroupRequest) => Promise<StudyGroup | null>;
    joinGroup: (groupId: number) => Promise<void>;
    joinByInviteCode: (inviteCode: string) => Promise<void>;
    leaveGroup: (groupId: number) => Promise<void>;
    updateGroup: (groupId: number, request: UpdateGroupRequest) => Promise<void>;
    deleteGroup: (groupId: number) => Promise<void>;
    getGroupDetails: (groupId: number) => Promise<void>;
    regenerateInviteCode: (groupId: number) => Promise<string | null>;
    removeMember: (groupId: number, memberId: number) => Promise<void>;
    changeRole: (groupId: number, memberId: number, role: string) => Promise<void>;
    searchGroups: (searchTerm: string) => Promise<any>;
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
            const data = await socialApi.getMyGroups();
            setGroupInfo(data);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Błąd podczas ładowania grup');
            toast.error('Nie udało się załadować grup');
        } finally {
            setLoading(false);
        }
    }, []);

    const createGroup = useCallback(async (request: CreateGroupRequest): Promise<StudyGroup | null> => {
        try {
            const response = await socialApi.createGroup(request);
            toast.success(response.message || 'Grupa została utworzona');
            await loadGroups();
            return response.group || null;
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się utworzyć grupy');
            return null;
        }
    }, [loadGroups]);

    const joinGroup = useCallback(async (groupId: number) => {
        try {
            const response = await socialApi.joinGroup(groupId);
            toast.success(response.message || 'Dołączyłeś do grupy');
            await loadGroups();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się dołączyć do grupy');
        }
    }, [loadGroups]);

    const joinByInviteCode = useCallback(async (inviteCode: string) => {
        try {
            const response = await socialApi.joinByInviteCode(inviteCode);
            toast.success(response.message || 'Dołączyłeś do grupy');
            await loadGroups();
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nieprawidłowy kod zaproszenia');
        }
    }, [loadGroups]);

    const leaveGroup = useCallback(async (groupId: number) => {
        try {
            const response = await socialApi.leaveGroup(groupId);
            toast.success(response.message || 'Opuściłeś grupę');
            await loadGroups();
            if (selectedGroup?.id === groupId) {
                setSelectedGroup(null);
            }
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się opuścić grupy');
        }
    }, [loadGroups, selectedGroup]);

    const updateGroup = useCallback(async (groupId: number, request: UpdateGroupRequest) => {
        try {
            const response = await socialApi.updateGroup(groupId, request);
            toast.success(response.message || 'Grupa została zaktualizowana');
            await loadGroups();
            if (selectedGroup?.id === groupId) {
                await getGroupDetails(groupId);
            }
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się zaktualizować grupy');
        }
    }, [loadGroups, selectedGroup]);

    const deleteGroup = useCallback(async (groupId: number) => {
        try {
            const response = await socialApi.deleteGroup(groupId);
            toast.success(response.message || 'Grupa została usunięta');
            await loadGroups();
            if (selectedGroup?.id === groupId) {
                setSelectedGroup(null);
            }
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się usunąć grupy');
        }
    }, [loadGroups, selectedGroup]);

    const getGroupDetails = useCallback(async (groupId: number) => {
        try {
            const data = await socialApi.getGroupDetails(groupId);
            setSelectedGroup(data.group);
        } catch (err: any) {
            toast.error('Nie udało się załadować szczegółów grupy');
        }
    }, []);

    const regenerateInviteCode = useCallback(async (groupId: number): Promise<string | null> => {
        try {
            const response = await socialApi.regenerateInviteCode(groupId);
            toast.success(response.message || 'Kod został wygenerowany ponownie');
            await loadGroups();
            if (selectedGroup?.id === groupId) {
                await getGroupDetails(groupId);
            }
            return response.newInviteCode || null;
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się wygenerować kodu');
            return null;
        }
    }, [loadGroups, selectedGroup, getGroupDetails]);

    const removeMember = useCallback(async (groupId: number, memberId: number) => {
        try {
            const response = await socialApi.removeMember(groupId, memberId);
            toast.success(response.message || 'Członek został usunięty');
            await getGroupDetails(groupId);
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się usunąć członka');
        }
    }, [getGroupDetails]);

    const changeRole = useCallback(async (groupId: number, memberId: number, role: string) => {
        try {
            const response = await socialApi.changeRole(groupId, memberId, role);
            toast.success(response.message || 'Rola została zmieniona');
            await getGroupDetails(groupId);
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Nie udało się zmienić roli');
        }
    }, [getGroupDetails]);

    const searchGroups = useCallback(async (searchTerm: string) => {
        try {
            return await socialApi.searchGroups(searchTerm);
        } catch (err: any) {
            toast.error('Nie udało się wyszukać grup');
            return { groups: { content: [] } };
        }
    }, []);

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
        joinGroup,
        joinByInviteCode,
        leaveGroup,
        updateGroup,
        deleteGroup,
        getGroupDetails,
        regenerateInviteCode,
        removeMember,
        changeRole,
        searchGroups,
        refreshGroups
    };
};