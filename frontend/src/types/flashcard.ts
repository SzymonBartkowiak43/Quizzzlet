export interface FlashcardSession {
    wordSetId: number;
    wordSetTitle: string;
    cards: FlashcardItem[];
    currentCardIndex: number;
    completedCards: number;
    correctAnswers: number;
    incorrectAnswers: number;
    startTime: Date;
    settings: FlashcardSettings;
}

export interface FlashcardItem {
    id: number;
    word: string;
    translation: string;
    difficulty: FlashcardDifficulty;
    timesShown: number;
    timesCorrect: number;
    timesIncorrect: number;
    lastShown?: Date;
}

export interface FlashcardSettings {
    showWordFirst: boolean;
    shuffleCards: boolean;
    reviewIncorrect: boolean;
    sessionLimit?: number;
}

export type FlashcardDifficulty = 'easy' | 'medium' | 'hard';

export interface FlashcardResult {
    cardId: number;
    isCorrect: boolean;
    responseTime: number;
}

export interface SessionStats {
    totalCards: number;
    correctAnswers: number;
    incorrectAnswers: number;
    accuracy: number;
    averageTime: number;
    sessionDuration: number;
}