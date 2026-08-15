---
version: alpha
name: Ghost Rose Gold Noir
description: Warm, elegant, modern, human, slightly nocturnal social layer for the physical world.
colors:
  # Primary background - very dark muted rose-charcoal derived from #B76E79
  bg: "#0f0b0c"
  # Deepest layer
  bg-deep: "#080607"
  # Card surface - visibly lighter than page background, subtly rose-toned
  bg-card: "#1a1416"
  # Card hover/elevated
  bg-card-hover: "#221a1d"
  bg-elevated: "#2a2023"
  bg-input: "#1e1719"

  # Text hierarchy - warm ivory/cream for primary, softer warm-gray for secondary
  text: "#faf6f0"
  text-dim: "#d4c8c0"
  text-muted: "#9a8d85"
  text-faint: "#70655d"

  # Accent system - Rose Gold identity
  accent: "#B76E79"           # Primary accent - the founder's chosen Rose Gold
  accent-light: "#d4969e"     # Lighter rose-gold highlight
  accent-soft: "#B76E79cc"    # Soft variant for glows
  accent-subtle: "#B76E7933"  # Subtle backgrounds
  accent-muted: "#8b5a60"     # Muted state - desaturated rose-gray

  # Borders & dividers - subtle rose-toned
  border: "#3d2d30"
  border-subtle: "#2a1e21"
  border-strong: "#5a3d43"

  # Focus & interaction
  focus: "#B76E79"
  focus-ring: "0 0 0 2px var(--focus)"

  # Layout
  nav-h: "64px"
  max-w: "480px"
  radius-sm: "4px"
  radius-md: "8px"
  radius-lg: "16px"
  radius-full: "9999px"

  # Spacing
  sp-xs: "0.375rem"
  sp-sm: "0.625rem"
  sp-md: "1rem"
  sp-lg: "1.5rem"
  sp-xl: "2rem"
  sp-2xl: "3rem"
  sp-3xl: "4rem"

  # Typography
  font-display: "'Playfair Display', Georgia, serif"
  font-ui: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif"
  font-mono: "'SF Mono', 'Fira Code', monospace"

  fs-xs: "0.6875rem"
  fs-sm: "0.8125rem"
  fs-base: "1rem"
  fs-lg: "1.125rem"
  fs-xl: "1.25rem"
  fs-2xl: "1.75rem"
  fs-3xl: "2.25rem"
  fs-4xl: "3rem"
  fs-5xl: "4rem"

  # Transitions
  t-fast: "100ms ease-out"
  t-med: "200ms ease-out"
  t-slow: "300ms ease-out"
  t-slower: "500ms ease-out"
  t-reveal: "600ms ease-out"

typography:
  h1:
    fontFamily: "{font-display}"
    fontSize: "{fs-5xl}"
    fontWeight: 400
    lineHeight: 1.1
    letterSpacing: "-0.02em"
    color: "{text}"
  h2:
    fontFamily: "{font-display}"
    fontSize: "{fs-2xl}"
    fontWeight: 400
    lineHeight: 1.2
    letterSpacing: "-0.01em"
    color: "{text}"
  h3:
    fontFamily: "{font-display}"
    fontSize: "{fs-3xl}"
    fontWeight: 400
    lineHeight: 1.1
    letterSpacing: "-0.01em"
    color: "{text}"
  body:
    fontFamily: "{font-ui}"
    fontSize: "{fs-base}"
    fontWeight: 400
    lineHeight: 1.65
    color: "{text}"
  body-muted:
    fontFamily: "{font-ui}"
    fontSize: "{fs-base}"
    fontWeight: 400
    lineHeight: 1.6
    color: "{text-dim}"
  caption:
    fontFamily: "{font-ui}"
    fontSize: "{fs-xs}"
    fontWeight: 500
    lineHeight: 1.5
    letterSpacing: "0.12em"
    textTransform: "uppercase"
    color: "{text-faint}"
  ghost-id:
    fontFamily: "{font-mono}"
    fontSize: "{fs-xs}"
    fontWeight: 400
    letterSpacing: "0.12em"
    color: "{text-faint}"

