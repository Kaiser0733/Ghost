// Ghost — Home Screen
// Quiet, intentional, sparse. "The people you almost met."

import type { ScreenId } from '../types/ghost';
import { useEncounter } from '../context/EncounterContext';

interface HomeProps {
  onNavigate: (id: ScreenId) => void;
}

export function Home({ onNavigate }: HomeProps) {
  const { encounter } = useEncounter();

  // Determine home state from encounter
  let stateLabel = 'Currently';
  let stateRows = [
    { icon: '○', text: 'No active encounters', dim: true },
  ];

  if (encounter) {
    if (encounter.phase === 'faded') {
      stateRows = [{ icon: '○', text: '1 faded encounter', dim: true }];
    } else if (encounter.phase === 'connected' || !!encounter.connectionId) {
      stateRows = [{ icon: '●', text: '1 connection', dim: false }];
    } else if (encounter.userARevealed && encounter.userBRevealed) {
      stateRows = [{ icon: '●', text: '1 mutual reveal', dim: false }];
    } else if (encounter.userARevealed || encounter.userBRevealed) {
      stateRows = [{ icon: '◐', text: '1 awaiting response', dim: false }];
    } else {
      stateRows = [{ icon: '○', text: '1 active encounter', dim: true }];
    }
  }

  return (
    <div className="screen home">
      <div className="home-hero">
        <h1 className="home-wordmark">Ghost</h1>
        <p className="home-tagline">The people you almost met.</p>
      </div>

      <div className="home-state">
        <p className="home-state-label">{stateLabel}</p>
        {stateRows.map((row, i) => (
          <div key={i} className="home-state-row">
            <span className="home-state-glyph" aria-hidden="true">{row.icon}</span>
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

      <p className="home-footnote">No profile to maintain. No feed to scroll. Just the world you already move through.</p>
    </div>
  );
}