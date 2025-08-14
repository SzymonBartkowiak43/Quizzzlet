import React from 'react';
import { LucideIcon } from 'lucide-react';

interface StatsCardProps {
    title: string;
    value: number;
    icon: LucideIcon;
    color: 'blue' | 'green' | 'orange' | 'purple';
    trend?: string;
}

const StatsCard: React.FC<StatsCardProps> = ({ title, value, icon: Icon, color, trend }) => {
    const colorClasses = {
        blue: {
            bg: 'bg-blue-50',
            icon: 'text-blue-600',
            text: 'text-blue-900',
            accent: 'border-blue-200'
        },
        green: {
            bg: 'bg-green-50',
            icon: 'text-green-600',
            text: 'text-green-900',
            accent: 'border-green-200'
        },
        orange: {
            bg: 'bg-orange-50',
            icon: 'text-orange-600',
            text: 'text-orange-900',
            accent: 'border-orange-200'
        },
        purple: {
            bg: 'bg-purple-50',
            icon: 'text-purple-600',
            text: 'text-purple-900',
            accent: 'border-purple-200'
        }
    };

    const styles = colorClasses[color];

    return (
        <div className={`${styles.bg} ${styles.accent} border rounded-lg p-6 transition-all duration-200 hover:shadow-md`}>
            <div className="flex items-center justify-between">
                <div>
                    <p className="text-sm font-medium text-gray-600 mb-1">{title}</p>
                    <p className={`text-3xl font-bold ${styles.text}`}>{value}</p>
                    {trend && (
                        <p className="text-xs text-gray-500 mt-1">{trend}</p>
                    )}
                </div>
                <div className={`${styles.icon} bg-white p-3 rounded-lg shadow-sm`}>
                    <Icon className="h-6 w-6" />
                </div>
            </div>
        </div>
    );
};

export default StatsCard;