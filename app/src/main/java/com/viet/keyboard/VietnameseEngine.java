package com.viet.keyboard;

import java.util.HashMap;
import java.util.Map;

/**
 * Vietnamese Engine supporting Telex typing, direct Vietnamese characters,
 * Tone Bar integration, and standard Vietnamese tone mark placement.
 */
public class VietnameseEngine {

    public enum Tone {
        NONE,    // 0: Ngang / Không dấu
        ACUTE,   // 1: Sắc (´ / s)
        GRAVE,   // 2: Huyền (` / f)
        HOOK,    // 3: Hỏi (? / r)
        TILDE,   // 4: Ngã (~ / x)
        DOT      // 5: Nặng (. / j)
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
     * according to standard modern Vietnamese orthography rules.
     */
    public static int findToneVowelIndex(String baseWord) {
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
            // Default for 2 vowels: 1st vowel or 2nd vowel depending on standard
            return firstVowel;
        }
    }

    private static boolean isVowel(char c) {
        c = Character.toLowerCase(c);
        return "aăâeêioôơuưy".indexOf(c) >= 0;
    }

    /**
     * Apply tone mark to the word at the grammatically correct position.
     */
    public static String applyTone(String word, Tone tone) {
        if (word == null || word.isEmpty()) return word;

        // 1. Strip current tone
        String baseWord = removeTone(word);
        if (tone == Tone.NONE) {
            return baseWord;
        }

        // 2. Find target vowel position
        int targetIdx = findToneVowelIndex(baseWord);
        if (targetIdx == -1 || targetIdx >= baseWord.length()) {
            return baseWord;
        }

        // 3. Replace the vowel with its toned counterpart
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

    /**
     * Process input character with Telex engine and return the updated composing word.
     */
    public static ProcessResult processKey(String currentWord, String inputKey) {
        if (inputKey == null || inputKey.isEmpty()) {
            return new ProcessResult(currentWord, false);
        }

        // If input is a full string or special symbol
        if (inputKey.length() > 1) {
            return new ProcessResult(currentWord + inputKey, false);
        }

        char ch = inputKey.charAt(0);
        char lowerCh = Character.toLowerCase(ch);

        // Check if input is a tone key: s, f, r, x, j, z
        Tone telexTone = null;
        switch (lowerCh) {
            case 's': telexTone = Tone.ACUTE; break;
            case 'f': telexTone = Tone.GRAVE; break;
            case 'r': telexTone = Tone.HOOK; break;
            case 'x': telexTone = Tone.TILDE; break;
            case 'j': telexTone = Tone.DOT; break;
            case 'z': telexTone = Tone.NONE; break;
        }

        if (telexTone != null && !currentWord.isEmpty()) {
            Tone curTone = getWordTone(currentWord);
            if (telexTone == Tone.NONE) {
                // 'z' removes tone
                if (curTone != Tone.NONE) {
                    return new ProcessResult(applyTone(currentWord, Tone.NONE), true);
                }
            } else if (curTone == telexTone) {
                // Repeated tone key toggles tone off and appends literal character
                String noTone = applyTone(currentWord, Tone.NONE);
                return new ProcessResult(noTone + ch, true);
            } else {
                // Apply tone if the word has at least one vowel
                String toned = applyTone(currentWord, telexTone);
                if (!toned.equals(currentWord)) {
                    return new ProcessResult(toned, true);
                }
            }
        }

        // Check for Telex hat/horn modifications:
        // aa -> â, aw -> ă, ee -> ê, oo -> ô, ow -> ơ, uw -> ư, w -> ư/ơ, dd -> đ
        if (!currentWord.isEmpty()) {
            String telexTrans = tryTransformTelexLetter(currentWord, ch);
            if (telexTrans != null) {
                return new ProcessResult(telexTrans, true);
            }
        }

        // Check if inputting direct Vietnamese letters: ă, â, đ, ê, ô, ơ, ư, or standard letter
        return new ProcessResult(currentWord + inputKey, false);
    }

    /**
     * Try Telex vowel/consonant transformation like aa->â, ee->ê, etc.
     */
    private static String tryTransformTelexLetter(String word, char inputChar) {
        int len = word.length();
        char lastChar = word.charAt(len - 1);
        char lowerInput = Character.toLowerCase(inputChar);
        boolean isUpper = Character.isUpperCase(inputChar) || (Character.isUpperCase(lastChar) && len == 1);

        // Preserve existing word tone
        Tone curTone = getWordTone(word);
        String baseWord = removeTone(word);
        char lastBaseChar = baseWord.charAt(len - 1);
        char lowerLastBase = Character.toLowerCase(lastBaseChar);

        // 1. Double letter: 'a' -> 'â', 'e' -> 'ê', 'o' -> 'ô', 'd' -> 'đ'
        if (lowerLastBase == lowerInput) {
            char transformed = 0;
            switch (lowerInput) {
                case 'a': transformed = 'â'; break;
                case 'e': transformed = 'ê'; break;
                case 'o': transformed = 'ô'; break;
                case 'd': transformed = 'đ'; break;
            }
            if (transformed != 0) {
                // If last letter already had hat (e.g., 'â' + 'a' -> 'aa', toggle back)
                if (lowerLastBase == 'â' || lowerLastBase == 'ê' || lowerLastBase == 'ô' || lowerLastBase == 'đ') {
                    char orig = lowerLastBase == 'đ' ? 'd' : (lowerLastBase == 'â' ? 'a' : (lowerLastBase == 'ê' ? 'e' : 'o'));
                    char cOut = Character.isUpperCase(lastBaseChar) ? Character.toUpperCase(orig) : orig;
                    String toggled = baseWord.substring(0, len - 1) + cOut + inputChar;
                    return applyTone(toggled, curTone);
                }

                char cOut = isUpper ? Character.toUpperCase(transformed) : transformed;
                String updated = baseWord.substring(0, len - 1) + cOut;
                return applyTone(updated, curTone);
            }
        }

        // 2. 'w' modifier
        if (lowerInput == 'w') {
            // 'aw' -> 'ă'
            if (lowerLastBase == 'a') {
                char cOut = isUpper ? 'Ă' : 'ă';
                String updated = baseWord.substring(0, len - 1) + cOut;
                return applyTone(updated, curTone);
            }
            // 'ow' -> 'ơ'
            if (lowerLastBase == 'o') {
                char cOut = isUpper ? 'Ơ' : 'ơ';
                String updated = baseWord.substring(0, len - 1) + cOut;
                return applyTone(updated, curTone);
            }
            // 'uw' -> 'ư'
            if (lowerLastBase == 'u') {
                char cOut = isUpper ? 'Ư' : 'ư';
                String updated = baseWord.substring(0, len - 1) + cOut;
                return applyTone(updated, curTone);
            }
            // 'uo' + 'w' -> 'ươ' (e.g. duong -> dươ)
            if (len >= 2) {
                char prevChar = Character.toLowerCase(baseWord.charAt(len - 2));
                if (prevChar == 'u' && lowerLastBase == 'o') {
                    boolean pUpper = Character.isUpperCase(baseWord.charAt(len - 2));
                    boolean lUpper = Character.isUpperCase(baseWord.charAt(len - 1));
                    char uChar = pUpper ? 'Ư' : 'ư';
                    char oChar = lUpper ? 'Ơ' : 'ơ';
                    String updated = baseWord.substring(0, len - 2) + uChar + oChar;
                    return applyTone(updated, curTone);
                }
            }
            // 'w' after consonant or standalone -> 'ư'
            if (!isVowel(lowerLastBase) || lowerLastBase == 'ư') {
                // If last was 'ư' and user types 'w' again -> undo to 'w'
                if (lowerLastBase == 'ư') {
                    char cOut = isUpper ? 'W' : 'w';
                    return baseWord.substring(0, len - 1) + cOut;
                }
                char cOut = isUpper ? 'Ư' : 'ư';
                return applyTone(baseWord + cOut, curTone);
            }
        }

        return null;
    }

    public static class ProcessResult {
        public final String word;
        public final boolean handledSpecial;

        public ProcessResult(String word, boolean handledSpecial) {
            this.word = word;
            this.handledSpecial = handledSpecial;
        }
    }
}
