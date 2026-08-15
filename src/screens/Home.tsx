// Ghost — Home Screen
// Quiet, intentional, sparse. "The people you almost met."

import type { ScreenId } from '../types/ghost';
import { useEncounter } from '../context/EncounterContext';

// Founder-provided encounter asset for all encounter states
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
      stateRows = [{ Icon: HomeEncounterIcon, text: '1 connection', dim: false }];
    } else if (encounter.userARevealed && encounter.userBRevealed) {
      stateRows = [{ Icon: HomeEncounterIcon, text: '1 mutual reveal', dim: false }];
    } else if (encounter.userARevealed || encounter.userBRevealed) {
      // Responder perspective: they have a reveal waiting for them
      stateRows = [{ Icon: HomeEncounterIcon, text: '1 reveal to answer', dim: false }];
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