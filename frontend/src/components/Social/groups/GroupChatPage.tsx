import React, { useEffect, useState } from 'react';
import socialApi from '../../../services/socialApi';
import { toast } from 'react-toastify';
import './GroupChatPage.css';
import { MessageCircle, Send, Users, Check } from 'lucide-react';
import LoadingSpinner from '../../Shared/LoadingSpinner';

interface Group {
    id: number;
    name: string;
    memberIds: number[];
    memberNames: string[];
}
interface Friend {
    id: number;
    name: string;
    email: string;
}
interface Message {
    id: number;
    senderName: string;
    content: string;
    createdAt: string;
}

const GroupChatPage: React.FC = () => {
    const [groups, setGroups] = useState<Group[]>([]);
    const [selectedGroup, setSelectedGroup] = useState<Group | null>(null);
    const [messages, setMessages] = useState<Message[]>([]);
    const [newMsg, setNewMsg] = useState('');
    const [showCreate, setShowCreate] = useState(false);
    const [groupName, setGroupName] = useState('');
    const [friends, setFriends] = useState<Friend[]>([]);
    const [selectedFriends, setSelectedFriends] = useState<number[]>([]);
    const [loadingGroups, setLoadingGroups] = useState(true);
    const [loadingMessages, setLoadingMessages] = useState(false);
    const [sendingMessage, setSendingMessage] = useState(false);
    const [creatingGroup, setCreatingGroup] = useState(false);

    useEffect(() => {
        fetchGroups();
        fetchFriends();
    }, []);

    const fetchGroups = async () => {
        setLoadingGroups(true);
        try {
            const data = await socialApi.getMyGroups();
            setGroups(data.groups);
        } finally {
            setLoadingGroups(false);
        }
    };

    const fetchFriends = async () => {
        try {
            const data = await socialApi.getMyFriendships();
            setFriends(data.friends);
        } catch {}
    };

    const loadMessages = async (groupId: number) => {
        const grp = groups.find(g => g.id === groupId) || null;
        setSelectedGroup(grp);
        setLoadingMessages(true);
        try {
            const data = await socialApi.getGroupMessages(groupId);
            setMessages(data.messages);
        } catch {
            toast.error("Nie udało się załadować wiadomości");
        } finally {
            setLoadingMessages(false);
        }
    };

    const sendMessage = async () => {
        if (!selectedGroup || !newMsg.trim()) return;
        setSendingMessage(true);
        try {
            await socialApi.sendGroupMessage(selectedGroup.id, newMsg);
            setNewMsg('');
            loadMessages(selectedGroup.id);
        } catch {
            toast.error("Błąd wysyłania wiadomości");
        } finally {
            setSendingMessage(false);
        }
    };

    const handleCreateGroup = async () => {
        if (!groupName.trim() || selectedFriends.length === 0) {
            toast.error('Podaj nazwę grupy i wybierz członków!');
            return;
        }
        setCreatingGroup(true);
        try {
            const data = await socialApi.createGroup(groupName, selectedFriends);
            toast.success('Grupa utworzona!');
            setShowCreate(false);
            setGroupName('');
            setSelectedFriends([]);
            fetchGroups();
        } catch {
            toast.error('Błąd przy tworzeniu grupy');
        } finally {
            setCreatingGroup(false);
        }
    };

    return (
        <div className="group-chat-page">
            {/* Lewa kolumna: grupy i tworzenie */}
            <aside className="glass-box">
                <h3 className="box-title">Moje grupy</h3>
                {loadingGroups ? (
                    <div className="flex justify-center items-center h-32">
                        <LoadingSpinner color="white" />
                    </div>
                ) : (
                    <ul className="group-list">
                        {groups.map(g => (
                            <li key={g.id}>
                                <button
                                    className={`group-btn ${selectedGroup?.id === g.id ? 'active' : ''}`}
                                    onClick={() => loadMessages(g.id)}
                                >
                                    {g.name}
                                </button>
                            </li>
                        ))}
                    </ul>
                )}
                <button
                    className="btn-primary-solid w-full mt-4"
                    onClick={() => setShowCreate(v => !v)}
                >
                    {showCreate ? 'Anuluj tworzenie' : 'Stwórz nową grupę'}
                </button>
                {showCreate && (
                    <div className="create-group-panel">
                        <h4 className="box-subtitle">Tworzenie grupy</h4>
                        <input
                            placeholder="Nazwa grupy"
                            value={groupName}
                            onChange={e => setGroupName(e.target.value)}
                            className="input-glass"
                        />
                        <div>
                            <span className="text-gray-300 text-sm">Dodaj znajomych:</span>
                            <div className="friend-list">
                                {friends.map(f => (
                                    <label key={f.id} className="friend-label">
                                        <input
                                            type="checkbox"
                                            checked={selectedFriends.includes(f.id)}
                                            onChange={e => {
                                                setSelectedFriends(sf => e.target.checked
                                                    ? [...sf, f.id]
                                                    : sf.filter(id => id !== f.id)
                                                );
                                            }}
                                            className="form-checkbox"
                                        /> {f.name}
                                    </label>
                                ))}
                            </div>
                        </div>
                        <button
                            className="btn-primary-solid mt-3"
                            onClick={handleCreateGroup}
                            disabled={creatingGroup}
                        >
                            {creatingGroup ? 'Tworzę...' : 'Utwórz grupę'}
                        </button>
                    </div>
                )}
            </aside>

            {/* Prawa kolumna: czat */}
            <section className="glass-box-flat flex flex-col">
                {selectedGroup ? (
                    <>
                        {/* Nagłówek czatu */}
                        <div className="p-4 border-b border-white/20">
                            <h4 className="box-title mb-1">{selectedGroup.name}</h4>
                            <div className="chat-members">
                                <Users size={14} />
                                {selectedGroup.memberNames.join(', ')}
                            </div>
                        </div>

                        {/* Wiadomości */}
                        <div className="chat-messages">
                            {loadingMessages ? (
                                <div className="empty-chat">
                                    <LoadingSpinner color="white" />
                                </div>
                            ) : messages.length === 0 ? (
                                <div className="empty-chat">Brak wiadomości</div>
                            ) : messages.map(m => (
                                <div key={m.id} className="chat-message">
                                    <div>
                                        <strong className="sender">{m.senderName}</strong>: {m.content}
                                    </div>
                                    <span className="timestamp">{new Date(m.createdAt).toLocaleString()}</span>
                                </div>
                            ))}
                        </div>

                        {/* Input */}
                        <div className="chat-input-row">
                            <input
                                value={newMsg}
                                onChange={e => setNewMsg(e.target.value)}
                                placeholder="Napisz wiadomość..."
                                className="input-glass flex-1"
                                onKeyDown={e => { if (e.key === 'Enter') sendMessage(); }}
                            />
                            <button
                                onClick={sendMessage}
                                className="btn-primary-solid"
                                disabled={sendingMessage}
                            >
                                {sendingMessage ? (
                                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                                ) : (
                                    <Send size={16} />
                                )}
                            </button>
                        </div>
                    </>
                ) : (
                    <div className="empty-chat-prompt">
                        <MessageCircle size={48} className="text-gray-400 mb-4" />
                        <h3 className="box-title">Wybierz grupę</h3>
                        <p className="text-gray-300">Wybierz grupę z listy po lewej, aby rozpocząć czat.</p>
                    </div>
                )}
            </section>
        </div>
    );
};

export default GroupChatPage;