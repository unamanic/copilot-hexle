export type LetterFeedback = 'CORRECT' | 'PRESENT' | 'ABSENT';
export type GameStatus = 'IN_PROGRESS' | 'WIN' | 'LOSE';

export interface GameState {
  gameId: string | null;
  guesses: string[];
  feedback: LetterFeedback[][];
  status: GameStatus | null;
  solution: string | null;
  maxGuesses: number;
  loading: boolean;
  error: string | null;
  currentInput: string;
}

export interface ApiGameState {
  gameId: string;
  guesses: string[];
  feedback: LetterFeedback[][];
  status: GameStatus;
  solution?: string;
  maxGuesses: number;
}

export interface GuessResponse {
  feedback: LetterFeedback[];
  status: GameStatus;
  guessNumber: number;
  gameOver: boolean;
  solution?: string;
  message?: string;
}
