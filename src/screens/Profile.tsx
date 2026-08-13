import { useEncounter } from '../context/EncounterContext';

export function Profile() {
  const { currentUser } = useEncounter();

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

      <p className="profile-footnote">
        Prototype data — revealed only after mutual encounter.
      </p>
    </div>
  );
}