package com.wordle.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class WordServiceTest {

    @Autowired
    private WordService wordService;

    @Test
    void wordListLoadsAndIsNotEmpty() {
        assertThat(wordService.getWordList()).isNotEmpty();
    }

    @Test
    void isValidWordReturnsTrueForValidWord() {
        String word = wordService.getWordList().get(0);
        assertThat(wordService.isValidWord(word)).isTrue();
    }

    @Test
    void isValidWordReturnsFalseForInvalidWord() {
        assertThat(wordService.isValidWord("xxxxzz")).isFalse();
    }

    @Test
    void isValidWordIsCaseInsensitive() {
        String word = wordService.getWordList().get(0);
        assertThat(wordService.isValidWord(word.toUpperCase())).isTrue();
    }

    @Test
    void getRandomWordReturnsSixLetterWord() {
        String word = wordService.getRandomWord();
        assertThat(word).hasSize(6);
    }
}
