import axios from 'axios';
import type { GuessResponse } from '../types/game';

export async function startGame(): Promise<{ gameId: string }> {
  const response = await axios.post<{ gameId: string }>('/api/game/start');
  return response.data;
}

export async function submitGuess(gameId: string, guess: string): Promise<GuessResponse> {
  const response = await axios.post<GuessResponse>('/api/game/guess', { gameId, guess });
  return response.data;
}
