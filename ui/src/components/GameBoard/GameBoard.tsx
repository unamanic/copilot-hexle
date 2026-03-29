import { useAppSelector } from '../../store/hooks';
import { GameRow } from './GameRow/GameRow';
import styles from './GameBoard.module.css';

export function GameBoard() {
  const { guesses, feedback, currentInput, maxGuesses } = useAppSelector((s) => s.game);

  const rows = Array.from({ length: maxGuesses }, (_, i) => {
    if (i < guesses.length) {
      return { letters: guesses[i].split(''), feedback: feedback[i], revealed: true };
    }
    if (i === guesses.length) {
      return { letters: currentInput.split(''), feedback: null, revealed: false };
    }
    return { letters: [], feedback: null, revealed: false };
  });

  return (
    <div className={styles.board}>
      {rows.map((row, i) => (
        <GameRow key={i} letters={row.letters} feedback={row.feedback} revealed={row.revealed} />
      ))}
    </div>
  );
}
