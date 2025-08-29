import React, { useEffect, useState } from 'react';
import socialApi from '../../../services/socialApi';
import { toast } from 'react-toastify';
import './GroupChatPage.css';

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
        const data = await socialApi.getGroupMessages(groupId);
        setMessages(data.messages);
        const grp = groups.find(g => g.id === groupId) || null;
        setSelectedGroup(grp);
    };

    const sendMessage = async () => {
        if (!selectedGroup || !newMsg.trim()) return;
        await socialApi.sendGroupMessage(selectedGroup.id, newMsg);
        setNewMsg('');
        loadMessages(selectedGroup.id);
    };

    const handleCreateGroup = async () => {
        if (!groupName.trim() || selectedFriends.length === 0) {
            toast.error('Podaj nazwę grupy i wybierz członków!');
            return;
        }
        try {
            const data = await socialApi.createGroup(groupName, selectedFriends);
            toast.success('Grupa utworzona!');
            setShowCreate(false);
            setGroupName('');
            setSelectedFriends([]);
            fetchGroups();
        } catch {
            toast.error('Błąd przy tworzeniu grupy');
        }
    };

    return (
        <div className="group-chat-page" style={{display: 'flex', gap: 32}}>
            {/* Lewa kolumna: grupy i tworzenie */}
            <aside style={{flex: 1, minWidth: 260}}>
                <h3>Moje grupy</h3>
                {loadingGroups ? <div>Ładuję grupy...</div> : (
                    <ul>
                        {groups.map(g => (
                            <li key={g.id}>
                                <button
                                    style={{
                                        background: selectedGroup?.id === g.id ? '#2563eb' : '#e2e8f0',
                                        color: selectedGroup?.id === g.id ? '#fff' : '#334155',
                                        borderRadius: 7, marginBottom: 6, padding: '7px 14px', border: 'none', cursor: 'pointer'
                                    }}
                                    onClick={() => loadMessages(g.id)}
                                >
                                    {g.name}
                                </button>
                            </li>
                        ))}
                    </ul>
                )}
                <button
                    style={{marginTop: 18, padding: '8px 18px', background: '#2563eb', color: '#fff', borderRadius: 8, border: 'none', cursor: 'pointer'}}
                    onClick={() => setShowCreate(v => !v)}
                >
                    {showCreate ? 'Anuluj' : 'Stwórz nową grupę'}
                </button>
                {showCreate && (
                    <div style={{marginTop: 16, padding: 10, border: '1px solid #e5e7eb', borderRadius: 8, background: '#f8fafc'}}>
                        <h4>Tworzenie grupy</h4>
                        <input
                            placeholder="Nazwa grupy"
                            value={groupName}
                            onChange={e => setGroupName(e.target.value)}
                            style={{marginBottom: 12, width: '100%', padding: 6, borderRadius: 6, border: '1px solid #cbd5e1'}}
                        />
                        <div>
                            <span>Dodaj znajomych:</span>
                            <div style={{maxHeight: 100, overflowY: 'auto', marginTop: 8}}>
                                {friends.map(f => (
                                    <label key={f.id} style={{display: 'block', marginBottom: 6}}>
                                        <input
                                            type="checkbox"
                                            checked={selectedFriends.includes(f.id)}
                                            onChange={e => {
                                                setSelectedFriends(sf => e.target.checked
                                                    ? [...sf, f.id]
                                                    : sf.filter(id => id !== f.id)
                                                );
                                            }}
                                        /> {f.name} ({f.email})
                                    </label>
                                ))}
                            </div>
                        </div>
                        <button
                            style={{marginTop: 12, padding: '7px 16px', background: '#2563eb', color: '#fff', borderRadius: 7, border: 'none'}}
                            onClick={handleCreateGroup}
                        >
                            Utwórz grupę
                        </button>
                    </div>
                )}
            </aside>
            {/* Prawa kolumna: czat */}
            <section style={{flex: 2, minWidth: 320, background: '#fff', borderRadius: 14, padding: 28}}>
                {selectedGroup ? (
                    <>
                        <h4>Czat grupy: <span style={{color: '#2563eb'}}>{selectedGroup.name}</span></h4>
                        <div style={{marginBottom: 10, color: '#64748b', fontSize: '0.98em'}}>
                            Członkowie: {selectedGroup.memberNames.join(', ')}
                        </div>
                        <div style={{height: 250, overflowY: 'auto', background: '#f3f7fc', borderRadius: 7, padding: 12, marginBottom: 16}}>
                            {messages.length === 0 ? (
                                <div style={{color: '#64748b'}}>Brak wiadomości</div>
                            ) : messages.map(m => (
                                <div key={m.id} style={{marginBottom: 7}}>
                                    <strong style={{color: '#2563eb'}}>{m.senderName}</strong>: {m.content}
                                    <span style={{float: 'right', color: '#94a3b8', fontSize: '0.88em'}}>{new Date(m.createdAt).toLocaleString()}</span>
                                </div>
                            ))}
                        </div>
                        <div style={{display: 'flex', gap: 7}}>
                            <input
                                value={newMsg}
                                onChange={e => setNewMsg(e.target.value)}
                                placeholder="Napisz wiadomość..."
                                style={{flex: 1, padding: 7, borderRadius: 7, border: '1px solid #cbd5e1'}}
                                onKeyDown={e => { if (e.key === 'Enter') sendMessage(); }}
                            />
                            <button
                                onClick={sendMessage}
                                style={{padding: '8px 17px', background: '#2563eb', color: '#fff', borderRadius: 7, border: 'none'}}
                            >
                                Wyślij
                            </button>
                        </div>
                    </>
                ) : (
                    <div style={{color: '#64748b'}}>Wybierz grupę, aby zobaczyć czat!</div>
                )}
            </section>
        </div>
    );
};

export default GroupChatPage;