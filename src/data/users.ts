// Ghost — Phase 1 Simulated Users
// User A and User B profiles for the prototype.
// Per DECISION-002: each user has username, Ghost ID, one photo, short bio.

import type { SimulatedUser } from '../types/ghost';

// User A — The initial prototype user
export const USER_A: SimulatedUser = {
  id: 'user_a',
  username: 'Nocturne',
  ghostId: 'ghost-7Xk2mQ-94Fp',
  bio: 'Quiet places, loud thoughts. Usually near the river.',
  photoUrl: '', // Empty = initials placeholder
  initials: 'NO',
};

// User B — The second simulated user
export const USER_B: SimulatedUser = {
  id: 'user_b',
  username: 'Marlow',
  ghostId: 'ghost-K9pL3m-12Qr',
  bio: 'Coffee at dawn. Books at dusk.',
  photoUrl: '', // Empty = initials placeholder
  initials: 'MA',
};

// Export both for easy access
export const SIMULATED_USERS = [USER_A, USER_B] as const;

export function getUserById(id: 'user_a' | 'user_b'): SimulatedUser {
  return id === 'user_a' ? USER_A : USER_B;
}