import React, { useState, useRef, useEffect } from 'react';
import { PrivateMessage } from '../../../types/social';
import {
    ArrowLeft,
    Send,
    Smile,
    Paperclip,
    MoreVertical,
    Phone,
    Video,
    Info,
    User,
    Clock
} from 'lucide-react';

interface ChatWindowProps {
    userId: number;
    userName: string;
    messages: PrivateMessage[];
    onSendMessage: (userId: number, content: string) => Promise<void>;
    onBack: () => void;
}

const ChatWindow: React.FC<ChatWindowProps> = ({
                                                   userId,
                                                   userName,
                                                   messages,
                                                   onSendMessage,
                                                   onBack,
                                               }) => {
    const [newMessage, setNewMessage] = useState('');
    const [sending, setSending] = useState(false);
    const messagesEndRef = useRef<HTMLDivElement>(null);
    const currentUser = 'SzymonBartkowiak43';

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);


    const handleSendMessage = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!newMessage.trim() || sending) return;

        setSending(true);
        try {
            await onSendMessage(userId, newMessage.trim());
            setNewMessage('');
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

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(yesterday.getDate() - 1);

        if (date.toDateString() === today.toDateString()) {
            return 'Dzisiaj';
        } else if (date.toDateString() === yesterday.toDateString()) {
            return 'Wczoraj';
        } else {
            return date.toLocaleDateString('pl-PL');
        }
    };

    const groupedMessages = messages.reduce((groups, message) => {
        const date = formatDate(message.createdAt);
        if (!groups[date]) {
            groups[date] = [];
        }
        groups[date].push(message);
        return groups;
    }, {} as Record<string, PrivateMessage[]>);

    return (
        <div className="bg-white rounded-lg shadow h-[600px] flex flex-col">
            {/* Header */}
            <div className="flex items-center justify-between p-4 border-b border-gray-200">
                <div className="flex items-center gap-3">
                    <button
                        onClick={onBack}
                        className="lg:hidden p-2 hover:bg-gray-100 rounded-full transition-colors"
                    >
                        <ArrowLeft className="h-5 w-5" />
                    </button>

                    <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
            <span className="text-sm font-semibold text-blue-600">
              {userName.charAt(0).toUpperCase()}
            </span>
                    </div>

                    <div>
                        <h2 className="font-semibold text-gray-900">{userName}</h2>
                        <p className="text-sm text-green-600">Online</p>
                    </div>
                </div>

                <div className="flex items-center gap-2">
                    <button className="p-2 hover:bg-gray-100 rounded-full transition-colors">
                        <Phone className="h-4 w-4 text-gray-600" />
                    </button>
                    <button className="p-2 hover:bg-gray-100 rounded-full transition-colors">
                        <Video className="h-4 w-4 text-gray-600" />
                    </button>
                    <button className="p-2 hover:bg-gray-100 rounded-full transition-colors">
                        <Info className="h-4 w-4 text-gray-600" />
                    </button>
                    <button className="p-2 hover:bg-gray-100 rounded-full transition-colors">
                        <MoreVertical className="h-4 w-4 text-gray-600" />
                    </button>
                </div>
            </div>

            {/* Messages */}
            <div className="flex-1 overflow-y-auto p-4 space-y-4">
                {Object.entries(groupedMessages).map(([date, dayMessages]) => (
                    <div key={date}>
                        {/* Date separator */}
                        <div className="flex items-center justify-center my-4">
                            <div className="bg-gray-100 text-gray-600 text-xs px-3 py-1 rounded-full">
                                {date}
                            </div>
                        </div>

                        {/* Messages for this date */}
                        {dayMessages.map((message, index) => {
                            const isOwnMessage = message.sender.name === currentUser;
                            const showAvatar = index === 0 || dayMessages[index - 1].sender.id !== message.sender.id;

                            return (
                                <div
                                    key={message.id}
                                    className={`flex items-end gap-2 ${isOwnMessage ? 'flex-row-reverse' : ''}`}
                                >
                                    {/* Avatar */}
                                    <div className={`w-8 h-8 ${showAvatar ? 'opacity-100' : 'opacity-0'}`}>
                                        {!isOwnMessage && (
                                            <div className="w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center">
                                                <User className="h-4 w-4 text-gray-600" />
                                            </div>
                                        )}
                                    </div>

                                    {/* Message bubble */}
                                    <div
                                        className={`max-w-xs lg:max-w-md px-4 py-2 rounded-2xl ${
                                            isOwnMessage
                                                ? 'bg-blue-600 text-white rounded-br-sm'
                                                : 'bg-gray-100 text-gray-900 rounded-bl-sm'
                                        }`}
                                    >
                                        {message.sharedWordSet ? (
                                            <div className="space-y-2">
                                                <div className="flex items-center gap-2">
                                                    <Paperclip className="h-4 w-4" />
                                                    <span className="font-medium">Udostępniony zestaw słówek</span>
                                                </div>
                                                <div className="text-sm opacity-90">
                                                    {message.content}
                                                </div>
                                                <div className="bg-white bg-opacity-20 rounded-lg p-2 text-sm">
                                                    <p className="font-medium">Nazwa zestawu</p>
                                                    <p className="opacity-90">20 słówek</p>
                                                </div>
                                            </div>
                                        ) : (
                                            <p className="break-words">{message.content}</p>
                                        )}

                                        <div className="flex items-center justify-end mt-1">
                                            <div className="flex items-center gap-1">
                                                <Clock className="h-3 w-3 opacity-70" />
                                                <span className="text-xs opacity-70">
                          {formatTime(message.createdAt)}
                        </span>
                                            </div>
                                            {isOwnMessage && (
                                                <div className="ml-1">
                                                    {message.isRead ? (
                                                        <div className="text-xs opacity-70">✓✓</div>
                                                    ) : (
                                                        <div className="text-xs opacity-70">✓</div>
                                                    )}
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                ))}
                <div ref={messagesEndRef} />
            </div>

            {/* Message input */}
            <form onSubmit={handleSendMessage} className="p-4 border-t border-gray-200">
                <div className="flex items-center gap-3">
                    <button
                        type="button"
                        className="p-2 hover:bg-gray-100 rounded-full transition-colors"
                    >
                        <Paperclip className="h-5 w-5 text-gray-600" />
                    </button>

                    <div className="flex-1 relative">
                        <input
                            type="text"
                            value={newMessage}
                            onChange={(e) => setNewMessage(e.target.value)}
                            placeholder="Napisz wiadomość..."
                            className="w-full px-4 py-2 border border-gray-300 rounded-full focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            disabled={sending}
                        />
                        <button
                            type="button"
                            className="absolute right-3 top-1/2 transform -translate-y-1/2 p-1 hover:bg-gray-100 rounded-full transition-colors"
                        >
                            <Smile className="h-4 w-4 text-gray-600" />
                        </button>
                    </div>

                    <button
                        type="submit"
                        disabled={!newMessage.trim() || sending}
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

export default ChatWindow;