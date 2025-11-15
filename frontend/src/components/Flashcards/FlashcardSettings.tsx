import React, { useState } from 'react';
import { WordSet } from '../../types/wordSet';
import { FlashcardSettings as FlashcardSettingsType } from '../../types/flashcard';
import './FlashcardSettings.css';

interface FlashcardSettingsProps {
    wordSet: WordSet;
    onStart: (settings: FlashcardSettingsType) => void;
    onBack: () => void;
}

const FlashcardSettings: React.FC<FlashcardSettingsProps> = ({ wordSet, onStart, onBack }) => {
    const [settings, setSettings] = useState<FlashcardSettingsType>({
        showWordFirst: true,
        shuffleCards: true,
        reviewIncorrect: true,
        sessionLimit: undefined
    });

    const handleSettingChange = (key: keyof FlashcardSettingsType, value: any) => {
        setSettings(prev => ({
            ...prev,
            [key]: value
        }));
    };

    const handleStart = () => {
        onStart(settings);
    };

    const maxCards = wordSet.words.length;
    const sessionCards = settings.sessionLimit || maxCards;

    return (
        <div className="flashcard-settings">
            <div className="settings-header">
                <h2>⚙️ Ustawienia sesji</h2>
                <p>Dostosuj tryb nauki do swoich potrzeb</p>
            </div>

            <div className="settings-grid">
                <div className="setting-group">
                    <h3>Kierunek nauki</h3>
                    <div className="setting-options">
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="direction"
                                checked={settings.showWordFirst}
                                onChange={() => handleSettingChange('showWordFirst', true)}
                            />
                            <div className="option-content">
                                <div className="option-title">🇬🇧 → 🇵🇱</div>
                                <div className="option-desc">Angielski → Polski</div>
                            </div>
                        </label>
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="direction"
                                checked={!settings.showWordFirst}
                                onChange={() => handleSettingChange('showWordFirst', false)}
                            />
                            <div className="option-content">
                                <div className="option-title">🇵🇱 → 🇬🇧</div>
                                <div className="option-desc">Polski → Angielski</div>
                            </div>
                        </label>
                    </div>
                </div>

                <div className="setting-group">
                    <h3>Kolejność kart</h3>
                    <label className="setting-checkbox">
                        <input
                            type="checkbox"
                            checked={settings.shuffleCards}
                            onChange={(e) => handleSettingChange('shuffleCards', e.target.checked)}
                        />
                        <span className="checkmark"></span>
                        Losowa kolejność
                    </label>
                </div>

                <div className="setting-group">
                    <h3>Dodatkowe opcje</h3>
                    <label className="setting-checkbox">
                        <input
                            type="checkbox"
                            checked={settings.reviewIncorrect}
                            onChange={(e) => handleSettingChange('reviewIncorrect', e.target.checked)}
                        />
                        <span className="checkmark"></span>
                        Powtarzaj błędne odpowiedzi na końcu
                    </label>
                </div>

                <div className="setting-group">
                    <h3>Liczba kart w sesji</h3>
                    <div className="session-limit">
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="sessionLimit"
                                checked={!settings.sessionLimit}
                                onChange={() => handleSettingChange('sessionLimit', undefined)}
                            />
                            <div className="option-content">
                                <div className="option-title">Wszystkie karty</div>
                                <div className="option-desc">{maxCards} słówek</div>
                            </div>
                        </label>
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="sessionLimit"
                                checked={settings.sessionLimit === 10}
                                onChange={() => handleSettingChange('sessionLimit', 10)}
                            />
                            <div className="option-content">
                                <div className="option-title">Krótka sesja</div>
                                <div className="option-desc">10 słówek</div>
                            </div>
                        </label>
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="sessionLimit"
                                checked={settings.sessionLimit === 25}
                                onChange={() => handleSettingChange('sessionLimit', 25)}
                            />
                            <div className="option-content">
                                <div className="option-title">Średnia sesja</div>
                                <div className="option-desc">25 słówek</div>
                            </div>
                        </label>
                        {maxCards > 25 && (
                            <label className="setting-option">
                                <input
                                    type="radio"
                                    name="sessionLimit"
                                    checked={settings.sessionLimit === 50}
                                    onChange={() => handleSettingChange('sessionLimit', 50)}
                                />
                                <div className="option-content">
                                    <div className="option-title">Długa sesja</div>
                                    <div className="option-desc">50 słówek</div>
                                </div>
                            </label>
                        )}
                    </div>
                </div>
            </div>

            <div className="settings-preview">
                <h3>📋 Podsumowanie sesji</h3>
                <div className="preview-stats">
                    <div className="preview-stat">
                        <span className="preview-label">Liczba kart:</span>
                        <span className="preview-value">{sessionCards}</span>
                    </div>
                    <div className="preview-stat">
                        <span className="preview-label">Kierunek:</span>
                        <span className="preview-value">
              {settings.showWordFirst ? 'Angielski → Polski' : 'Polski → Angielski'}
            </span>
                    </div>
                    <div className="preview-stat">
                        <span className="preview-label">Kolejność:</span>
                        <span className="preview-value">
              {settings.shuffleCards ? 'Losowa' : 'Według zestawu'}
            </span>
                    </div>
                </div>
            </div>

            <div className="settings-actions">
                <button onClick={onBack} className="btn-glass">
                    Anuluj
                </button>
                <button onClick={handleStart} className="btn-primary-solid">
                    🚀 Rozpocznij sesję
                </button>
            </div>
        </div>
    );
};

export default FlashcardSettings;