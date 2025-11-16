import React, { useEffect, useState } from 'react';
import socialApi from '../../../services/socialApi';
import './ChatPanel.css';
import { Send } from 'lucide-react';
import LoadingSpinner from '../../Shared/LoadingSpinner';

interface ChatPanelProps {
    friend: { id: number, name: string, email: string };
    onClose: () => void;
}

interface Message {
    id: number;
    senderId: number;
    recipientId: number;
    content: string;
    timestamp: string;
}

const ChatPanel: React.FC<ChatPanelProps> = ({ friend, onClose }) => {
    const [messages, setMessages] = useState<Message[]>([]);
    const [loading, setLoading] = useState(true);
    const [messageText, setMessageText] = useState('');
    const [sending, setSending] = useState(false);

    useEffect(() => {
        const fetchMessages = async () => {
            setLoading(true);
            try {
                const data = await socialApi.getConversation(friend.id);
                setMessages(data.messages || []);
            } catch (e) {
                setMessages([]);
            } finally {
                setLoading(false);
            }
        };
        fetchMessages();
    }, [friend.id]);

    const sendMessage = async () => {
        if (!messageText.trim()) return;
        setSending(true);
        try {
            await socialApi.sendPrivateMessage(friend.id, messageText);
            setMessageText('');
            const data = await socialApi.getConversation(friend.id);
            setMessages(data.messages || []);
        } finally {
            setSending(false);
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    };

    return (
        <div className="chatpanel-overlay" onClick={onClose}>
            <div className="chatpanel-window" onClick={(e) => e.stopPropagation()}>
                <div className="chatpanel-header">
                    <span>Rozmowa z <b>{friend.name}</b></span>
                    <button className="chatpanel-close" onClick={onClose}>×</button>
                </div>
                <div className="chatpanel-messages">
                    {loading ? (
                        <div className="chatpanel-loader">
                            <LoadingSpinner color="white" />
                        </div>
                    ) : (
                        messages.length === 0 ? (
                            <div className="chatpanel-empty">Brak wiadomości</div>
                        ) : (
                            messages.map(msg => (
                                <div key={msg.id} className={`chatpanel-message ${msg.senderId === friend.id ? 'incoming' : 'outgoing'}`}>
                                    <div className="msg-content">{msg.content}</div>
                                    <div className="msg-timestamp">{new Date(msg.timestamp).toLocaleTimeString('pl-PL', { hour: '2-digit', minute: '2-digit' })}</div>
                                </div>
                            ))
                        )
                    )}
                </div>
                <div className="chatpanel-inputs">
                    <input
                        type="text"
                        value={messageText}
                        onChange={e => setMessageText(e.target.value)}
                        onKeyDown={handleKeyPress}
                        placeholder="Napisz wiadomość..."
                        disabled={sending}
                    />
                    <button
                        onClick={sendMessage}
                        disabled={sending || !messageText.trim()}
                    >
                        {sending ? (
                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                        ) : (
                            <Send size={16} />
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ChatPanel;