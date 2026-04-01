import axios from 'axios';
import type { GuessResponse, ApiGameState, StartGameResponse, CreateChallengeResponse } from '../types/game';

export async function startGame(challengeToken?: string): Promise<StartGameResponse> {
  const body = challengeToken ? { challengeToken } : {};
  const response = await axios.post<StartGameResponse>('/api/game/start', body);
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

export async function createChallengeToken(gameId: string): Promise<CreateChallengeResponse> {
  const response = await axios.post<CreateChallengeResponse>('/api/game/challenge/create', { gameId });
  return response.data;
}
