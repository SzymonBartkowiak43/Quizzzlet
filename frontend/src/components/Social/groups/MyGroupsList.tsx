import React, { useState } from 'react';
import { StudyGroup } from '../../../types/social';
import {
    Users,
    MoreVertical,
    Settings,
    LogOut,
    Trash2,
    Globe,
    Lock,
    Crown,
    Shield,
    User,
    Calendar,
    Hash
} from 'lucide-react';

interface MyGroupsListProps {
    groups: StudyGroup[];
    searchTerm?: string;
    onGroupSelect: (groupId: number) => void;
    onLeaveGroup: (groupId: number) => Promise<void>;
    onDeleteGroup: (groupId: number) => Promise<void>;
    currentUser: string;
}

const MyGroupsList: React.FC<MyGroupsListProps> = ({
                                                       groups,
                                                       searchTerm,
                                                       onGroupSelect,
                                                       onLeaveGroup,
                                                       onDeleteGroup,
                                                       currentUser
                                                   }) => {
    const [showDropdown, setShowDropdown] = useState<number | null>(null);
    const [loading, setLoading] = useState<number | null>(null);

    const handleAction = async (action: () => Promise<void>, groupId: number) => {
        if (!confirm('Czy jesteś pewien?')) return;

        setLoading(groupId);
        try {
            await action();
            setShowDropdown(null);
        } finally {
            setLoading(null);
        }
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('pl-PL');
    };

    const highlightText = (text: string, highlight: string) => {
        if (!highlight) return text;

        const parts = text.split(new RegExp(`(${highlight})`, 'gi'));
        return parts.map((part, index) =>
            part.toLowerCase() === highlight.toLowerCase() ? (
                <mark key={index} className="bg-yellow-200 px-1 rounded">{part}</mark>
            ) : part
        );
    };

    const getUserRole = (group: StudyGroup) => {
        // W prawdziwej aplikacji to by pochodziło z group.members
        if (group.creator.email.includes(currentUser)) return 'ADMIN';
        return 'MEMBER'; // Mock role
    };

    const getRoleIcon = (role: string) => {
        switch (role) {
            case 'ADMIN': return Crown;
            case 'MODERATOR': return Shield;
            default: return User;
        }
    };

    const getRoleColor = (role: string) => {
        switch (role) {
            case 'ADMIN': return 'text-yellow-600 bg-yellow-100';
            case 'MODERATOR': return 'text-purple-600 bg-purple-100';
            default: return 'text-gray-600 bg-gray-100';
        }
    };

    if (groups.length === 0) {
        return (
            <div className="text-center py-12">
                <Users className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">
                    {searchTerm ? 'Brak wyników wyszukiwania' : 'Nie należysz do żadnych grup'}
                </h3>
                <p className="text-gray-500 mb-6">
                    {searchTerm
                        ? `Nie znaleziono grup pasujących do "${searchTerm}"`
                        : 'Dołącz do istniejących grup lub utwórz własną!'
                    }
                </p>
                {!searchTerm && (
                    <div className="flex gap-3 justify-center">
                        <button className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors">
                            Utwórz grupę
                        </button>
                        <button className="bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 transition-colors">
                            Znajdź grupy
                        </button>
                    </div>
                )}
            </div>
        );
    }

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-gray-900">
                    Twoje grupy ({groups.length})
                </h3>
                {searchTerm && (
                    <div className="text-sm text-gray-500">
                        Wyniki dla: "{searchTerm}"
                    </div>
                )}
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {groups.map(group => {
                    const userRole = getUserRole(group);
                    const RoleIcon = getRoleIcon(userRole);
                    const roleColor = getRoleColor(userRole);
                    const isAdmin = userRole === 'ADMIN';

                    return (
                        <div
                            key={group.id}
                            className="bg-white border border-gray-200 rounded-lg p-5 hover:border-blue-300 hover:shadow-md transition-all duration-200 relative"
                        >
                            {/* Group Header */}
                            <div className="flex items-start justify-between mb-4">
                                <div className="flex items-center gap-3">
                                    <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                                        <Hash className="h-6 w-6 text-white" />
                                    </div>

                                    <div className="flex-1">
                                        <h4 className="font-semibold text-gray-900 mb-1 line-clamp-1">
                                            {highlightText(group.name, searchTerm || '')}
                                        </h4>
                                        <div className="flex items-center gap-2">
                                            <div className={`flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${roleColor}`}>
                                                <RoleIcon className="h-3 w-3" />
                                                {/*<span>{userRole === 'ADMIN' ? 'Admin' : 'MODERATOR' !== userRole ? 'Członek' : 'Moderator'}</span>*/}
                                            </div>
                                            {group.isPrivate ? (
                                                <Lock className="h-3 w-3 text-gray-500" />
                                            ) : (
                                                <Globe className="h-3 w-3 text-gray-500" />
                                            )}
                                        </div>
                                    </div>
                                </div>

                                <div className="relative">
                                    <button
                                        onClick={() => setShowDropdown(showDropdown === group.id ? null : group.id)}
                                        className="p-1 hover:bg-gray-100 rounded-full transition-colors"
                                        disabled={loading === group.id}
                                    >
                                        <MoreVertical className="h-4 w-4 text-gray-500" />
                                    </button>

                                    {showDropdown === group.id && (
                                        <div className="absolute right-0 top-8 bg-white border border-gray-200 rounded-lg shadow-lg z-10 min-w-[160px]">
                                            <button
                                                onClick={() => {
                                                    onGroupSelect(group.id);
                                                    setShowDropdown(null);
                                                }}
                                                className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 flex items-center gap-2"
                                            >
                                                <Settings className="h-4 w-4" />
                                                Szczegóły
                                            </button>

                                            {isAdmin ? (
                                                <button
                                                    onClick={() => handleAction(() => onDeleteGroup(group.id), group.id)}
                                                    className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                                                    disabled={loading === group.id}
                                                >
                                                    <Trash2 className="h-4 w-4" />
                                                    Usuń grupę
                                                </button>
                                            ) : (
                                                <button
                                                    onClick={() => handleAction(() => onLeaveGroup(group.id), group.id)}
                                                    className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                                                    disabled={loading === group.id}
                                                >
                                                    <LogOut className="h-4 w-4" />
                                                    Opuść grupę
                                                </button>
                                            )}
                                        </div>
                                    )}
                                </div>
                            </div>

                            {/* Description */}
                            <p className="text-sm text-gray-600 mb-4 line-clamp-3">
                                {highlightText(group.description, searchTerm || '')}
                            </p>

                            {/* Stats */}
                            <div className="flex items-center justify-between mb-4">
                                <div className="flex items-center gap-4 text-sm text-gray-500">
                                    <div className="flex items-center gap-1">
                                        <Users className="h-3 w-3" />
                                        <span>{group.memberCount}/{group.maxMembers}</span>
                                    </div>
                                    <div className="flex items-center gap-1">
                                        <Calendar className="h-3 w-3" />
                                        <span>{formatDate(group.createdAt)}</span>
                                    </div>
                                </div>

                                <div className="flex items-center gap-1">
                                    <div className={`w-2 h-2 rounded-full ${group.memberCount > 0 ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                                    <span className="text-xs text-gray-500">
                    {group.memberCount > 0 ? 'Aktywna' : 'Nieaktywna'}
                  </span>
                                </div>
                            </div>

                            {/* Actions */}
                            <div className="flex gap-2">
                                <button
                                    onClick={() => onGroupSelect(group.id)}
                                    className="flex-1 bg-blue-600 text-white py-2 px-3 rounded-md text-sm hover:bg-blue-700 transition-colors"
                                >
                                    Zobacz szczegóły
                                </button>
                            </div>

                            {loading === group.id && (
                                <div className="absolute inset-0 bg-white bg-opacity-75 rounded-lg flex items-center justify-center">
                                    <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default MyGroupsList;