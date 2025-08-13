export interface QuizSession {
    id: string;
    wordSetId: number;
    wordSetTitle: string;
    type: QuizType;
    questions: QuizQuestion[];
    currentQuestionIndex: number;
    answers: QuizAnswer[];
    startTime: Date;
    timeLimit?: number;
    settings: QuizSettings;
    status: 'active' | 'completed' | 'paused';
}

export interface QuizQuestion {
    id: number;
    wordId: number;
    question: string;
    correctAnswer: string;
    options?: string[]; // dla multiple choice
    type: QuestionType;
    points: number;
}

export interface QuizAnswer {
    questionId: number;
    userAnswer: string;
    isCorrect: boolean;
    timeSpent: number;
    points: number;
}

export interface QuizSettings {
    questionCount?: number;
    timeLimit?: number;
    shuffleQuestions: boolean;
    shuffleOptions: boolean;
    showWordFirst: boolean;
    includeMultipleChoice: boolean;
    includeTyping: boolean;
}

export interface QuizResult {
    sessionId: string;
    totalQuestions: number;
    correctAnswers: number;
    incorrectAnswers: number;
    totalPoints: number;
    maxPoints: number;
    accuracy: number;
    averageTimePerQuestion: number;
    totalTime: number;
    completedAt: Date;
}

export type QuizType = 'mixed' | 'multiple-choice' | 'typing' | 'timed';
export type QuestionType = 'multiple-choice' | 'typing';