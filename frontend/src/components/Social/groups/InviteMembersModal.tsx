import React, { useState } from 'react';
import { StudyGroup, User } from '../../../types/social';
import { X, UserPlus, Copy, Users, Mail, Hash } from 'lucide-react';

interface InviteMembersModalProps {
    group: StudyGroup;
    friends: User[];
    onClose: () => void;
}

const InviteMembersModal: React.FC<InviteMembersModalProps> = ({
                                                                   group,
                                                                   friends,
                                                                   onClose
                                                               }) => {
    const [copiedCode, setCopiedCode] = useState(false);
    const [copiedLink, setCopiedLink] = useState(false);
    const [selectedFriends, setSelectedFriends] = useState<number[]>([]);
    const [invitingFriends, setInvitingFriends] = useState(false);

    const handleCopyCode = async () => {
        try {
            await navigator.clipboard.writeText(group.inviteCode);
            setCopiedCode(true);
            setTimeout(() => setCopiedCode(false), 2000);
        } catch (error) {
            console.error('Failed to copy code:', error);
        }
    };

    const handleCopyLink = async () => {
        try {
            const inviteLink = `${window.location.origin}/social/groups/join?code=${group.inviteCode}`;
            await navigator.clipboard.writeText(inviteLink);
            setCopiedLink(true);
            setTimeout(() => setCopiedLink(false), 2000);
        } catch (error) {
            console.error('Failed to copy link:', error);
        }
    };

    const handleFriendToggle = (friendId: number) => {
        setSelectedFriends(prev =>
            prev.includes(friendId)
                ? prev.filter(id => id !== friendId)
                : [...prev, friendId]
        );
    };

    const handleInviteFriends = async () => {
        if (selectedFriends.length === 0) return;

        setInvitingFriends(true);
        try {
            // Mock API call - w prawdziwej aplikacji wysłałbyś zaproszenia
            await new Promise(resolve => setTimeout(resolve, 1000));

            // Reset selection
            setSelectedFriends([]);

            // Show success message
            alert(`Wysłano zaproszenia do ${selectedFriends.length} przyjaciół!`);
        } catch (error) {
            console.error('Failed to invite friends:', error);
        } finally {
            setInvitingFriends(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-gray-200">
                    <div className="flex items-center gap-3">
                        <Hash className="h-6 w-6 text-blue-600" />
                        <div>
                            <h2 className="text-xl font-semibold text-gray-900">Zaproś członków</h2>
                            <p className="text-sm text-gray-600">do grupy "{group.name}"</p>
                        </div>
                    </div>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-600 transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                {/* Content */}
                <div className="flex-1 overflow-y-auto p-6 space-y-6">
                    {/* Invite Code Section */}
                    <div className="bg-blue-50 rounded-lg p-6">
                        <h3 className="text-lg font-medium text-gray-900 mb-4 flex items-center gap-2">
                            <Copy className="h-5 w-5" />
                            Kod zaproszenia
                        </h3>

                        <div className="bg-white rounded-lg p-4 mb-4">
                            <div className="flex items-center justify-between">
                                <div className="font-mono text-2xl font-bold text-gray-900 tracking-widest">
                                    {group.inviteCode}
                                </div>
                                <button
                                    onClick={handleCopyCode}
                                    className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
                                >
                                    <Copy className="h-4 w-4" />
                                    {copiedCode ? 'Skopiowano!' : 'Kopiuj kod'}
                                </button>
                            </div>
                        </div>

                        <div className="bg-white rounded-lg p-4">
                            <div className="flex items-center justify-between">
                                <div className="flex-1">
                                    <p className="text-sm font-medium text-gray-900 mb-1">Link zaproszenia</p>
                                    <p className="text-xs text-gray-600 break-all">
                                        {window.location.origin}/social/groups/join?code={group.inviteCode}
                                    </p>
                                </div>
                                <button
                                    onClick={handleCopyLink}
                                    className="ml-4 flex items-center gap-1 text-blue-600 hover:text-blue-700 text-sm"
                                >
                                    <Copy className="h-3 w-3" />
                                    {copiedLink ? 'Skopiowano!' : 'Kopiuj'}
                                </button>
                            </div>
                        </div>

                        <p className="text-sm text-blue-700 mt-3">
                            💡 Udostępnij kod lub link, aby zaprosić nowych członków do grupy
                        </p>
                    </div>

                    {/* Invite Friends Section */}
                    {friends.length > 0 && (
                        <div>
                            <h3 className="text-lg font-medium text-gray-900 mb-4 flex items-center gap-2">
                                <Users className="h-5 w-5" />
                                Zaproś przyjaciół ({selectedFriends.length} wybranych)
                            </h3>

                            <div className="max-h-64 overflow-y-auto border border-gray-200 rounded-lg">
                                {friends.map(friend => (
                                    <label
                                        key={friend.id}
                                        className="flex items-center gap-3 p-3 hover:bg-gray-50 cursor-pointer border-b border-gray-100 last:border-b-0"
                                    >
                                        <input
                                            type="checkbox"
                                            checked={selectedFriends.includes(friend.id)}
                                            onChange={() => handleFriendToggle(friend.id)}
                                            className="w-4 h-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                                        />

                                        <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                      <span className="text-sm font-semibold text-blue-600">
                        {friend.name.charAt(0).toUpperCase()}
                      </span>
                                        </div>

                                        <div className="flex-1">
                                            <p className="font-medium text-gray-900">{friend.name}</p>
                                            <div className="flex items-center gap-1 text-sm text-gray-500">
                                                <Mail className="h-3 w-3" />
                                                <span>{friend.email}</span>
                                            </div>
                                        </div>
                                    </label>
                                ))}
                            </div>

                            {selectedFriends.length > 0 && (
                                <div className="mt-4 flex items-center justify-between bg-green-50 rounded-lg p-3">
                                    <p className="text-sm text-green-700">
                                        Wybrano {selectedFriends.length} {selectedFriends.length === 1 ? 'przyjaciela' : 'przyjaciół'}
                                    </p>
                                    <button
                                        onClick={handleInviteFriends}
                                        disabled={invitingFriends}
                                        className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition-colors disabled:opacity-50 flex items-center gap-2 text-sm"
                                    >
                                        {invitingFriends ? (
                                            <>
                                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                                                Wysyłanie...
                                            </>
                                        ) : (
                                            <>
                                                <UserPlus className="h-4 w-4" />
                                                Wyślij zaproszenia
                                            </>
                                        )}
                                    </button>
                                </div>
                            )}
                        </div>
                    )}

                    {/* No Friends Message */}
                    {friends.length === 0 && (
                        <div className="text-center py-8 bg-gray-50 rounded-lg">
                            <Users className="h-12 w-12 text-gray-300 mx-auto mb-3" />
                            <p className="text-gray-600 mb-2">Nie masz jeszcze przyjaciół</p>
                            <p className="text-sm text-gray-500">
                                Dodaj przyjaciół, aby móc wysyłać im zaproszenia do grup
                            </p>
                        </div>
                    )}

                    {/* Tips */}
                    <div className="bg-yellow-50 rounded-lg p-4">
                        <h4 className="font-medium text-yellow-900 mb-2">💡 Wskazówki</h4>
                        <ul className="text-sm text-yellow-800 space-y-1">
                            <li>• Kod zaproszenia jest unikalny dla tej grupy</li>
                            <li>• Możesz udostępnić kod przez różne kanały komunikacji</li>
                            <li>• Zaproszenia do przyjaciół zostaną wysłane jako wiadomości</li>
                            <li>• Maksymalna liczba członków: {group.maxMembers}</li>
                        </ul>
                    </div>
                </div>

                {/* Footer */}
                <div className="p-6 border-t border-gray-200">
                    <button
                        onClick={onClose}
                        className="w-full bg-gray-100 text-gray-700 py-3 rounded-lg hover:bg-gray-200 transition-colors font-medium"
                    >
                        Zamknij
                    </button>
                </div>
            </div>
        </div>
    );
};

export default InviteMembersModal;