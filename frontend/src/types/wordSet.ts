export interface WordSet {
    id: number;
    title: string;
    description?: string;
    words: Word[];
    createdAt: string;
    updatedAt: string;
    userId?: number;
    language?: string;
    translationLanguage?: string;
}

export interface Word {
    id: number;
    word: string;
    translation: string;
    wordSetId: number;
    points?: number;
    star?: boolean;
    lastPracticed?: string | null;
    createdAt?: string;
}

export interface CreateWordSetRequest {
    title: string;
    description?: string;
    language?: string;
    translationLanguage?: string;
}

export interface UpdateWordSetRequest {
    title: string;
    description?: string;
}

export interface AddWordRequest {
    words: {
        word: string;
        translation: string;
    }[];
}

export interface UpdateWordRequest {
    word: string;
    translation: string;
}

export interface DeleteWordsRequest {
    wordIds: number[];
}