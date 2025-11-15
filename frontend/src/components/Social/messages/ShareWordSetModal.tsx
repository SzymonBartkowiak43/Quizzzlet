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
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="glass-box-flat w-full max-w-2xl max-h-[90vh] flex flex-col">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-white/20">
                    <div className="flex items-center gap-3">
                        <BookOpen className="h-6 w-6 text-green-300" />
                        <div>
                            <h2 className="text-xl font-semibold text-white">Udostępnij zestaw słówek</h2>
                            <p className="text-sm text-gray-300">Podziel się swoimi zestawami</p>
                        </div>
                    </div>
                    <button
                        onClick={onClose}
                        className="text-gray-300 hover:text-white transition-colors"
                    >
                        <X className="h-6 w-6" />
                    </button>
                </div>

                <div className="p-6 space-y-6 overflow-y-auto">
                    {/* Wybór zestawu */}
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-3">
                            Wybierz zestaw słówek
                        </label>
                        <div className="grid grid-cols-1 gap-3">
                            {mockWordSets.map(wordSet => (
                                <label
                                    key={wordSet.id}
                                    className={`flex items-center gap-3 p-3 border-2 rounded-lg cursor-pointer transition-colors ${
                                        selectedWordSet === wordSet.id
                                            ? 'border-blue-400 bg-white/20'
                                            : 'border-white/20 hover:bg-white/10'
                                    }`}
                                >
                                    <input
                                        type="radio"
                                        name="wordset"
                                        value={wordSet.id}
                                        checked={selectedWordSet === wordSet.id}
                                        onChange={() => setSelectedWordSet(wordSet.id)}
                                        className="text-blue-500 focus:ring-blue-400"
                                    />
                                    <div className="w-10 h-10 bg-green-500/30 rounded-lg flex items-center justify-center">
                                        <BookOpen className="h-5 w-5 text-green-200" />
                                    </div>
                                    <div className="flex-1">
                                        <h4 className="font-medium text-white">{wordSet.name}</h4>
                                        <p className="text-sm text-gray-300">{wordSet.wordCount} słówek</p>
                                    </div>
                                </label>
                            ))}
                        </div>
                    </div>

                    {/* Wybór typu */}
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-3">
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
                                        ? 'border-blue-400 bg-white/20'
                                        : 'border-white/20 hover:bg-white/10'
                                }`}
                            >
                                <Users className="h-6 w-6 text-blue-300" />
                                <div className="text-center">
                                    <div className="font-medium text-white">Przyjacielowi</div>
                                    <div className="text-sm text-gray-300">{friends.length} dostępnych</div>
                                </div>
                            </button>
                            <button
                                onClick={() => {
                                    setShareType('group');
                                    setSelectedId(null);
                                }}
                                className={`flex items-center justify-center gap-2 p-4 rounded-lg border-2 transition-colors ${
                                    shareType === 'group'
                                        ? 'border-blue-400 bg-white/20'
                                        : 'border-white/20 hover:bg-white/10'
                                }`}
                            >
                                <Hash className="h-6 w-6 text-blue-300" />
                                <div className="text-center">
                                    <div className="font-medium text-white">Grupie</div>
                                    <div className="text-sm text-gray-300">{groups.length} dostępnych</div>
                                </div>
                            </button>
                        </div>
                    </div>

                    {/* Wybór odbiorcy */}
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-3">
                            {shareType === 'friend' ? 'Wybierz przyjaciela' : 'Wybierz grupę'}
                        </label>
                        {shareType === 'friend' ? (
                            <div className="max-h-48 overflow-y-auto border border-white/20 rounded-lg">
                                {friends.length === 0 ? (
                                    <div className="p-6 text-center text-gray-400">
                                        <Users className="h-8 w-8 mx-auto mb-2 text-gray-500" />
                                        <p>Nie masz jeszcze przyjaciół</p>
                                    </div>
                                ) : (
                                    friends.map(friend => (
                                        <label
                                            key={friend.id}
                                            className={`flex items-center gap-3 p-3 cursor-pointer border-b border-white/10 last:border-b-0 transition-colors ${
                                                selectedId === friend.id ? 'bg-white/20' : 'hover:bg-white/10'
                                            }`}
                                        >
                                            <input
                                                type="radio"
                                                name="recipient"
                                                value={friend.id}
                                                checked={selectedId === friend.id}
                                                onChange={() => setSelectedId(friend.id)}
                                                className="text-blue-500 focus:ring-blue-400"
                                            />
                                            <div className="w-10 h-10 bg-blue-500/30 rounded-full flex items-center justify-center">
                                                <span className="text-sm font-semibold text-blue-100">
                                                  {friend.name.charAt(0).toUpperCase()}
                                                </span>
                                            </div>
                                            <div>
                                                <p className="font-medium text-white">{friend.name}</p>
                                                <p className="text-sm text-gray-300">{friend.email}</p>
                                            </div>
                                        </label>
                                    ))
                                )}
                            </div>
                        ) : (
                            <div className="max-h-48 overflow-y-auto border border-white/20 rounded-lg">
                                {groups.length === 0 ? (
                                    <div className="p-6 text-center text-gray-400">
                                        <Hash className="h-8 w-8 mx-auto mb-2 text-gray-500" />
                                        <p>Nie należysz do żadnych grup</p>
                                    </div>
                                ) : (
                                    groups.map(group => (
                                        <label
                                            key={group.id}
                                            className={`flex items-center gap-3 p-3 cursor-pointer border-b border-white/10 last:border-b-0 transition-colors ${
                                                selectedId === group.id ? 'bg-white/20' : 'hover:bg-white/10'
                                            }`}
                                        >
                                            <input
                                                type="radio"
                                                name="recipient"
                                                value={group.id}
                                                checked={selectedId === group.id}
                                                onChange={() => setSelectedId(group.id)}
                                                className="text-blue-500 focus:ring-blue-400"
                                            />
                                            <div className="w-10 h-10 bg-green-500/30 rounded-full flex items-center justify-center">
                                                <Hash className="h-5 w-5 text-green-200" />
                                            </div>
                                            <div>
                                                <p className="font-medium text-white">{group.name}</p>
                                                <p className="text-sm text-gray-300">
                                                    {group.memberCount} członków • {group.isPrivate ? 'Prywatna' : 'Publiczna'}
                                                </p>
                                            </div>
                                        </label>
                                    ))
                                )}
                            </div>
                        )}
                    </div>

                    {/* Wiadomość */}
                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">
                            Wiadomość
                        </label>
                        <textarea
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            placeholder="Dodaj wiadomość do udostępnianego zestawu słówek..."
                            rows={3}
                            className="w-full px-3 py-2 bg-white/10 border border-white/30 rounded-lg text-white placeholder-gray-400 focus:ring-2 focus:ring-white/50 focus:border-transparent resize-none"
                            maxLength={500}
                        />
                        <p className="text-sm text-gray-400 mt-1">{message.length}/500 znaków</p>
                    </div>

                    {/* Podgląd */}
                    {selectedWordSet && selectedId && message && (
                        <div className="bg-black/20 rounded-lg p-4">
                            <h4 className="font-medium text-white mb-2">Podgląd wiadomości</h4>
                            <div className="bg-white/10 rounded-lg p-3 border border-white/20">
                                <div className="flex items-center gap-2 mb-2">
                                    <BookOpen className="h-4 w-4 text-green-300" />
                                    <span className="font-medium text-green-300">Udostępniony zestaw słówek</span>
                                </div>
                                <p className="text-sm text-gray-200 mb-2">{message}</p>
                                <div className="bg-green-500/20 rounded p-2">
                                    <p className="font-medium text-white">{getSelectedWordSet()?.name}</p>
                                    <p className="text-sm text-gray-300">{getSelectedWordSet()?.wordCount} słówek</p>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                {/* Footer */}
                <div className="flex justify-end gap-3 p-6 border-t border-white/20">
                    <button
                        onClick={onClose}
                        className="btn-glass"
                    >
                        Anuluj
                    </button>
                    <button
                        onClick={handleShare}
                        disabled={!selectedId || !message.trim() || !selectedWordSet || sharing}
                        className="btn-primary-solid flex items-center gap-2"
                    >
                        {sharing ? (
                            <>
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
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