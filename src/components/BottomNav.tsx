import type { ScreenId } from '../types/ghost';

interface BottomNavProps {
  active: ScreenId;
  onNavigate: (id: ScreenId) => void;
}

// Custom SVG Icons for bottom navigation
const HomeIcon = () => (
  <svg viewBox="0 0 24 24" width="100%" height="100%" stroke="currentColor" fill="none" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
    <polyline points="9 22 9 12 15 12 15 22"/>
  </svg>
);

const EncountersIcon = () => (
  <svg viewBox="0 0 24 24" width="100%" height="100%" stroke="currentColor" fill="none" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
    <circle cx="9" cy="7" r="4"/>
    <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
    <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
  </svg>
);

const ProfileIcon = () => (
  <svg viewBox="0 0 24 24" width="100%" height="100%" stroke="currentColor" fill="none" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
    <circle cx="12" cy="7" r="4"/>
  </svg>
);

const NAV_ITEMS: { id: ScreenId; label: string; Icon: React.ComponentType<{}> }[] = [
  { id: 'home', label: 'Home', Icon: HomeIcon },
  { id: 'encounters', label: 'Encounters', Icon: EncountersIcon },
  { id: 'profile', label: 'Profile', Icon: ProfileIcon },
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
          <span className="nav-icon" aria-hidden="true">
            <item.Icon />
          </span>
          <span className="nav-label">{item.label}</span>
        </button>
      ))}
    </nav>
  );
}