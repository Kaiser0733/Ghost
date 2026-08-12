// Ghost — Phase 1 Placeholder Simulation Data
// Per DECISION-002: these are UI placeholders only.
// No encounter state machine, no localStorage sync, no real detection.
// All data is clearly simulated for prototype visual validation.

import type { Encounter, GhostProfile, ConnectionRecord } from '../types/ghost';

// Simulated prototype user profile (Per DECISION-002 Section 5 & 6)
export const SIMULATED_USER: GhostProfile = {
  username: 'Nocturne',
  ghostId: 'ghost-7Xk2mQ-94Fp',
  bio: 'Quiet places, loud thoughts. Usually near the river.',
  photoUrl: '', // Empty — avatar component renders initials placeholder
};

// Placeholder Encounter A: Anonymous active encounter (DECISION-002 Section 1 & 2)
// Pre-reveal: no username, no photo, no Ghost ID, no exact location
export const PLACEHOLDER_ENCOUNTER_ACTIVE: Encounter = {
  id: 'enc-sim-001',
  timestamp: Date.now() - 18 * 60 * 1000, // 18 minutes ago
  venueCategory: 'indoor_commercial',
  ttl: 72 * 60 * 60 * 1000,
  status: 'active',
  myReveal: false,
  otherReveal: false,
};

// Placeholder Encounter B: Waiting for response (DECISION-002 Section 3)
// User A pressed REVEAL, waiting for User B
export const PLACEHOLDER_ENCOUNTER_WAITING: Encounter = {
  id: 'enc-sim-002',
  timestamp: Date.now() - 2 * 60 * 60 * 1000, // 2 hours ago
  venueCategory: 'outdoor_park',
  ttl: 72 * 60 * 60 * 1000,
  status: 'revealed_me',
  myReveal: true,
  otherReveal: false,
};

// Placeholder Connection: Mutual reveal completed (DECISION-002 Section 4 & 6)
export const PLACEHOLDER_CONNECTION: ConnectionRecord = {
  id: 'conn-sim-001',
  username: 'Marlow',
  firstCrossedPaths: Date.now() - 26 * 60 * 60 * 1000, // ~1 day ago
  status: 'mutual',
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
    indoor_commercial: 'Indoor · Commercial',
    indoor_public: 'Indoor · Public',
    outdoor_park: 'Outdoor · Park',
    outdoor_street: 'Outdoor · Street',
    transit: 'Transit',
    unknown: 'Unknown location',
  };
  return labels[category] ?? 'Unknown location';
}
