// Ghost — Home Screen
// Quiet, intentional, sparse. "The people you almost met."

import type { ScreenId } from '../types/ghost';
import { useEncounter } from '../context/EncounterContext';

// Custom SVG Icons for home state glyphs - specialized states only
const HomeWaitingIcon = () => (
  <svg viewBox="0 0 24 24" width="100%" height="100%" stroke="currentColor" fill="none" strokeWidth="1.5" strokeLinecap="round" aria-hidden="true">
    <path d="M12 4a8 8 0 0 1 8 8c0 2.5-1.5 4.5-4 6-2.5-1.5-4-3.5-4-6a8 8 0 0 1 8-8z"/>
    <path d="M12 8v4l3 3"/>
  </svg>
);

const HomeMutualIcon = () => (
  <svg viewBox="0 0 24 24" width="100%" height="100%" stroke="currentColor" fill="none" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    <path d="M8 12c0-2.2 1.8-4 4-4s4 1.8 4 4"/>
    <path d="M16 12c0 2.2-1.8 4-4 4s-4-1.8-4-4"/>
  </svg>
);

// Founder-provided encounter asset for neutral/anonymous encounter state
const HomeEncounterIcon = () => (
  <img
    src="/src/assets/encounter/encounter_optimized.png"
    alt=""
    className="home-encounter-icon"
    width="32"
    height="32"
    loading="eager"
    decoding="async"
    aria-hidden="true"
  />
);

interface HomeProps {
  onNavigate: (id: ScreenId) => void;
}

export function Home({ onNavigate }: HomeProps) {
  const { encounter } = useEncounter();

  // Determine home state from encounter
  let stateLabel = 'Currently';
  let stateRows = [
    { Icon: HomeEncounterIcon, text: 'No active encounters', dim: true },
  ];

  if (encounter) {
    if (encounter.phase === 'faded') {
      stateRows = [{ Icon: HomeEncounterIcon, text: '1 faded encounter', dim: true }];
    } else if (encounter.phase === 'connected' || !!encounter.connectionId) {
      stateRows = [{ Icon: HomeMutualIcon, text: '1 connection', dim: false }];
    } else if (encounter.userARevealed && encounter.userBRevealed) {
      stateRows = [{ Icon: HomeMutualIcon, text: '1 mutual reveal', dim: false }];
    } else if (encounter.userARevealed || encounter.userBRevealed) {
      // Responder perspective: they have a reveal waiting for them
      stateRows = [{ Icon: HomeWaitingIcon, text: '1 reveal to answer', dim: false }];
    } else {
      // Waiter perspective or neutral: active encounter
      stateRows = [{ Icon: HomeEncounterIcon, text: '1 active encounter', dim: true }];
    }
  }

  return (
    <div className="screen home">
      <div className="home-hero">
        <h1 className="home-wordmark">Ghost</h1>
        <p className="home-tagline">The people you almost met.</p>
      </div>

      <div className="home-context">
        <p className="home-context-text">Ghost notices when paths cross. No profiles to maintain. No feed to scroll. Just the world you already move through.</p>
      </div>

      <div className="home-state">
        <p className="home-state-label">{stateLabel}</p>
        {stateRows.map((row, i) => (
          <div key={i} className="home-state-row">
            <span className="home-state-glyph" aria-hidden="true">
              <row.Icon />
            </span>
            <span className="home-state-text" style={{ opacity: row.dim ? 0.6 : 1 }}>{row.text}</span>
          </div>
        ))}
      </div>

      <button
        className="home-cta"
        onClick={() => onNavigate('encounters')}
      >
        View encounters
      </button>
    </div>
  );
}