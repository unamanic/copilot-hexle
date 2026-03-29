import { useAppDispatch, useAppSelector } from '../../store/hooks';
import { addLetter, removeLetter } from '../../store/gameSlice';
import { submitGuessThunk } from '../../store/gameSlice';
import type { LetterFeedback } from '../../types/game';
import styles from './Keyboard.module.css';

const ROWS = [
  ['Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'],
  ['A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L'],
  ['ENTER', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', 'BACKSPACE'],
];

const FEEDBACK_PRIORITY: Record<LetterFeedback, number> = {
  CORRECT: 3,
  PRESENT: 2,
  ABSENT: 1,
};

export function Keyboard() {
  const dispatch = useAppDispatch();
  const { guesses, feedback, currentInput, status } = useAppSelector((s) => s.game);

  const letterStatus: Record<string, LetterFeedback> = {};
  guesses.forEach((guess, gi) => {
    guess.split('').forEach((letter, li) => {
      const fb = feedback[gi]?.[li];
      if (!fb) return;
      const current = letterStatus[letter];
      if (!current || FEEDBACK_PRIORITY[fb] > FEEDBACK_PRIORITY[current]) {
        letterStatus[letter] = fb;
      }
    });
  });

  const handleKey = (key: string) => {
    if (status && status !== 'IN_PROGRESS') return;
    if (key === 'BACKSPACE') {
      dispatch(removeLetter());
    } else if (key === 'ENTER') {
      if (currentInput.length === 6) {
        dispatch(submitGuessThunk(currentInput));
      }
    } else {
      dispatch(addLetter(key));
    }
  };

  const getKeyClass = (key: string) => {
    if (key === 'ENTER' || key === 'BACKSPACE') return `${styles.key} ${styles.wide}`;
    const fb = letterStatus[key];
    if (!fb) return styles.key;
    switch (fb) {
      case 'CORRECT': return `${styles.key} ${styles.correct}`;
      case 'PRESENT': return `${styles.key} ${styles.present}`;
      case 'ABSENT':  return `${styles.key} ${styles.absent}`;
    }
  };

  return (
    <div className={styles.keyboard}>
      {ROWS.map((row, ri) => (
        <div key={ri} className={styles.row}>
          {row.map((key) => (
            <button
              key={key}
              className={getKeyClass(key)}
              onClick={() => handleKey(key)}
              aria-label={key}
            >
              {key === 'BACKSPACE' ? '⌫' : key}
            </button>
          ))}
        </div>
      ))}
    </div>
  );
}
