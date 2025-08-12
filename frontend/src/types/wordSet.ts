export interface WordSet {
    id: number;
    name: string;
    description?: string;
    words: Word[];
    createdAt: string;
    updatedAt: string;
    userId: number;
}

export interface Word {
    id: number;
    englishWord: string;
    polishTranslation: string;
    wordSetId: number;
    createdAt?: string;
}

export interface CreateWordSetRequest {
    name: string;
    description?: string;
}

export interface UpdateWordSetRequest {
    name: string;
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