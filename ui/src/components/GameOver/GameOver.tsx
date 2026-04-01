import { useState } from 'react';
import { useAppDispatch, useAppSelector } from '../../store/hooks';
import { startGameThunk } from '../../store/gameSlice';
import { createChallengeToken } from '../../api/gameApi';
import type { LetterFeedback, GameStatus } from '../../types/game';
import styles from './GameOver.module.css';

function toEmoji(f: LetterFeedback): string {
  if (f === 'CORRECT') return '🟣';
  if (f === 'PRESENT') return '🩷';
  return '⬛';
}

function buildShareText(
  guesses: string[],
  feedback: LetterFeedback[][],
  status: GameStatus | null,
  maxGuesses: number,
): string {
  const score = status === 'WIN' ? `${guesses.length}/${maxGuesses}` : `X/${maxGuesses}`;
  const grid = feedback.map((row) => row.map(toEmoji).join('')).join('\n');
  return `Hexle ${score}\n\n${grid}`;
}

export function GameOver() {
  const dispatch = useAppDispatch();
  const { status, guesses, solution, feedback, maxGuesses, gameId } = useAppSelector((s) => s.game);

  const [copyLabel, setCopyLabel] = useState('📋 Copy Results');
  const [challengeLabel, setChallengeLabel] = useState('⚔️ Challenge a Friend');

  if (status !== 'WIN' && status !== 'LOSE') return null;

  const handleCopyResults = async () => {
    const text = buildShareText(guesses, feedback, status, maxGuesses);
    await navigator.clipboard.writeText(text);
    setCopyLabel('Copied!');
    setTimeout(() => setCopyLabel('📋 Copy Results'), 2000);
  };

  const handleChallenge = async () => {
    if (!gameId) return;
    try {
      const { token } = await createChallengeToken(gameId);
      const url = `${window.location.origin}?challenge=${token}`;
      await navigator.clipboard.writeText(url);
      setChallengeLabel('Link Copied!');
      setTimeout(() => setChallengeLabel('⚔️ Challenge a Friend'), 2000);
    } catch {
      setChallengeLabel('Failed – try again');
      setTimeout(() => setChallengeLabel('⚔️ Challenge a Friend'), 2500);
    }
  };

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

        <div className={styles.emojiGrid} aria-label="Your results">
          {feedback.map((row, i) => (
            <div key={i} className={styles.emojiRow}>
              {row.map(toEmoji).join('')}
            </div>
          ))}
        </div>

        <div className={styles.actions}>
          <button className={styles.shareButton} onClick={handleCopyResults}>
            {copyLabel}
          </button>
          <button className={styles.challengeButton} onClick={handleChallenge}>
            {challengeLabel}
          </button>
        </div>

        <button className={styles.playAgain} onClick={() => dispatch(startGameThunk())}>
          Play Again
        </button>
      </div>
    </div>
  );
}
