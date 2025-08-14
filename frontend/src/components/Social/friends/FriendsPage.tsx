import React, { useState } from 'react';
import { useFriendships } from '../../../hooks/social/userFriendships';
import { Users, UserPlus, Search, Filter, RefreshCw } from 'lucide-react';
import LoadingSpinner from '../../Shared/LoadingSpinner';
import FriendsList from './FriendsList';
import PendingRequests from './PendingRequests';
import SentRequests from './SentRequests';
import SuggestedFriends from './SuggestedFriends';
import AddFriendModal from './AddFriendModal';
import SearchFriends from './SearchFriends';

type TabType = 'friends' | 'pending' | 'sent' | 'suggested';

const FriendsPage: React.FC = () => {
    const {
        friendshipInfo,
        loading,
        error,
        sendFriendRequest,
        acceptFriendRequest,
        declineFriendRequest,
        removeFriend,
        blockUser,
        refreshFriendships
    } = useFriendships();

    const [activeTab, setActiveTab] = useState<TabType>('friends');
    const [showAddModal, setShowAddModal] = useState(false);
    const [showSearch, setShowSearch] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [refreshing, setRefreshing] = useState(false);

    const handleRefresh = async () => {
        setRefreshing(true);
        await refreshFriendships();
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

    if (!friendshipInfo) return null;

    const tabs = [
        {
            id: 'friends' as TabType,
            label: 'Przyjaciele',
            count: friendshipInfo.friendsCount,
            icon: Users
        },
        {
            id: 'pending' as TabType,
            label: 'Oczekujące',
            count: friendshipInfo.pendingRequestsCount,
            icon: UserPlus,
            highlight: friendshipInfo.pendingRequestsCount > 0
        },
        {
            id: 'sent' as TabType,
            label: 'Wysłane',
            count: friendshipInfo.sentRequestsCount,
            icon: UserPlus
        },
        {
            id: 'suggested' as TabType,
            label: 'Sugerowane',
            count: friendshipInfo.suggestedFriends.length,
            icon: Users
        }
    ];

    const filteredFriends = friendshipInfo.friends.filter(friend =>
        friend.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        friend.email.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="max-w-6xl mx-auto p-6">
            {/* Header */}
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Przyjaciele</h1>
                    <p className="text-gray-600 mt-1">
                        Zarządzaj swoimi kontaktami i znajdź nowych przyjaciół
                    </p>
                </div>

                <div className="flex gap-3">
                    <button
                        onClick={() => setShowSearch(!showSearch)}
                        className="flex items-center gap-2 bg-white border border-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-50 transition-colors"
                    >
                        <Search className="h-4 w-4" />
                        Szukaj
                    </button>

                    <button
                        onClick={() => setShowAddModal(true)}
                        className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                    >
                        <UserPlus className="h-4 w-4" />
                        Dodaj przyjaciela
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
                            placeholder="Wyszukaj wśród przyjaciół..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        />
                    </div>
                </div>
            )}

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
                    relative flex items-center gap-2 py-4 px-1 border-b-2 font-medium text-sm transition-colors
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
                      ${tab.highlight ? 'bg-red-100 text-red-600 animate-pulse' : ''}
                    `}>
                      {tab.count}
                    </span>
                                    )}
                                    {tab.highlight && (
                                        <div className="absolute -top-1 -right-1 w-2 h-2 bg-red-500 rounded-full"></div>
                                    )}
                                </button>
                            );
                        })}
                    </nav>
                </div>

                {/* Tab Content */}
                <div className="p-6">
                    {activeTab === 'friends' && (
                        <FriendsList
                            friends={searchTerm ? filteredFriends : friendshipInfo.friends}
                            onRemoveFriend={removeFriend}
                            onBlockUser={blockUser}
                            searchTerm={searchTerm}
                        />
                    )}

                    {activeTab === 'pending' && (
                        <PendingRequests
                            requests={friendshipInfo.pendingRequests}
                            onAccept={acceptFriendRequest}
                            onDecline={declineFriendRequest}
                        />
                    )}

                    {activeTab === 'sent' && (
                        <SentRequests
                            requests={friendshipInfo.sentRequests}
                        />
                    )}

                    {activeTab === 'suggested' && (
                        <SuggestedFriends
                            suggestedFriends={friendshipInfo.suggestedFriends}
                            onSendRequest={sendFriendRequest}
                        />
                    )}
                </div>
            </div>

            {/* Modals */}
            {showAddModal && (
                <AddFriendModal
                    onClose={() => setShowAddModal(false)}
                    onSendRequest={sendFriendRequest}
                />
            )}

            {showSearch && (
                <SearchFriends onClose={() => setShowSearch(false)} />
            )}
        </div>
    );
};

export default FriendsPage;