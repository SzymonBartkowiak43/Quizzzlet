import React from 'react';
import { SocialDashboard } from '../../../types/social';
import {
    Plus,
    Users,
    MessageCircle,
    Search,
    UserPlus,
    Hash
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface QuickActionsProps {
    dashboard: SocialDashboard;
    onRefresh: () => void;
}

const QuickActions: React.FC<QuickActionsProps> = ({ dashboard, onRefresh }) => {
    const navigate = useNavigate();

    const actions = [
        {
            title: 'Utwórz grupę',
            description: 'Stwórz nową grupę nauki',
            icon: Plus,
            color: 'bg-blue-600 hover:bg-blue-700',
            onClick: () => navigate('/social/groups/create'),
            enabled: dashboard.quickActions.canCreateGroup
        },
        {
            title: 'Znajdź przyjaciół',
            description: 'Wyszukaj nowych użytkowników',
            icon: UserPlus,
            color: 'bg-green-600 hover:bg-green-700',
            onClick: () => navigate('/social/friends/search')
        },
        {
            title: 'Wiadomości',
            description: `${dashboard.stats.unreadMessages} nieprzeczytanych`,
            icon: MessageCircle,
            color: 'bg-orange-600 hover:bg-orange-700',
            onClick: () => navigate('/social/messages'),
            highlight: dashboard.quickActions.hasUnreadMessages
        },
        {
            title: 'Dołącz do grupy',
            description: 'Użyj kodu zaproszenia',
            icon: Hash,
            color: 'bg-purple-600 hover:bg-purple-700',
            onClick: () => navigate('/social/groups/join')
        }
    ];

    return (
        <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Szybkie akcje</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {actions.map((action, index) => {
                    const Icon = action.icon;
                    return (
                        <button
                            key={index}
                            onClick={action.onClick}
                            disabled={action.enabled === false}
                            className={`
                ${action.color} text-white p-4 rounded-lg transition-all duration-200 
                transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed 
                disabled:hover:scale-100 relative
                ${action.highlight ? 'ring-2 ring-yellow-400 ring-opacity-50' : ''}
              `}
                        >
                            {action.highlight && (
                                <div className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full animate-pulse"></div>
                            )}
                            <div className="flex items-center gap-3">
                                <Icon className="h-6 w-6" />
                                <div className="text-left">
                                    <h3 className="font-medium">{action.title}</h3>
                                    <p className="text-sm opacity-90">{action.description}</p>
                                </div>
                            </div>
                        </button>
                    );
                })}
            </div>
        </div>
    );
};

export default QuickActions;