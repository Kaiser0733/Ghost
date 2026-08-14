// Ghost — User Switcher (Development Only)
// Allows switching between User A and User B perspectives.
// Visually distinct as a dev/test control. Easy to remove.

import { useEncounter } from '../context/EncounterContext';

export function UserSwitcher() {
  const { perspective, switchUser } = useEncounter();

  return (
    <div className="user-switcher" role="region" aria-label="Prototype user switcher">
      <span className="switcher-label">Prototype — viewing as</span>
      <div className="switcher-buttons">
        <button
          className={`switcher-btn ${perspective === 'user_a' ? 'active' : ''}`}
          onClick={() => switchUser('user_a')}
          aria-pressed={perspective === 'user_a'}
        >
          @Nocturne
        </button>
        <button
          className={`switcher-btn ${perspective === 'user_b' ? 'active' : ''}`}
          onClick={() => switchUser('user_b')}
          aria-pressed={perspective === 'user_b'}
        >
          @Marlow
        </button>
      </div>
    </div>
  );
}