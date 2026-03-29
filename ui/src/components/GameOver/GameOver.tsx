import { useAppDispatch, useAppSelector } from '../../store/hooks';
import { startGameThunk } from '../../store/gameSlice';
import styles from './GameOver.module.css';

export function GameOver() {
  const dispatch = useAppDispatch();
  const { status, guesses, solution } = useAppSelector((s) => s.game);

  if (status !== 'WIN' && status !== 'LOSE') return null;

  return (
    <div className={styles.overlay}>
      <div className={styles.modal}>
        {status === 'WIN' ? (
          <p className={styles.message}>
            🎉 You won! Solved in {guesses.length} guess{guesses.length !== 1 ? 'es' : ''}!
          </p>
        ) : (
          <>
            <p className={styles.message}>😞 Game over!</p>
            {solution && (
              <p className={styles.solution}>
                The word was <span className={styles.word}>{solution.toUpperCase()}</span>
              </p>
            )}
          </>
        )}
        <button className={styles.playAgain} onClick={() => dispatch(startGameThunk())}>
          Play Again
        </button>
      </div>
    </div>
  );
}
