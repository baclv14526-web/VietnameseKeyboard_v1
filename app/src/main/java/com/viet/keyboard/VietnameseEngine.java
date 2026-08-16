package com.viet.keyboard;

import java.util.HashMap;
import java.util.Map;

/**
 * Vietnamese Engine focusing on standard tone mark placement and direct character input.
 * Telex/VNI auto-conversion is removed for direct and clean typing.
 */
public class VietnameseEngine {

    public enum Tone {
        NONE,    // 0: Ngang / Không dấu
        ACUTE,   // 1: Sắc (´)
        GRAVE,   // 2: Huyền (`)
        HOOK,    // 3: Hỏi (?)
        TILDE,   // 4: Ngã (~)
        DOT      // 5: Nặng (.)
    }

    // Mapping vowel to its base forms and tone variations
    // Base vowels: a, ă, â, e, ê, i, o, ô, ơ, u, ư, y
    // Index: 0: none, 1: acute (sắc), 2: grave (huyền), 3: hook (hỏi), 4: tilde (ngã), 5: dot (nặng)
    private static final Map<Character, String> VOWEL_TONE_MAP = new HashMap<>();
    private static final Map<Character, Character> VOWEL_BASE_MAP = new HashMap<>();
    private static final Map<Character, Tone> CHAR_TO_TONE = new HashMap<>();

    static {
        // [Base, sắc, huyền, hỏi, ngã, nặng]
        String[] toneRows = {
            "aáàảãạ",
            "AÁÀẢÃẠ",
            "ăắằẳẵặ",
            "ĂẮẰẲẴẶ",
            "âấầẩẫậ",
            "ÂẤẦẨẪẬ",
            "eéèẻẽẹ",
            "EÉÈẺẼẸ",
            "êếềểễệ",
            "ÊẾỀỂỄỆ",
            "iíìỉĩị",
            "IÍÌỈĨỊ",
            "oóòỏõọ",
            "OÓÒỎÕỌ",
            "ôốồổỗộ",
            "ÔỐỒỔỖỘ",
            "ơớờởỡợ",
            "ƠỚỜỞỠỢ",
            "uúùủũụ",
            "UÚÙỦŨỤ",
            "ưứừửữự",
            "ƯỨỪỬỮỰ",
            "yýỳỷỹỵ",
            "YÝỲỶỸỴ"
        };

        for (String row : toneRows) {
            char baseChar = row.charAt(0);
            VOWEL_TONE_MAP.put(baseChar, row);
            Tone[] tones = {Tone.NONE, Tone.ACUTE, Tone.GRAVE, Tone.HOOK, Tone.TILDE, Tone.DOT};
            for (int i = 0; i < row.length(); i++) {
                char c = row.charAt(i);
                VOWEL_BASE_MAP.put(c, baseChar);
                CHAR_TO_TONE.put(c, tones[i]);
            }
        }
    }

    public static boolean isVietnameseWordChar(char c) {
        return Character.isLetter(c);
    }

    /**
     * Remove all tones from a word, returning raw characters with hats/horns intact.
     */
    public static String removeTone(String word) {
        if (word == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            Character base = VOWEL_BASE_MAP.get(c);
            sb.append(base != null ? base : c);
        }
        return sb.toString();
    }

    /**
     * Get the current tone of the word.
     */
    public static Tone getWordTone(String word) {
        if (word == null) return Tone.NONE;
        for (int i = 0; i < word.length(); i++) {
            Tone t = CHAR_TO_TONE.get(word.charAt(i));
            if (t != null && t != Tone.NONE) {
                return t;
            }
        }
        return Tone.NONE;
    }

