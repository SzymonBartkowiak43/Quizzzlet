import React, { useEffect, useState } from 'react';
import socialApi from '../../services/socialApi';
import { toast } from 'react-toastify';
import LoadingSpinner from '../Shared/LoadingSpinner';
import { Trash2, ShieldAlert } from 'lucide-react';

import './AdminUsersPage.css';

interface User {
    id: number;
    name: string;
    email: string;
    roles?: string[]; // Dodaję roles jako opcjonalne
}

const AdminUsersPage: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        setLoading(true);
        try {
            const data = await socialApi.getAllUsers();
            setUsers(data);
        } catch (error) {
            toast.error('Nie udało się pobrać listy użytkowników.');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (userId: number) => {
        if (window.confirm('Czy na pewno chcesz trwale usunąć tego użytkownika? Tej operacji nie można cofnąć.')) {
            try {
                await socialApi.deleteUser(userId);
                toast.success('Użytkownik został usunięty.');
                setUsers(prev => prev.filter(u => u.id !== userId));
            } catch (error) {
                toast.error('Błąd podczas usuwania użytkownika.');
            }
        }
    };

    // Funkcja pomocnicza do wyświetlania roli
    const renderRole = (roles: string[] | undefined) => {
        const roleName = roles && roles.includes('ADMIN') ? 'ADMIN' : 'USER';
        const badgeClass = roleName === 'ADMIN' ? 'role-admin' : 'role-user';
        return <span className={`user-role-badge ${badgeClass}`}>{roleName}</span>;
    };

    return (
        <div className="admin-page">
            <div className="admin-glass-box">
                <div className="admin-header">
                    <ShieldAlert className="admin-icon" size={32} />
                    <h1 className="admin-title">Panel Administratora - Użytkownicy</h1>
                </div>

                {loading ? (
                    <LoadingSpinner color="white" />
                ) : (
                    <div className="table-container">
                        <table className="admin-table">
                            <thead>
                            <tr>
                                <th style={{ width: '80px' }}>ID</th>
                                <th>Użytkownik</th>
                                <th>Rola</th>
                                <th className="text-right">Akcje</th>
                            </tr>
                            </thead>
                            <tbody>
                            {users.map(user => (
                                <tr key={user.id} className="user-row">
                                    <td className="user-id">#{user.id}</td>
                                    <td>
                                        <div className="user-name">{user.name}</div>
                                        <div className="user-email">{user.email}</div>
                                    </td>
                                    <td>
                                        {renderRole(user.roles)}
                                    </td>
                                    <td className="text-right">
                                        <button
                                            onClick={() => handleDelete(user.id)}
                                            className="delete-button"
                                            title="Usuń konto"
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                        {users.length === 0 && (
                            <div className="empty-state">Brak użytkowników w bazie.</div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminUsersPage;