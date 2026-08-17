package com.viet.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class VietnameseKeyboardManager {

    // Keyboard mode enum to strictly prevent UI conflicts / duplication
    public enum KeyboardMode {
        TEXT,
        SYMBOLS,
        EMOJI
    }

    // Special key codes
    public static final int KEY_DELETE         = -1;
    public static final int KEY_SPACE          = -2;
    public static final int KEY_ENTER          = -3;
    public static final int KEY_SHIFT          = -4;
    public static final int KEY_TOGGLE_NUMBERS  = -5;
    public static final int KEY_TOGGLE_EMOJI    = -6;
    public static final int KEY_DELETE_WORD     = -7;
    public static final int KEY_DELETE_ALL      = -8;

    // Vietnamese tones
    public static final String TONE_ACUTE = "TONE:ACUTE"; // Sắc
    public static final String TONE_GRAVE = "TONE:GRAVE"; // Huyền
    public static final String TONE_HOOK  = "TONE:HOOK";  // Hỏi
    public static final String TONE_TILDE = "TONE:TILDE"; // Ngã
    public static final String TONE_DOT   = "TONE:DOT";   // Nặng
    public static final String TONE_NONE  = "TONE:NONE";  // Ngang / Xóa dấu

    private static final String[][] TONE_KEYS = {
        {"\u02CA", "Sắc", "#FFFF6B6B", TONE_ACUTE},
        {"\u02CB", "Huyền", "#FF4ECDC4", TONE_GRAVE},
        {"?", "Hỏi", "#FF95E1D3", TONE_HOOK},
        {"~", "Ngã", "#FFFFE66D", TONE_TILDE},
        {"\u2022", "Nặng", "#FFFF8B94", TONE_DOT},
        {"\u2014", "Ngang", "#FF8899AA", TONE_NONE}
    };

    // Vietnamese 29 letters + special chars
    private static final String[] ROW1_LOWER = {"q","w","e","r","t","y","u","i","o","p"};
    private static final String[] ROW2_LOWER = {"a","s","d","f","g","h","j","k","l"};
    private static final String[] ROW3_LOWER = {"z","x","c","v","b","n","m"};

    // Vietnamese special characters - extra letters (ă, â, ê, ô, ơ, ư, đ)
    private static final String[] VN_EXTRA_LOWER = {"ă","â","ê","ô","ơ","ư","đ"};
    private static final String[] VN_EXTRA_UPPER = {"Ă","Â","Ê","Ô","Ơ","Ư","Đ"};

    // Number row
    private static final String[] NUMBERS = {"1","2","3","4","5","6","7","8","9","0"};

    // Emoji Categories and Full Popular Emoji List (Standard Unicode supported across all Android versions)
    private static final String[][] EMOJI_CATEGORIES = {
        // 1. Mặt cười & Cảm xúc (Unicode 9-12, compatible Android 6+)
        {"\uD83D\uDE0A M\u1EB7t c\u01B0\u1EDDi & C\u1EA3m x\u00FAc",
         "\uD83D\uDE0A","\uD83D\uDE0D","\uD83E\uDD70","\uD83D\uDE18","\uD83D\uDE02","\uD83E\uDD23","\uD83D\uDE05","\uD83D\uDE06","\uD83D\uDE01","\uD83D\uDE04","\uD83D\uDE03","\uD83D\uDE00","\uD83D\uDE0B","\uD83D\uDE0E","\uD83E\uDD73","\uD83E\uDD29","\uD83D\uDE1C","\uD83E\uDD2A","\uD83E\uDD17","\uD83E\uDD14","\uD83D\uDE34","\uD83E\uDD24","\uD83D\uDE37","\uD83E\uDD12","\uD83E\uDD27","\uD83E\uDD7A","\uD83D\uDE22","\uD83D\uDE2D","\uD83D\uDE24","\uD83D\uDE20","\uD83D\uDE21","\uD83E\uDD2C","\uD83E\uDD2F","\uD83D\uDE33","\uD83E\uDD75","\uD83E\uDD76","\uD83D\uDE31","\uD83D\uDE28","\uD83D\uDE30","\uD83D\uDE25","\uD83D\uDE13","\uD83D\uDE0F","\uD83D\uDE44","\uD83D\uDE2C","\uD83D\uDE14","\uD83D\uDE2A","\uD83D\uDE35","\uD83E\uDD20","\uD83D\uDC7B","\uD83E\uDD21","\uD83D\uDCA9","\uD83D\uDE08","\uD83D\uDC80","\uD83D\uDC7D","\uD83E\uDD16"},
        // 2. Cử chỉ & Bàn tay
        {"\uD83D\uDC4D C\u1EED ch\u1EC9 & B\u00E0n tay",
         "\uD83D\uDC4D","\uD83D\uDC4E","\uD83D\uDC4F","\uD83D\uDE4C","\uD83D\uDC50","\uD83E\uDD32","\uD83E\uDD1D","\uD83D\uDE4F","\u270C","\uD83E\uDD1E","\uD83E\uDD1F","\uD83E\uDD18","\uD83E\uDD19","\uD83D\uDC48","\uD83D\uDC49","\uD83D\uDC46","\uD83D\uDD95","\uD83D\uDC47","\u261D","\uD83D\uDC4B","\uD83E\uDD1A","\uD83D\uDD90","\u270B","\u270D","\uD83E\uDD73","\uD83D\uDC85","\uD83D\uDCAA","\uD83D\uDCAF","\uD83C\uDD97","\uD83C\uDD92","\uD83C\uDD99","\uD83C\uDD98","\uD83D\uDCA2","\uD83D\uDCA5","\uD83D\uDCAB","\uD83D\uDCA6","\uD83D\uDCA8","\uD83D\uDC42","\uD83D\uDC43","\uD83D\uDC40","\uD83E\uDDE0","\uD83D\uDC45","\uD83D\uDC44","\uD83D\uDC8B"},
        // 3. Trái tim & Tình yêu (100% tương thích Android 6-9+)
        {"\u2764 Tr\u00E1i tim & T\u00ECnh y\u00EAu",
         "\u2764","\uD83E\uDDE1","\uD83D\uDC9B","\uD83D\uDC9A","\uD83D\uDC99","\uD83D\uDC9C","\uD83D\uDDA4","\uD83D\uDC8C","\uD83D\uDC8D","\uD83D\uDC94","\u2763","\uD83D\uDC95","\uD83D\uDC9E","\uD83D\uDC93","\uD83D\uDC97","\uD83D\uDC96","\uD83D\uDC98","\uD83D\uDC9D","\uD83D\uDC9F","\uD83C\uDF80","\u2728","\u2B50","\uD83C\uDF1F","\uD83D\uDCAB","\u26A1","\uD83D\uDD25","\u2600","\uD83C\uDF19","\u2601","\uD83C\uDF08","\u2744","\uD83D\uDC8E","\uD83D\uDC51","\uD83D\uDD2E","\uD83C\uDF89","\uD83C\uDF8A","\uD83C\uDF81","\uD83C\uDF88","\uD83C\uDFEE"},
        // 4. Đồ ăn & Thức uống (100% tương thích Android 6-9+)
        {"\uD83C\uDF5C \u0110\u1ED3 \u0103n & Th\u1EE9c u\u1ED1ng",
         "\uD83C\uDF5C","\uD83C\uDF72","\uD83C\uDF71","\uD83C\uDF63","\uD83C\uDF59","\uD83C\uDF5A","\uD83C\uDF5B","\uD83E\uDD5F","\uD83C\uDF62","\uD83C\uDF67","\uD83C\uDF68","\uD83C\uDF66","\uD83C\uDF70","\uD83C\uDF82","\uD83C\uDF6E","\uD83C\uDF6D","\uD83C\uDF6C","\uD83C\uDF6B","\uD83C\uDF7F","\uD83C\uDF69","\uD83C\uDF6A","\u2615","\uD83C\uDF75","\uD83C\uDF76","\uD83C\uDF77","\uD83C\uDF78","\uD83C\uDF79","\uD83C\uDF7A","\uD83C\uDF7B","\uD83E\uDD42","\uD83C\uDF7E","\uD83C\uDF55","\uD83C\uDF54","\uD83C\uDF5F","\uD83C\uDF2D","\uD83E\uDD6A","\uD83C\uDF2E","\uD83C\uDF2F","\uD83E\uDD57","\uD83C\uDF5D","\uD83E\uDD56","\uD83E\uDD50","\uD83C\uDF5E","\uD83C\uDF73","\uD83E\uDD5E","\uD83C\uDF4E","\uD83C\uDF49","\uD83C\uDF47","\uD83C\uDF53","\uD83C\uDF52","\uD83C\uDF51","\uD83E\uDD6D","\uD83C\uDF4D","\uD83E\uDD65","\uD83E\uDD5D","\uD83E\uDD51"},
        // 5. Động vật & Thiên nhiên
        {"\uD83C\uDF38 \u0110\u1ED9ng v\u1EADt & Thi\u00EAn nhi\u00EAn",
         "\uD83D\uDC31","\uD83D\uDC36","\uD83D\uDC3B","\uD83D\uDC3C","\uD83D\uDC28","\uD83D\uDC2F","\uD83E\uDD81","\uD83D\uDC2E","\uD83D\uDC37","\uD83D\uDC38","\uD83D\uDC35","\uD83D\uDC14","\uD83D\uDC27","\uD83D\uDC26","\uD83D\uDC24","\uD83D\uDC23","\uD83D\uDC25","\uD83E\uDD86","\uD83E\uDD85","\uD83E\uDD89","\uD83E\uDD87","\uD83D\uDC3A","\uD83D\uDC17","\uD83D\uDC34","\uD83E\uDD84","\uD83D\uDC1D","\uD83D\uDC1B","\uD83E\uDD8B","\uD83D\uDC0C","\uD83D\uDC1E","\uD83D\uDC1C","\uD83D\uDC22","\uD83D\uDC0D","\uD83D\uDC19","\uD83E\uDD91","\uD83E\uDD90","\uD83E\uDD9E","\uD83E\uDD80","\uD83D\uDC21","\uD83D\uDC20","\uD83D\uDC1F","\uD83D\uDC2C","\uD83D\uDC33","\uD83E\uDD88","\uD83D\uDC0A","\uD83C\uDF38","\uD83C\uDF3A","\uD83C\uDF3B","\uD83C\uDF39","\uD83E\uDD40","\uD83C\uDF37","\uD83C\uDF3C","\uD83D\uDC90","\uD83C\uDF3E","\uD83C\uDF3F","\uD83C\uDF40","\uD83C\uDF41","\uD83C\uDF42","\uD83C\uDF43"},
        // 6. Hoạt động & Du lịch
        {"\u26BD Ho\u1EA1t \u0111\u1ED9ng & Du l\u1ECBch",
         "\u26BD","\uD83C\uDFC0","\uD83C\uDFC8","\u26BE","\uD83E\uDD4E","\uD83C\uDFBE","\uD83C\uDFD0","\uD83C\uDFC9","\uD83E\uDD4F","\uD83C\uDFB1","\uD83C\uDFD3","\uD83C\uDFF8","\uD83C\uDFD2","\u26F3","\uD83C\uDFAF","\uD83C\uDFAE","\uD83C\uDFB2","\uD83E\uDDE9","\uD83C\uDFA8","\uD83C\uDFAC","\uD83C\uDFA4","\uD83C\uDFA7","\uD83C\uDFBC","\uD83C\uDFB9","\uD83E\uDD41","\uD83C\uDFB7","\uD83C\uDFBA","\uD83C\uDFB8","\uD83D\uDE97","\uD83D\uDE95","\uD83D\uDE99","\uD83D\uDE8C","\uD83D\uDE8E","\uD83D\uDE93","\uD83D\uDE91","\uD83D\uDE92","\uD83D\uDE90","\uD83D\uDE9A","\uD83D\uDE9B","\uD83D\uDE9C","\uD83D\uDEF5","\uD83D\uDEB2","\uD83D\uDEF4","\u2708","\uD83D\uDE80","\uD83D\uDEF8","\uD83D\uDEA2","\u2693","\uD83C\uDFD6","\uD83C\uDFDD","\uD83C\uDFD5","\u26FA","\uD83C\uDFE0","\uD83C\uDFE1","\uD83C\uDFE2"}
    };

    // Symbols and Numbers layouts (Samsung keyboard style)
    private static final String[] SYM_PAGE1_ROW1 = {"1","2","3","4","5","6","7","8","9","0"};
    private static final String[] SYM_PAGE1_ROW2 = {"@","#","$","%","&","-","+","(",")","/"};
    private static final String[] SYM_PAGE1_ROW3 = {"*","\"", "'",":",";","!","?","~","\\","="};
    private static final String[] SYM_PAGE1_ROW4 = {"_","<",">","[","]","{","}","^"};

    private static final String[] SYM_PAGE2_ROW1 = {"1","2","3","4","5","6","7","8","9","0"};
    private static final String[] SYM_PAGE2_ROW2 = {"^","°","•","|","`","¥","€","£","¢","§"};
    private static final String[] SYM_PAGE2_ROW3 = {"×","÷","±","≠","≤","≥","«","»","“","”"};
    private static final String[] SYM_PAGE2_ROW4 = {"©","®","™","…","¿","¡","√","π"};

    private final Context mContext;
    private final View mRootView;
    private final OnKeyListener mKeyListener;
    private final OnSpecialKeyListener mSpecialKeyListener;

    private LinearLayout mNumberRow;
    private LinearLayout mToneRow;
    private LinearLayout mVnRow;
    private LinearLayout mRow1, mRow2, mRow3, mRowBottom;
    private LinearLayout mEmojiContainer;
    private ScrollView mEmojiPanel;
    private LinearLayout mSymbolsPanel;
    private LinearLayout mSymRow1, mSymRow2, mSymRow3, mSymRow4;

    /** Shift key states: OFF → one-shot ON → CAPS_LOCK → OFF */
    public enum ShiftState { OFF, ON, CAPS_LOCK }

    private List<TextView> mAllLetterKeys = new ArrayList<>();
    private TextView mEmojiBtn;
    private TextView mNumToggleBtn;
    private TextView mShiftKey; // reference to ⇧ key for visual update
    private ShiftState mShiftState = ShiftState.OFF;
    private boolean mShiftOn = false; // convenience alias: true when ON or CAPS_LOCK
    private boolean mShowNumbers = true;
    private boolean mSymbolsPage2 = false;
    private KeyboardMode mCurrentMode = KeyboardMode.TEXT;

    public interface OnKeyListener {
        void onKey(String text);
    }

    public interface OnSpecialKeyListener {
        void onSpecialKey(int keyCode);
    }

    public VietnameseKeyboardManager(Context context, View rootView,
                                      OnKeyListener keyListener,
                                      OnSpecialKeyListener specialKeyListener) {
        mContext = context;
        mRootView = rootView;
        mKeyListener = keyListener;
        mSpecialKeyListener = specialKeyListener;

        mNumberRow    = rootView.findViewById(R.id.number_row);
        mToneRow      = rootView.findViewById(R.id.tone_row);
        mVnRow        = rootView.findViewById(R.id.vn_row);
        mRow1         = rootView.findViewById(R.id.row1);
        mRow2         = rootView.findViewById(R.id.row2);
        mRow3         = rootView.findViewById(R.id.row3);
        mRowBottom    = rootView.findViewById(R.id.row_bottom);
        mEmojiPanel   = rootView.findViewById(R.id.emoji_panel);
        mEmojiContainer = rootView.findViewById(R.id.emoji_container);

        mSymbolsPanel = rootView.findViewById(R.id.symbols_panel);
        mSymRow1      = rootView.findViewById(R.id.sym_row1);
        mSymRow2      = rootView.findViewById(R.id.sym_row2);
        mSymRow3      = rootView.findViewById(R.id.sym_row3);
        mSymRow4      = rootView.findViewById(R.id.sym_row4);
    }

    public void buildKeyboard(boolean showNumbers) {
        mShowNumbers = showNumbers;
        mAllLetterKeys.clear();

        buildNumberRow();
        buildToneRow();
        buildVnExtraRow();
        buildRow1();
        buildRow2();
        buildRow3();
        buildBottomRow();
        buildEmojiPanel();
        buildSymbolsPanel();

        setMode(KeyboardMode.TEXT);
    }

    // ──────────────────────────────────────────────────────────
    // Number row
    // ──────────────────────────────────────────────────────────
    private void buildNumberRow() {
        mNumberRow.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lp.setMargins(2, 2, 2, 2);

        for (String num : NUMBERS) {
            TextView key = makeKey(num, 14, "#FF1E3A5F", "#FFCCE5FF");
            key.setLayoutParams(lp);
            key.setOnClickListener(v -> mKeyListener.onKey(num));
            mNumberRow.addView(key);
        }
    }

    // ──────────────────────────────────────────────────────────
    // Tone row (dấu thanh) + Number row toggle button
    // ──────────────────────────────────────────────────────────
    // ──────────────────────────────────────────────────────────
    // Tone row (dấu thanh) + Number row toggle button on right
    // ──────────────────────────────────────────────────────────
    private void buildToneRow() {
        mToneRow.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lp.setMargins(3, 0, 3, 0);

        // 1. Các phím dấu thanh: Sắc, Huyền, Hỏi, Ngã, Nặng, Ngang
        for (String[] tone : TONE_KEYS) {
            String symbol = tone[0];
            String label  = tone[1];
            String color  = tone[2];
            String action = tone[3];

            LinearLayout cell = new LinearLayout(mContext);
            cell.setLayoutParams(lp);
            cell.setOrientation(LinearLayout.VERTICAL);
            cell.setGravity(Gravity.CENTER);
            cell.setBackgroundResource(R.drawable.key_bg_rounded);
            cell.getBackground().setTint(Color.parseColor("#FF16213E"));

            TextView symView = new TextView(mContext);
            symView.setText(symbol);
            symView.setTextSize(17f);
            symView.setTextColor(Color.parseColor(color));
            symView.setTypeface(Typeface.DEFAULT_BOLD);
            symView.setIncludeFontPadding(false);
            symView.setGravity(Gravity.CENTER);

            TextView lblView = new TextView(mContext);
            lblView.setText(label);
            lblView.setTextSize(8.5f);
            lblView.setTextColor(Color.parseColor("#FFAAAACC"));
            lblView.setTypeface(Typeface.DEFAULT_BOLD);
            lblView.setIncludeFontPadding(false);
            lblView.setGravity(Gravity.CENTER);

            cell.addView(symView);
            cell.addView(lblView);

            // Emit tone action
            cell.setOnClickListener(v -> mKeyListener.onKey(action));
            mToneRow.addView(cell);
        }

        // 2. Nút toggle ẩn/hiện dãy số (1-9, 0) ở bên phải
        LinearLayout numToggleCell = new LinearLayout(mContext);
        numToggleCell.setLayoutParams(lp);
        numToggleCell.setOrientation(LinearLayout.VERTICAL);
        numToggleCell.setGravity(Gravity.CENTER);
        numToggleCell.setBackgroundResource(R.drawable.key_bg_rounded);
        numToggleCell.getBackground().setTint(mShowNumbers ? Color.parseColor("#FF1E3A5F") : Color.parseColor("#FF16213E"));

        TextView numIcon = new TextView(mContext);
        numIcon.setText(mShowNumbers ? "123▾" : "123▴");
        numIcon.setTextSize(12.5f);
        numIcon.setTextColor(mShowNumbers ? Color.parseColor("#FF4ECDC4") : Color.parseColor("#FF8899AA"));
        numIcon.setTypeface(Typeface.DEFAULT_BOLD);
        numIcon.setIncludeFontPadding(false);
        numIcon.setGravity(Gravity.CENTER);

        TextView numLbl = new TextView(mContext);
        numLbl.setText(mShowNumbers ? "Ẩn số" : "Hiện số");
        numLbl.setTextSize(7.5f);
        numLbl.setTextColor(mShowNumbers ? Color.parseColor("#FFCCE5FF") : Color.parseColor("#FF8899AA"));
        numLbl.setTypeface(Typeface.DEFAULT_BOLD);
        numLbl.setIncludeFontPadding(false);
        numLbl.setGravity(Gravity.CENTER);

        numToggleCell.addView(numIcon);
        numToggleCell.addView(numLbl);
        numToggleCell.setOnClickListener(v -> toggleNumberRow(!mShowNumbers));
        mToneRow.addView(numToggleCell);
    }

    // ──────────────────────────────────────────────────────────
    // Vietnamese extra letters row: ă, â, ê, ô, ơ, ư, đ
    // ──────────────────────────────────────────────────────────
    private void buildVnExtraRow() {
        if (mVnRow == null) return;
        mVnRow.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lp.setMargins(3, 0, 3, 0);

        for (String vnChar : VN_EXTRA_LOWER) {
            TextView key = makeLetterKey(vnChar);
            key.setLayoutParams(lp);
            mVnRow.addView(key);
        }
    }

    // ──────────────────────────────────────────────────────────
    // QWERTY rows
    // ──────────────────────────────────────────────────────────
    private void buildRow1() {
        mRow1.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lp.setMargins(3, 0, 3, 0);

        // q w e r t y u i o p
        for (String ch : ROW1_LOWER) {
            TextView key = makeLetterKey(ch);
            key.setLayoutParams(lp);
            mRow1.addView(key);
        }
    }

    private void buildRow2() {
        mRow2.removeAllViews();
        LinearLayout.LayoutParams lpKey = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lpKey.setMargins(3, 0, 3, 0);

        // Small spacer at start
        View spacer1 = new View(mContext);
        spacer1.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 0.5f));
        mRow2.addView(spacer1);

        // a s d f g h j k l
        for (String ch : ROW2_LOWER) {
            TextView key = makeLetterKey(ch);
            key.setLayoutParams(lpKey);
            mRow2.addView(key);
        }

        View spacer2 = new View(mContext);
        spacer2.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 0.5f));
        mRow2.addView(spacer2);
    }

    private void buildRow3() {
        mRow3.removeAllViews();
        LinearLayout.LayoutParams lpShift = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.5f);
        lpShift.setMargins(3, 0, 3, 0);

        LinearLayout.LayoutParams lpKey = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lpKey.setMargins(3, 0, 3, 0);

        LinearLayout.LayoutParams lpDel = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.5f);
        lpDel.setMargins(3, 0, 3, 0);

        // Shift key
        mShiftKey = makeKey("⇧", 18, "#FF2D2D44", "#FFFFFFFF");
        mShiftKey.setLayoutParams(lpShift);
        mShiftKey.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_SHIFT));
        mRow3.addView(mShiftKey);

        // z x c v b n m
        for (String ch : ROW3_LOWER) {
            TextView key = makeLetterKey(ch);
            key.setLayoutParams(lpKey);
            mRow3.addView(key);
        }

        // Backspace key
        TextView del = makeKey("⌫", 18, "#FF2D2D44", "#FFFF6B6B");
        del.setLayoutParams(lpDel);
        setupDeleteKey(del);
        mRow3.addView(del);
    }

    // ──────────────────────────────────────────────────────────
    // Bottom row: [😊][123][,][  Khoảng trắng  ][.][⏎ Gửi]
    // ──────────────────────────────────────────────────────────
    private void buildBottomRow() {
        mRowBottom.removeAllViews();

        LinearLayout.LayoutParams lpSmall = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.1f);
        lpSmall.setMargins(3, 0, 3, 0);

        LinearLayout.LayoutParams lpPunct = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.1f);
        lpPunct.setMargins(3, 0, 3, 0);

        LinearLayout.LayoutParams lpSpace = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 3.8f);
        lpSpace.setMargins(3, 0, 3, 0);

        LinearLayout.LayoutParams lpEnter = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.5f);
        lpEnter.setMargins(3, 0, 3, 0);

        // Emoji toggle
        mEmojiBtn = makeKey("😊", 18, "#FF1A1A2E", "#FFFFFFFF");
        mEmojiBtn.setLayoutParams(lpSmall);
        mEmojiBtn.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_TOGGLE_EMOJI));
        mRowBottom.addView(mEmojiBtn);

        // Number row toggle (123 / ABC)
        mNumToggleBtn = makeKey("123", 13, "#FF1A1A2E", "#FF4ECDC4");
        mNumToggleBtn.setLayoutParams(lpSmall);
        mNumToggleBtn.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_TOGGLE_NUMBERS));
        mRowBottom.addView(mNumToggleBtn);

        // Comma (dấu phẩy ở bên trái khoảng trắng)
        TextView commaBtn = makeKey(",", 16, "#FF2D2D44", "#FFFFFFFF");
        commaBtn.setLayoutParams(lpPunct);
        commaBtn.setOnClickListener(v -> mKeyListener.onKey(","));
        mRowBottom.addView(commaBtn);

        // Space (Khoảng trắng)
        TextView space = makeKey("Khoảng trắng", 12, "#FF2D2D44", "#FFAAAACC");
        space.setLayoutParams(lpSpace);
        space.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_SPACE));
        mRowBottom.addView(space);

        // Period (dấu chấm ở bên phải khoảng trắng)
        TextView dotBtn = makeKey(".", 16, "#FF2D2D44", "#FFFFFFFF");
        dotBtn.setLayoutParams(lpPunct);
        dotBtn.setOnClickListener(v -> mKeyListener.onKey("."));
        mRowBottom.addView(dotBtn);

        // Enter/Return
        TextView enter = makeKey("⏎ Gửi", 13, "#FFE94560", "#FFFFFFFF");
        enter.setLayoutParams(lpEnter);
        enter.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_ENTER));
        mRowBottom.addView(enter);
    }

    // ──────────────────────────────────────────────────────────
    // Symbols & Numbers Panel (Samsung Keyboard style)
    // ──────────────────────────────────────────────────────────
    private void buildSymbolsPanel() {
        if (mSymbolsPanel == null) return;
        mSymRow1.removeAllViews();
        mSymRow2.removeAllViews();
        mSymRow3.removeAllViews();
        mSymRow4.removeAllViews();

        LinearLayout.LayoutParams lpKey = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lpKey.setMargins(3, 0, 3, 0);

        LinearLayout.LayoutParams lpSpecial = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.4f);
        lpSpecial.setMargins(3, 0, 3, 0);

        String[] row1 = mSymbolsPage2 ? SYM_PAGE2_ROW1 : SYM_PAGE1_ROW1;
        String[] row2 = mSymbolsPage2 ? SYM_PAGE2_ROW2 : SYM_PAGE1_ROW2;
        String[] row3 = mSymbolsPage2 ? SYM_PAGE2_ROW3 : SYM_PAGE1_ROW3;
        String[] row4 = mSymbolsPage2 ? SYM_PAGE2_ROW4 : SYM_PAGE1_ROW4;

        // Row 1 (Numbers)
        for (String k : row1) {
            TextView key = makeKey(k, 16, "#FF1E3A5F", "#FFCCE5FF");
            key.setLayoutParams(lpKey);
            key.setOnClickListener(v -> mKeyListener.onKey(k));
            mSymRow1.addView(key);
        }

        // Row 2 (Symbols 1)
        for (String k : row2) {
            TextView key = makeKey(k, 16, "#FF2D2D44", "#FFFFFFFF");
            key.setLayoutParams(lpKey);
            key.setOnClickListener(v -> mKeyListener.onKey(k));
            mSymRow2.addView(key);
        }

        // Row 3 (Symbols 2)
        for (String k : row3) {
            TextView key = makeKey(k, 16, "#FF2D2D44", "#FFFFFFFF");
            key.setLayoutParams(lpKey);
            key.setOnClickListener(v -> mKeyListener.onKey(k));
            mSymRow3.addView(key);
        }

        // Row 4: [1/2 or 2/2] [Symbols...] [⌫]
        String pageLabel = mSymbolsPage2 ? "2/2" : "1/2";
        TextView pageToggle = makeKey(pageLabel, 13, "#FF16213E", "#FF4ECDC4");
        pageToggle.setLayoutParams(lpSpecial);
        pageToggle.setOnClickListener(v -> {
            mSymbolsPage2 = !mSymbolsPage2;
            buildSymbolsPanel();
        });
        mSymRow4.addView(pageToggle);

        for (String k : row4) {
            TextView key = makeKey(k, 16, "#FF2D2D44", "#FFFFFFFF");
            key.setLayoutParams(lpKey);
            key.setOnClickListener(v -> mKeyListener.onKey(k));
            mSymRow4.addView(key);
        }

        TextView del = makeKey("⌫", 18, "#FF2D2D44", "#FFFF6B6B");
        del.setLayoutParams(lpSpecial);
        setupDeleteKey(del);
        mSymRow4.addView(del);
    }

    // ──────────────────────────────────────────────────────────
    // Delete key behavior:
    // - Tap: Delete 1 character
    // - Hold >400ms: Delete whole word iteratively
    // - Hold >3000ms: Delete all text in input
    // ──────────────────────────────────────────────────────────
    private void setupDeleteKey(TextView del) {
        Handler deleteHandler = new Handler(Looper.getMainLooper());

        class DeleteState {
            long startTime = 0;
            boolean isLongPressing = false;
            boolean deletedAll = false;
        }

        final DeleteState state = new DeleteState();

        final Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - state.startTime;
                state.isLongPressing = true;

                if (elapsed >= 3000) {
                    if (!state.deletedAll) {
                        mSpecialKeyListener.onSpecialKey(KEY_DELETE_ALL);
                        state.deletedAll = true;
                    }
                    return;
                } else if (elapsed >= 400) {
                    mSpecialKeyListener.onSpecialKey(KEY_DELETE_WORD);
                    deleteHandler.postDelayed(this, 180);
                } else {
                    deleteHandler.postDelayed(this, 50);
                }
            }
        };

        del.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    del.getBackground().setTint(Color.parseColor("#FFE94560"));
                    del.animate().scaleX(0.92f).scaleY(0.92f).setDuration(60).start();

                    state.startTime = System.currentTimeMillis();
                    state.isLongPressing = false;
                    state.deletedAll = false;

                    // Delete single char on tap
                    mSpecialKeyListener.onSpecialKey(KEY_DELETE);

                    deleteHandler.removeCallbacks(deleteRunnable);
                    deleteHandler.postDelayed(deleteRunnable, 400);
                    return true;

                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    del.getBackground().setTint(Color.parseColor("#FF2D2D44"));
                    del.animate().scaleX(1f).scaleY(1f).setDuration(60).start();

                    deleteHandler.removeCallbacks(deleteRunnable);
                    return true;
            }
            return false;
        });
    }

    // ──────────────────────────────────────────────────────────
    // Emoji Panel – Categories with Vertical Scrolling & Grid Rows
    // ──────────────────────────────────────────────────────────
    private void buildEmojiPanel() {
        if (mEmojiContainer == null) return;
        mEmojiContainer.removeAllViews();

        int marginPx = (int) (3 * mContext.getResources().getDisplayMetrics().density);
        int padPx = (int) (6 * mContext.getResources().getDisplayMetrics().density);
        int heightPx = (int) (46 * mContext.getResources().getDisplayMetrics().density);

        for (String[] cat : EMOJI_CATEGORIES) {
            String title = cat[0];

            // Category Title Badge
            TextView titleView = new TextView(mContext);
            titleView.setText(title);
            titleView.setTextSize(12f);
            titleView.setTextColor(Color.parseColor("#FF4ECDC4"));
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            titleView.setPadding(padPx * 2, padPx, padPx * 2, padPx);
            mEmojiContainer.addView(titleView);

            // Group emojis into rows of 7 columns
            LinearLayout currentRow = null;
            int count = 0;
            int numCols = 7;

            for (int i = 1; i < cat.length; i++) {
                final String emoji = cat[i];
                if (count % numCols == 0) {
                    currentRow = new LinearLayout(mContext);
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, heightPx);
                    rowLp.setMargins(0, marginPx, 0, marginPx);
                    currentRow.setLayoutParams(rowLp);
                    mEmojiContainer.addView(currentRow);
                }

                TextView ev = new TextView(mContext);
                ev.setText(emoji);
                ev.setTextSize(24f);
                ev.setIncludeFontPadding(false);
                ev.setSingleLine(true);
                ev.setGravity(Gravity.CENTER);
                ev.setBackgroundResource(R.drawable.key_bg_rounded);
                // Nền xám xanh slate tinh tế làm nổi bật mọi sắc màu emoji
                ev.getBackground().setTint(Color.parseColor("#FF282B42"));

                LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                itemLp.setMargins(marginPx, 0, marginPx, 0);
                ev.setLayoutParams(itemLp);

                // Click and Touch Feedback
                ev.setOnClickListener(v -> mKeyListener.onKey(emoji));
                ev.setOnTouchListener((v, event) -> {
                    switch (event.getAction()) {
                        case android.view.MotionEvent.ACTION_DOWN:
                            ev.getBackground().setTint(Color.parseColor("#FFE94560"));
                            ev.animate().scaleX(0.88f).scaleY(0.88f).setDuration(60).start();
                            break;
                        case android.view.MotionEvent.ACTION_UP:
                        case android.view.MotionEvent.ACTION_CANCEL:
                            ev.getBackground().setTint(Color.parseColor("#FF282B42"));
                            ev.animate().scaleX(1f).scaleY(1f).setDuration(60).start();
                            break;
                    }
                    return false;
                });

                if (currentRow != null) {
                    currentRow.addView(ev);
                }
                count++;
            }

            // Fill empty cells in the last row for consistent spacing
            if (currentRow != null && count % numCols != 0) {
                int remaining = numCols - (count % numCols);
                for (int r = 0; r < remaining; r++) {
                    View spacer = new View(mContext);
                    LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                    itemLp.setMargins(marginPx, 0, marginPx, 0);
                    spacer.setLayoutParams(itemLp);
                    currentRow.addView(spacer);
                }
            }
        }
    }

    // ──────────────────────────────────────────────────────────
    // Letter key with shift support
    // ──────────────────────────────────────────────────────────
    private TextView makeLetterKey(String lower) {
        String upper = lower.length() == 1 ?
                lower.toUpperCase() :
                lower.substring(0, 1).toUpperCase() + lower.substring(1);

        // Find Vietnamese upper
        for (int i = 0; i < VN_EXTRA_LOWER.length; i++) {
            if (VN_EXTRA_LOWER[i].equals(lower)) {
                upper = VN_EXTRA_UPPER[i];
                break;
            }
        }

        final String lo = lower;
        final String up = upper;

        TextView key = makeKey(lower, 16, "#FF2D2D44", "#FFFFFFFF");

        key.setOnClickListener(v -> {
            mKeyListener.onKey(mShiftOn ? up : lo);
            // One-shot shift: auto-off after typing one letter
            if (mShiftState == ShiftState.ON) {
                setShiftState(ShiftState.OFF);
            }
        });

        mAllLetterKeys.add(key);
        return key;
    }

    // ──────────────────────────────────────────────────────────
    // Generic key factory
    // ──────────────────────────────────────────────────────────
    private TextView makeKey(String label, float textSizeSp, String bgHex, String textHex) {
        TextView tv = new TextView(mContext);
        tv.setText(label);
        tv.setTextSize(textSizeSp);
        tv.setTextColor(Color.parseColor(textHex));
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setBackgroundResource(R.drawable.key_bg_rounded);
        tv.getBackground().setTint(Color.parseColor(bgHex));
        tv.setPadding(4, 4, 4, 4);

        // Press feedback
        tv.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    tv.getBackground().setTint(Color.parseColor("#FFE94560"));
                    tv.animate().scaleX(0.92f).scaleY(0.92f).setDuration(60).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    tv.getBackground().setTint(Color.parseColor(bgHex));
                    tv.animate().scaleX(1f).scaleY(1f).setDuration(60).start();
                    break;
            }
            return false;
        });

        return tv;
    }

    // ──────────────────────────────────────────────────────────
    // Public mode and state control
    // ──────────────────────────────────────────────────────────

    /** Cycle shift states: OFF → ON → CAPS_LOCK → OFF */
    public void cycleShiftState() {
        switch (mShiftState) {
            case OFF:      setShiftState(ShiftState.ON);       break;
            case ON:       setShiftState(ShiftState.CAPS_LOCK); break;
            case CAPS_LOCK: setShiftState(ShiftState.OFF);     break;
        }
    }

    public void setShiftState(ShiftState state) {
        mShiftState = state;
        mShiftOn = (state != ShiftState.OFF);
        // Update letter key labels and colours
        for (TextView key : mAllLetterKeys) {
            String current = key.getText().toString();
            if (mShiftOn) {
                key.setText(current.toUpperCase());
                key.setTextColor(Color.parseColor("#FFFFE66D"));
            } else {
                key.setText(current.toLowerCase());
                key.setTextColor(Color.parseColor("#FFFFFFFF"));
            }
        }
        // Update ⇧ key icon
        if (mShiftKey != null) {
            switch (state) {
                case OFF:
                    mShiftKey.setText("⇧");
                    mShiftKey.setTextColor(Color.parseColor("#FFFFFFFF"));
                    mShiftKey.getBackground().setTint(Color.parseColor("#FF2D2D44"));
                    break;
                case ON:
                    mShiftKey.setText("⇧");
                    mShiftKey.setTextColor(Color.parseColor("#FFFFE66D"));
                    mShiftKey.getBackground().setTint(Color.parseColor("#FF1E3A5F"));
                    break;
                case CAPS_LOCK:
                    mShiftKey.setText("⇪");
                    mShiftKey.setTextColor(Color.parseColor("#FF4ECDC4"));
                    mShiftKey.getBackground().setTint(Color.parseColor("#FF1E3A5F"));
                    break;
            }
        }
    }

    /** Legacy compat – called from IME for reset */
    public void updateShiftState(boolean shiftOn) {
        setShiftState(shiftOn ? ShiftState.ON : ShiftState.OFF);
    }

    public ShiftState getShiftState() {
        return mShiftState;
    }

    public void resetKeyboardState() {
        mSymbolsPage2 = false;
        setShiftState(ShiftState.OFF);
        setMode(KeyboardMode.TEXT);
    }

    public KeyboardMode getCurrentMode() {
        return mCurrentMode;
    }

    public void setMode(KeyboardMode mode) {
        mCurrentMode = mode;

        // Hide all major layout panels first to prevent overlay / duplicate bugs
        if (mNumberRow != null) mNumberRow.setVisibility(View.GONE);
        if (mToneRow != null) mToneRow.setVisibility(View.GONE);
        if (mVnRow != null) mVnRow.setVisibility(View.GONE);
        if (mRow1 != null) mRow1.setVisibility(View.GONE);
        if (mRow2 != null) mRow2.setVisibility(View.GONE);
        if (mRow3 != null) mRow3.setVisibility(View.GONE);
        if (mSymbolsPanel != null) mSymbolsPanel.setVisibility(View.GONE);
        if (mEmojiPanel != null) mEmojiPanel.setVisibility(View.GONE);

        switch (mode) {
            case TEXT:
                if (mToneRow != null) mToneRow.setVisibility(View.VISIBLE);
                if (mVnRow != null) mVnRow.setVisibility(View.VISIBLE);
                if (mRow1 != null) mRow1.setVisibility(View.VISIBLE);
                if (mRow2 != null) mRow2.setVisibility(View.VISIBLE);
                if (mRow3 != null) mRow3.setVisibility(View.VISIBLE);
                if (mNumberRow != null && mShowNumbers) mNumberRow.setVisibility(View.VISIBLE);

                if (mEmojiBtn != null) mEmojiBtn.setText("😊");
                if (mNumToggleBtn != null) {
                    mNumToggleBtn.setText("123");
                    mNumToggleBtn.setTextColor(Color.parseColor("#FF4ECDC4"));
                }
                break;

            case SYMBOLS:
                if (mSymbolsPanel != null) {
                    mSymbolsPanel.setVisibility(View.VISIBLE);
                    buildSymbolsPanel();
                }
                if (mEmojiBtn != null) mEmojiBtn.setText("😊");
                if (mNumToggleBtn != null) {
                    mNumToggleBtn.setText("ABC");
                    mNumToggleBtn.setTextColor(Color.parseColor("#FFFFE66D"));
                }
                break;

            case EMOJI:
                if (mEmojiPanel != null) {
                    mEmojiPanel.setVisibility(View.VISIBLE);
                    mEmojiPanel.scrollTo(0, 0);
                }
                if (mEmojiBtn != null) mEmojiBtn.setText("⌨️");
                if (mNumToggleBtn != null) {
                    mNumToggleBtn.setText("123");
                    mNumToggleBtn.setTextColor(Color.parseColor("#FF4ECDC4"));
                }
                break;
        }
    }

    public void toggleEmoji() {
        if (mCurrentMode == KeyboardMode.EMOJI) {
            setMode(KeyboardMode.TEXT);
        } else {
            setMode(KeyboardMode.EMOJI);
        }
    }

    public void toggleSymbols() {
        if (mCurrentMode == KeyboardMode.SYMBOLS) {
            setMode(KeyboardMode.TEXT);
        } else {
            setMode(KeyboardMode.SYMBOLS);
        }
    }

    public void toggleNumberRow(boolean show) {
        mShowNumbers = show;
        if (mCurrentMode == KeyboardMode.TEXT && mNumberRow != null) {
            mNumberRow.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        buildToneRow();
    }
}
