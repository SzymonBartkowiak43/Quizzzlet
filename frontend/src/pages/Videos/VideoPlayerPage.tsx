import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { videoService } from '../../services/videoService';
import { Video, VideoComment } from '../../types/video';
import './VideoPlayerPage.css';

const VideoPlayerPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const { isAuthenticated, user } = useAuth();
    const [video, setVideo] = useState<Video | null>(null);
    const [comments, setComments] = useState<VideoComment[]>([]);
    const [newComment, setNewComment] = useState('');
    const [userRating, setUserRating] = useState<number>(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (id) {
            loadVideoData(parseInt(id));
        }
    }, [id]);

    const loadVideoData = async (videoId: number) => {
        try {
            setLoading(true);
            const [videoData, commentsData] = await Promise.all([
                videoService.getVideoById(videoId),
                videoService.getVideoComments(videoId)
            ]);

            setVideo(videoData);
            setComments(commentsData);

            // Increment views
            await videoService.incrementViews(videoId);
        } catch (err) {
            setError('Failed to load video');
            console.error('Error loading video:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleRating = async (rating: number) => {
        if (!video || !isAuthenticated) return;

        try {
            await videoService.rateVideo(video.id, rating);
            setUserRating(rating);
            // Refresh video data to get updated rating
            const updatedVideo = await videoService.getVideoById(video.id);
            setVideo(updatedVideo);
        } catch (err) {
            console.error('Error rating video:', err);
        }
    };

    const handleAddComment = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!video || !newComment.trim() || !isAuthenticated) return;

        try {
            const comment = await videoService.addComment(video.id, newComment.trim());
            setComments([comment, ...comments]);
            setNewComment('');
        } catch (err) {
            console.error('Error adding comment:', err);
        }
    };

    const formatViews = (views: number) => {
        if (views >= 1000000) {
            return `${(views / 1000000).toFixed(1)}M views`;
        } else if (views >= 1000) {
            return `${(views / 1000).toFixed(1)}K views`;
        } else {
            return `${views} views`;
        }
    };

    const getVideoEmbedUrl = (url: string) => {
        // Convert YouTube URL to embed format
        if (url.includes('youtube.com/watch?v=')) {
            const videoId = url.split('v=')[1].split('&')[0];
            return `https://www.youtube.com/embed/${videoId}`;
        } else if (url.includes('youtu.be/')) {
            const videoId = url.split('youtu.be/')[1];
            return `https://www.youtube.com/embed/${videoId}`;
        }
        return url;
    };

    if (loading) {
        return <div className="video-player-page loading">Loading video...</div>;
    }

    if (error || !video) {
        return (
            <div className="video-player-page error">
                <h2>Video not found</h2>
                <Link to="/videos" className="back-link">← Back to videos</Link>
            </div>
        );
    }

    return (
        <div className="video-player-page">
            <div className="video-container">
                <div className="video-player">
                    <iframe
                        src={getVideoEmbedUrl(video.url)}
                        title={video.title}
                        frameBorder="0"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                        allowFullScreen
                    ></iframe>
                </div>

                <div className="video-info">
                    <h1 className="video-title">{video.title}</h1>

                    <div className="video-stats">
                        <div className="views-date">
                            <span className="views">{formatViews(video.views)}</span>
                            <span className="upload-date">
                {new Date(video.uploadDate).toLocaleDateString()}
              </span>
                        </div>

                        <div className="rating-section">
                            <span>Rate this video:</span>
                            <div className="stars-interactive">
                                {[1, 2, 3, 4, 5].map(star => (
                                    <button
                                        key={star}
                                        className={`star-btn ${star <= (userRating || video.rating) ? 'filled' : ''}`}
                                        onClick={() => handleRating(star)}
                                        disabled={!isAuthenticated}
                                    >
                                        ★
                                    </button>
                                ))}
                            </div>
                            <span className="rating-value">({video.rating.toFixed(1)})</span>
                        </div>
                    </div>

                    <div className="video-description">
                        <h3>Description</h3>
                        <p>{video.description}</p>

                        <div className="video-tags">
                            {video.tags.map(tag => (
                                <span key={tag} className="tag">#{tag}</span>
                            ))}
                        </div>
                    </div>
                </div>
            </div>

            <div className="comments-section">
                <h3>{comments.length} Comments</h3>

                {isAuthenticated ? (
                    <form onSubmit={handleAddComment} className="comment-form">
                        <div className="comment-input-container">
              <textarea
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  placeholder="Add a comment..."
                  className="comment-input"
                  rows={3}
              />
                            <div className="comment-actions">
                                <button
                                    type="button"
                                    onClick={() => setNewComment('')}
                                    className="cancel-btn"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    disabled={!newComment.trim()}
                                    className="submit-btn"
                                >
                                    Comment
                                </button>
                            </div>
                        </div>
                    </form>
                ) : (
                    <div className="login-prompt">
                        <Link to="/login">Sign in</Link> to leave a comment
                    </div>
                )}

                <div className="comments-list">
                    {comments.map(comment => (
                        <div key={comment.id} className="comment">
                            <div className="comment-header">
                                <span className="comment-author">{comment.userName}</span>
                                <span className="comment-date">
                  {new Date(comment.createdAt).toLocaleDateString()}
                </span>
                            </div>
                            <div className="comment-content">{comment.content}</div>
                            <div className="comment-actions">
                                <button className="like-btn">
                                    👍 {comment.likes}
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            <div className="back-to-videos">
                <Link to="/videos" className="back-link">← Back to all videos</Link>
            </div>
        </div>
    );
};

export default VideoPlayerPage;