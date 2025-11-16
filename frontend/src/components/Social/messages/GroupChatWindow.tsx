import React, { useState, useRef, useEffect } from 'react';
import {
    ArrowLeft,
    Send,
    Hash,
    Users,
    Settings,
    MoreVertical,
    Smile,
    Paperclip,
    Phone,
    Video
} from 'lucide-react';

interface GroupChatWindowProps {
    groupId: number;
    groupName: string;
    onSendMessage: (groupId: number, content: string) => Promise<void>;
    onBack: () => void;
}

const GroupChatWindow: React.FC<GroupChatWindowProps> = ({
                                                             groupId,
                                                             groupName,
                                                             onSendMessage,
                                                             onBack
                                                         }) => {
    const [message, setMessage] = useState('');
    const [sending, setSending] = useState(false);
    const messagesEndRef = useRef<HTMLDivElement>(null);

    const mockMessages = [
        {
            id: 1,
            sender: { id: 1, name: 'Anna Kowalska' },
            content: 'Cześć wszystkim! Jak tam nauka?',
            createdAt: '2024-08-14T10:30:00Z',
            isCurrentUser: false
        },
        {
            id: 2,
            sender: { id: 2, name: 'SzymonBartkowiak43' },
            content: 'Świetnie! Właśnie skończyłem zestaw z Business English',
            createdAt: '2024-08-14T10:32:00Z',
            isCurrentUser: true
        },
        {
            id: 3,
            sender: { id: 3, name: 'Piotr Nowak' },
            content: 'Super! Mogę prosić o udostępnienie tego zestawu?',
            createdAt: '2024-08-14T10:35:00Z',
            isCurrentUser: false
        }
    ];

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    useEffect(() => {
        scrollToBottom();
    }, [mockMessages]);

    const handleSend = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!message.trim() || sending) return;

        setSending(true);
        try {
            await onSendMessage(groupId, message.trim());
            setMessage('');
        } finally {
            setSending(false);
        }
    };

    const formatTime = (dateString: string) => {
        return new Date(dateString).toLocaleTimeString('pl-PL', {
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    return (
        <div className="glass-box-flat h-[600px] flex flex-col">
            <div className="flex items-center justify-between p-4 border-b border-white/20">
                <div className="flex items-center gap-3">
                    <button
                        onClick={onBack}
                        className="lg:hidden p-2 hover:bg-white/10 rounded-full transition-colors"
                    >
                        <ArrowLeft className="h-5 w-5 text-white" />
                    </button>

                    <div className="w-10 h-10 bg-green-500/30 rounded-full flex items-center justify-center">
                        <Hash className="h-6 w-6 text-green-200" />
                    </div>

                    <div>
                        <h2 className="font-semibold text-white">{groupName}</h2>
                        <p className="text-sm text-green-300">3 członków online</p>
                    </div>
                </div>

                <div className="flex items-center gap-2">
                    <button className="p-2 hover:bg-white/10 rounded-full transition-colors">
                        <Phone className="h-4 w-4 text-gray-300" />
                    </button>
                    <button className="p-2 hover:bg-white/10 rounded-full transition-colors">
                        <Video className="h-4 w-4 text-gray-300" />
                    </button>
                    <button className="p-2 hover:bg-white/10 rounded-full transition-colors">
                        <Users className="h-4 w-4 text-gray-300" />
                    </button>
                    <button className="p-2 hover:bg-white/10 rounded-full transition-colors">
                        <Settings className="h-4 w-4 text-gray-300" />
                    </button>
                    <button className="p-2 hover:bg-white/10 rounded-full transition-colors">
                        <MoreVertical className="h-4 w-4 text-gray-300" />
                    </button>
                </div>
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-4">
                <div className="text-center py-4">
                    <div className="w-16 h-16 bg-green-500/30 rounded-full flex items-center justify-center mx-auto mb-3">
                        <Hash className="h-8 w-8 text-green-200" />
                    </div>
                    <h3 className="font-semibold text-white mb-1">Witaj w grupie {groupName}! 🎉</h3>
                    <p className="text-sm text-gray-300">
                        To jest początek konwersacji grupowej.
                    </p>
                </div>

                {mockMessages.map((msg, index) => {
                    const showAvatar = index === 0 || mockMessages[index - 1].sender.id !== msg.sender.id;

                    return (
                        <div
                            key={msg.id}
                            className={`flex items-end gap-2 ${msg.isCurrentUser ? 'flex-row-reverse' : ''}`}
                        >
                            {/* Avatar */}
                            <div className={`w-8 h-8 ${showAvatar ? 'opacity-100' : 'opacity-0'}`}>
                                {!msg.isCurrentUser && (
                                    <div className="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
                                        <span className="text-xs font-medium text-gray-200">
                                          {msg.sender.name.charAt(0).toUpperCase()}
                                        </span>
                                    </div>
                                )}
                            </div>

                            {/* Dymek */}
                            <div className="max-w-xs lg:max-w-md">
                                {/* Nadawca */}
                                {showAvatar && !msg.isCurrentUser && (
                                    <p className="text-xs text-gray-400 mb-1 ml-3">{msg.sender.name}</p>
                                )}

                                <div
                                    className={`px-4 py-2 rounded-2xl ${
                                        msg.isCurrentUser
                                            ? 'bg-blue-600 text-white rounded-br-sm'
                                            : 'bg-white/20 text-white rounded-bl-sm'
                                    }`}
                                >
                                    <p className="break-words">{msg.content}</p>
                                    <div className="flex items-center justify-end mt-1">
                                        <span className="text-xs opacity-70">
                                          {formatTime(msg.createdAt)}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    );
                })}
                <div ref={messagesEndRef} />
            </div>

            {/* Input */}
            <form onSubmit={handleSend} className="p-4 border-t border-white/20">
                <div className="flex items-center gap-3">
                    <button
                        type="button"
                        className="p-2 hover:bg-white/10 rounded-full transition-colors"
                    >
                        <Paperclip className="h-5 w-5 text-gray-300" />
                    </button>

                    <div className="flex-1 relative">
                        <input
                            type="text"
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            placeholder={`Napisz wiadomość do ${groupName}...`}
                            className="w-full px-4 py-2 bg-white/10 border border-white/30 rounded-full text-white placeholder-gray-400 focus:ring-2 focus:ring-white/50 focus:border-transparent"
                            disabled={sending}
                        />
                        <button
                            type="button"
                            className="absolute right-3 top-1/2 transform -translate-y-1/2 p-1 hover:bg-white/10 rounded-full transition-colors"
                        >
                            <Smile className="h-4 w-4 text-gray-300" />
                        </button>
                    </div>

                    <button
                        type="submit"
                        disabled={!message.trim() || sending}
                        className="bg-blue-600 text-white p-2 rounded-full hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {sending ? (
                            <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                        ) : (
                            <Send className="h-5 w-5" />
                        )}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default GroupChatWindow;