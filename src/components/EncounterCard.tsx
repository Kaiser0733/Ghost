// Ghost — Encounter Card
// Renders an encounter from the current user's perspective.
// Handles all phases: anonymous, waiting, notification, mutual, connected, faded.

import { useEncounter } from '../context/EncounterContext';
import type { Encounter } from '../types/ghost';
import { formatElapsed, venueLabel } from '../data/placeholders';

interface EncounterCardProps {
  encounter: Encounter | null;
  perspective: 'user_a' | 'user_b';
}

export function EncounterCard({ encounter, perspective }: EncounterCardProps) {
  const { reveal, revealBack, letFade } = useEncounter();

  if (!encounter) return null;

  // Determine what to show based on encounter phase and perspective
  const meRevealed = perspective === 'user_a' ? encounter.userARevealed : encounter.userBRevealed;
  const otherRevealed = perspective === 'user_a' ? encounter.userBRevealed : encounter.userARevealed;

  // Don't render faded encounters
  if (encounter.phase === 'faded') return null;

  const elapsed = formatElapsed(encounter.timestamp);
  const venue = venueLabel(encounter.venueCategory);

  // Anonymous: no one has revealed
  if (!meRevealed && !otherRevealed) {
    return (
      <div className="encounter-card encounter-anonymous">
        <div className="encounter-marker" aria-hidden="true">○</div>
        <div className="encounter-body">
          <p className="encounter-line">You crossed paths with someone.</p>
          <p className="encounter-meta">You were near each other {elapsed}.</p>
          <p className="encounter-venue">{venue}</p>
        </div>
        <div className="encounter-actions">
          <button className="btn-reveal" onClick={reveal}>Reveal</button>
          <button className="btn-fade" onClick={letFade}>Let it fade</button>
        </div>
      </div>
    );
  }

  // Waiting: I revealed, waiting for other
  if (meRevealed && !otherRevealed) {
    return (
      <div className="encounter-card encounter-waiting">
        <div className="encounter-marker" aria-hidden="true">◐</div>
        <div className="encounter-body">
          <p className="encounter-line">Someone remembers this encounter.</p>
          <p className="encounter-meta">You were near each other {elapsed}.</p>
          <p className="encounter-venue">{venue}</p>
          <p className="encounter-status">You chose to reveal yourself. Waiting.</p>
        </div>
        <div className="encounter-actions">
          <button className="btn-reveal" disabled>Reveal sent</button>
        </div>
      </div>
    );
  }

  // Notification: other revealed, I haven't
  if (!meRevealed && otherRevealed) {
    return (
      <div className="encounter-card encounter-notification">
        <div className="encounter-marker" aria-hidden="true">◐</div>
        <div className="encounter-body">
          <p className="encounter-line">They chose to reveal themselves.</p>
          <p className="encounter-meta">You were near each other {elapsed}.</p>
          <p className="encounter-venue">{venue}</p>
          <p className="encounter-status">Someone remembers this encounter.</p>
        </div>
        <div className="encounter-actions">
          <button className="btn-reveal" onClick={revealBack}>Reveal back</button>
          <button className="btn-fade" onClick={letFade}>Let it fade</button>
        </div>
      </div>
    );
  }

  // Mutual or Connected: both revealed
  // For mutual, we need the other user's profile
  return (
    <div className="encounter-card encounter-connected">
      <div className="encounter-marker" aria-hidden="true">●</div>
      <div className="encounter-body">
        <p className="encounter-line-emphasis">It's mutual.</p>
        <p className="encounter-meta">You both remembered.</p>
        <div className="connection-detail">
          <p className="connection-username">CONNECTION — @Marlow</p>
          <p className="connection-meta">
            First crossed paths:{' '}
            {new Date(encounter.timestamp).toLocaleDateString('en-US', {
              month: 'short',
              day: 'numeric',
            })} ·{' '}
            {new Date(encounter.timestamp).toLocaleTimeString('en-US', {
              hour: 'numeric',
              minute: '2-digit',
            })}
          </p>
          <p className="connection-status">Status: Mutual</p>
        </div>
      </div>
    </div>
  );
}