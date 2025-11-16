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
import './GroupDetails.css';

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
    const isModerator = false; // Mock
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

    // Mock members data
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
            case 'ADMIN': return 'text-yellow-300 bg-yellow-500/30';
            case 'MODERATOR': return 'text-purple-300 bg-purple-500/30';
            default: return 'text-gray-300 bg-gray-500/30';
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
        <div className="group-details-page">
            {/* Header */}
            <div className="flex items-center gap-4 mb-6">
                <button
                    onClick={onBack}
                    className="p-2 hover:bg-white/10 rounded-full transition-colors"
                >
                    <ArrowLeft className="h-5 w-5 text-white" />
                </button>

                <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                        <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                            <Hash className="h-6 w-6 text-white" />
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold text-white">{group.name}</h1>
                            <div className="flex items-center gap-2">
                                {group.isPrivate ? (
                                    <div className="flex items-center gap-1 text-orange-300">
                                        <Lock className="h-4 w-4" />
                                        <span className="text-sm">Grupa prywatna</span>
                                    </div>
                                ) : (
                                    <div className="flex items-center gap-1 text-green-300">
                                        <Globe className="h-4 w-4" />
                                        <span className="text-sm">Grupa publiczna</span>
                                    </div>
                                )}
                                <div className="flex items-center gap-1 text-gray-300 text-sm">
                                    <Users className="h-4 w-4" />
                                    <span>{group.memberCount}/{group.maxMembers} członków</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="flex gap-2">
                    {canManage && (
                        <>
                            <button
                                onClick={() => setShowInviteModal(true)}
                                className="btn-glass-success"
                            >
                                <UserPlus className="h-4 w-4" />
                                Zaproś
                            </button>

                            <button
                                onClick={() => setShowEditModal(true)}
                                className="btn-primary-solid"
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
                            className="btn-glass-danger"
                        >
                            {loading === 'delete' ? (
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                            ) : (
                                <Trash2 className="h-4 w-4" />
                            )}
                            Usuń
                        </button>
                    ) : (
                        <button
                            onClick={() => handleAction(() => onLeaveGroup(group.id), 'leave')}
                            disabled={loading === 'leave'}
                            className="btn-glass-danger"
                        >
                            {loading === 'leave' ? (
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                            ) : (
                                <LogOut className="h-4 w-4" />
                            )}
                            Opuść
                        </button>
                    )}
                </div>
            </div>

            {/* Taby */}
            <div className="glass-box-flat">
                <div className="border-b border-white/20">
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
                                        ? 'border-white text-white'
                                        : 'border-transparent text-gray-400 hover:text-white hover:border-gray-500'
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
                    {activeTab === 'overview' && (
                        <div className="space-y-6">
                            <div>
                                <h3 className="box-title mb-3">Opis grupy</h3>
                                <p className="text-gray-200 leading-relaxed">{group.description}</p>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                <div className="stat-card">
                                    <Users className="h-8 w-8 text-blue-300" />
                                    <div>
                                        <p className="stat-value">{group.memberCount}</p>
                                        <p className="stat-label">Aktywnych członków</p>
                                    </div>
                                </div>
                                <div className="stat-card">
                                    <Calendar className="h-8 w-8 text-green-300" />
                                    <div>
                                        <p className="stat-value">
                                            {Math.floor((new Date().getTime() - new Date(group.createdAt).getTime()) / (1000 * 60 * 60 * 24))}
                                        </p>
                                        <p className="stat-label">Dni istnienia</p>
                                    </div>
                                </div>
                                <div className="stat-card">
                                    <Hash className="h-8 w-8 text-purple-300" />
                                    <div>
                                        <p className="stat-value">42</p>
                                        <p className="stat-label">Wiadomości dzisiaj</p>
                                    </div>
                                </div>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="space-y-4">
                                    <h3 className="box-title">Informacje</h3>
                                    <div className="space-y-3">
                                        <div className="info-item">
                                            <span className="text-gray-300">Utworzono</span>
                                            <span className="font-medium text-white">{formatDate(group.createdAt)}</span>
                                        </div>
                                        <div className="info-item">
                                            <span className="text-gray-300">Założyciel</span>
                                            <span className="font-medium text-white">{group.creator.name}</span>
                                        </div>
                                        <div className="info-item">
                                            <span className="text-gray-300">Typ grupy</span>
                                            <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                                                group.isPrivate
                                                    ? 'bg-orange-500/30 text-orange-200'
                                                    : 'bg-green-500/30 text-green-200'
                                            }`}>
                                                {group.isPrivate ? 'Prywatna' : 'Publiczna'}
                                            </span>
                                        </div>
                                        <div className="info-item">
                                            <span className="text-gray-300">Limit członków</span>
                                            <span className="font-medium text-white">{group.maxMembers}</span>
                                        </div>
                                    </div>
                                </div>

                                <div className="space-y-4">
                                    <h3 className="box-title">Kod zaproszenia</h3>
                                    <div className="bg-black/20 rounded-lg p-4">
                                        <div className="flex items-center justify-between mb-3">
                                            <div className="font-mono text-lg font-bold text-white tracking-widest">
                                                {group.inviteCode}
                                            </div>
                                            <button
                                                onClick={handleCopyCode}
                                                className="flex items-center gap-1 text-sm text-blue-300 hover:text-blue-200"
                                            >
                                                <Copy className="h-4 w-4" />
                                                {copiedCode ? 'Skopiowano!' : 'Kopiuj'}
                                            </button>
                                        </div>
                                        <p className="text-sm text-gray-300 mb-3">
                                            Udostępnij ten kod, aby zaprosić nowych członków
                                        </p>
                                        {canManage && (
                                            <button
                                                onClick={handleRegenerateCode}
                                                disabled={loading === 'regenerate'}
                                                className="flex items-center gap-1 text-sm text-orange-300 hover:text-orange-200 disabled:opacity-50"
                                            >
                                                {loading === 'regenerate' ? (
                                                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-orange-300"></div>
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

                    {/* Zakładka: Członkowie */}
                    {activeTab === 'members' && (
                        <div className="space-y-4">
                            <div className="flex items-center justify-between">
                                <h3 className="box-title">
                                    Członkowie grupy ({mockMembers.length})
                                </h3>
                                {canManage && (
                                    <button
                                        onClick={() => setShowInviteModal(true)}
                                        className="btn-glass-success text-sm"
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
                                        <div key={member.id} className="flex items-center justify-between p-4 bg-white/5 rounded-lg hover:bg-white/10 transition-colors">
                                            <div className="flex items-center gap-4">
                                                <div className="w-12 h-12 bg-blue-500/30 rounded-full flex items-center justify-center">
                                                  <span className="text-lg font-semibold text-blue-100">
                                                    {member.user.name.charAt(0).toUpperCase()}
                                                  </span>
                                                </div>
                                                <div>
                                                    <div className="flex items-center gap-2">
                                                        <h4 className="font-medium text-white">{member.user.name}</h4>
                                                        {isCurrentUser && (
                                                            <span className="text-xs bg-blue-500/30 text-blue-200 px-2 py-1 rounded-full">
                                                                To ty
                                                            </span>
                                                        )}
                                                    </div>
                                                    <div className="flex items-center gap-3 text-sm text-gray-400">
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
                                                        {member.role === 'ADMIN' ? 'Admin' :
                                                            member.role === 'MODERATOR' ? 'Moderator' : 'Członek'}
                                                    </span>
                                                </div>
                                                {canManage && !isCurrentUser && member.role !== 'ADMIN' && (
                                                    <div className="relative">
                                                        <button
                                                            onClick={() => setShowMemberActions(showMemberActions === member.id ? null : member.id)}
                                                            className="p-2 hover:bg-white/20 rounded-full transition-colors"
                                                        >
                                                            <MoreVertical className="h-4 w-4 text-gray-300" />
                                                        </button>
                                                        {showMemberActions === member.id && (
                                                            <div className="absolute right-0 top-10 glass-box-flat border border-white/30 z-10 min-w-[180px]">
                                                                {roleOptions.map(role => (
                                                                    <button
                                                                        key={role}
                                                                        onClick={() => {
                                                                            onChangeRole(group.id, member.id, role);
                                                                            setShowMemberActions(null);
                                                                        }}
                                                                        className="w-full text-left px-4 py-2 text-sm text-gray-200 hover:bg-white/10 flex items-center gap-2"
                                                                    >
                                                                        {getRoleIcon(role)({ className: "h-4 w-4" })}
                                                                        Ustaw jako {role}
                                                                    </button>
                                                                ))}
                                                                <hr className="my-1 border-white/10" />
                                                                <button
                                                                    onClick={() => {
                                                                        onRemoveMember(group.id, member.id);
                                                                        setShowMemberActions(null);
                                                                    }}
                                                                    className="w-full text-left px-4 py-2 text-sm text-red-400 hover:bg-red-500/20 flex items-center gap-2"
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

                    {/* Zakładka: Ustawienia */}
                    {activeTab === 'settings' && canManage && (
                        <div className="space-y-6">
                            <h3 className="box-title">Ustawienia grupy</h3>
                            <div className="danger-zone">
                                <h4 className="text-lg font-medium text-red-300 mb-4">Strefa niebezpieczna</h4>
                                <div className="space-y-4">
                                    <div className="flex items-center justify-between">
                                        <div>
                                            <h5 className="font-medium text-white">Regeneruj kod zaproszenia</h5>
                                            <p className="text-sm text-gray-300">Stary kod przestanie działać</p>
                                        </div>
                                        <button
                                            onClick={handleRegenerateCode}
                                            disabled={loading === 'regenerate'}
                                            className="btn-glass-warning"
                                        >
                                            {loading === 'regenerate' ? 'Regenerowanie...' : 'Regeneruj'}
                                        </button>
                                    </div>

                                    {isAdmin && (
                                        <div className="flex items-center justify-between pt-4 border-t border-red-500/30">
                                            <div>
                                                <h5 className="font-medium text-white">Usuń grupę</h5>
                                                <p className="text-sm text-gray-300">Ta akcja jest nieodwracalna</p>
                                            </div>
                                            <button
                                                onClick={() => handleAction(() => onDeleteGroup(group.id), 'delete')}
                                                disabled={loading === 'delete'}
                                                className="btn-glass-danger"
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

            {/* Modale */}
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