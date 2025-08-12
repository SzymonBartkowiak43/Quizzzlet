import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
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
import './App.css';

function App() {
  return (
      <AuthProvider>
        <Router>
          <div className="App">
            <Header />
            <main>
              <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/word-sets" element={<WordSetsPage />} />
                <Route path="/word-sets/new" element={<CreateWordSetPage />} />
                <Route path="/word-sets/:id" element={<WordSetDetailsPage />} />
                <Route path="/word-sets/:id/edit" element={<EditWordSetPage />} />
                <Route path="/profile" element={<div>Profile - Coming Soon</div>} />
                <Route path="/videos" element={<VideosPage />} />
                <Route path="/videos/:id" element={<VideoPlayerPage />} />
                <Route path="/review" element={<div>Review - Coming Soon</div>} />
              </Routes>
            </main>
          </div>
        </Router>
      </AuthProvider>
  );
}

export default App;