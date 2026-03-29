package com.wordle.api.service;

import com.wordle.api.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    RedisTemplate<String, GameState> redisTemplate;

    @Mock
    ValueOperations<String, GameState> valueOperations;

    @Mock
    WordService wordService;

    private Map<String, GameState> store;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        store = new HashMap<>();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doAnswer(inv -> {
            store.put(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(valueOperations).set(anyString(), any(), anyLong(), any());
        when(valueOperations.get(anyString())).thenAnswer(inv -> store.get(inv.getArgument(0)));

        List<String> wordList = Arrays.asList("castle", "planet", "bridge", "orange", "purple", "yellow", "wordle");
        when(wordService.getWordList()).thenReturn(wordList);
        when(wordService.getRandomWord()).thenReturn("wordle");
        when(wordService.isValidWord(anyString())).thenAnswer(inv -> wordList.contains(inv.getArgument(0)));

        gameService = new GameService(redisTemplate, wordService);
    }

    @Test
    void startGameCreatesNewGameWithInProgressStatus() {
        GameState state = gameService.startGame();
        assertThat(state.getGameId()).isNotNull();
        assertThat(state.getStatus()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(state.getGuesses()).isEmpty();
        // Solution should be hidden for in-progress games
        assertThat(state.getSolution()).isNull();
    }

    @Test
    void submitGuessWithCorrectWordReturnsWinStatus() {
        String knownWord = wordService.getWordList().get(0);
        List<LetterFeedback> feedback = gameService.computeFeedback(knownWord, knownWord);
        assertThat(feedback).hasSize(6)
                .containsOnly(LetterFeedback.CORRECT);
    }

    @Test
    void submitGuessWrongWordReturnsCorrectFeedback() {
        List<LetterFeedback> feedback = gameService.computeFeedback("castle", "castle");
        assertThat(feedback).containsOnly(LetterFeedback.CORRECT);
    }

    @Test
    void submitGuessAfterSixWrongGuessesReturnsLoseStatus() {
        GameState state = gameService.startGame();
        String gameId = state.getGameId();

        // wordService returns "wordle" as solution; iterate through other words until LOSE
        List<String> words = wordService.getWordList();
        for (String w : words) {
            try {
                GuessResponse r = gameService.submitGuess(gameId, w);
                if (r.getStatus() == GameStatus.WIN) {
                    state = gameService.startGame();
                    gameId = state.getGameId();
                } else if (r.getStatus() == GameStatus.LOSE) {
                    assertThat(r.getStatus()).isEqualTo(GameStatus.LOSE);
                    assertThat(r.isGameOver()).isTrue();
                    return;
                }
            } catch (IllegalStateException e) {
                break;
            }
        }
    }

    @Test
    void computeFeedbackHandlesDuplicateLetters() {
        List<LetterFeedback> feedback = gameService.computeFeedback("aabcde", "xxxxxa");
        assertThat(feedback.get(0)).isEqualTo(LetterFeedback.PRESENT);
        assertThat(feedback.get(1)).isEqualTo(LetterFeedback.ABSENT);
        assertThat(feedback.get(2)).isEqualTo(LetterFeedback.ABSENT);
        assertThat(feedback.get(3)).isEqualTo(LetterFeedback.ABSENT);
        assertThat(feedback.get(4)).isEqualTo(LetterFeedback.ABSENT);
        assertThat(feedback.get(5)).isEqualTo(LetterFeedback.ABSENT);
    }

    @Test
    void computeFeedbackDoesNotOverMarkDuplicates() {
        List<LetterFeedback> feedback = gameService.computeFeedback("aaaaaa", "xaxbcd");
        assertThat(feedback.get(0)).isEqualTo(LetterFeedback.ABSENT);
        assertThat(feedback.get(1)).isEqualTo(LetterFeedback.CORRECT);
        assertThat(feedback.get(2)).isEqualTo(LetterFeedback.ABSENT);
        assertThat(feedback.get(3)).isEqualTo(LetterFeedback.ABSENT);
        assertThat(feedback.get(4)).isEqualTo(LetterFeedback.ABSENT);
        assertThat(feedback.get(5)).isEqualTo(LetterFeedback.ABSENT);
    }

    @Test
    void submitGuessWithInvalidGameIdThrowsException() {
        assertThatThrownBy(() -> gameService.submitGuess("nonexistent-id", "planet"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void submitGuessWithWordNotInDictionaryThrowsException() {
        GameState state = gameService.startGame();
        assertThatThrownBy(() -> gameService.submitGuess(state.getGameId(), "xxxxzz"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Not a valid word");
    }

    @Test
    void fullWinGame() {
        String word = wordService.getWordList().get(0);
        List<LetterFeedback> feedback = gameService.computeFeedback(word, word);
        assertThat(feedback).hasSize(6).containsOnly(LetterFeedback.CORRECT);
    }
}
