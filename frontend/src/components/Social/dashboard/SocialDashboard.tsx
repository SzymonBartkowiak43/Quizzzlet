import React, { useState } from 'react';
import { useSocialDashboard } from '../../../hooks/useSocialDashboard';
import {
    Users,
    UserPlus,
    MessageCircle,
    Users2,
    Plus,
    Search,
    Bell,
    TrendingUp,
    RefreshCw
} from 'lucide-react';
import LoadingSpinner from '../../Shared/LoadingSpinner';
import StatsCard from './StatsCard';
import QuickActions from './QuickActions';
import './SocialDashboard.css';


const SocialDashboard: React.FC = () => {
    const { dashboard, loading, error, refreshDashboard } = useSocialDashboard();
    const [refreshing, setRefreshing] = useState(false);

    const handleRefresh = async () => {
        setRefreshing(true);
        await refreshDashboard();
        setRefreshing(false);
    };

    // Style przycisków
    const btnGlass = "btn-glass";
    const btnPrimary = "btn-primary-solid";
    const btnGlassDanger = "btn-glass-danger";

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-[400px]">
                <LoadingSpinner size="lg" color="white" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="text-center py-8">
                <div className="error-message max-w-md mx-auto">
                    <h3 className="font-medium mb-2">Błąd ładowania</h3>
                    <p className="mb-4">{error}</p>
                    <button
                        onClick={handleRefresh}
                        className={btnGlassDanger}
                    >
                        Spróbuj ponownie
                    </button>
                </div>
            </div>
        );
    }

    if (!dashboard) return null;

    const stats = [
        {
            title: 'Przyjaciele',
            value: dashboard.stats.totalFriends,
            icon: Users,
            trend: '+2 w tym tygodniu'
        },
        {
            title: 'Grupy',
            value: dashboard.stats.totalGroups,
            icon: Users2,
            trend: '+1 w tym miesiącu'
        },
        {
            title: 'Nieprzeczytane',
            value: dashboard.stats.unreadMessages,
            icon: MessageCircle,
            trend: dashboard.stats.unreadMessages > 0 ? 'Nowe wiadomości!' : 'Brak nowych'
        },
        {
            title: 'Zaproszenia',
            value: dashboard.stats.pendingFriendRequests,
            icon: UserPlus,
            trend: dashboard.stats.pendingFriendRequests > 0 ? 'Wymagają akcji' : 'Brak oczekujących'
        }
    ];

    return (
        <div className="social-dashboard-page">
            {/* Header */}
            <div className="page-header">
                <div>
                    <h1>Dashboard Społecznościowy</h1>
                    <p className="page-subtitle">
                        Zarządzaj przyjaciółmi, grupami i wiadomościami w jednym miejscu
                    </p>
                </div>
                <div className="flex gap-3">
                    <button
                        onClick={handleRefresh}
                        disabled={refreshing}
                        className={btnGlass}
                    >
                        <RefreshCw className={`h-4 w-4 ${refreshing ? 'animate-spin' : ''}`} />
                        {refreshing ? 'Odświeżanie...' : 'Odśwież'}
                    </button>
                </div>
            </div>

            {/* Karty statystyk */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {stats.map((stat, index) => (
                    <StatsCard key={index} {...stat} />
                ))}
            </div>

            {/* Powiadomienie "Wymagają uwagi" */}
            {(dashboard.quickActions.hasPendingRequests ||
                dashboard.quickActions.hasUnreadMessages ||
                dashboard.stats.pendingFriendRequests > 0) && (
                <div className="attention-box">
                    <div className="flex items-center gap-2 mb-2">
                        <Bell className="h-5 w-5" />
                        <h3 className="font-medium">Wymagają uwagi</h3>
                    </div>
                    <div className="space-y-2 text-sm">
                        {dashboard.quickActions.hasPendingRequests && (
                            <p>• Masz {dashboard.stats.pendingFriendRequests} oczekujących zaproszeń do przyjaźni</p>
                        )}
                        {dashboard.quickActions.hasUnreadMessages && (
                            <p>• Masz {dashboard.stats.unreadMessages} nieprzeczytanych wiadomości</p>
                        )}
                    </div>
                </div>
            )}

            {/* Główna siatka (Akcje + Grupy) */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <div className="lg:col-span-2 space-y-6">
                    <QuickActions
                        dashboard={dashboard}
                        onRefresh={handleRefresh}
                    />
                </div>

                <div className="space-y-6">
                    <div className="glass-box">
                        <h3 className="box-title">
                            Aktywne grupy
                        </h3>
                        <div className="space-y-3">
                            {dashboard.groupInfo.activeGroups.slice(0, 3).map(group => (
                                <div key={group.id} className="border-l-4 border-blue-400 pl-4">
                                    <h4 className="font-medium text-white">{group.name}</h4>
                                    <p className="text-sm text-gray-300">
                                        {group.memberCount} członków
                                    </p>
                                    <p className="text-xs text-gray-400">
                                        {group.isPrivate ? 'Prywatna' : 'Publiczna'}
                                    </p>
                                </div>
                            ))}
                        </div>
                        <button className="mt-4 w-full text-center text-sm text-blue-300 hover:text-blue-200 font-medium">
                            Zobacz wszystkie grupy →
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SocialDashboard;