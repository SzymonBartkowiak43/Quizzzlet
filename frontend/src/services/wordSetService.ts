import axiosInstance from './authService';
import { WordSet, CreateWordSetRequest, UpdateWordSetRequest, AddWordRequest, UpdateWordRequest, DeleteWordsRequest } from '../types/wordSet';

export const wordSetService = {
    // Pobierz wszystkie zestawy użytkownika
    async getAllWordSets(): Promise<WordSet[]> {
        const response = await axiosInstance.get('/api/word-sets'); // ✅ Zostaw /api w URL
        return response.data;
    },

    // Stwórz nowy zestaw
    async createWordSet(request: CreateWordSetRequest): Promise<WordSet> {
        const response = await axiosInstance.post('/api/word-sets', request);
        return response.data;
    },

    // Aktualizuj zestaw
    async updateWordSet(wordSetId: number, request: UpdateWordSetRequest): Promise<WordSet> {
        const response = await axiosInstance.put(`/api/word-sets/${wordSetId}`, request);
        return response.data;
    },

    // Usuń zestaw
    async deleteWordSet(wordSetId: number): Promise<void> {
        await axiosInstance.delete(`/api/word-sets/${wordSetId}`);
    },

    // Dodaj słowa do zestawu
    async addWordsToSet(wordSetId: number, request: AddWordRequest): Promise<void> {
        await axiosInstance.post(`/api/word-sets/${wordSetId}/words`, request);
    },

    // Aktualizuj słowo
    async updateWord(wordSetId: number, wordId: number, request: UpdateWordRequest): Promise<void> {
        await axiosInstance.put(`/api/word-sets/${wordSetId}/words/${wordId}`, request);
    },

    // Usuń pojedyncze słowo
    async deleteWord(wordSetId: number, wordId: number): Promise<void> {
        await axiosInstance.delete(`/api/word-sets/${wordSetId}/words/${wordId}`);
    },

    // Usuń wiele słów
    async deleteWords(wordSetId: number, request: DeleteWordsRequest): Promise<void> {
        await axiosInstance.delete(`/api/word-sets/${wordSetId}/words`, { data: request });
    }
};