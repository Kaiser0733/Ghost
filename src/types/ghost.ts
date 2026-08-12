// Ghost — Phase 1 Type Definitions
// Based on PRODUCT_SPEC.md Section 5 and DECISION-002

export type VenueCategory =
  | 'indoor_commercial'
  | 'indoor_public'
  | 'outdoor_park'
  | 'outdoor_street'
  | 'transit'
  | 'unknown';

export type EncounterStatus =
  | 'active'
  | 'revealed_me'
  | 'revealed_both'
  | 'expired';

export interface Encounter {
  id: string;
  timestamp: number;
  venueCategory: VenueCategory;
  ttl: number;
  status: EncounterStatus;
  myReveal: boolean;
  otherReveal: boolean;
  connectionId?: string;
}

export interface GhostProfile {
  username: string;
  ghostId: string;
  bio: string;
  photoUrl: string;
}

export interface ConnectionRecord {
  id: string;
  username: string;
  firstCrossedPaths: number;
  status: 'mutual';
}

export type ScreenId = 'home' | 'encounters' | 'profile';
