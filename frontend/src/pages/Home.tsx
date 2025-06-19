import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Home.css';

const Home: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div>
            <div className="container1">
                <h1>CHOOSE YOUR ACTIVITY</h1>
            </div>
            <div className="container2">
                <div className="centered-frame-blue blue-frame">
                    <div className="content-frame-blue" onClick={() => navigate('/review')}>
                        <h2 className="title">Review Previous Word</h2>
                        <p className="description">
                            Here you can review the words you have previously learned. This section allows you to
                            revisit and reinforce your vocabulary knowledge, ensuring better retention and understanding of the
                            words you have studied.
                        </p>
                        <button className="review-button" onClick={() => navigate('/review')}>
                            REVIEW WORD
                        </button>
                    </div>
                </div>

                <div className="centered-frame-red red-frame">
                    <div className="content-frame-red" onClick={() => navigate('/video/1')}>
                        <h2 className="title">Watch Video</h2>
                        <p className="description">
                            Watch English videos to improve your language skills. This section provides a variety
                            of videos that cover different aspects of the English language, including grammar, vocabulary,
                            pronunciation, and more.
                        </p>
                        <button className="watch-button" onClick={() => navigate('/video/1')}>
                            LET'S WATCH
                        </button>
                    </div>
                </div>

                <div className="centered-frame-green green-frame" onClick={() => navigate('/wordSet')}>
                    <div className="content-frame-green">
                        <h2 className="title">My Sets</h2>
                        <p className="description">
                            Explore your personalized sets! Create, manage your custom flashcard sets to enhance
                            your learning experience. Dive into a world of tailored content that suits your study needs and track
                            your progress as you master new topics.
                        </p>
                        <button className="sets-button" onClick={() => navigate('/wordSet')}>
                            GO TO SETS
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Home;