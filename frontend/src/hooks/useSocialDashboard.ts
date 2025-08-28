import { useState, useEffect } from 'react';
import { SocialDashboard } from '../types/social';
import socialApi from '../services/socialApi';

export const useSocialDashboard = () => {
    const [dashboard, setDashboard] = useState<SocialDashboard | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchDashboard = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await socialApi.getSocialDashboard();
            setDashboard(data);
        } catch (err) {
            console.error('Failed to fetch social dashboard:', err);
            setError('Nie udało się załadować dashboardu społecznościowego');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDashboard();
    }, []);

    return {
        dashboard,
        loading,
        error,
        refetch: fetchDashboard,
        refreshDashboard: fetchDashboard
    };
};