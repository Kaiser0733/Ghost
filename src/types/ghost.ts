// Ghost — Phase 1 Type Definitions
// Based on PRODUCT_SPEC.md Section 5, DECISION-002, DECISION-003

export type VenueCategory =
  | 'indoor_commercial'
  | 'indoor_public'
  | 'outdoor_park'
  | 'outdoor_street'
  | 'transit'
  | 'unknown';

export type EncounterPhase =
  | 'anonymous'              // No reveals yet
  | 'revealed_by_a'          // User A revealed, waiting for B
  | 'revealed_by_b'          // User B revealed, waiting for A
  | 'mutual'                 // Both revealed
  | 'connected'              // Connection formed (persistent)
  | 'faded';                 // Encounter faded/expired

export type UserPerspective = 'user_a' | 'user_b';

export interface SimulatedUser {
  id: 'user_a' | 'user_b';
  username: string;
  ghostId: string;
  bio: string;
  photoUrl: string;              // Empty string = initials placeholder
  initials: string;
}

export interface Encounter {
  id: string;
  timestamp: number;             // Unix ms when proximity was detected
  venueCategory: VenueCategory;
  ttl: number;                   // Ms until expiry (default: 72h)
  phase: EncounterPhase;
  // Reveal tracking
  userARevealed: boolean;
  userBRevealed: boolean;
  // Connection
  connectionId?: string;
  connectedAt?: number;
}

export interface GhostProfile {
  username: string;
  ghostId: string;
  bio: string;
  photoUrl: string;
  initials: string;
}

export interface ConnectionRecord {
  id: string;
  username: string;
  ghostId: string;
  photoUrl: string;
  initials: string;
  bio: string;
  firstCrossedPaths: number;     // Unix ms
  connectedAt: number;           // Unix ms
  status: 'mutual';
}

export type ScreenId = 'home' | 'encounters' | 'profile';

// Encounter Actions for reducer
export type EncounterAction =
  | { type: 'REVEAL'; perspective: UserPerspective }
  | { type: 'REVEAL_BACK'; perspective: UserPerspective }
  | { type: 'LET_FADE'; perspective: UserPerspective }
  | { type: 'SWITCH_USER'; perspective: UserPerspective }
  | { type: 'RESET_ENCOUNTER' };

// Central simulation state
export interface SimulationState {
  activePerspective: UserPerspective;
  userA: SimulatedUser;
  userB: SimulatedUser;
  encounter: Encounter | null;
  // Derived: connections formed (persistent across encounters)
  connections: ConnectionRecord[];
}

// Helpers for getting the other perspective
export function otherPerspective(p: UserPerspective): UserPerspective {
  return p === 'user_a' ? 'user_b' : 'user_a';
}

export function getUserByPerspective(state: SimulationState, p: UserPerspective): SimulatedUser {
  return p === 'user_a' ? state.userA : state.userB;
}

export function getOtherUserByPerspective(state: SimulationState, p: UserPerspective): SimulatedUser {
  return p === 'user_a' ? state.userB : state.userA;
}

// Encounter phase helpers
export function isRevealedByMe(encounter: Encounter, perspective: UserPerspective): boolean {
  return perspective === 'user_a' ? encounter.userARevealed : encounter.userBRevealed;
}

export function isRevealedByOther(encounter: Encounter, perspective: UserPerspective): boolean {
  return perspective === 'user_a' ? encounter.userBRevealed : encounter.userARevealed;
}

export function isMutual(encounter: Encounter): boolean {
  return encounter.userARevealed && encounter.userBRevealed;
}

export function isWaitingForOther(encounter: Encounter, perspective: UserPerspective): boolean {
  return isRevealedByMe(encounter, perspective) && !isRevealedByOther(encounter, perspective);
}

export function isAnonymous(encounter: Encounter): boolean {
  return !encounter.userARevealed && !encounter.userBRevealed;
}

export function isFaded(encounter: Encounter): boolean {
  return encounter.phase === 'faded';
}

export function isConnected(encounter: Encounter): boolean {
  return encounter.phase === 'connected';
}