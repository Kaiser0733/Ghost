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

// Custom SVG Icons for encounter states
const WaitingIcon = () => (
  <svg viewBox="0 0 24 24" width="100%" height="100%" stroke="currentColor" fill="none" strokeWidth="1.2" strokeLinecap="round" aria-hidden="true">
    <path d="M12 4a8 8 0 0 1 8 8c0 2.5-1.5 4.5-4 6-2.5-1.5-4-3.5-4-6a8 8 0 0 1 8-8z"/>
    <path d="M12 8v4l3 3" strokeWidth="2"/>
  </svg>
);

const IncomingIcon = () => (
  <svg viewBox="0 0 24 24" width="100%" height="100%" stroke="currentColor" fill="none" strokeWidth="1.2" strokeLinecap="round" aria-hidden="true">
    <path d="M12 4a8 8 0 0 1 8 8c0 2.5-1.5 4.5-4 6-2.5-1.5-4-3.5-4-6a8 8 0 0 1 8-8z"/>
    <path d="M12 16v-6" strokeWidth="2"/>
    <path d="M9 13l3-3 3 3" strokeWidth="2"/>
  </svg>
);

const MutualIcon = () => (
  <svg viewBox="0 0 24 24" width="100%" height="100%" stroke="currentColor" fill="none" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    <path d="M8 12c0-2.2 1.8-4 4-4s4 1.8 4 4"/>
    <path d="M16 12c0 2.2-1.8 4-4 4s-4-1.8-4-4"/>
  </svg>
);

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
      <article className="encounter-card anonymous">
        <div className="encounter-presence">
          <div className="presence-silhouette">
            <div className="presence-shimmer" />
          </div>
        </div>
        <div className="encounter-body">
          <p className="encounter-lead">You crossed paths with someone.</p>
          <p className="encounter-meta">You were near each other <strong>{elapsed}</strong>.</p>
          <p className="encounter-context">Your paths overlapped for about 23 minutes.</p>
          <p className="encounter-venue">{venue}</p>
        </div>
        <div className="encounter-actions">
          <button className="btn btn-primary" onClick={reveal}>Reveal</button>
          <button className="btn btn-secondary" onClick={letFade}>Let it fade</button>
        </div>
      </article>
    );
  }

  // Waiting: I revealed, waiting for other
  if (meRevealed && !otherRevealed) {
    return (
      <article className="encounter-card waiting">
        <div className="encounter-glyph" aria-hidden="true">
          <WaitingIcon />
        </div>
        <div className="encounter-body">
          <p className="encounter-lead">You remembered this encounter.</p>
          <p className="encounter-meta">You were near each other <strong>{elapsed}</strong>.</p>
          <p className="encounter-context">Your paths overlapped for about 23 minutes.</p>
          <p className="encounter-venue">{venue}</p>
          <p className="encounter-status">You've revealed yourself. <strong>Now it's their choice.</strong></p>
        </div>
        <div className="encounter-actions">
          <button className="btn btn-primary" disabled>Waiting…</button>
        </div>
      </article>
    );
  }

  // Notification: other revealed, I haven't
  if (!meRevealed && otherRevealed) {
    return (
      <article className="encounter-card notification">
        <div className="encounter-glyph" aria-hidden="true">
          <IncomingIcon />
        </div>
        <div className="encounter-body">
          <p className="encounter-lead">Someone remembers this encounter.</p>
          <p className="encounter-meta">You were near each other <strong>{elapsed}</strong>.</p>
          <p className="encounter-context">Your paths overlapped for about 23 minutes.</p>
          <p className="encounter-venue">{venue}</p>
          <p className="encounter-status"><strong>They chose to reveal themselves.</strong></p>
        </div>
        <div className="encounter-actions">
          <button className="btn btn-primary" onClick={revealBack}>Reveal back</button>
          <button className="btn btn-secondary" onClick={letFade}>Let it fade</button>
        </div>
      </article>
    );
  }

  // Mutual: both revealed
  // Check if it's a connection (has connectionId) or just mutual
  const isConnected = encounter.phase === 'connected' || !!encounter.connectionId;

  if (isConnected) {
    // For connected state, we need the other user's profile from context
    const { otherUser } = useEncounter();
    return (
      <article className="encounter-card connected">
        <div className="encounter-glyph" aria-hidden="true">
          <MutualIcon />
        </div>
        <div className="encounter-body">
          <div className="connection-record">
            <p className="connection-header">Connection</p>
            <div className="connection-item">
              <div className="connection-avatar">{otherUser.initials}</div>
              <div className="connection-info">
                <p className="connection-username">@{otherUser.username}</p>
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
              </div>
              <span className="connection-status">Mutual</span>
            </div>
          </div>
        </div>
      </article>
    );
  }

  // Mutual reveal (just happened, not yet a persisted connection)
  const { otherUser } = useEncounter();
  return (
    <article className="encounter-card mutual mutual-reveal">
      <div className="encounter-glyph" aria-hidden="true">
        <MutualIcon />
      </div>
      <div className="encounter-body">
        <div className="mutual-header">
          <p className="mutual-lead">It's mutual.</p>
          <p className="mutual-sub">You both remembered.</p>
        </div>
        <div className="mutual-reveal-line" />
        <div className="mutual-profile">
          <div className="mutual-avatar" aria-hidden="true">{otherUser.initials}</div>
          <p className="mutual-username">@{otherUser.username}</p>
          <p className="mutual-ghostid">{otherUser.ghostId}</p>
          <p className="mutual-bio">{otherUser.bio}</p>
          <p className="mutual-footnote">Revealed only after mutual encounter.</p>
        </div>
      </div>
    </article>
  );
}