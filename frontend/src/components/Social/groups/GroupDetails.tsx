import React, { useState } from 'react';
import { StudyGroup, User, UpdateGroupRequest } from '../../../types/social';
import {
    ArrowLeft,
    Users,
    Settings,
    Copy,
    RefreshCw,
    Edit3,
    Trash2,
    LogOut,
    UserPlus,
    Crown,
    Shield,
    User as UserIcon,
    MoreVertical,
    Globe,
    Lock,
    Calendar,
    Hash,
    Mail,
    Ban
} from 'lucide-react';
import EditGroupModal from './EditGroupModal';
import InviteMembersModal from './InviteMembersModal';

interface GroupDetailsProps {
    group: StudyGroup;
    friends: User[];
    onBack: () => void;
    onUpdateGroup: (groupId: number, request: UpdateGroupRequest) => Promise<void>;
    onDeleteGroup: (groupId: number) => Promise<void>;
    onLeaveGroup: (groupId: number) => Promise<void>;
    onRegenerateCode: (groupId: number) => Promise<string | null>;
    onRemoveMember: (groupId: number, memberId: number) => Promise<void>;
    onChangeRole: (groupId: number, memberId: number, role: string) => Promise<void>;
    currentUser: string;
}

const GroupDetails: React.FC<GroupDetailsProps> = ({
                                                       group,
                                                       friends,
                                                       onBack,
                                                       onUpdateGroup,
                                                       onDeleteGroup,
                                                       onLeaveGroup,
                                                       onRegenerateCode,
                                                       onRemoveMember,
                                                       onChangeRole,
                                                       currentUser
                                                   }) => {
    const [activeTab, setActiveTab] = useState<'overview' | 'members' | 'settings'>('overview');
    const [showEditModal, setShowEditModal] = useState(false);
    const [showInviteModal, setShowInviteModal] = useState(false);
    const [showMemberActions, setShowMemberActions] = useState<number | null>(null);
    const [loading, setLoading] = useState<string | null>(null);
    const [copiedCode, setCopiedCode] = useState(false);

    const isAdmin = group.creator.email.includes(currentUser);
    const isModerator = false; // Mock - w prawdziwej aplikacji z group.members
    const canManage = isAdmin || isModerator;

    const handleCopyCode = async () => {
        try {
            await navigator.clipboard.writeText(group.inviteCode);
            setCopiedCode(true);
            setTimeout(() => setCopiedCode(false), 2000);
        } catch (error) {
            console.error('Failed to copy code:', error);
        }
    };

    const handleRegenerateCode = async () => {
        if (!confirm('Czy na pewno chcesz wygenerować nowy kod? Stary kod przestanie działać.')) return;

        setLoading('regenerate');
        try {
            await onRegenerateCode(group.id);
        } finally {
            setLoading(null);
        }
    };

    const handleAction = async (action: () => Promise<void>, actionType: string) => {
        setLoading(actionType);
        try {
            await action();
            if (actionType === 'delete' || actionType === 'leave') {
                onBack();
            }
        } finally {
            setLoading(null);
        }
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('pl-PL', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    // Mock members data - w prawdziwej aplikacji z group.members
    const mockMembers = [
        {
            id: 1,
            user: group.creator,
            role: 'ADMIN',
            joinedAt: group.createdAt
        },
        {
            id: 2,
            user: { id: 2, name: 'Anna Kowalska', email: 'anna@test.pl', createdAt: '', updatedAt: '' },
            role: 'MODERATOR',
            joinedAt: '2024-08-10T10:00:00Z'
        },
        {
            id: 3,
            user: { id: 3, name: 'Piotr Nowak', email: 'piotr@test.pl', createdAt: '', updatedAt: '' },
            role: 'MEMBER',
            joinedAt: '2024-08-12T15:30:00Z'
        }
    ];

    const getRoleIcon = (role: string) => {
        switch (role) {
            case 'ADMIN': return Crown;
            case 'MODERATOR': return Shield;
            default: return UserIcon;
        }
    };

    const getRoleColor = (role: string) => {
        switch (role) {
            case 'ADMIN': return 'text-yellow-600 bg-yellow-100';
            case 'MODERATOR': return 'text-purple-600 bg-purple-100';
            default: return 'text-gray-600 bg-gray-100';
        }
    };

    const getRoleOptions = (currentRole: string) => {
        const options = [];
        if (isAdmin) {
            if (currentRole !== 'ADMIN') options.push('ADMIN');
            if (currentRole !== 'MODERATOR') options.push('MODERATOR');
            if (currentRole !== 'MEMBER') options.push('MEMBER');
        } else if (isModerator) {
            if (currentRole !== 'MEMBER') options.push('MEMBER');
        }
        return options;
    };

    return (
        <div className="max-w-6xl mx-auto">
            {/* Header */}
            <div className="flex items-center gap-4 mb-6">
                <button
                    onClick={onBack}
                    className="p-2 hover:bg-gray-100 rounded-full transition-colors"
                >
                    <ArrowLeft className="h-5 w-5" />
                </button>

                <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                        <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                            <Hash className="h-6 w-6 text-white" />
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">{group.name}</h1>
                            <div className="flex items-center gap-2">
                                {group.isPrivate ? (
                                    <div className="flex items-center gap-1 text-orange-600">
                                        <Lock className="h-4 w-4" />
                                        <span className="text-sm">Grupa prywatna</span>
                                    </div>
                                ) : (
                                    <div className="flex items-center gap-1 text-green-600">
                                        <Globe className="h-4 w-4" />
                                        <span className="text-sm">Grupa publiczna</span>
                                    </div>
                                )}
                                <div className="flex items-center gap-1 text-gray-500 text-sm">
                                    <Users className="h-4 w-4" />
                                    <span>{group.memberCount}/{group.maxMembers} członków</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Action Buttons */}
                <div className="flex gap-2">
                    {canManage && (
                        <>
                            <button
                                onClick={() => setShowInviteModal(true)}
                                className="flex items-center gap-2 bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors"
                            >
                                <UserPlus className="h-4 w-4" />
                                Zaproś
                            </button>

                            <button
                                onClick={() => setShowEditModal(true)}
                                className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                            >
                                <Edit3 className="h-4 w-4" />
                                Edytuj
                            </button>
                        </>
                    )}

                    {isAdmin ? (
                        <button
                            onClick={() => handleAction(() => onDeleteGroup(group.id), 'delete')}
                            disabled={loading === 'delete'}
                            className="flex items-center gap-2 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50"
                        >
                            {loading === 'delete' ? (
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                            ) : (
                                <Trash2 className="h-4 w-4" />
                            )}
                            Usuń grupę
                        </button>
                    ) : (
                        <button
                            onClick={() => handleAction(() => onLeaveGroup(group.id), 'leave')}
                            disabled={loading === 'leave'}
                            className="flex items-center gap-2 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50"
                        >
                            {loading === 'leave' ? (
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                            ) : (
                                <LogOut className="h-4 w-4" />
                            )}
                            Opuść grupę
                        </button>
                    )}
                </div>
            </div>

            {/* Tabs */}
            <div className="bg-white rounded-lg shadow mb-6">
                <div className="border-b border-gray-200">
                    <nav className="flex space-x-8 px-6">
                        {[
                            { id: 'overview', label: 'Przegląd', icon: Hash },
                            { id: 'members', label: `Członkowie (${mockMembers.length})`, icon: Users },
                            ...(canManage ? [{ id: 'settings', label: 'Ustawienia', icon: Settings }] : [])
                        ].map(tab => {
                            const Icon = tab.icon;
                            return (
                                <button
                                    key={tab.id}
                                    onClick={() => setActiveTab(tab.id as any)}
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
                                </button>
                            );
                        })}
                    </nav>
                </div>

                <div className="p-6">
                    {/* Overview Tab */}
                    {activeTab === 'overview' && (
                        <div className="space-y-6">
                            {/* Description */}
                            <div>
                                <h3 className="text-lg font-semibold text-gray-900 mb-3">Opis grupy</h3>
                                <p className="text-gray-600 leading-relaxed">{group.description}</p>
                            </div>

                            {/* Stats */}
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                <div className="bg-blue-50 rounded-lg p-4">
                                    <div className="flex items-center gap-3">
                                        <Users className="h-8 w-8 text-blue-600" />
                                        <div>
                                            <p className="text-2xl font-bold text-blue-900">{group.memberCount}</p>
                                            <p className="text-sm text-blue-600">Aktywnych członków</p>
                                        </div>
                                    </div>
                                </div>

                                <div className="bg-green-50 rounded-lg p-4">
                                    <div className="flex items-center gap-3">
                                        <Calendar className="h-8 w-8 text-green-600" />
                                        <div>
                                            <p className="text-2xl font-bold text-green-900">
                                                {Math.floor((new Date().getTime() - new Date(group.createdAt).getTime()) / (1000 * 60 * 60 * 24))}
                                            </p>
                                            <p className="text-sm text-green-600">Dni istnienia</p>
                                        </div>
                                    </div>
                                </div>

                                <div className="bg-purple-50 rounded-lg p-4">
                                    <div className="flex items-center gap-3">
                                        <Hash className="h-8 w-8 text-purple-600" />
                                        <div>
                                            <p className="text-2xl font-bold text-purple-900">42</p>
                                            <p className="text-sm text-purple-600">Wiadomości dzisiaj</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Group Info */}
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="space-y-4">
                                    <h3 className="text-lg font-semibold text-gray-900">Informacje</h3>
                                    <div className="space-y-3">
                                        <div className="flex items-center justify-between py-2 border-b border-gray-100">
                                            <span className="text-gray-600">Utworzono</span>
                                            <span className="font-medium text-gray-900">{formatDate(group.createdAt)}</span>
                                        </div>
                                        <div className="flex items-center justify-between py-2 border-b border-gray-100">
                                            <span className="text-gray-600">Założyciel</span>
                                            <span className="font-medium text-gray-900">{group.creator.name}</span>
                                        </div>
                                        <div className="flex items-center justify-between py-2 border-b border-gray-100">
                                            <span className="text-gray-600">Typ grupy</span>
                                            <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                                                group.isPrivate
                                                    ? 'bg-orange-100 text-orange-600'
                                                    : 'bg-green-100 text-green-600'
                                            }`}>
                        {group.isPrivate ? 'Prywatna' : 'Publiczna'}
                      </span>
                                        </div>
                                        <div className="flex items-center justify-between py-2">
                                            <span className="text-gray-600">Limit członków</span>
                                            <span className="font-medium text-gray-900">{group.maxMembers}</span>
                                        </div>
                                    </div>
                                </div>

                                {/* Invite Code (only for members) */}
                                <div className="space-y-4">
                                    <h3 className="text-lg font-semibold text-gray-900">Kod zaproszenia</h3>
                                    <div className="bg-gray-50 rounded-lg p-4">
                                        <div className="flex items-center justify-between mb-3">
                                            <div className="font-mono text-lg font-bold text-gray-900 tracking-widest">
                                                {group.inviteCode}
                                            </div>
                                            <button
                                                onClick={handleCopyCode}
                                                className="flex items-center gap-1 text-sm text-blue-600 hover:text-blue-700"
                                            >
                                                <Copy className="h-4 w-4" />
                                                {copiedCode ? 'Skopiowano!' : 'Kopiuj'}
                                            </button>
                                        </div>
                                        <p className="text-sm text-gray-600 mb-3">
                                            Udostępnij ten kod, aby zaprosić nowych członków do grupy
                                        </p>
                                        {canManage && (
                                            <button
                                                onClick={handleRegenerateCode}
                                                disabled={loading === 'regenerate'}
                                                className="flex items-center gap-1 text-sm text-orange-600 hover:text-orange-700 disabled:opacity-50"
                                            >
                                                {loading === 'regenerate' ? (
                                                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-orange-600"></div>
                                                ) : (
                                                    <RefreshCw className="h-4 w-4" />
                                                )}
                                                Wygeneruj nowy kod
                                            </button>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Members Tab */}
                    {activeTab === 'members' && (
                        <div className="space-y-4">
                            <div className="flex items-center justify-between">
                                <h3 className="text-lg font-semibold text-gray-900">
                                    Członkowie grupy ({mockMembers.length})
                                </h3>
                                {canManage && (
                                    <button
                                        onClick={() => setShowInviteModal(true)}
                                        className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors text-sm"
                                    >
                                        <UserPlus className="h-4 w-4" />
                                        Zaproś członków
                                    </button>
                                )}
                            </div>

                            <div className="space-y-3">
                                {mockMembers.map(member => {
                                    const RoleIcon = getRoleIcon(member.role);
                                    const roleColor = getRoleColor(member.role);
                                    const isCurrentUser = member.user.email.includes(currentUser);
                                    const roleOptions = getRoleOptions(member.role);

                                    return (
                                        <div key={member.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                                            <div className="flex items-center gap-4">
                                                <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                          <span className="text-lg font-semibold text-blue-600">
                            {member.user.name.charAt(0).toUpperCase()}
                          </span>
                                                </div>

                                                <div>
                                                    <div className="flex items-center gap-2">
                                                        <h4 className="font-medium text-gray-900">{member.user.name}</h4>
                                                        {isCurrentUser && (
                                                            <span className="text-xs bg-blue-100 text-blue-600 px-2 py-1 rounded-full">
                                To ty
                              </span>
                                                        )}
                                                    </div>
                                                    <div className="flex items-center gap-3 text-sm text-gray-500">
                                                        <span>{member.user.email}</span>
                                                        <span>•</span>
                                                        <span>Dołączył {formatDate(member.joinedAt)}</span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div className="flex items-center gap-2">
                                                <div className={`flex items-center gap-1 px-3 py-1 rounded-full text-sm font-medium ${roleColor}`}>
                                                    <RoleIcon className="h-4 w-4" />
                                                    <span>
                            {member.role === 'ADMIN' ? 'Administrator' :
                                member.role === 'MODERATOR' ? 'Moderator' : 'Członek'}
                          </span>
                                                </div>

                                                {canManage && !isCurrentUser && member.role !== 'ADMIN' && (
                                                    <div className="relative">
                                                        <button
                                                            onClick={() => setShowMemberActions(showMemberActions === member.id ? null : member.id)}
                                                            className="p-2 hover:bg-gray-200 rounded-full transition-colors"
                                                        >
                                                            <MoreVertical className="h-4 w-4 text-gray-500" />
                                                        </button>

                                                        {showMemberActions === member.id && (
                                                            <div className="absolute right-0 top-10 bg-white border border-gray-200 rounded-lg shadow-lg z-10 min-w-[160px]">
                                                                {roleOptions.map(role => (
                                                                    <button
                                                                        key={role}
                                                                        onClick={() => {
                                                                            onChangeRole(group.id, member.id, role);
                                                                            setShowMemberActions(null);
                                                                        }}
                                                                        className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 flex items-center gap-2"
                                                                    >
                                                                        {getRoleIcon(role)({ className: "h-4 w-4" })}
                                                                        Ustaw jako {role === 'ADMIN' ? 'Admin' : role === 'MODERATOR' ? 'Moderator' : 'Członek'}
                                                                    </button>
                                                                ))}
                                                                <hr className="my-1" />
                                                                <button
                                                                    onClick={() => {
                                                                        onRemoveMember(group.id, member.id);
                                                                        setShowMemberActions(null);
                                                                    }}
                                                                    className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                                                                >
                                                                    <Ban className="h-4 w-4" />
                                                                    Usuń z grupy
                                                                </button>
                                                            </div>
                                                        )}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        </div>
                    )}

                    {/* Settings Tab */}
                    {activeTab === 'settings' && canManage && (
                        <div className="space-y-6">
                            <h3 className="text-lg font-semibold text-gray-900">Ustawienia grupy</h3>

                            {/* Danger Zone */}
                            <div className="bg-red-50 border border-red-200 rounded-lg p-6">
                                <h4 className="text-lg font-medium text-red-900 mb-4">Strefa niebezpieczna</h4>
                                <div className="space-y-4">
                                    <div className="flex items-center justify-between">
                                        <div>
                                            <h5 className="font-medium text-red-900">Regeneruj kod zaproszenia</h5>
                                            <p className="text-sm text-red-700">Stary kod przestanie działać</p>
                                        </div>
                                        <button
                                            onClick={handleRegenerateCode}
                                            disabled={loading === 'regenerate'}
                                            className="bg-orange-600 text-white px-4 py-2 rounded-md hover:bg-orange-700 transition-colors disabled:opacity-50"
                                        >
                                            {loading === 'regenerate' ? 'Regenerowanie...' : 'Regeneruj'}
                                        </button>
                                    </div>

                                    {isAdmin && (
                                        <div className="flex items-center justify-between pt-4 border-t border-red-200">
                                            <div>
                                                <h5 className="font-medium text-red-900">Usuń grupę</h5>
                                                <p className="text-sm text-red-700">Ta akcja jest nieodwracalna</p>
                                            </div>
                                            <button
                                                onClick={() => handleAction(() => onDeleteGroup(group.id), 'delete')}
                                                disabled={loading === 'delete'}
                                                className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors disabled:opacity-50"
                                            >
                                                {loading === 'delete' ? 'Usuwanie...' : 'Usuń grupę'}
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* Modals */}
            {showEditModal && (
                <EditGroupModal
                    group={group}
                    onClose={() => setShowEditModal(false)}
                    onUpdate={onUpdateGroup}
                />
            )}

            {showInviteModal && (
                <InviteMembersModal
                    group={group}
                    friends={friends}
                    onClose={() => setShowInviteModal(false)}
                />
            )}
        </div>
    );
};

export default GroupDetails;