    /**
     * Determine the index of the vowel that should receive the tone mark
     * according to standard Vietnamese orthography rules.
     */
    public static int findToneVowelIndex(String baseWord) {
        if (baseWord == null || baseWord.isEmpty()) return -1;
        String lower = baseWord.toLowerCase();
        int len = lower.length();

        // 1. Find all vowel indices in the word
        int firstVowel = -1;
        int lastVowel = -1;
        int vowelCount = 0;

        for (int i = 0; i < len; i++) {
            char c = lower.charAt(i);
            if (isVowel(c)) {
                if (firstVowel == -1) firstVowel = i;
                lastVowel = i;
                vowelCount++;
            }
        }

        if (vowelCount == 0) return -1;
        if (vowelCount == 1) return firstVowel;

        // Check for special clusters "qu" and "gi"
        int startIndex = firstVowel;
        if (lower.startsWith("qu") && len > 2) {
            // 'u' is part of initial consonant 'qu', main vowels start after 'u'
            startIndex = 2;
            firstVowel = -1;
            lastVowel = -1;
            vowelCount = 0;
            for (int i = startIndex; i < len; i++) {
                char c = lower.charAt(i);
                if (isVowel(c)) {
                    if (firstVowel == -1) firstVowel = i;
                    lastVowel = i;
                    vowelCount++;
                }
            }
            if (vowelCount == 0) return 1; // if just "qu"
            if (vowelCount == 1) return firstVowel;
        } else if (lower.startsWith("gi") && len > 2) {
            // If followed by another vowel, 'gi' acts as consonant
            char nextChar = lower.charAt(2);
            if (isVowel(nextChar)) {
                startIndex = 2;
                firstVowel = -1;
                lastVowel = -1;
                vowelCount = 0;
                for (int i = startIndex; i < len; i++) {
                    char c = lower.charAt(i);
                    if (isVowel(c)) {
                        if (firstVowel == -1) firstVowel = i;
                        lastVowel = i;
                        vowelCount++;
                    }
                }
                if (vowelCount == 0) return 1;
                if (vowelCount == 1) return firstVowel;
            }
        }

        // Check if there is an ending consonant after the vowels
        boolean hasEndingConsonant = false;
        for (int i = lastVowel + 1; i < len; i++) {
            if (Character.isLetter(lower.charAt(i))) {
                hasEndingConsonant = true;
                break;
            }
        }

        // Check for diphthongs/triphthongs with special vowel letters (ê, ơ, ư, ô)
        for (int i = firstVowel; i <= lastVowel; i++) {
            char c = lower.charAt(i);
            if (c == 'ê' || c == 'ơ' || c == 'ư' || c == 'ô') {
                // If contains 'ươ' or 'uô', put tone on 2nd vowel (ơ or ô)
                if (i > firstVowel && (lower.charAt(i - 1) == 'ư' || lower.charAt(i - 1) == 'u')) {
                    return i;
                }
                // 'iê', 'yê'
                if (i > firstVowel && (lower.charAt(i - 1) == 'i' || lower.charAt(i - 1) == 'y')) {
                    return i;
                }
                return i;
            }
        }

        if (hasEndingConsonant) {
            // Closed syllable (has ending consonant like c, ch, m, n, ng, nh, p, t):
            // Place tone on the 2nd vowel in vowel group (e.g., toán, hoàng, tiến, muốn, việc, duyệt)
            int secondVowel = -1;
            int count = 0;
            for (int i = firstVowel; i <= lastVowel; i++) {
                if (isVowel(lower.charAt(i))) {
                    count++;
                    if (count == 2) {
                        secondVowel = i;
                        break;
                    }
                }
            }
            return (secondVowel != -1) ? secondVowel : firstVowel;
        } else {
            // Open syllable (no ending consonant):
            // Rules:
            // 1. "oa", "oe", "uy" -> tone on 2nd vowel (e.g. hoà/hóa, hoè/hóe, thuỷ/thúy)
            String sub = lower.substring(firstVowel, lastVowel + 1);
            if (sub.equals("oa") || sub.equals("oe") || sub.equals("uy")) {
                return lastVowel;
            }
            // 2. "ia", "ua", "ưa" -> tone on 1st vowel (e.g. mía, múa, mứa, chia, của)
            if (sub.equals("ia") || sub.equals("ua") || sub.equals("ưa")) {
                return firstVowel;
            }
            // 3. Triphthongs like "oai", "uay", "yeu", "ieu"
            if (vowelCount >= 3) {
                // Return middle vowel (2nd vowel)
                int count = 0;
                for (int i = firstVowel; i <= lastVowel; i++) {
                    if (isVowel(lower.charAt(i))) {
                        count++;
                        if (count == 2) return i;
                    }
                }
            }
            // Default for 2 vowels: 1st vowel
            return firstVowel;
        }
    }

    private static boolean isVowel(char c) {
        c = Character.toLowerCase(c);
        return "aăâeêioôơuưy".indexOf(c) >= 0;
    }

    /**
     * Apply tone mark to the word at the grammatically correct position.
     * If the word already has this tone, removes it (toggle behavior).
     */
    public static String applyTone(String word, Tone tone) {
        if (word == null || word.isEmpty()) return word;

        Tone curTone = getWordTone(word);
        String baseWord = removeTone(word);

        // If user selects same tone -> toggle back to none
        if (curTone == tone || tone == Tone.NONE) {
            return baseWord;
        }

        // Find target vowel position
        int targetIdx = findToneVowelIndex(baseWord);
        if (targetIdx == -1 || targetIdx >= baseWord.length()) {
            return baseWord;
        }

        // Replace vowel with toned version
        char targetChar = baseWord.charAt(targetIdx);
        String toneVariants = VOWEL_TONE_MAP.get(targetChar);
        if (toneVariants == null || tone.ordinal() >= toneVariants.length()) {
            return baseWord;
        }

        char tonedChar = toneVariants.charAt(tone.ordinal());
        StringBuilder sb = new StringBuilder(baseWord);
        sb.setCharAt(targetIdx, tonedChar);
        return sb.toString();
    }
}
