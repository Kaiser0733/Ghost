import { useState, useCallback } from 'react';
import type { ScreenId } from './types/ghost';
import { Home } from './screens/Home';
import { Encounters } from './screens/Encounters';
import { Profile } from './screens/Profile';
import { BottomNav } from './components/BottomNav';

export default function App() {
  const [screen, setScreen] = useState<ScreenId>('home');

  const navigate = useCallback((id: ScreenId) => {
    setScreen(id);
  }, []);

  return (
    <div className="app-shell">
      <main className="app-main">
        {screen === 'home' && <Home onNavigate={navigate} />}
        {screen === 'encounters' && <Encounters />}
        {screen === 'profile' && <Profile />}
      </main>
      <BottomNav active={screen} onNavigate={navigate} />
    </div>
  );
}
