import { useState, useEffect, useCallback } from 'react';
import { socialApi } from '../../services/social/socialApi';
import { SocialDashboard } from '../../types/social';
import { toast } from 'react-toastify';

interface UseSocialDashboardReturn {
    dashboard: SocialDashboard | null;
    loading: boolean;
    error: string | null;
    searchResults: any;
    refreshDashboard: () => Promise<void>;
    searchSocial: (searchTerm?: string) => Promise<void>;
}

export const useSocialDashboard = (): UseSocialDashboardReturn => {
    const [dashboard, setDashboard] = useState<SocialDashboard | null>(null);
    const [searchResults, setSearchResults] = useState<any>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const loadDashboard = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await socialApi.getSocialDashboard();
            setDashboard(data);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Błąd podczas ładowania dashboard');
            toast.error('Nie udało się załadować dashboard');
        } finally {
            setLoading(false);
        }
    }, []);

    const searchSocial = useCallback(async (searchTerm?: string) => {
        try {
            const results = await socialApi.searchSocial(searchTerm);
            setSearchResults(results);
        } catch (err: any) {
            toast.error('Nie udało się wykonać wyszukiwania');
        }
    }, []);

    const refreshDashboard = useCallback(async () => {
        await loadDashboard();
    }, [loadDashboard]);

    useEffect(() => {
        loadDashboard();
    }, [loadDashboard]);

    return {
        dashboard,
        loading,
        error,
        searchResults,
        refreshDashboard,
        searchSocial
    };
};