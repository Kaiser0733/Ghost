import type { ScreenId } from '../types/ghost';
import { useEncounter } from '../context/EncounterContext';

interface HomeProps {
  onNavigate: (id: ScreenId) => void;
}

export function Home({ onNavigate }: HomeProps) {
  const { encounter } = useEncounter();

  // Determine home state from encounter
  let stateLabel = 'No active encounters';
  let stateIcon = '○';
  let stateText = 'No active encounters';

  if (encounter) {
    if (encounter.phase === 'faded') {
      stateLabel = 'Currently';
      stateIcon = '○';
      stateText = '1 faded encounter';
    } else if (encounter.phase === 'connected') {
      stateLabel = 'Currently';
      stateIcon = '●';
      stateText = '1 connection';
    } else if (encounter.userARevealed && encounter.userBRevealed) {
      stateLabel = 'Currently';
      stateIcon = '●';
      stateText = '1 mutual reveal';
    } else if (encounter.userARevealed || encounter.userBRevealed) {
      stateLabel = 'Currently';
      stateIcon = '◐';
      stateText = '1 awaiting response';
    } else {
      stateLabel = 'Currently';
      stateIcon = '○';
      stateText = '1 active encounter';
    }
  }

  return (
    <div className="screen home">
      <div className="home-hero">
        <h1 className="home-title">Ghost</h1>
        <p className="home-tagline">The people you almost met.</p>
      </div>

      <div className="home-state">
        <p className="home-state-label">{stateLabel}</p>
        <div className="home-state-row">
          <span className="home-state-icon" aria-hidden="true">{stateIcon}</span>
          <span className="home-state-text">{stateText}</span>
        </div>
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