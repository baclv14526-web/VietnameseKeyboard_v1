package com.viet.keyboard;

import org.junit.Test;
import static org.junit.Assert.*;

public class VietnameseEngineTest {

    @Test
    public void testTonePlacementWithEndingConsonants() {
        // Có âm cuối -> dấu đặt chính xác ở nguyên âm thứ 2 của cụm
        assertEquals("toán", VietnameseEngine.applyTone("toan", VietnameseEngine.Tone.ACUTE));
        assertEquals("hoàng", VietnameseEngine.applyTone("hoang", VietnameseEngine.Tone.GRAVE));
        assertEquals("tiến", VietnameseEngine.applyTone("tiên", VietnameseEngine.Tone.ACUTE));
        assertEquals("nghiêng", VietnameseEngine.applyTone("nghiêng", VietnameseEngine.Tone.NONE));
        assertEquals("nghiếng", VietnameseEngine.applyTone("nghiêng", VietnameseEngine.Tone.ACUTE));
        assertEquals("chuyến", VietnameseEngine.applyTone("chuyên", VietnameseEngine.Tone.ACUTE));
        assertEquals("thuyết", VietnameseEngine.applyTone("thuyêt", VietnameseEngine.Tone.ACUTE));
        assertEquals("duyệt", VietnameseEngine.applyTone("duyêt", VietnameseEngine.Tone.DOT));
        assertEquals("chuẩn", VietnameseEngine.applyTone("chuân", VietnameseEngine.Tone.HOOK));
        assertEquals("muốn", VietnameseEngine.applyTone("muôn", VietnameseEngine.Tone.ACUTE));
        assertEquals("mượn", VietnameseEngine.applyTone("mươn", VietnameseEngine.Tone.DOT));
        assertEquals("đoàn", VietnameseEngine.applyTone("đoan", VietnameseEngine.Tone.GRAVE));
        assertEquals("điện", VietnameseEngine.applyTone("điên", VietnameseEngine.Tone.DOT));
    }

    @Test
    public void testTonePlacementOpenSyllables() {
        // oa, oe, uy -> dấu ở âm thứ 2
        assertEquals("hoá", VietnameseEngine.applyTone("hoa", VietnameseEngine.Tone.ACUTE));
        assertEquals("hoà", VietnameseEngine.applyTone("hoa", VietnameseEngine.Tone.GRAVE));
        assertEquals("thuý", VietnameseEngine.applyTone("thuy", VietnameseEngine.Tone.ACUTE));
        assertEquals("hoè", VietnameseEngine.applyTone("hoe", VietnameseEngine.Tone.GRAVE));

        // ia, ua, ưa -> dấu ở âm thứ 1
        assertEquals("mía", VietnameseEngine.applyTone("mia", VietnameseEngine.Tone.ACUTE));
        assertEquals("múa", VietnameseEngine.applyTone("mua", VietnameseEngine.Tone.ACUTE));
        assertEquals("mứa", VietnameseEngine.applyTone("mưa", VietnameseEngine.Tone.ACUTE));
        assertEquals("của", VietnameseEngine.applyTone("cua", VietnameseEngine.Tone.HOOK));
    }

    @Test
    public void testQuAndGiSpecialConsonants() {
        // qu -> u là âm đệm
        assertEquals("quá", VietnameseEngine.applyTone("qua", VietnameseEngine.Tone.ACUTE));
        assertEquals("quán", VietnameseEngine.applyTone("quan", VietnameseEngine.Tone.ACUTE));
        assertEquals("quyến", VietnameseEngine.applyTone("quyên", VietnameseEngine.Tone.ACUTE));
        assertEquals("quận", VietnameseEngine.applyTone("quân", VietnameseEngine.Tone.DOT));

        // gi -> i là phụ âm nếu sau có nguyên âm
        assertEquals("giá", VietnameseEngine.applyTone("gia", VietnameseEngine.Tone.ACUTE));
        assertEquals("giếng", VietnameseEngine.applyTone("giêng", VietnameseEngine.Tone.ACUTE));
        assertEquals("giúp", VietnameseEngine.applyTone("giup", VietnameseEngine.Tone.ACUTE));
        assertEquals("gìn", VietnameseEngine.applyTone("gin", VietnameseEngine.Tone.GRAVE));
    }

    @Test
    public void testToneToggleAndRemove() {
        // Áp dụng cùng dấu sẽ hoàn lại từ không dấu (toggle)
        assertEquals("toan", VietnameseEngine.applyTone("toán", VietnameseEngine.Tone.ACUTE));
        assertEquals("toan", VietnameseEngine.applyTone("toán", VietnameseEngine.Tone.NONE));
        assertEquals("hoang", VietnameseEngine.applyTone("hoàng", VietnameseEngine.Tone.GRAVE));
    }

    @Test
    public void testUpperCasePreservation() {
        assertEquals("TOÁN", VietnameseEngine.applyTone("TOAN", VietnameseEngine.Tone.ACUTE));
        assertEquals("Toán", VietnameseEngine.applyTone("Toan", VietnameseEngine.Tone.ACUTE));
        assertEquals("HOÀNG", VietnameseEngine.applyTone("HOANG", VietnameseEngine.Tone.GRAVE));
        assertEquals("ĐIỆN", VietnameseEngine.applyTone("ĐIÊN", VietnameseEngine.Tone.DOT));
    }
}
