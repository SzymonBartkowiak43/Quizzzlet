export interface Video {
    id: number;
    title: string;
    description: string;
    url: string;
    thumbnailUrl: string;
    duration: string;
    views: number;
    rating: number;
    uploadDate: string;
    category: string;
    tags: string[];
}

export interface VideoComment {
    id: number;
    videoId: number;
    userId: number;
    userName: string;
    content: string;
    createdAt: string;
    likes: number;
}

export interface VideoRating {
    videoId: number;
    rating: number;
    userId: number;
}