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
        {"´", "Sắc", "#FFFF6B6B", TONE_ACUTE},
        {"`", "Huyền", "#FF4ECDC4", TONE_GRAVE},
        {"?", "Hỏi", "#FF95E1D3", TONE_HOOK},
        {"~", "Ngã", "#FFFFE66D", TONE_TILDE},
        {".", "Nặng", "#FFFF8B94", TONE_DOT},
        {"—", "Ngang", "#FF8899AA", TONE_NONE}
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
        // 1. Mặt cười & Cảm xúc
        {"😊 Mặt cười & Cảm xúc",
         "😊","😍","🥰","😘","😂","🤣","😅","😆","🥹","😁","😄","😃","😀","😋","😎","🥳","🤩","😜","🤪","🤗","🤔","🤫","🫡","😴","🤤","😷","🤒","🤧","🥺","😢","😭","😤","😠","😡","🤬","🤯","😳","🥵","🥶","😱","😨","😰","😥","😓","😏","🙄","😬","😌","😔","😪","😵","🤠","👻","🤡","💩","😈","💀","👽","🤖"},
        // 2. Cử chỉ & Bàn tay
        {"👍 Cử chỉ & Bàn tay",
         "👍","👎","👏","🙌","👐","🤲","🤝","🙏","✌️","🤞","🤟","🤘","🤙","👈","👉","👆","🖕","👇","☝️","👋","🤚","🖐️","✋","✍️","🤳","💅","💪","🫶","💯","🆗","🆒","🆙","🆘","💢","💥","💫","💦","💨","👂","👃","👀","👁️","🧠","👅","👄","💋"},
        // 3. Trái tim & Tình yêu
        {"❤️ Trái tim & Tình yêu",
         "❤️","🧡","💛","💚","💙","💜","🖤","🤍","🤎","💔","❣️","💕","💞","💓","💗","💖","💘","💝","💟","✨","⭐","🌟","💫","⚡","🔥","☀️","🌙","☁️","🌧️","❄️","🌈","💎","👑","🔮","🧿","🎉","🎊","🎁","🎈","🏮"},
        // 4. Đồ ăn & Thức uống
        {"🍜 Đồ ăn & Thức uống",
         "🍜","🍲","🍱","🍣","🍙","🍚","🍛","🥟","🍢","🍧","🍨","🍦","🍰","🎂","🍮","🍭","🍬","🍫","🍿","🍩","🍪","☕","🍵","🧋","🥤","🧃","🍺","🍻","🥂","🍷","🍸","🍹","🍾","🍕","🍔","🍟","🌭","🥪","🌮","🌯","🥗","🍝","🥖","🥐","🍞","🍳","🧇","🥞","🍎","🍉","🍇","🍓","🍒","🍑","🥭","🍍","🥥","🥝","🥑"},
        // 5. Động vật & Thiên nhiên
        {"🌸 Động vật & Thiên nhiên",
         "🐱","🐶","🐻","🐼","🐨","🐯","🦁","🐮","🐷","🐸","🐵","🐔","🐧","🐦","🐤","🐣","🐥","🦆","🦅","🦉","🦇","🐺","🐗","🐴","🦄","🐝","🐛","🦋","🐌","🐞","🐜","🐢","🐍","🐙","🦑","🦐","🦞","🦀","🐡","🐠","🐟","🐬","🐳","🦈","🐊","🌸","🌺","🌻","🌹","🥀","🌷","🌼","💐","🌾","🌿","🍀","🍁","🍂","🍃"},
        // 6. Hoạt động & Du lịch
        {"⚽ Hoạt động & Du lịch",
         "⚽","🏀","🏈","⚾","🥎","🎾","🏐","🏉","🥏","🎱","🪀","🏓","🏸","🏒","⛳","🎯","🎮","🎲","🧩","🎨","🎬","🎤","🎧","🎼","🎹","🥁","🎷","🎺","🎸","🚗","🚕","🚙","🚌","🚎","🏎️","🚓","🚑","🚒","🚐","🛻","🚚","🚛","🚜","🛵","🏍️","🚲","🛴","✈️","🚀","🛸","🚢","⚓","🏖️","🏝️","🏕️","⛺","🏠","🏡","🏢"}
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

    private List<TextView> mAllLetterKeys = new ArrayList<>();
    private TextView mEmojiBtn;
    private TextView mNumToggleBtn;
    private boolean mShiftOn = false;
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
            symView.setTextSize(12.5f);
            symView.setTextColor(Color.parseColor(color));
            symView.setTypeface(Typeface.DEFAULT_BOLD);
            symView.setGravity(Gravity.CENTER);

            TextView lblView = new TextView(mContext);
            lblView.setText(label);
            lblView.setTextSize(7.5f);
            lblView.setTextColor(Color.parseColor("#FFAAAACC"));
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
        numIcon.setTextSize(11.5f);
        numIcon.setTextColor(mShowNumbers ? Color.parseColor("#FF4ECDC4") : Color.parseColor("#FF8899AA"));
        numIcon.setTypeface(Typeface.DEFAULT_BOLD);
        numIcon.setGravity(Gravity.CENTER);

        TextView numLbl = new TextView(mContext);
        numLbl.setText(mShowNumbers ? "Ẩn số" : "Hiện số");
        numLbl.setTextSize(7f);
        numLbl.setTextColor(mShowNumbers ? Color.parseColor("#FFCCE5FF") : Color.parseColor("#FF8899AA"));
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
        TextView shift = makeKey("⇧", 18, "#FF2D2D44", "#FFFFFFFF");
        shift.setLayoutParams(lpShift);
        shift.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_SHIFT));
        mRow3.addView(shift);

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
    public void updateShiftState(boolean shiftOn) {
        mShiftOn = shiftOn;
        for (TextView key : mAllLetterKeys) {
            String current = key.getText().toString();
            if (shiftOn) {
                key.setText(current.toUpperCase());
                key.setTextColor(Color.parseColor("#FFFFE66D"));
            } else {
                key.setText(current.toLowerCase());
                key.setTextColor(Color.parseColor("#FFFFFFFF"));
            }
        }
    }

    public void resetKeyboardState() {
        mShiftOn = false;
        mSymbolsPage2 = false;
        updateShiftState(false);
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
