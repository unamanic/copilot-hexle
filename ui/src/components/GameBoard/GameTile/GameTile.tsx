import type { LetterFeedback } from '../../../types/game';
import styles from './GameTile.module.css';

interface GameTileProps {
  letter: string;
  feedback: LetterFeedback | null;
  revealed: boolean;
}

export function GameTile({ letter, feedback, revealed }: GameTileProps) {
  const getClassName = () => {
    if (!revealed || !feedback) {
      return letter ? `${styles.tile} ${styles.filled}` : styles.tile;
    }
    switch (feedback) {
      case 'CORRECT':
        return `${styles.tile} ${styles.correct} ${styles.flip}`;
      case 'PRESENT':
        return `${styles.tile} ${styles.present} ${styles.flip}`;
      case 'ABSENT':
        return `${styles.tile} ${styles.absent} ${styles.flip}`;
      default:
        return styles.tile;
    }
  };

  return (
    <div className={getClassName()}>
      <span>{letter}</span>
    </div>
  );
}
