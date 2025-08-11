import React from 'react';
import { useNavigate } from 'react-router-dom';
import './HomePage.css';

const HomePage: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div className="home-page">
            <div className="container1">
                <h1>CHOOSE YOUR ACTIVITY</h1>
            </div>

            <div className="container2">
                <div className="activity-card blue-frame">
                    <div className="content-frame">
                        <h2 className="title">Review Previous Words</h2>
                        <p className="description">
                            Here you can review the words you have previously learned. This section allows you to
                            revisit and reinforce your vocabulary knowledge, ensuring better retention and understanding of the
                            words you have studied.
                        </p>
                        <button
                            className="action-button review-button"
                            onClick={() => navigate('/review')}
                        >
                            REVIEW WORDS
                        </button>
                    </div>
                </div>

                <div className="activity-card red-frame">
                    <div className="content-frame">
                        <h2 className="title">Watch Videos</h2>
                        <p className="description">
                            Watch educational videos to improve your language skills. This section provides a variety
                            of videos that cover different aspects of language learning, including grammar, vocabulary,
                            pronunciation, and more.
                        </p>
                        <button
                            className="action-button watch-button"
                            onClick={() => navigate('/videos')}
                        >
                            LET'S WATCH
                        </button>
                    </div>
                </div>

                <div className="activity-card green-frame">
                    <div className="content-frame">
                        <h2 className="title">My Sets</h2>
                        <p className="description">
                            Explore your personalized sets! Create and manage your custom flashcard sets to enhance
                            your learning experience. Dive into a world of tailored content that suits your study needs and track
                            your progress as you master new topics.
                        </p>
                        <button
                            className="action-button sets-button"
                            onClick={() => navigate('/word-sets')}
                        >
                            GO TO SETS
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default HomePage;