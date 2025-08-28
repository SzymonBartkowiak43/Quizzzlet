import React from 'react';
import { Friendship, GroupInfo, MessagingInfo } from '../../../types/social';
import { Users, MessageCircle, Calendar, Clock } from 'lucide-react';

interface RecentActivityProps {
    friendshipInfo: Friendship;
    groupInfo: GroupInfo;
    messagingInfo: MessagingInfo;
}

const RecentActivity: React.FC<RecentActivityProps> = ({
                                                           friendshipInfo,
                                                           groupInfo,
                                                           messagingInfo
                                                       }) => {
    // Mock recent activity data - w prawdziwej aplikacji to by przyszło z API
    const activities = [
        {
            type: 'friend',
            text: `Zaproszenie do przyjaźni od ${friendshipInfo.requester?.name || 'użytkownika'}`,
            time: '2 godziny temu',
            icon: Users,
            color: 'text-blue-600 bg-blue-50'
        },
        {
            type: 'group',
            text: `Dołączyłeś do grupy "${groupInfo.memberGroups[0]?.name || 'Nowa grupa'}"`,
            time: '1 dzień temu',
            icon: Users,
            color: 'text-green-600 bg-green-50'
        },
        {
            type: 'message',
            text: `Nowa wiadomość od ${messagingInfo.unreadMessages[0]?.sender?.name || 'użytkownika'}`,
            time: '3 godziny temu',
            icon: MessageCircle,
            color: 'text-orange-600 bg-orange-50'
        }
    ].filter(activity => {
        // Filter out activities without data
        if (activity.type === 'friend' && friendshipInfo.requester.id === 0) return false;
        if (activity.type === 'group' && groupInfo.memberGroups.length === 0) return false;
        if (activity.type === 'message' && messagingInfo.unreadMessages.length === 0) return false;
        return true;
    });

    if (activities.length === 0) {
        return (
            <div className="bg-white rounded-lg shadow p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">Ostatnia aktywność</h2>
                <div className="text-center py-8">
                    <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                    <p className="text-gray-500">Brak ostatniej aktywności</p>
                    <p className="text-sm text-gray-400 mt-1">
                        Zacznij dodawać przyjaciół lub dołączaj do grup!
                    </p>
                </div>
            </div>
        );
    }

    return (
        <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Ostatnia aktywność</h2>
            <div className="space-y-4">
                {activities.map((activity, index) => {
                    const Icon = activity.icon;
                    return (
                        <div key={index} className="flex items-start gap-3 p-3 rounded-lg hover:bg-gray-50 transition-colors">
                            <div className={`p-2 rounded-lg ${activity.color}`}>
                                <Icon className="h-4 w-4" />
                            </div>
                            <div className="flex-1">
                                <p className="text-sm text-gray-900">{activity.text}</p>
                                <div className="flex items-center gap-1 mt-1">
                                    <Clock className="h-3 w-3 text-gray-400" />
                                    <span className="text-xs text-gray-500">{activity.time}</span>
                                </div>
                            </div>
                        </div>
                    );
                })}
            </div>
            <button className="mt-4 w-full text-center text-sm text-blue-600 hover:text-blue-700 font-medium">
                Zobacz całą historię →
            </button>
        </div>
    );
};

export default RecentActivity;