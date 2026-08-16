package com.viet.keyboard;

import org.junit.Test;
import static org.junit.Assert.*;

public class VietnameseEngineTest {

    private String typeWord(String input) {
        String cur = "";
        for (int i = 0; i < input.length(); i++) {
            String ch = String.valueOf(input.charAt(i));
            VietnameseEngine.ProcessResult res = VietnameseEngine.processKey(cur, ch);
            cur = res.word;
        }
        return cur;
    }

    @Test
    public void testTelexBasicHatsAndHorns() {
        assertEquals("â", typeWord("aa"));
        assertEquals("ă", typeWord("aw"));
        assertEquals("ê", typeWord("ee"));
        assertEquals("ô", typeWord("oo"));
        assertEquals("ơ", typeWord("ow"));
        assertEquals("ư", typeWord("uw"));
        assertEquals("ư", typeWord("w"));
        assertEquals("đ", typeWord("dd"));
        assertEquals("Đ", typeWord("DD"));
    }

    @Test
    public void testTonePlacementWithEndingConsonants() {
        // Có âm cuối -> dấu ở nguyên âm thứ 2
        assertEquals("toán", typeWord("toans"));
        assertEquals("hoàng", typeWord("hoangf"));
        assertEquals("tiến", typeWord("tieens"));
        assertEquals("nghiêng", typeWord("nghieengs"));
        assertEquals("chuyến", typeWord("chuyeens"));
        assertEquals("thuyết", typeWord("thuyeetj"));
        assertEquals("duyệt", typeWord("duyeetj"));
        assertEquals("chuẩn", typeWord("chuaanr"));
        assertEquals("muốn", typeWord("muoons"));
        assertEquals("mượn", typeWord("muownj"));
        assertEquals("đoàn", typeWord("ddoanf"));
        assertEquals("điện", typeWord("ddieenj"));
    }

    @Test
    public void testTonePlacementOpenSyllables() {
        // oa, oe, uy -> dấu ở âm thứ 2
        assertEquals("hoá", typeWord("hoas"));
        assertEquals("hoà", typeWord("hoaf"));
        assertEquals("thuý", typeWord("thuys"));
        assertEquals("hoè", typeWord("hoef"));

        // ia, ua, ưa -> dấu ở âm thứ 1
        assertEquals("mía", typeWord("mias"));
        assertEquals("múa", typeWord("muas"));
        assertEquals("mứa", typeWord("muwas"));
        assertEquals("của", typeWord("cuar"));
        assertEquals("chia", typeWord("chia"));
    }

    @Test
    public void testQuAndGiSpecialConsonants() {
        // qu -> u là âm đệm
        assertEquals("quá", typeWord("quas"));
        assertEquals("quán", typeWord("quans"));
        assertEquals("quyến", typeWord("quyeens"));
        assertEquals("quận", typeWord("quaanj"));

        // gi -> i là phụ âm nếu sau có nguyên âm
        assertEquals("giá", typeWord("gias"));
        assertEquals("giếng", typeWord("giieengs"));
        assertEquals("giúp", typeWord("giups"));
        assertEquals("gìn", typeWord("ginf"));
    }

    @Test
    public void testDirectToneApplication() {
        // Test applyTone trực tiếp (tương đương bấm thanh Tone Bar)
        assertEquals("toán", VietnameseEngine.applyTone("toan", VietnameseEngine.Tone.ACUTE));
        assertEquals("hoàng", VietnameseEngine.applyTone("hoang", VietnameseEngine.Tone.GRAVE));
        assertEquals("nghiêng", VietnameseEngine.applyTone("nghieng", VietnameseEngine.Tone.ACUTE));
        assertEquals("khuỷu", VietnameseEngine.applyTone("khuyu", VietnameseEngine.Tone.HOOK));
        assertEquals("thuở", VietnameseEngine.applyTone("thuơ", VietnameseEngine.Tone.HOOK));
        assertEquals("toan", VietnameseEngine.applyTone("toán", VietnameseEngine.Tone.NONE));
    }

    @Test
    public void testDirectVietnameseCharacters() {
        // Gõ trực tiếp phím ă, â, đ, ê, ô, ơ, ư kết hợp phím dấu
        assertEquals("đường", typeWord("đươngf"));
        assertEquals("tiết", typeWord("tiêt" + "s"));
        assertEquals("thực", typeWord("thưc" + "j"));
        assertEquals("bật", typeWord("bât" + "j"));
    }

    @Test
    public void testUpperCasePreservation() {
        assertEquals("TOÁN", typeWord("TOANS"));
        assertEquals("Toán", typeWord("Toans"));
        assertEquals("HOÀNG", typeWord("HOANGF"));
        assertEquals("ĐIỆN", typeWord("DDIEENJ"));
    }
}
