import type { LetterFeedback } from '../../../types/game';
import { GameTile } from '../GameTile/GameTile';
import styles from './GameRow.module.css';

interface GameRowProps {
  letters: string[];
  feedback: LetterFeedback[] | null;
  revealed: boolean;
}

export function GameRow({ letters, feedback, revealed }: GameRowProps) {
  const tiles = Array.from({ length: 6 }, (_, i) => ({
    letter: letters[i] ?? '',
    feedback: feedback ? (feedback[i] ?? null) : null,
  }));

  return (
    <div className={styles.row}>
      {tiles.map((tile, i) => (
        <GameTile
          key={i}
          letter={tile.letter}
          feedback={tile.feedback}
          revealed={revealed}
        />
      ))}
    </div>
  );
}
