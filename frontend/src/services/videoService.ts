import axiosInstance from './authService';
import { Video, VideoComment, VideoRating } from '../types/video';

export const videoService = {
    async getAllVideos(): Promise<Video[]> {
        try {
            const response = await axiosInstance.get('/videos');
            console.log('API Response:', response.data);

            if (response.data.videos) {
                return response.data.videos.map((video: any) => ({
                    id: video.id,
                    title: video.title,
                    description: video.title,
                    url: video.url,
                    thumbnailUrl: generateThumbnailUrl(video.url),
                    duration: "10:30", // Domyślnie
                    views: Math.floor(Math.random() * 50000) + 1000, // Losowe
                    rating: video.averageRating || 4.0 + Math.random(),
                    uploadDate: new Date().toISOString(),
                    category: "education",
                    tags: ["english", "learning"]
                }));
            }
            return [];
        } catch (error) {
            console.error('Error in videoService:', error);
            throw error;
        }
    },

    async getVideoById(id: number): Promise<Video> {
        const response = await axiosInstance.get(`/videos/${id}`);
        const video = response.data;

        return {
            id: video.id,
            title: video.title,
            description: video.title,
            url: video.url,
            thumbnailUrl: generateThumbnailUrl(video.url),
            duration: "10:30",
            views: Math.floor(Math.random() * 50000) + 1000,
            rating: video.averageRating || 4.0 + Math.random(),
            uploadDate: new Date().toISOString(),
            category: "education",
            tags: ["english", "learning"]
        };
    },

    async getVideoComments(videoId: number): Promise<VideoComment[]> {
        const response = await axiosInstance.get(`/videos/${videoId}`);
        const comments = response.data.comments || [];

        return comments.map((comment: any) => ({
            id: comment.id,
            videoId: videoId,
            userId: comment.userId || 1,
            userName: comment.userFullName || 'Anonymous',
            content: comment.content,
            createdAt: comment.createdAt || new Date().toISOString(),
            likes: 0
        }));
    },

    async addComment(videoId: number, content: string): Promise<VideoComment> {
        const response = await axiosInstance.post(`/videos/${videoId}/comments`, { content });
        return response.data;
    },

    async rateVideo(videoId: number, rating: number): Promise<void> {
        await axiosInstance.post(`/videos/${videoId}/rate`, { rating });
    },

    async incrementViews(videoId: number): Promise<void> {
        // Implementuj później jeśli potrzebujesz
    }
};

function generateThumbnailUrl(videoUrl: string): string {
    if (videoUrl.includes("youtube.com/watch?v=")) {
        const videoId = videoUrl.split("v=")[1].split("&")[0];
        return `https://img.youtube.com/vi/${videoId}/maxresdefault.jpg`;
    }
    return "https://via.placeholder.com/320x180?text=Video+Thumbnail";
}