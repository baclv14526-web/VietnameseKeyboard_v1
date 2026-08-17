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
     * Accurately handles: ươ (nướng, hướng, rượu, người, nước), uô (muốn, cuốn),
     * iê/yê (tiến, chuyện), oa/oe/uy, ua/ưa/ia, qu/gi clusters, etc.
     */
    public static int findToneVowelIndex(String baseWord) {
        if (baseWord == null || baseWord.isEmpty()) return -1;
        String lower = baseWord.toLowerCase();
        int len = lower.length();

        // 1. Xác định vị trí các nguyên âm trong từ
        // Xử lý các phụ âm đầu đặc biệt "qu" và "gi"
        int vowelSearchStart = 0;
        if (lower.startsWith("qu") && len > 2) {
            // 'u' đi cùng 'q' đóng vai trò là phụ âm đầu ghép, nguyên âm thực sự bắt đầu từ index 2
            vowelSearchStart = 2;
        } else if (lower.startsWith("gi") && len > 2 && isVowel(lower.charAt(2))) {
            // 'i' đi cùng 'g' đóng vai trò là phụ âm đầu ghép nếu sau nó là nguyên âm (ví dụ "già", "giáo", "giương")
            vowelSearchStart = 2;
        }

        int firstVowel = -1;
        int lastVowel = -1;
        int vowelCount = 0;

        for (int i = vowelSearchStart; i < len; i++) {
            char c = lower.charAt(i);
            if (isVowel(c)) {
                if (firstVowel == -1) firstVowel = i;
                lastVowel = i;
                vowelCount++;
            } else if (firstVowel != -1) {
                // Đã tìm thấy chuỗi nguyên âm liên tiếp và gặp phụ âm cuối -> dừng tìm nguyên âm
                break;
            }
        }

        // Nếu trường hợp đặc biệt "qu" hoặc "gi" đứng 1 mình (vowelCount == 0)
        if (vowelCount == 0) {
            if (lower.startsWith("qu")) return 1; // chữ 'u'
            if (lower.startsWith("gi")) return 1; // chữ 'i'
            for (int i = 0; i < len; i++) {
                if (isVowel(lower.charAt(i))) return i;
            }
            return -1;
        }

        // Nếu chỉ có 1 nguyên âm duy nhất -> luôn đặt dấu trên nguyên âm đó
        if (vowelCount == 1) {
            return firstVowel;
        }

        // Kiểm tra xem có phụ âm cuối không (các ký tự chữ sau lastVowel)
        boolean hasEndingConsonant = false;
        for (int i = lastVowel + 1; i < len; i++) {
            if (Character.isLetter(lower.charAt(i))) {
                hasEndingConsonant = true;
                break;
            }
        }

        String vowelCluster = lower.substring(firstVowel, lastVowel + 1);

        // QUY TẮC ĐẶT DẤU TIẾNG VIỆT CHUẨN XÁC:

        // 1. Nhóm chứa 'ươ' (ươ, ươn, ương, ươc, ươt, ươm, ươp, ươi, ươu...)
        // -> LUÔN LUÔN đặt dấu trên chữ 'ơ' (e.g. nướng, hướng, rượu, người, thương, nước, tươi, lười, mười, hươu)
        int uoPos = vowelCluster.indexOf("ươ");
        if (uoPos != -1) {
            return firstVowel + uoPos + 1; // vị trí của 'ơ'
        }

        // 2. Nhóm chứa 'uô' (uôn, uông, uôc, uôt, uôm, uôi...)
        // -> LUÔN LUÔN đặt dấu trên chữ 'ô' (e.g. muốn, cuống, cuộc, chuột, buồn, suối)
        int uoHatPos = vowelCluster.indexOf("uô");
        if (uoHatPos != -1) {
            return firstVowel + uoHatPos + 1; // vị trí của 'ô'
        }

        // 3. Nhóm chứa 'iê' hoặc 'yê' (iên, iêng, iêt, iêc, iêm, iêp, yê, yên, yêt, uyên, uyêt...)
        // -> LUÔN LUÔN đặt dấu trên chữ 'ê' (e.g. tiến, tiếng, biết, việc, kiếm, yến, chuyến, duyệt, thuyền)
        int iePos = vowelCluster.indexOf("iê");
        if (iePos != -1) {
            return firstVowel + iePos + 1; // vị trí của 'ê'
        }
        int yePos = vowelCluster.indexOf("yê");
        if (yePos != -1) {
            return firstVowel + yePos + 1; // vị trí của 'ê'
        }

        // 4. Nếu có các nguyên âm có dấu mũ hoặc móc đơn độc: 'ê', 'ơ', 'ô', 'ă', 'â'
        // Ưu tiên đặt dấu trên các nguyên âm này
        for (int i = firstVowel; i <= lastVowel; i++) {
            char c = lower.charAt(i);
            if (c == 'ê' || c == 'ơ' || c == 'ô' || c == 'ă' || c == 'â') {
                return i;
            }
        }

        // 5. Nếu có nguyên âm 'ưa', 'ua', 'ia' (ví dụ 'mưa', 'múa', 'mía', 'lửa')
        if (vowelCluster.equals("ưa") || vowelCluster.equals("ua") || vowelCluster.equals("ia")) {
            if (hasEndingConsonant) {
                return lastVowel;
            } else {
                return firstVowel; // mưa, múa, mía, của, lừa
            }
        }

        // 6. Trường hợp có phụ âm cuối:
        // Đặt dấu trên nguyên âm chính thứ hai (e.g. toán, hoàng, hoan, toàn, tuần, luận, biển)
        if (hasEndingConsonant) {
            if (vowelCount >= 2) {
                return firstVowel + 1; // nguyên âm thứ 2
            }
            return firstVowel;
        }

        // 7. Trường hợp không có phụ âm cuối (âm tiết mở):
        // - "oa", "oe", "uy" -> dấu ở nguyên âm sau (e.g. hòa, hòe, thủy, thúy, quỷ)
        if (vowelCluster.equals("oa") || vowelCluster.equals("oe") || vowelCluster.equals("uy")) {
            return lastVowel;
        }

        // - Cụm 3 nguyên âm mở như "oai", "uay", "oeo", "yeu", "ieu" -> đặt dấu ở nguyên âm giữa
        if (vowelCount >= 3) {
            return firstVowel + 1; // nguyên âm thứ 2 (ở giữa)
        }

        // Mặc định cụm 2 nguyên âm mở (e.g. ai, oi, ui, ay, ey): đặt ở nguyên âm thứ nhất (e.g. cái, nói, túi, cây)
        return firstVowel;
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
