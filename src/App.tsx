import { useState, useCallback, useEffect } from 'react';
import { Home } from './screens/Home';
import { Encounters } from './screens/Encounters';
import { Profile } from './screens/Profile';
import { BottomNav } from './components/BottomNav';
import { UserSwitcher } from './components/UserSwitcher';
import { EncounterProvider } from './context/EncounterContext';
import { USER_A, USER_B } from './data/users';
import { createInitialState } from './context/EncounterReducer';

export default function App() {
  const [screen, setScreen] = useState<'home' | 'encounters' | 'profile'>('home');
  const [initialized, setInitialized] = useState(false);
  const [initialState, setInitialState] = useState<ReturnType<typeof createInitialState> | null>(null);

  // Initialize state with real users on mount
  useEffect(() => {
    const state = createInitialState(USER_A, USER_B);
    setInitialState(state);
    setInitialized(true);
  }, []);

  const navigate = useCallback((id: 'home' | 'encounters' | 'profile') => {
    setScreen(id);
  }, []);

  if (!initialized || !initialState) {
    return (
      <div className="app-shell">
        <div className="loading">Loading...</div>
      </div>
    );
  }

  return (
    <EncounterProvider initialState={initialState}>
      <div className="app-shell">
        <UserSwitcher />
        <main className="app-main">
          {screen === 'home' && <Home onNavigate={navigate} />}
          {screen === 'encounters' && <Encounters />}
          {screen === 'profile' && <Profile />}
        </main>
        <BottomNav active={screen} onNavigate={navigate} />
      </div>
    </EncounterProvider>
  );
}