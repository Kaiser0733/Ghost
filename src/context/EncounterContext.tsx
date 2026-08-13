// Ghost — Encounter Context
// React context + provider for the encounter simulation state.

import { createContext, useContext, useReducer, ReactNode } from 'react';
import type { SimulationState, EncounterAction, SimulatedUser, UserPerspective } from '../types/ghost';
import {
  encounterReducer,
  getCurrentUser,
  getOtherUser,
  getEncounterForPerspective,
  getCurrentPerspective,
  getConnectionsForCurrentUser,
} from './EncounterReducer';

interface EncounterContextValue {
  state: SimulationState;
  dispatch: React.Dispatch<EncounterAction>;
  // Convenience selectors
  currentUser: SimulatedUser;
  otherUser: SimulatedUser;
  encounter: ReturnType<typeof getEncounterForPerspective>;
  perspective: UserPerspective;
  connections: ReturnType<typeof getConnectionsForCurrentUser>;
  // Actions
  reveal: () => void;
  revealBack: () => void;
  letFade: () => void;
  switchUser: (perspective: UserPerspective) => void;
  resetEncounter: () => void;
}

const EncounterContext = createContext<EncounterContextValue | null>(null);

interface EncounterProviderProps {
  children: ReactNode;
  initialState: SimulationState;
}

export function EncounterProvider({ children, initialState }: EncounterProviderProps) {
  const [state, dispatch] = useReducer(encounterReducer, initialState);

  const value: EncounterContextValue = {
    state,
    dispatch,
    currentUser: getCurrentUser(state),
    otherUser: getOtherUser(state),
    encounter: getEncounterForPerspective(state),
    perspective: getCurrentPerspective(state),
    connections: getConnectionsForCurrentUser(state),
    reveal: () => dispatch({ type: 'REVEAL', perspective: getCurrentPerspective(state) }),
    revealBack: () => dispatch({ type: 'REVEAL_BACK', perspective: getCurrentPerspective(state) }),
    letFade: () => dispatch({ type: 'LET_FADE', perspective: getCurrentPerspective(state) }),
    switchUser: (perspective: UserPerspective) => dispatch({ type: 'SWITCH_USER', perspective }),
    resetEncounter: () => dispatch({ type: 'RESET_ENCOUNTER' }),
  };

  return (
    <EncounterContext.Provider value={value}>
      {children}
    </EncounterContext.Provider>
  );
}

export function useEncounter(): EncounterContextValue {
  const context = useContext(EncounterContext);
  if (!context) {
    throw new Error('useEncounter must be used within an EncounterProvider');
  }
  return context;
}