package com.hexle.api.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

@Service
public class WordService {

    private List<String> wordList = new ArrayList<>();
    private Set<String> wordSet = new HashSet<>();
    private final Random random = new Random();

    @PostConstruct
    public void loadWords() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("words.txt").getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (word.length() == 6 && !word.isEmpty()) {
                    wordList.add(word);
                    wordSet.add(word);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load word list", e);
        }
    }

    public boolean isValidWord(String word) {
        return wordSet.contains(word.toLowerCase());
    }

    public String getRandomWord() {
        if (wordList.isEmpty()) {
            throw new IllegalStateException("Word list is empty");
        }
        return wordList.get(random.nextInt(wordList.size()));
    }

    public List<String> getWordList() {
        return wordList;
    }
}
