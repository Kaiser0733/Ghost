import type { ScreenId } from '../types/ghost';

interface BottomNavProps {
  active: ScreenId;
  onNavigate: (id: ScreenId) => void;
}

// Navigation icons using founder-provided PNG assets
const NAV_ITEMS: { id: ScreenId; label: string; iconSrc: string }[] = [
  { id: 'home', label: 'Home', iconSrc: '/src/assets/icons/home_optimized.png' },
  { id: 'encounters', label: 'Encounters', iconSrc: '/src/assets/icons/intrection_optimized.png' },
  { id: 'profile', label: 'Profile', iconSrc: '/src/assets/icons/profile_optimized.png' },
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
            <img
              src={item.iconSrc}
              alt=""
              width="24"
              height="24"
              loading="eager"
              decoding="async"
            />
          </span>
          <span className="nav-label">{item.label}</span>
        </button>
      ))}
    </nav>
  );
}