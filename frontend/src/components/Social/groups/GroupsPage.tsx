import React, { useState } from 'react';
import { useGroups } from '../../../hooks/useGroups';
import { useFriendships } from '../../../hooks/userFriendships';
import {
    Users,
    Plus,
    Search,
    Filter,
    RefreshCw,
    Hash,
    Lock,
    Globe,
    Crown,
    Shield,
    User
} from 'lucide-react';
import LoadingSpinner from '../../Shared/LoadingSpinner';
import GroupDetails from './GroupDetails';
import CreateGroupModal from './CreateGroupModal';
import JoinGroupModal from './JoinGroupModal';
import GroupSearch from './GroupSearch';

type TabType = 'my-groups' | 'created' | 'joined';
type ViewMode = 'list' | 'details';

const GroupsPage: React.FC = () => {
    const {
        groupInfo,
        selectedGroup,
        loading,
        error,
        createGroup,
        // joinGroup,
        joinByInviteCode,
        leaveGroup,
        updateGroup,
        // deleteGroup,
        getGroupDetails,
        regenerateInviteCode,
        // removeMember,
        // changeRole,
        // searchGroups,
        refreshGroups
    } = useGroups();

    const { friendshipInfo } = useFriendships();

    const [activeTab, setActiveTab] = useState<TabType>('my-groups');
    const [viewMode, setViewMode] = useState<ViewMode>('list');
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showJoinModal, setShowJoinModal] = useState(false);
    const [showSearch, setShowSearch] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [refreshing, setRefreshing] = useState(false);

    const handleRefresh = async () => {
        setRefreshing(true);
        await refreshGroups();
        setRefreshing(false);
    };

    const handleGroupSelect = async (groupId: number) => {
        await getGroupDetails(groupId);
        setViewMode('details');
    };

    const backToList = () => {
        setViewMode('list');
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-[400px]">
                <LoadingSpinner size="lg" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="text-center py-8">
                <div className="bg-red-50 border border-red-200 rounded-lg p-6 max-w-md mx-auto">
                    <h3 className="text-red-800 font-medium mb-2">Błąd ładowania</h3>
                    <p className="text-red-600 mb-4">{error}</p>
                    <button
                        onClick={handleRefresh}
                        className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors"
                    >
                        Spróbuj ponownie
                    </button>
                </div>
            </div>
        );
    }

    if (!groupInfo) return null;

    const tabs = [
        {
            id: 'my-groups' as TabType,
            label: 'Wszystkie grupy',
            count: groupInfo.memberGroupsCount,
            icon: Users,
            groups: groupInfo.memberGroups
        },
        {
            id: 'created' as TabType,
            label: 'Utworzone przeze mnie',
            count: groupInfo.createdGroupsCount,
            icon: Crown,
            groups: groupInfo.createdGroups
        },
        {
            id: 'joined' as TabType,
            label: 'Dołączyłem',
            count: groupInfo.memberGroupsCount - groupInfo.createdGroupsCount,
            icon: User,
            groups: groupInfo.memberGroups.filter(group =>
                !groupInfo.createdGroups.some(created => created.id === group.id)
            )
        }
    ];

    const currentGroups = tabs.find(tab => tab.id === activeTab)?.groups || [];
    const filteredGroups = currentGroups.filter(group =>
        group.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        group.description.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="max-w-7xl mx-auto p-6">
            {viewMode === 'list' && (
                <>
                    {/* Header */}
                    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900">Grupy nauki</h1>
                            <p className="text-gray-600 mt-1">
                                Zarządzaj swoimi grupami i znajdź nowe społeczności do nauki
                            </p>
                        </div>

                        <div className="flex gap-3">
                            <button
                                onClick={() => setShowSearch(!showSearch)}
                                className="flex items-center gap-2 bg-white border border-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-50 transition-colors"
                            >
                                <Search className="h-4 w-4" />
                                Szukaj grup
                            </button>

                            <button
                                onClick={() => setShowJoinModal(true)}
                                className="flex items-center gap-2 bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors"
                            >
                                <Hash className="h-4 w-4" />
                                Dołącz kodem
                            </button>

                            <button
                                onClick={() => setShowCreateModal(true)}
                                className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                            >
                                <Plus className="h-4 w-4" />
                                Utwórz grupę
                            </button>

                            <button
                                onClick={handleRefresh}
                                disabled={refreshing}
                                className="flex items-center gap-2 bg-white border border-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50"
                            >
                                <RefreshCw className={`h-4 w-4 ${refreshing ? 'animate-spin' : ''}`} />
                                Odśwież
                            </button>
                        </div>
                    </div>

                    {/* Search Bar */}
                    {showSearch && (
                        <div className="mb-6 bg-white rounded-lg shadow p-4">
                            <div className="relative">
                                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                                <input
                                    type="text"
                                    placeholder="Wyszukaj w swoich grupach..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                />
                            </div>
                        </div>
                    )}

                    {/* Stats Cards */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
                        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm font-medium text-gray-600 mb-1">Wszystkie grupy</p>
                                    <p className="text-3xl font-bold text-blue-900">{groupInfo.memberGroupsCount}</p>
                                    <p className="text-xs text-blue-600 mt-1">Jesteś członkiem</p>
                                </div>
                                <Users className="h-8 w-8 text-blue-600" />
                            </div>
                        </div>

                        <div className="bg-green-50 border border-green-200 rounded-lg p-6">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm font-medium text-gray-600 mb-1">Utworzone</p>
                                    <p className="text-3xl font-bold text-green-900">{groupInfo.createdGroupsCount}</p>
                                    <p className="text-xs text-green-600 mt-1">Jesteś administratorem</p>
                                </div>
                                <Crown className="h-8 w-8 text-green-600" />
                            </div>
                        </div>

                        <div className="bg-purple-50 border border-purple-200 rounded-lg p-6">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm font-medium text-gray-600 mb-1">Aktywne</p>
                                    <p className="text-3xl font-bold text-purple-900">{groupInfo.activeGroups.length}</p>
                                    <p className="text-xs text-purple-600 mt-1">Z ostatnią aktywnością</p>
                                </div>
                                <Shield className="h-8 w-8 text-purple-600" />
                            </div>
                        </div>
                    </div>

                    {/* Tabs */}
                    <div className="bg-white rounded-lg shadow mb-6">
                        <div className="border-b border-gray-200">
                            <nav className="flex space-x-8 px-6">
                                {tabs.map(tab => {
                                    const Icon = tab.icon;
                                    return (
                                        <button
                                            key={tab.id}
                                            onClick={() => setActiveTab(tab.id)}
                                            className={`
                        flex items-center gap-2 py-4 px-1 border-b-2 font-medium text-sm transition-colors
                        ${activeTab === tab.id
                                                ? 'border-blue-500 text-blue-600'
                                                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                            }
                      `}
                                        >
                                            <Icon className="h-4 w-4" />
                                            {tab.label}
                                            {tab.count > 0 && (
                                                <span className={`
                          ml-2 px-2 py-1 text-xs rounded-full
                          ${activeTab === tab.id
                                                    ? 'bg-blue-100 text-blue-600'
                                                    : 'bg-gray-100 text-gray-600'
                                                }
                        `}>
                          {tab.count}
                        </span>
                                            )}
                                        </button>
                                    );
                                })}
                            </nav>
                        </div>

                        {/* Tab Content */}
                    </div>
                </>
            )}

            {/*{viewMode === 'details' && selectedGroup && (*/}
            {/*    <GroupDetails*/}
            {/*        group={selectedGroup}*/}
            {/*        friends={friendshipInfo?.friends || []}*/}
            {/*        onBack={backToList}*/}
            {/*        onUpdateGroup={updateGroup}*/}
            {/*        onDeleteGroup={deleteGroup}*/}
            {/*        onLeaveGroup={leaveGroup}*/}
            {/*        onRegenerateCode={regenerateInviteCode}*/}
            {/*        onRemoveMember={removeMember}*/}
            {/*        onChangeRole={changeRole}*/}
            {/*        currentUser="SzymonBartkowiak43"*/}
            {/*    />*/}
            {/*)}*/}

            {/*/!* Modals *!/*/}
            {/*{showCreateModal && (*/}
            {/*    <CreateGroupModal*/}
            {/*        onClose={() => setShowCreateModal(false)}*/}
            {/*        onCreate={createGroup}*/}
            {/*    />*/}
            {/*)}*/}

            {/*{showJoinModal && (*/}
            {/*    <JoinGroupModal*/}
            {/*        onClose={() => setShowJoinModal(false)}*/}
            {/*        onJoinByCode={joinByInviteCode}*/}
            {/*    />*/}
            {/*)}*/}

            {/*{showSearch && (*/}
            {/*    <GroupSearch*/}
            {/*        onClose={() => setShowSearch(false)}*/}
            {/*        onJoinGroup={joinGroup}*/}
            {/*        onSearch={searchGroups}*/}
            {/*    />*/}
            {/*)}*/}
        </div>
    );
};

export default GroupsPage;