import { SIMULATED_USER } from '../data/placeholders';

export function Profile() {
  const user = SIMULATED_USER;
  const initials = user.username.slice(0, 2).toUpperCase();

  return (
    <div className="screen profile">
      <header className="screen-header">
        <h2 className="screen-title">Profile</h2>
        <p className="screen-subtitle">Simulated prototype user.</p>
      </header>

      <div className="profile-card">
        <div className="profile-avatar" aria-hidden="true">
          {initials}
        </div>
        <h3 className="profile-username">@{user.username}</h3>
        <p className="profile-ghostid">{user.ghostId}</p>
        <p className="profile-bio">{user.bio}</p>
      </div>

      <div className="profile-connections">
        <p className="profile-section-label">Connections</p>
        <div className="profile-connection-item">
          <span className="profile-connection-dot" aria-hidden="true">●</span>
          <span className="profile-connection-name">@Marlow</span>
          <span className="profile-connection-date">Connected 1 day ago</span>
        </div>
      </div>

      <p className="profile-footnote">
        Prototype data — revealed only after mutual encounter.
      </p>
    </div>
  );
}
