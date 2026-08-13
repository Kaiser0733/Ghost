import { useEncounter } from '../context/EncounterContext';
import { EncounterCard } from '../components/EncounterCard';

export function Encounters() {
  const { encounter, perspective } = useEncounter();

  return (
    <div className="screen encounters">
      <header className="screen-header">
        <h2 className="screen-title">Encounters</h2>
        <p className="screen-subtitle">Moments where paths crossed.</p>
      </header>

      <EncounterCard encounter={encounter} perspective={perspective} />

      <p className="encounters-footnote">Prototype data — encounters are simulated for design validation.</p>
    </div>
  );
}