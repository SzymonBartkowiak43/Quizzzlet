import React, { useState } from 'react';
import { useSocialDashboard } from '../../../hooks/useSocialDashboard';
import { useFriendships } from '../../../hooks/userFriendships';
import { useGroups } from '../../../hooks/useGroups';
import { useMessages } from '../../../hooks/useMessages';
import {
    Users,
    UserPlus,
    MessageCircle,
    Users2,
    Plus,
    Search,
    Bell,
    TrendingUp
} from 'lucide-react';
import LoadingSpinner from '../../Shared/LoadingSpinner';
import StatsCard from './StatsCard';
import QuickActions from './QuickActions';
import RecentActivity from './RecentActivity';
import PendingRequests from './PendingRequests';

const SocialDashboard: React.FC = () => {
    const { dashboard, loading, error, refreshDashboard } = useSocialDashboard();
    // const { acceptFriendRequest, declineFriendRequest } = useFriendships();
    const [refreshing, setRefreshing] = useState(false);

    const handleRefresh = async () => {
        setRefreshing(true);
        await refreshDashboard();
        setRefreshing(false);
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

    if (!dashboard) return null;

    // Poprawione typy kolorów
    const stats = [
        {
            title: 'Przyjaciele',
            value: dashboard.stats.totalFriends,
            icon: Users,
            color: 'blue' as const, // Dodaj "as const"
            trend: '+2 w tym tygodniu'
        },
        {
            title: 'Grupy',
            value: dashboard.stats.totalGroups,
            icon: Users2,
            color: 'green' as const, // Dodaj "as const"
            trend: '+1 w tym miesiącu'
        },
        {
            title: 'Nieprzeczytane',
            value: dashboard.stats.unreadMessages,
            icon: MessageCircle,
            color: 'orange' as const, // Dodaj "as const"
            trend: dashboard.stats.unreadMessages > 0 ? 'Nowe wiadomości!' : 'Brak nowych'
        },
        {
            title: 'Zaproszenia',
            value: dashboard.stats.pendingFriendRequests,
            icon: UserPlus,
            color: 'purple' as const, // Dodaj "as const"
            trend: dashboard.stats.pendingFriendRequests > 0 ? 'Wymagają akcji' : 'Brak oczekujących'
        }
    ];

    return (
        <div className="max-w-7xl mx-auto p-6 space-y-6">
            {/* Header */}
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Dashboard Społecznościowy</h1>
                    <p className="text-gray-600 mt-1">
                        Zarządzaj przyjaciółmi, grupami i wiadomościami w jednym miejscu
                    </p>
                </div>
                <div className="flex gap-3">
                    <button
                        onClick={handleRefresh}
                        disabled={refreshing}
                        className="flex items-center gap-2 bg-white border border-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50"
                    >
                        <TrendingUp className="h-4 w-4" />
                        {refreshing ? 'Odświeżanie...' : 'Odśwież'}
                    </button>
                </div>
            </div>

            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {stats.map((stat, index) => (
                    <StatsCard key={index} {...stat} />
                ))}
            </div>

            {/* Quick Actions & Notifications */}
            {(dashboard.quickActions.hasPendingRequests ||
                dashboard.quickActions.hasUnreadMessages ||
                dashboard.stats.pendingFriendRequests > 0) && (
                <div className="bg-amber-50 border border-amber-200 rounded-lg p-4">
                    <div className="flex items-center gap-2 mb-2">
                        <Bell className="h-5 w-5 text-amber-600" />
                        <h3 className="font-medium text-amber-800">Wymagają uwagi</h3>
                    </div>
                    <div className="space-y-2 text-sm text-amber-700">
                        {dashboard.quickActions.hasPendingRequests && (
                            <p>• Masz {dashboard.stats.pendingFriendRequests} oczekujących zaproszeń do przyjaźni</p>
                        )}
                        {dashboard.quickActions.hasUnreadMessages && (
                            <p>• Masz {dashboard.stats.unreadMessages} nieprzeczytanych wiadomości</p>
                        )}
                    </div>
                </div>
            )}

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Main Content */}
                <div className="lg:col-span-2 space-y-6">
                    {/* Quick Actions */}
                    <QuickActions
                        dashboard={dashboard}
                        onRefresh={handleRefresh}
                    />

                    {/* Recent Activity */}
                    {/*<RecentActivity*/}
                    {/*    friendshipInfo={dashboard.friendshipInfo}*/}
                    {/*    groupInfo={dashboard.groupInfo}*/}
                    {/*    messagingInfo={dashboard.messagingInfo}*/}
                    {/*/>*/}
                </div>

                {/* Sidebar */}
                <div className="space-y-6">
                    {/*/!* Pending Friend Requests *!/*/}
                    {/*{dashboard.friendshipInfo.pendingRequests.length > 0 && (*/}
                    {/*    <PendingRequests*/}
                    {/*        requests={dashboard.friendshipInfo.pendingRequests}*/}
                    {/*        onAccept={acceptFriendRequest}*/}
                    {/*        onDecline={declineFriendRequest}*/}
                    {/*    />*/}
                    {/*)}*/}

                    {/* Suggested Friends */}
                    {/*{dashboard.friendshipInfo.suggestedFriends?.length > 0 && (*/}
                    {/*    <div className="bg-white rounded-lg shadow p-6">*/}
                    {/*        <h3 className="text-lg font-semibold text-gray-900 mb-4">*/}
                    {/*            Sugerowani przyjaciele*/}
                    {/*        </h3>*/}
                    {/*        <div className="space-y-3">*/}
                    {/*            {dashboard.friendshipInfo.suggestedFriends .slice(0, 5).map(user => (*/}
                    {/*                <div key={user.id} className="flex items-center justify-between">*/}
                    {/*                    <div className="flex items-center gap-3">*/}
                    {/*                        <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center">*/}
                    {/*    <span className="text-sm font-medium text-gray-600">*/}
                    {/*      {user.name.charAt(0).toUpperCase()}*/}
                    {/*    </span>*/}
                    {/*                        </div>*/}
                    {/*                        <div>*/}
                    {/*                            <p className="font-medium text-gray-900">{user.name}</p>*/}
                    {/*                            <p className="text-sm text-gray-500">{user.email}</p>*/}
                    {/*                        </div>*/}
                    {/*                    </div>*/}
                    {/*                    <button className="text-sm bg-blue-600 text-white px-3 py-1 rounded-md hover:bg-blue-700 transition-colors">*/}
                    {/*                        Dodaj*/}
                    {/*                    </button>*/}
                    {/*                </div>*/}
                    {/*            ))}*/}
                    {/*        </div>*/}
                    {/*    </div>*/}
                    {/*)}*/}

                    {/* Active Groups Preview */}
                    <div className="bg-white rounded-lg shadow p-6">
                        <h3 className="text-lg font-semibold text-gray-900 mb-4">
                            Aktywne grupy
                        </h3>
                        <div className="space-y-3">
                            {dashboard.groupInfo.activeGroups.slice(0, 3).map(group => (
                                <div key={group.id} className="border-l-4 border-blue-500 pl-4">
                                    <h4 className="font-medium text-gray-900">{group.name}</h4>
                                    <p className="text-sm text-gray-500">
                                        {group.memberCount} członków
                                    </p>
                                    <p className="text-xs text-gray-400">
                                        {group.isPrivate ? 'Prywatna' : 'Publiczna'}
                                    </p>
                                </div>
                            ))}
                        </div>
                        <button className="mt-4 w-full text-center text-sm text-blue-600 hover:text-blue-700 font-medium">
                            Zobacz wszystkie grupy →
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SocialDashboard;