// Ghost — Phase 1 Placeholder Simulation Data
// Per DECISION-002 and DECISION-003: these are UI placeholders only.
// No encounter state machine, no localStorage sync, no real detection.
// All data is clearly simulated for prototype visual validation.

import { USER_A, USER_B } from './users';

// Simulated prototype user profile (Per DECISION-002 Section 5 & 6)
export const SIMULATED_USER = {
  username: USER_A.username,
  ghostId: USER_A.ghostId,
  bio: USER_A.bio,
  photoUrl: USER_A.photoUrl,
  initials: USER_A.initials,
};

// Placeholder Encounter A: Anonymous active encounter (DECISION-002 Section 1 & 2)
// Pre-reveal: no username, no photo, no Ghost ID, no exact location
export const PLACEHOLDER_ENCOUNTER_ACTIVE = {
  id: 'enc-sim-001',
  timestamp: Date.now() - 18 * 60 * 1000, // 18 minutes ago
  venueCategory: 'indoor_commercial' as const,
  ttl: 72 * 60 * 60 * 1000,
  phase: 'anonymous' as const,
  userARevealed: false,
  userBRevealed: false,
};

// Placeholder Encounter B: Waiting for response (DECISION-002 Section 3)
// User A pressed REVEAL, waiting for User B
export const PLACEHOLDER_ENCOUNTER_WAITING = {
  id: 'enc-sim-002',
  timestamp: Date.now() - 2 * 60 * 60 * 1000, // 2 hours ago
  venueCategory: 'outdoor_park' as const,
  ttl: 72 * 60 * 60 * 1000,
  phase: 'revealed_by_a' as const,
  userARevealed: true,
  userBRevealed: false,
};

// Placeholder Connection: Mutual reveal completed (DECISION-002 Section 4 & 6)
export const PLACEHOLDER_CONNECTION = {
  id: 'conn-sim-001',
  username: USER_B.username,
  ghostId: USER_B.ghostId,
  photoUrl: USER_B.photoUrl,
  initials: USER_B.initials,
  bio: USER_B.bio,
  firstCrossedPaths: Date.now() - 26 * 60 * 60 * 1000, // ~1 day ago
  connectedAt: Date.now() - 24 * 60 * 60 * 1000,
  status: 'mutual' as const,
};

// Helper: format elapsed time per DECISION-002 Section 1
// "You were near each other 18 minutes ago."
export function formatElapsed(timestamp: number): string {
  const diffMs = Date.now() - timestamp;
  const minutes = Math.floor(diffMs / 60000);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);
  if (days > 0) return `${days} day${days > 1 ? 's' : ''} ago`;
  if (hours > 0) return `${hours} hour${hours > 1 ? 's' : ''} ago`;
  if (minutes > 0) return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
  return 'just now';
}

// Helper: venue category to display label (no venue names per privacy principle)
export function venueLabel(category: string): string {
  const labels: Record<string, string> = {
    indoor_commercial: 'a quiet café',
    indoor_public: 'a public space',
    outdoor_park: 'a park',
    outdoor_street: 'a street corner',
    transit: 'transit',
    unknown: 'somewhere nearby',
  };
  return labels[category] ?? 'somewhere nearby';
}