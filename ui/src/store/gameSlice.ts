import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import { startGame, submitGuess, getGameStatus } from '../api/gameApi';
import type { GameState, LetterFeedback, ApiGameState } from '../types/game';

const initialState: GameState = {
  gameId: null,
  guesses: [],
  feedback: [],
  status: null,
  solution: null,
  maxGuesses: 6,
  loading: false,
  error: null,
  currentInput: '',
  challengerAttempts: null,
};

export const startGameThunk = createAsyncThunk('game/start', async (challengeToken?: string) => {
  return await startGame(challengeToken);
});

export const resumeGameThunk = createAsyncThunk(
  'game/resume',
  async (gameId: string, { rejectWithValue }) => {
    try {
      const state = await getGameStatus(gameId);
      return state;
    } catch {
      return rejectWithValue('Game not found');
    }
  }
);

export const submitGuessThunk = createAsyncThunk(
  'game/submitGuess',
  async (guess: string, { getState, rejectWithValue }) => {
    const state = getState() as { game: GameState };
    const { gameId } = state.game;
    if (!gameId) return rejectWithValue('No active game');
    try {
      const result = await submitGuess(gameId, guess);
      return { guess, result };
    } catch (err: unknown) {
      const data = (err as { response?: { data?: { error?: string; message?: string } } })?.response?.data;
      const rawMessage = data?.error ?? data?.message ?? 'Failed to submit guess';
      const message = rawMessage.toLowerCase().includes('not a valid word') ? 'Not in word list' : rawMessage;
      return rejectWithValue(message);
    }
  },
);

const gameSlice = createSlice({
  name: 'game',
  initialState,
  reducers: {
    addLetter(state, action: PayloadAction<string>) {
      if (state.currentInput.length < 6) {
        state.currentInput += action.payload.toUpperCase();
      }
    },
    removeLetter(state) {
      state.currentInput = state.currentInput.slice(0, -1);
    },
    clearError(state) {
      state.error = null;
    },
    clearCurrentInput(state) {
      state.currentInput = '';
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(startGameThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(startGameThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.gameId = action.payload.gameId;
        state.challengerAttempts = action.payload.challengerAttempts ?? null;
        state.guesses = [];
        state.feedback = [];
        state.status = null;
        state.solution = null;
        state.currentInput = '';
        state.error = null;
      })
      .addCase(startGameThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message ?? 'Failed to start game';
      })
      .addCase(submitGuessThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(submitGuessThunk.fulfilled, (state, action) => {
        state.loading = false;
        const { guess, result } = action.payload as {
          guess: string;
          result: { feedback: LetterFeedback[]; status: GameState['status']; solution?: string };
        };
        state.guesses.push(guess);
        state.feedback.push(result.feedback);
        state.status = result.status;
        state.solution = result.solution ?? null;
        state.currentInput = '';
      })
      .addCase(submitGuessThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string ?? 'Failed to submit guess';
      })
      .addCase(resumeGameThunk.fulfilled, (state, action) => {
        const s = action.payload as ApiGameState;
        state.gameId = s.gameId;
        state.guesses = s.guesses;
        state.feedback = s.feedback;
        state.status = s.status;
        state.solution = s.solution ?? null;
        state.currentInput = '';
        state.loading = false;
        state.error = null;
      })
      .addCase(resumeGameThunk.rejected, (state) => {
        state.gameId = null;
        state.loading = false;
      });
  },
});

export const { addLetter, removeLetter, clearError, clearCurrentInput } = gameSlice.actions;
export default gameSlice.reducer;

export const resumeGameThunk = createAsyncThunk(
  'game/resume',
  async (gameId: string, { rejectWithValue }) => {
    try {
      const state = await getGameStatus(gameId);
      return state;
    } catch {
      return rejectWithValue('Game not found');
    }
  }
);

export const submitGuessThunk = createAsyncThunk(
  'game/submitGuess',
  async (guess: string, { getState, rejectWithValue }) => {
    const state = getState() as { game: GameState };
    const { gameId } = state.game;
    if (!gameId) return rejectWithValue('No active game');
    try {
      const result = await submitGuess(gameId, guess);
      return { guess, result };
    } catch (err: unknown) {
      const data = (err as { response?: { data?: { error?: string; message?: string } } })?.response?.data;
      const rawMessage = data?.error ?? data?.message ?? 'Failed to submit guess';
      const message = rawMessage.toLowerCase().includes('not a valid word') ? 'Not in word list' : rawMessage;
      return rejectWithValue(message);
    }
  },
);

const gameSlice = createSlice({
  name: 'game',
  initialState,
  reducers: {
    addLetter(state, action: PayloadAction<string>) {
      if (state.currentInput.length < 6) {
        state.currentInput += action.payload.toUpperCase();
      }
    },
    removeLetter(state) {
      state.currentInput = state.currentInput.slice(0, -1);
    },
    clearError(state) {
      state.error = null;
    },
    clearCurrentInput(state) {
      state.currentInput = '';
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(startGameThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(startGameThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.gameId = action.payload.gameId;
        state.guesses = [];
        state.feedback = [];
        state.status = null;
        state.solution = null;
        state.currentInput = '';
        state.error = null;
      })
      .addCase(startGameThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message ?? 'Failed to start game';
      })
      .addCase(submitGuessThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(submitGuessThunk.fulfilled, (state, action) => {
        state.loading = false;
        const { guess, result } = action.payload as {
          guess: string;
          result: { feedback: LetterFeedback[]; status: GameState['status']; solution?: string };
        };
        state.guesses.push(guess);
        state.feedback.push(result.feedback);
        state.status = result.status;
        state.solution = result.solution ?? null;
        state.currentInput = '';
      })
      .addCase(submitGuessThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string ?? 'Failed to submit guess';
      })
      .addCase(resumeGameThunk.fulfilled, (state, action) => {
        const s = action.payload as ApiGameState;
        state.gameId = s.gameId;
        state.guesses = s.guesses;
        state.feedback = s.feedback;
        state.status = s.status;
        state.solution = s.solution ?? null;
        state.currentInput = '';
        state.loading = false;
        state.error = null;
      })
      .addCase(resumeGameThunk.rejected, (state) => {
        state.gameId = null;
        state.loading = false;
      });
  },
});

export const { addLetter, removeLetter, clearError, clearCurrentInput } = gameSlice.actions;
export default gameSlice.reducer;
