// Ghost — Encounter Reducer
// Pure reducer function for the encounter state machine.
// No side effects, no async, fully deterministic.

import type {
  SimulationState,
  EncounterAction,
  UserPerspective,
  SimulatedUser,
  Encounter,
  EncounterPhase,
} from '../types/ghost';

// Initial encounter state factory
export function createInitialEncounter(): Encounter {
  return {
    id: 'enc-sim-001',
    timestamp: Date.now() - 18 * 60 * 1000, // 18 minutes ago
    venueCategory: 'indoor_commercial',
    ttl: 72 * 60 * 60 * 1000,
    phase: 'anonymous',
    userARevealed: false,
    userBRevealed: false,
  };
}

// Initial state factory
export function createInitialState(userA: SimulatedUser, userB: SimulatedUser): SimulationState {
  return {
    activePerspective: 'user_a',
    userA,
    userB,
    encounter: createInitialEncounter(),
    connections: [],
  };
}

// Helper: get encounter phase display label
export function getPhaseLabel(phase: EncounterPhase): string {
  switch (phase) {
    case 'anonymous':
      return 'Anonymous';
    case 'revealed_by_a':
      return 'Revealed by A';
    case 'revealed_by_b':
      return 'Revealed by B';
    case 'mutual':
      return 'Mutual';
    case 'connected':
      return 'Connected';
    case 'faded':
      return 'Faded';
  }
}

// Pure reducer
export function encounterReducer(state: SimulationState, action: EncounterAction): SimulationState {
  const { encounter } = state;

  // If no encounter, nothing to do (except switch user)
  if (!encounter && action.type !== 'SWITCH_USER' && action.type !== 'RESET_ENCOUNTER') {
    return state;
  }

  switch (action.type) {
    case 'SWITCH_USER': {
      return {
        ...state,
        activePerspective: action.perspective,
      };
    }

    case 'RESET_ENCOUNTER': {
      return {
        ...state,
        encounter: createInitialEncounter(),
      };
    }

    case 'REVEAL': {
      if (!encounter || encounter.phase === 'faded' || (encounter.userARevealed && encounter.userBRevealed)) {
        return state; // Can't reveal if faded or already mutual
      }

      const perspective = action.perspective;

      const newEncounter: Encounter = {
        ...encounter,
        phase: perspective === 'user_a' ? 'revealed_by_a' : 'revealed_by_b',
        userARevealed: perspective === 'user_a' ? true : encounter.userARevealed,
        userBRevealed: perspective === 'user_b' ? true : encounter.userBRevealed,
      };

      // If other already revealed -> mutual
      if ((perspective === 'user_a' && encounter.userBRevealed) ||
          (perspective === 'user_b' && encounter.userARevealed)) {
        newEncounter.phase = 'mutual';
      }

      return {
        ...state,
        encounter: newEncounter,
      };
    }

    case 'REVEAL_BACK': {
      if (!encounter || encounter.phase === 'faded') {
        return state; // Can't reveal back if faded
      }

      // REVEAL_BACK is only valid if the other user revealed first
      const perspective = action.perspective;
      const otherRevealed = perspective === 'user_a' ? encounter.userBRevealed : encounter.userARevealed;

      if (!otherRevealed) {
        return state; // Can't reveal back if other hasn't revealed
      }

      const newEncounter: Encounter = {
        ...encounter,
        phase: 'mutual',
        userARevealed: true,
        userBRevealed: true,
        connectionId: `conn-${Date.now()}`,
        connectedAt: Date.now(),
      };

      // Create connection record for both users
      const otherUser = perspective === 'user_a' ? state.userB : state.userA;

      const connectionForMe = {
        id: newEncounter.connectionId!,
        username: otherUser.username,
        ghostId: otherUser.ghostId,
        photoUrl: otherUser.photoUrl,
        initials: otherUser.initials,
        bio: otherUser.bio,
        firstCrossedPaths: encounter.timestamp,
        connectedAt: newEncounter.connectedAt!,
        status: 'mutual' as const,
      };

      return {
        ...state,
        encounter: newEncounter,
        connections: [...state.connections, connectionForMe],
      };
    }

    case 'LET_FADE': {
      if (!encounter || encounter.phase === 'faded' || (encounter.userARevealed && encounter.userBRevealed)) {
        return state; // Can't fade if already mutual or faded
      }

      // Fading only makes sense if the other user revealed first
      const perspective = action.perspective;
      const otherRevealed = perspective === 'user_a' ? encounter.userBRevealed : encounter.userARevealed;

      if (!otherRevealed) {
        return state; // Nothing to fade if other hasn't revealed
      }

      const newEncounter: Encounter = {
        ...encounter,
        phase: 'faded',
      };

      return {
        ...state,
        encounter: newEncounter,
      };
    }

    default:
      return state;
  }
}

// Selectors for common derived state
export function getCurrentUser(state: SimulationState): SimulatedUser {
  return state.activePerspective === 'user_a' ? state.userA : state.userB;
}

export function getOtherUser(state: SimulationState): SimulatedUser {
  return state.activePerspective === 'user_a' ? state.userB : state.userA;
}

export function getEncounterForPerspective(state: SimulationState): Encounter | null {
  return state.encounter;
}

export function getCurrentPerspective(state: SimulationState): UserPerspective {
  return state.activePerspective;
}

export function getConnectionsForCurrentUser(state: SimulationState) {
  return state.connections;
}