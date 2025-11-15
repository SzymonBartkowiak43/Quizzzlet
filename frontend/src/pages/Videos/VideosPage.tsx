import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { videoService } from '../../services/videoService';
import { Video } from '../../types/video';
import './VideosPage.css';

const VideosPage: React.FC = () => {
    const [videos, setVideos] = useState<Video[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('all');

    useEffect(() => {
        loadVideos();
    }, []);

    const loadVideos = async () => {
        try {
            const videosData = await videoService.getAllVideos();
            setVideos(videosData);
        } catch (err) {
            setError('Failed to load videos');
            console.error('Error loading videos:', err);
        } finally {
            setLoading(false);
        }
    };

    const filteredVideos = videos.filter(video => {
        const matchesSearch = video.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            video.description.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesCategory = selectedCategory === 'all' || video.category === selectedCategory;
        return matchesSearch && matchesCategory;
    });

    const categories = ['all', ...Array.from(new Set(videos.map(v => v.category)))];

    const formatViews = (views: number) => {
        if (views >= 1000000) {
            return `${(views / 1000000).toFixed(1)}M views`;
        } else if (views >= 1000) {
            return `${(views / 1000).toFixed(1)}K views`;
        } else {
            return `${views} views`;
        }
    };

    const formatDuration = (duration: string) => {
        return duration;
    };

    if (loading) {
        return (
            <div className="videos-page">
                <div className="loading">Loading videos...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="videos-page">
                <div className="error-message">{error}</div>
            </div>
        );
    }

    return (
        <div className="videos-page">
            <div className="videos-header">
                <h1>Obejrzyj filmy</h1>
                <p>Udoskonalaj swoje umiejętności dzięki naszym materiałom edukacyjnym</p>
            </div>

            <div className="videos-controls">
                <div className="search-bar">
                    <input
                        type="text"
                        placeholder="Wyszukaj filmy..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input"
                    />
                </div>

                <div className="category-filter">
                    <select
                        value={selectedCategory}
                        onChange={(e) => setSelectedCategory(e.target.value)}
                        className="category-select"
                    >
                        {categories.map(category => (
                            <option key={category} value={category}>
                                {category.charAt(0).toUpperCase() + category.slice(1)}
                            </option>
                        ))}
                    </select>
                </div>
            </div>

            <div className="videos-grid">
                {filteredVideos.map(video => (
                    <Link to={`/videos/${video.id}`} key={video.id} className="video-card">
                        <div className="video-thumbnail">
                            <img
                                src={video.thumbnailUrl}
                                alt={video.title}
                                onError={(e) => {
                                    e.currentTarget.src = '/img/default-thumbnail.jpg';
                                }}
                            />
                            <span className="video-duration">{formatDuration(video.duration)}</span>
                        </div>

                        <div className="video-info">
                            <h3 className="video-title">{video.title}</h3>
                            <div className="video-meta">
                                <span className="video-views">{formatViews(video.views)}</span>
                                <span className="video-date">
                  {new Date(video.uploadDate).toLocaleDateString()}
                </span>
                            </div>
                            <div className="video-rating">
                                <div className="stars">
                                    {[1, 2, 3, 4, 5].map(star => (
                                        <span
                                            key={star}
                                            className={`star ${star <= video.rating ? 'filled' : ''}`}
                                        >
                      ★
                    </span>
                                    ))}
                                </div>
                                <span className="rating-value">({video.rating.toFixed(1)})</span>
                            </div>
                        </div>
                    </Link>
                ))}
            </div>

            {filteredVideos.length === 0 && (
                <div className="no-videos">
                    <h2>Nie znaleziono filmów</h2>
                    <p>Spróbuj zmienić filtry lub wyszukiwaną frazę.</p>
                </div>
            )}
        </div>
    );
};

export default VideosPage;