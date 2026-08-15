# Rose Gold Noir — Design Comparison

## Three Takes on the Encounter Experience

| Dimension | Editorial (001) | Atmospheric (002) | Minimal (003) |
|-----------|-----------------|-------------------|---------------|
| **Density** | Generous, calm | Generous, immersive | Tighter, utilitarian |
| **Ambient Depth** | Dual radial gradients | Three radial layers + glows | Single subtle gradient |
| **Anonymous Presence** | Silhouette + shimmer | Silhouette + aura pulse | Simple ring + inner pulse |
| **Card Hover** | Border color shift | Top accent line reveal | Border color shift only |
| **Waiting/Notification** | Border + glow + pulse | Stronger glow + inset highlight | Border color only |
| **Mutual Reveal** | 600ms staged emergence | 700ms blur→sharp + ring expand | 500ms simple scale/fade |
| **Avatar Entrance** | Scale + fade | Scale + fade + expanding ring | Scale + fade |
| **Bottom Nav** | Standard | Animated indicator bar | Flat, no indicator |
| **Visual Weight** | Balanced premium | Cinematic, moody | Light, fast |
| **GPU Cost** | Low | Medium (gradients/glows) | Lowest |

## Recommendation: **Editorial (001)**

**Why:**
- Best balances the founder's requirements: "warm + elegant + modern + human + slightly nocturnal"
- Custom anonymous-presence illustration (silhouette + shimmer) directly addresses "current icons too primitive" and "design the encounter card as an actual product object"
- 600ms choreographed mutual reveal with staged emergence (avatar → username → bio → timestamp) delivers "real designed reveal moment" without dating-app effects
- Custom SVG icon set for nav (home/world, crossing paths, silhouette) and encounter states (anonymous/waiting/incoming/mutual/faded) replaces primitive ○/◐/●
- Rose-gold identity (#B76E79) feels like Ghost's signature without being "overly pink"
- Clear card-to-background separation via visibly lighter rose-toned card surface
- Human-readable context line ("Your paths overlapped for about 23 minutes") replaces cryptic "INDOOR · COMMERCIAL"
- All accessibility considerations met: prefers-reduced-motion, contrast, focus states

**Atmospheric (002)** is a strong runner-up for users who want more cinematic immersion, but the heavier glows/gradients risk "gothic/funeral" feel the founder explicitly wanted to avoid.

**Minimal (003)** is too utilitarian — loses the "emotional resonance" and "premium feel" the founder asked for.

## Next Steps
1. Implement Editorial variant as the production Rose Gold Noir design
2. Update `src/styles/global.css` with DESIGN.md token spec
3. Update `EncounterCard.tsx` with new anonymous presence illustration, custom SVG glyphs, and staged mutual reveal
4. Update `Home.tsx`, `Profile.tsx`, `BottomNav.tsx`, `UserSwitcher.tsx` with new icon system
5. Run typecheck, build, and full 13-step manual test
6. Commit "Phase 1: rose gold visual polish"