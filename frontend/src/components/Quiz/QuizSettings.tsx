import React, { useState } from 'react';
import { WordSet } from '../../types/wordSet';
import { QuizSettings as QuizSettingsType } from '../../types/quiz';
import './QuizSettings.css';

interface QuizSettingsProps {
    wordSet: WordSet;
    onStart: (settings: QuizSettingsType) => void;
    onBack: () => void;
}

const QuizSettings: React.FC<QuizSettingsProps> = ({ wordSet, onStart, onBack }) => {
    const [settings, setSettings] = useState<QuizSettingsType>({
        questionCount: undefined,
        timeLimit: undefined,
        shuffleQuestions: true,
        shuffleOptions: true,
        showWordFirst: true,
        includeMultipleChoice: true,
        includeTyping: true
    });

    const handleSettingChange = (key: keyof QuizSettingsType, value: any) => {
        setSettings(prev => ({
            ...prev,
            [key]: value
        }));
    };

    const handleStart = () => {
        onStart(settings);
    };

    const maxQuestions = wordSet.words.length;
    const questionCount = settings.questionCount || maxQuestions;

    const hasSelectedQuestionType = settings.includeMultipleChoice || settings.includeTyping;

    return (
        <div className="quiz-settings">
            <div className="settings-header">
                <h2>🧠 Ustawienia testu</h2>
                <p>Skonfiguruj swój test wiedzy</p>
            </div>

            <div className="settings-grid">
                <div className="setting-group">
                    <h3>Rodzaje pytań</h3>
                    <div className="setting-checkboxes">
                        <label className="setting-checkbox">
                            <input
                                type="checkbox"
                                checked={settings.includeMultipleChoice}
                                onChange={(e) => handleSettingChange('includeMultipleChoice', e.target.checked)}
                            />
                            <span className="checkmark"></span>
                            <div className="checkbox-content">
                                <div className="checkbox-title">🔤 Multiple Choice</div>
                                <div className="checkbox-desc">Wybór z 4 odpowiedzi</div>
                            </div>
                        </label>
                        <label className="setting-checkbox">
                            <input
                                type="checkbox"
                                checked={settings.includeTyping}
                                onChange={(e) => handleSettingChange('includeTyping', e.target.checked)}
                            />
                            <span className="checkmark"></span>
                            <div className="checkbox-content">
                                <div className="checkbox-title">⌨️ Wpisywanie</div>
                                <div className="checkbox-desc">Wpisz poprawne tłumaczenie</div>
                            </div>
                        </label>
                    </div>
                </div>

                <div className="setting-group">
                    <h3>Kierunek tłumaczenia</h3>
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
                    <h3>Liczba pytań</h3>
                    <div className="question-limit">
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="questionLimit"
                                checked={!settings.questionCount}
                                onChange={() => handleSettingChange('questionCount', undefined)}
                            />
                            <div className="option-content">
                                <div className="option-title">Wszystkie pytania</div>
                                <div className="option-desc">{maxQuestions} pytań</div>
                            </div>
                        </label>
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="questionLimit"
                                checked={settings.questionCount === 10}
                                onChange={() => handleSettingChange('questionCount', 10)}
                            />
                            <div className="option-content">
                                <div className="option-title">Krótki test</div>
                                <div className="option-desc">10 pytań</div>
                            </div>
                        </label>
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="questionLimit"
                                checked={settings.questionCount === 25}
                                onChange={() => handleSettingChange('questionCount', 25)}
                            />
                            <div className="option-content">
                                <div className="option-title">Średni test</div>
                                <div className="option-desc">25 pytań</div>
                            </div>
                        </label>
                        {maxQuestions > 25 && (
                            <label className="setting-option">
                                <input
                                    type="radio"
                                    name="questionLimit"
                                    checked={settings.questionCount === 50}
                                    onChange={() => handleSettingChange('questionCount', 50)}
                                />
                                <div className="option-content">
                                    <div className="option-title">Długi test</div>
                                    <div className="option-desc">50 pytań</div>
                                </div>
                            </label>
                        )}
                    </div>
                </div>

                <div className="setting-group">
                    <h3>Limit czasu</h3>
                    <div className="time-limit">
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="timeLimit"
                                checked={!settings.timeLimit}
                                onChange={() => handleSettingChange('timeLimit', undefined)}
                            />
                            <div className="option-content">
                                <div className="option-title">Bez limitu</div>
                                <div className="option-desc">Tyle czasu ile potrzebujesz</div>
                            </div>
                        </label>
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="timeLimit"
                                checked={settings.timeLimit === 300}
                                onChange={() => handleSettingChange('timeLimit', 300)}
                            />
                            <div className="option-content">
                                <div className="option-title">⚡ Szybki test</div>
                                <div className="option-desc">5 minut</div>
                            </div>
                        </label>
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="timeLimit"
                                checked={settings.timeLimit === 600}
                                onChange={() => handleSettingChange('timeLimit', 600)}
                            />
                            <div className="option-content">
                                <div className="option-title">⏰ Standard</div>
                                <div className="option-desc">10 minut</div>
                            </div>
                        </label>
                        <label className="setting-option">
                            <input
                                type="radio"
                                name="timeLimit"
                                checked={settings.timeLimit === 1200}
                                onChange={() => handleSettingChange('timeLimit', 1200)}
                            />
                            <div className="option-content">
                                <div className="option-title">🕐 Relaks</div>
                                <div className="option-desc">20 minut</div>
                            </div>
                        </label>
                    </div>
                </div>

                <div className="setting-group">
                    <h3>Dodatkowe opcje</h3>
                    <label className="setting-checkbox">
                        <input
                            type="checkbox"
                            checked={settings.shuffleQuestions}
                            onChange={(e) => handleSettingChange('shuffleQuestions', e.target.checked)}
                        />
                        <span className="checkmark"></span>
                        Losowa kolejność pytań
                    </label>
                    {settings.includeMultipleChoice && (
                        <label className="setting-checkbox">
                            <input
                                type="checkbox"
                                checked={settings.shuffleOptions}
                                onChange={(e) => handleSettingChange('shuffleOptions', e.target.checked)}
                            />
                            <span className="checkmark"></span>
                            Losowa kolejność odpowiedzi
                        </label>
                    )}
                </div>
            </div>

            <div className="settings-preview">
                <h3>📋 Podsumowanie testu</h3>
                <div className="preview-stats">
                    <div className="preview-stat">
                        <span className="preview-label">Liczba pytań:</span>
                        <span className="preview-value">{questionCount}</span>
                    </div>
                    <div className="preview-stat">
                        <span className="preview-label">Kierunek:</span>
                        <span className="preview-value">
              {settings.showWordFirst ? 'Angielski → Polski' : 'Polski → Angielski'}
            </span>
                    </div>
                    <div className="preview-stat">
                        <span className="preview-label">Limit czasu:</span>
                        <span className="preview-value">
              {settings.timeLimit
                  ? `${Math.floor(settings.timeLimit / 60)} min`
                  : 'Bez limitu'
              }
            </span>
                    </div>
                    <div className="preview-stat">
                        <span className="preview-label">Rodzaje pytań:</span>
                        <span className="preview-value">
              {[
                  settings.includeMultipleChoice && 'Multiple Choice',
                  settings.includeTyping && 'Wpisywanie'
              ].filter(Boolean).join(', ')}
            </span>
                    </div>
                </div>
            </div>

            <div className="settings-actions">
                <button onClick={onBack} className="btn btn-secondary">
                    Anuluj
                </button>
                <button
                    onClick={handleStart}
                    className="btn btn-primary"
                    disabled={!hasSelectedQuestionType}
                >
                    🚀 Rozpocznij test
                </button>
            </div>

            {!hasSelectedQuestionType && (
                <div className="error-message">
                    Wybierz przynajmniej jeden rodzaj pytań
                </div>
            )}
        </div>
    );
};

export default QuizSettings;