import {
  PLACEHOLDER_ENCOUNTER_ACTIVE,
  PLACEHOLDER_ENCOUNTER_WAITING,
  PLACEHOLDER_CONNECTION,
  formatElapsed,
  venueLabel,
} from '../data/placeholders';

export function Encounters() {
  const active = PLACEHOLDER_ENCOUNTER_ACTIVE;
  const waiting = PLACEHOLDER_ENCOUNTER_WAITING;
  const connection = PLACEHOLDER_CONNECTION;

  return (
    <div className="screen encounters">
      <header className="screen-header">
        <h2 className="screen-title">Encounters</h2>
        <p className="screen-subtitle">Moments where paths crossed.</p>
      </header>

      {/* Anonymous active encounter — DECISION-002 Section 1 & 2 */}
      <div className="encounter-card encounter-active">
        <div className="encounter-marker" aria-hidden="true">○</div>
        <div className="encounter-body">
          <p className="encounter-line">You crossed paths with someone.</p>
          <p className="encounter-meta">
            You were near each other {formatElapsed(active.timestamp)}.
          </p>
          <p className="encounter-venue">{venueLabel(active.venueCategory)}</p>
        </div>
        <div className="encounter-actions">
          <button className="btn-reveal">Reveal</button>
          <button className="btn-fade">Let it fade</button>
        </div>
      </div>

      {/* Waiting for response — DECISION-002 Section 3 */}
      <div className="encounter-card encounter-waiting">
        <div className="encounter-marker" aria-hidden="true">◐</div>
        <div className="encounter-body">
          <p className="encounter-line">Someone remembers this encounter.</p>
          <p className="encounter-meta">
            You were near each other {formatElapsed(waiting.timestamp)}.
          </p>
          <p className="encounter-venue">{venueLabel(waiting.venueCategory)}</p>
          <p className="encounter-status">You chose to reveal yourself. Waiting.</p>
        </div>
        <div className="encounter-actions">
          <button className="btn-reveal" disabled>Reveal sent</button>
        </div>
      </div>

      {/* Reciprocal notification — DECISION-002 Section 3 (other user's perspective) */}
      <div className="encounter-card encounter-notify">
        <div className="encounter-marker" aria-hidden="true">◐</div>
        <div className="encounter-body">
          <p className="encounter-line">They chose to reveal themselves.</p>
          <p className="encounter-meta">
            You were near each other {formatElapsed(waiting.timestamp)}.
          </p>
          <p className="encounter-status">Someone remembers this encounter.</p>
        </div>
        <div className="encounter-actions">
          <button className="btn-reveal">Reveal back</button>
          <button className="btn-fade">Let it fade</button>
        </div>
      </div>

      {/* Connection — DECISION-002 Section 4 & 6 */}
      <div className="encounter-card encounter-connected">
        <div className="encounter-marker" aria-hidden="true">●</div>
        <div className="encounter-body">
          <p className="encounter-line-emphasis">It's mutual.</p>
          <p className="encounter-meta">You both remembered.</p>
          <div className="connection-detail">
            <p className="connection-username">CONNECTION — @{connection.username}</p>
            <p className="connection-meta">
              First crossed paths:{' '}
              {new Date(connection.firstCrossedPaths).toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
              })}{' '}
              ·{' '}
              {new Date(connection.firstCrossedPaths).toLocaleTimeString('en-US', {
                hour: 'numeric',
                minute: '2-digit',
              })}
            </p>
            <p className="connection-status">Status: Mutual</p>
          </div>
        </div>
      </div>

      <p className="encounters-footnote">Prototype data — encounters are simulated for design validation.</p>
    </div>
  );
}
