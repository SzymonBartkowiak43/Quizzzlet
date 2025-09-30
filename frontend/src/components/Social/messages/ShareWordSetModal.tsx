import React, { useState } from 'react';
import { User, StudyGroup } from '../../../types/social';
import { X, Share, Users, Hash, BookOpen } from 'lucide-react';

interface ShareWordSetModalProps {
    friends: User[];
    groups: StudyGroup[];
    onClose: () => void;
    onSharePrivate: (recipientId: number, wordSetId: number, message: string) => Promise<void>;
    onShareGroup: (groupId: number, wordSetId: number, message: string) => Promise<void>;
}

const ShareWordSetModal: React.FC<ShareWordSetModalProps> = ({
                                                                 friends,
                                                                 groups,
                                                                 onClose,
                                                                 onSharePrivate,
                                                                 onShareGroup
                                                             }) => {
    const [shareType, setShareType] = useState<'friend' | 'group'>('friend');
    const [selectedId, setSelectedId] = useState<number | null>(null);
    const [message, setMessage] = useState('');
    const [selectedWordSet, setSelectedWordSet] = useState<number | null>(null);
    const [sharing, setSharing] = useState(false);

    const mockWordSets = [
        { id: 1, name: 'Business English Basics', wordCount: 25 },
        { id: 2, name: 'Travel Vocabulary', wordCount: 30 },
        { id: 3, name: 'Daily Conversations', wordCount: 20 },
        { id: 4, name: 'Advanced Grammar', wordCount: 40 }
    ];

    const handleShare = async () => {
        if (!selectedId || !message.trim() || !selectedWordSet) return;

        setSharing(true);
        try {
            if (shareType === 'friend') {
                await onSharePrivate(selectedId, selectedWordSet, message);
            } else {
                await onShareGroup(selectedId, selectedWordSet, message);
            }
            onClose();
        } finally {
            setSharing(false);
        }
    };

    const getSelectedWordSet = () => {
        return mockWordSets.find(ws => ws.id === selectedWordSet);
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-gray-200">
                    <div className="flex items-center gap-3">
                        <BookOpen className="h-6 w-6 text-green-600" />
                        <div>
                            <h2 className="text-xl font-semibold text-gray-900">Udostępnij zestaw słówek</h2>
                            <p className="text-sm text-gray-600">Podziel się swoimi zestawami z przyjaciółmi lub grupami</p>
                        </div>
                    </div>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-600 transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                <div className="p-6 space-y-6">
                    {/* Word Set Selection */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-3">
                            Wybierz zestaw słówek do udostępnienia
                        </label>
                        <div className="grid grid-cols-1 gap-3">
                            {mockWordSets.map(wordSet => (
                                <label
                                    key={wordSet.id}
                                    className={`flex items-center gap-3 p-3 border-2 rounded-lg cursor-pointer transition-colors ${
                                        selectedWordSet === wordSet.id
                                            ? 'border-blue-500 bg-blue-50'
                                            : 'border-gray-200 hover:bg-gray-50'
                                    }`}
                                >
                                    <input
                                        type="radio"
                                        name="wordset"
                                        value={wordSet.id}
                                        checked={selectedWordSet === wordSet.id}
                                        onChange={() => setSelectedWordSet(wordSet.id)}
                                        className="text-blue-600 focus:ring-blue-500"
                                    />

                                    <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                                        <BookOpen className="h-5 w-5 text-green-600" />
                                    </div>

                                    <div className="flex-1">
                                        <h4 className="font-medium text-gray-900">{wordSet.name}</h4>
                                        <p className="text-sm text-gray-500">{wordSet.wordCount} słówek</p>
                                    </div>
                                </label>
                            ))}
                        </div>
                    </div>

                    {/* Share Type Selection */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-3">
                            Udostępnij
                        </label>
                        <div className="grid grid-cols-2 gap-4">
                            <button
                                onClick={() => {
                                    setShareType('friend');
                                    setSelectedId(null);
                                }}
                                className={`flex items-center justify-center gap-2 p-4 rounded-lg border-2 transition-colors ${
                                    shareType === 'friend'
                                        ? 'border-blue-500 bg-blue-50'
                                        : 'border-gray-200 hover:bg-gray-50'
                                }`}
                            >
                                <Users className="h-6 w-6 text-blue-600" />
                                <div className="text-center">
                                    <div className="font-medium text-gray-900">Przyjacielowi</div>
                                    <div className="text-sm text-gray-600">{friends.length} dostępnych</div>
                                </div>
                            </button>

                            <button
                                onClick={() => {
                                    setShareType('group');
                                    setSelectedId(null);
                                }}
                                className={`flex items-center justify-center gap-2 p-4 rounded-lg border-2 transition-colors ${
                                    shareType === 'group'
                                        ? 'border-blue-500 bg-blue-50'
                                        : 'border-gray-200 hover:bg-gray-50'
                                }`}
                            >
                                <Hash className="h-6 w-6 text-blue-600" />
                                <div className="text-center">
                                    <div className="font-medium text-gray-900">Grupie</div>
                                    <div className="text-sm text-gray-600">{groups.length} dostępnych</div>
                                </div>
                            </button>
                        </div>
                    </div>

                    {/* Recipients Selection */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-3">
                            {shareType === 'friend' ? 'Wybierz przyjaciela' : 'Wybierz grupę'}
                        </label>

                        {shareType === 'friend' ? (
                            <div className="max-h-48 overflow-y-auto border border-gray-200 rounded-lg">
                                {friends.length === 0 ? (
                                    <div className="p-6 text-center text-gray-500">
                                        <Users className="h-8 w-8 mx-auto mb-2 text-gray-300" />
                                        <p>Nie masz jeszcze przyjaciół</p>
                                    </div>
                                ) : (
                                    friends.map(friend => (
                                        <label
                                            key={friend.id}
                                            className={`flex items-center gap-3 p-3 cursor-pointer border-b border-gray-100 last:border-b-0 transition-colors ${
                                                selectedId === friend.id ? 'bg-blue-50' : 'hover:bg-gray-50'
                                            }`}
                                        >
                                            <input
                                                type="radio"
                                                name="recipient"
                                                value={friend.id}
                                                checked={selectedId === friend.id}
                                                onChange={() => setSelectedId(friend.id)}
                                                className="text-blue-600 focus:ring-blue-500"
                                            />

                                            <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                        <span className="text-sm font-semibold text-blue-600">
                          {friend.name.charAt(0).toUpperCase()}
                        </span>
                                            </div>

                                            <div>
                                                <p className="font-medium text-gray-900">{friend.name}</p>
                                                <p className="text-sm text-gray-500">{friend.email}</p>
                                            </div>
                                        </label>
                                    ))
                                )}
                            </div>
                        ) : (
                            <div className="max-h-48 overflow-y-auto border border-gray-200 rounded-lg">
                                {groups.length === 0 ? (
                                    <div className="p-6 text-center text-gray-500">
                                        <Hash className="h-8 w-8 mx-auto mb-2 text-gray-300" />
                                        <p>Nie należysz do żadnych grup</p>
                                    </div>
                                ) : (
                                    groups.map(group => (
                                        <label
                                            key={group.id}
                                            className={`flex items-center gap-3 p-3 cursor-pointer border-b border-gray-100 last:border-b-0 transition-colors ${
                                                selectedId === group.id ? 'bg-blue-50' : 'hover:bg-gray-50'
                                            }`}
                                        >
                                            <input
                                                type="radio"
                                                name="recipient"
                                                value={group.id}
                                                checked={selectedId === group.id}
                                                onChange={() => setSelectedId(group.id)}
                                                className="text-blue-600 focus:ring-blue-500"
                                            />

                                            <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                                                <Hash className="h-5 w-5 text-green-600" />
                                            </div>

                                            <div>
                                                <p className="font-medium text-gray-900">{group.name}</p>
                                                <p className="text-sm text-gray-500">
                                                    {group.memberCount} członków • {group.isPrivate ? 'Prywatna' : 'Publiczna'}
                                                </p>
                                            </div>
                                        </label>
                                    ))
                                )}
                            </div>
                        )}
                    </div>

                    {/* Message */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Wiadomość
                        </label>
                        <textarea
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            placeholder="Dodaj wiadomość do udostępnianego zestawu słówek..."
                            rows={3}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                            maxLength={500}
                        />
                        <p className="text-sm text-gray-500 mt-1">{message.length}/500 znaków</p>
                    </div>

                    {/* Preview */}
                    {selectedWordSet && selectedId && message && (
                        <div className="bg-gray-50 rounded-lg p-4">
                            <h4 className="font-medium text-gray-900 mb-2">Podgląd wiadomości</h4>
                            <div className="bg-white rounded-lg p-3 border">
                                <div className="flex items-center gap-2 mb-2">
                                    <BookOpen className="h-4 w-4 text-green-600" />
                                    <span className="font-medium text-green-600">Udostępniony zestaw słówek</span>
                                </div>
                                <p className="text-sm text-gray-600 mb-2">{message}</p>
                                <div className="bg-green-50 rounded p-2">
                                    <p className="font-medium text-gray-900">{getSelectedWordSet()?.name}</p>
                                    <p className="text-sm text-gray-600">{getSelectedWordSet()?.wordCount} słówek</p>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                {/* Footer */}
                <div className="flex justify-end gap-3 p-6 border-t border-gray-200">
                    <button
                        onClick={onClose}
                        className="bg-gray-100 text-gray-700 px-6 py-2 rounded-lg hover:bg-gray-200 transition-colors"
                    >
                        Anuluj
                    </button>
                    <button
                        onClick={handleShare}
                        disabled={!selectedId || !message.trim() || !selectedWordSet || sharing}
                        className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                    >
                        {sharing ? (
                            <>
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                                Udostępnianie...
                            </>
                        ) : (
                            <>
                                <Share className="h-4 w-4" />
                                Udostępnij
                            </>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ShareWordSetModal;