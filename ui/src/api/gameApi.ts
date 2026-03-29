import axios from 'axios';
import type { GuessResponse, ApiGameState } from '../types/game';

export async function startGame(): Promise<{ gameId: string }> {
  const response = await axios.post<{ gameId: string }>('/api/game/start');
  return response.data;
}

export async function submitGuess(gameId: string, guess: string): Promise<GuessResponse> {
  const response = await axios.post<GuessResponse>('/api/game/guess', { gameId, guess });
  return response.data;
}

export async function getGameStatus(gameId: string): Promise<ApiGameState> {
  const response = await axios.get<ApiGameState>(`/api/game/status/${gameId}`);
  return response.data;
}
