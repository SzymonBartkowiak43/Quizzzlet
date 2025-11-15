import React from 'react';
import { LucideIcon } from 'lucide-react';
import './StatsCard.css';

interface StatsCardProps {
    title: string;
    value: number;
    icon: LucideIcon;
    trend?: string;
}

const StatsCard: React.FC<StatsCardProps> = ({ title, value, icon: Icon, trend }) => {

    return (
        <div className="glass-box stat-card">
            <div className="flex items-center justify-between">
                <div>
                    <p className="text-sm font-medium text-gray-300 mb-1">{title}</p>
                    <p className="text-3xl font-bold text-white">{value}</p>
                    {trend && (
                        <p className="text-xs text-gray-400 mt-1">{trend}</p>
                    )}
                </div>
                <div className="bg-white/20 p-3 rounded-lg">
                    <Icon className="h-6 w-6 text-white" />
                </div>
            </div>
        </div>
    );
};

// --- DODAJ TE STYLE DO CSS ---
/*
.glass-box {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(12px);
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.15);
    padding: 1.5rem;
}
.stat-card {
    transition: all 0.2s ease;
}
.stat-card:hover {
    transform: translateY(-4px);
    border-color: rgba(255, 255, 255, 0.4);
}
*/

export default StatsCard;