import type { ScreenId } from '../types/ghost';

interface HomeProps {
  onNavigate: (id: ScreenId) => void;
}

export function Home({ onNavigate }: HomeProps) {
  return (
    <div className="screen home">
      <div className="home-hero">
        <h1 className="home-title">Ghost</h1>
        <p className="home-tagline">The people you almost met.</p>
      </div>

      <div className="home-state">
        <p className="home-state-label">Currently</p>
        <div className="home-state-row">
          <span className="home-state-icon" aria-hidden="true">○</span>
          <span className="home-state-text">1 active encounter</span>
        </div>
        <div className="home-state-row">
          <span className="home-state-icon" aria-hidden="true">◐</span>
          <span className="home-state-text">1 awaiting response</span>
        </div>
        <div className="home-state-row">
          <span className="home-state-icon" aria-hidden="true">●</span>
          <span className="home-state-text">1 connection</span>
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
