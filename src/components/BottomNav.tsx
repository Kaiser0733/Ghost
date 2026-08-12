import type { ScreenId } from '../types/ghost';

interface BottomNavProps {
  active: ScreenId;
  onNavigate: (id: ScreenId) => void;
}

const NAV_ITEMS: { id: ScreenId; label: string; icon: string }[] = [
  { id: 'home', label: 'Home', icon: '⌂' },
  { id: 'encounters', label: 'Encounters', icon: '○' },
  { id: 'profile', label: 'Profile', icon: '◍' },
];

export function BottomNav({ active, onNavigate }: BottomNavProps) {
  return (
    <nav className="bottom-nav" role="navigation" aria-label="Primary">
      {NAV_ITEMS.map((item) => (
        <button
          key={item.id}
          className={`nav-item ${active === item.id ? 'nav-active' : ''}`}
          onClick={() => onNavigate(item.id)}
          aria-current={active === item.id ? 'page' : undefined}
        >
          <span className="nav-icon" aria-hidden="true">{item.icon}</span>
          <span className="nav-label">{item.label}</span>
        </button>
      ))}
    </nav>
  );
}
