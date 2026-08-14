// Ghost — Profile Screen
// Current simulated user's profile. Coherent with the rest of Ghost.

import { useEncounter } from '../context/EncounterContext';

export function Profile() {
  const { currentUser, connections } = useEncounter();

  return (
    <div className="screen profile">
      <header className="screen-header">
        <h2 className="screen-title">Profile</h2>
        <p className="screen-subtitle">Simulated prototype user.</p>
      </header>

      <div className="profile-card">
        <div className="profile-avatar" aria-hidden="true">
          {currentUser.initials}
        </div>
        <h3 className="profile-username">@{currentUser.username}</h3>
        <p className="profile-ghostid">{currentUser.ghostId}</p>
        <p className="profile-bio">{currentUser.bio}</p>
      </div>

      <div className="profile-connections">
        <p className="profile-section-label">Connections</p>
        {connections.length > 0 ? (
          connections.map((conn, i) => (
            <div key={i} className="profile-connection-item">
              <span className="profile-connection-glyph" aria-hidden="true"></span>
              <span className="profile-connection-name">@{conn.username}</span>
              <span className="profile-connection-date">
                Connected{' '}
                {new Date(conn.connectedAt).toLocaleDateString('en-US', {
                  month: 'short',
                  day: 'numeric',
                })}
              </span>
            </div>
          ))
        ) : (
          <p className="profile-footnote">No connections yet. Reveal an encounter to connect.</p>
        )}
      </div>

      <p className="profile-footnote">
        Prototype data — revealed only after mutual encounter.
      </p>
    </div>
  );
}