import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Existing components
import Header from './components/Header/Header';
import HomePage from './pages/Home/HomePage';
import LoginPage from './pages/Auth/LoginPage';
import RegisterPage from './pages/Auth/RegisterPage';
import VideosPage from './pages/Videos/VideosPage';
import VideoPlayerPage from './pages/Videos/VideoPlayerPage';
import WordSetsPage from './pages/WordSets/WordSetsPage';
import CreateWordSetPage from './pages/WordSets/CreateWordSetPage';
import EditWordSetPage from './pages/WordSets/EditWordSetPage';
import WordSetDetailsPage from './pages/WordSets/WordSetDetailsPage';
import FlashcardsPage from './pages/Flashcards/FlashcardsPage';
import QuizPage from './pages/Quiz/QuizPage';

// New Social components
import SocialDashboard from './components/Social/dashboard/SocialDashboard';
import FriendsPage from './components/Social/friends/FriendsPage';
import MessagesPage from './components/Social/messages/MessagesPage';
import GroupsPage from './components/Social/groups/GroupsPage';
import CommunityPage from './components/Social/friends/CommunityPage';

import './App.css';

function App() {
  return (
      <AuthProvider>
        <Router>
          <div className="App">
            <Header />
            <main>
              <Routes>
                {/* Existing routes */}
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/word-sets" element={<WordSetsPage />} />
                <Route path="/word-sets/new" element={<CreateWordSetPage />} />
                <Route path="/word-sets/:id" element={<WordSetDetailsPage />} />
                <Route path="/word-sets/:id/edit" element={<EditWordSetPage />} />
                <Route path="/flashcards/:id" element={<FlashcardsPage />} />
                <Route path="/quiz/:id" element={<QuizPage />} />
                <Route path="/profile" element={<div>Profile - Coming Soon</div>} />
                <Route path="/videos" element={<VideosPage />} />
                <Route path="/videos/:id" element={<VideoPlayerPage />} />
                <Route path="/review" element={<div>Review - Coming Soon</div>} />

                {/* New Social routes */}
                <Route path="/social" element={<Navigate to="/social/dashboard" replace />} />
                <Route path="/social/dashboard" element={<SocialDashboard />} />
                <Route path="/social/friends/*" element={<FriendsPage />} />
                <Route path="/social/messages/*" element={<MessagesPage />} />
                <Route path="/social/groups/*" element={<GroupsPage />} />
                <Route path="/social/community" element={<CommunityPage />} />

                {/* Catch all - redirect to home */}
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </main>
          </div>

          {/* Toast notifications dla social features */}
          <ToastContainer
              position="top-right"
              autoClose={5000}
              hideProgressBar={false}
              newestOnTop={false}
              closeOnClick
              rtl={false}
              pauseOnFocusLoss
              draggable
              pauseOnHover
              theme="light"
          />
        </Router>
      </AuthProvider>
  );
}

export default App;