components:
  app-shell:
    backgroundColor: "{bg}"
    backgroundImage: "radial-gradient(ellipse 100% 60% at 50% -10%, rgba(183, 110, 121, 0.04) 0%, transparent 60%), radial-gradient(ellipse 80% 50% at 50% 110%, rgba(138, 90, 96, 0.02) 0%, transparent 50%)"

  encounter-card:
    backgroundColor: "{bg-card}"
    borderColor: "{border-subtle}"
    borderWidth: "1px"
    rounded: "{radius-lg}"
    padding: "{sp-xl} {sp-lg}"
    boxShadow: "0 4px 24px -4px rgba(0,0,0,0.4), 0 0 0 1px var(--border-subtle)"

  encounter-card-hover:
    backgroundColor: "{bg-card-hover}"
    borderColor: "{border}"

  encounter-card-anonymous:
    borderColor: "{border-subtle}"

  encounter-card-waiting:
    borderColor: "{accent}"
    boxShadow: "0 0 0 1px var(--accent), 0 0 32px -8px rgba(183, 110, 121, 0.25)"

  encounter-card-notification:
    borderColor: "{accent}"
    boxShadow: "0 0 0 1px var(--accent), 0 0 32px -8px rgba(183, 110, 121, 0.3)"

  encounter-card-mutual:
    borderColor: "{accent}"
    backgroundImage: "linear-gradient(135deg, var(--bg-card) 0%, rgba(183, 110, 121, 0.06) 50%, var(--bg-card) 100%)"
    boxShadow: "0 0 0 1px var(--accent), 0 8px 48px -12px rgba(183, 110, 121, 0.35)"

  encounter-glyph:
    width: "72px"
    height: "72px"
    rounded: "50%"
    borderWidth: "1px"
    borderColor: "{border}"
    backgroundColor: "{bg-deep}"
    color: "{text-dim}"
    transition: "all var(--t-slower)"

  encounter-glyph-anonymous:
    color: "{text-dim}"
    borderColor: "{border}"

  encounter-glyph-anonymous-before:
    animation: "breath 4s ease-in-out infinite"

  encounter-glyph-waiting:
    color: "{accent}"
    borderColor: "{accent}"
    backgroundColor: "rgba(183, 110, 121, 0.08)"
    animation: "pulse-rose 2.5s ease-in-out infinite"

  encounter-glyph-notification:
    color: "{accent}"
    borderColor: "{accent}"
    backgroundColor: "rgba(183, 110, 121, 0.1)"
    animation: "pulse-rose 2.5s ease-in-out infinite"

  encounter-glyph-mutual:
    color: "{bg}"
    borderColor: "{accent}"
    backgroundColor: "{accent}"
    boxShadow: "0 0 48px 16px rgba(183, 110, 121, 0.3)"

  btn-primary:
    borderWidth: "1px"
    borderColor: "{border}"
    color: "{text}"
    backgroundColor: "transparent"
    padding: "{sp-md} {sp-lg}"
    rounded: "{radius-md}"
    fontSize: "{fs-sm}"
    fontWeight: 500
    fontFamily: "{font-ui}"
    transition: "all var(--t-med)"

  btn-primary-hover:
    borderColor: "{accent}"
    backgroundColor: "rgba(183, 110, 121, 0.1)"
    color: "{accent}"

  btn-primary-active:
    backgroundColor: "{border}"

  btn-primary-disabled:
    borderStyle: "dashed"
    color: "{text-faint}"
    opacity: 0.5

  btn-secondary:
    borderWidth: "1px"
    borderColor: "transparent"
    color: "{text-dim}"
    backgroundColor: "transparent"
    padding: "{sp-md} {sp-lg}"
    rounded: "{radius-md}"
    fontSize: "{fs-sm}"
    fontWeight: 500
    fontFamily: "{font-ui}"

  btn-secondary-hover:
    color: "{text}"
    borderColor: "{border}"
    backgroundColor: "{bg-card}"

  home-wordmark:
    fontFamily: "{font-display}"
    fontSize: "{fs-5xl}"
    fontWeight: 400
    letterSpacing: "-0.02em"
    color: "{text}"

  home-tagline:
    fontFamily: "{font-display}"
    fontSize: "{fs-xl}"
    fontWeight: 300
    fontStyle: "italic"
    color: "{text-dim}"
    lineHeight: 1.4

  mutual-lead:
    fontFamily: "{font-display}"
    fontSize: "{fs-5xl}"
    fontWeight: 400
    letterSpacing: "-0.02em"
    color: "{text}"
    lineHeight: 1.1

  mutual-avatar:
    width: "120px"
    height: "120px"
    rounded: "50%"
    borderWidth: "2px"
    borderColor: "{accent}"
    backgroundColor: "{bg-deep}"
    color: "{accent}"
    fontFamily: "{font-display}"
    fontSize: "{fs-5xl}"
    fontWeight: 300
    boxShadow: "0 0 48px 16px rgba(183, 110, 121, 0.25)"

  mutual-username:
    fontFamily: "{font-display}"
    fontSize: "{fs-3xl}"
    fontWeight: 400
    letterSpacing: "-0.01em"
    color: "{text}"

  mutual-ghostid:
    fontFamily: "{font-mono}"
    fontSize: "{fs-xs}"
    letterSpacing: "0.12em"
    color: "{text-faint}"

  mutual-bio:
    fontFamily: "{font-ui}"
    fontSize: "{fs-base}"
    lineHeight: 1.8
    color: "{text-dim}"

  profile-avatar:
    width: "112px"
    height: "112px"
    rounded: "50%"
    borderWidth: "2px"
    borderColor: "{border}"
    backgroundColor: "{bg-deep}"
    color: "{text-dim}"
    fontFamily: "{font-display}"
    fontSize: "{fs-5xl}"
    fontWeight: 300
    transition: "all var(--t-med)"

  profile-avatar-hover:
    borderColor: "{accent}"
    color: "{accent}"

  bottom-nav:
    backgroundColor: "{bg}"
    borderTopColor: "{border}"
    backgroundImage: "linear-gradient(to top, var(--bg) 0%, transparent 100%)"

  nav-item:
    color: "{text-dim}"
    transition: "color var(--t-med)"

  nav-item-active:
    color: "{accent}"

  nav-icon:
    fontSize: "{fs-2xl}"

  user-switcher:
    padding: "{sp-sm} {sp-md}"
    backgroundColor: "{bg-card}"
    borderBottomColor: "{border-subtle}"

  switcher-btn:
    padding: "{sp-sm} {sp-md}"
    rounded: "{radius-md}"
    borderWidth: "1px"
    borderColor: "{border-subtle}"
    backgroundColor: "{bg-deep}"
    color: "{text-dim}"
    fontSize: "{fs-sm}"
    fontWeight: 500
    transition: "all var(--t-fast)"

  switcher-btn-active:
    color: "{accent}"
    borderColor: "{accent}"
    backgroundColor: "rgba(183, 110, 121, 0.1)"

  screen:
    animation: "fadeIn var(--t-med) ease-out"

  mutual-reveal:
    animation: "mutualReveal var(--t-reveal) ease-out"

  focus-visible:
    outline: "none"
    boxShadow: "{focus-ring}"