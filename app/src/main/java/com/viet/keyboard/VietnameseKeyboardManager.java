package com.viet.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class VietnameseKeyboardManager {

    // Special key codes
    public static final int KEY_DELETE        = -1;
    public static final int KEY_SPACE         = -2;
    public static final int KEY_ENTER         = -3;
    public static final int KEY_SHIFT         = -4;
    public static final int KEY_TOGGLE_NUMBERS = -5;
    public static final int KEY_TOGGLE_EMOJI   = -6;

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

    // Vietnamese special characters - extra letters
    private static final String[] VN_EXTRA_LOWER = {"ă","â","đ","ê","ô","ơ","ư"};
    private static final String[] VN_EXTRA_UPPER = {"Ă","Â","Đ","Ê","Ô","Ơ","Ư"};

    // Number row
    private static final String[] NUMBERS = {"1","2","3","4","5","6","7","8","9","0"};

    // Emoji categories
    private static final String[][] EMOJI_ROWS = {
        // Happy & Love
        {"😊","😍","🥰","😘","😂","🤣","😅","😆","🥹","😁","😄","😃","😀","😋","😎"},
        // Sad & Emotions
        {"😢","😭","😤","😠","🥺","😩","😫","🥴","😵","🤯","😱","😨","😰","😓","😔"},
        // Nature & Animals
        {"🌸","🌺","🌻","🌹","🌷","🍀","🌈","⭐","🌙","☀️","🐱","🐶","🐻","🦋","🐝"},
        // Food & Drinks
        {"🍜","🍣","🍕","🍔","🍦","🍰","🎂","☕","🧋","🍵","🍱","🥗","🍇","🍓","🍑"},
        // Hearts & Symbols
        {"❤️","🧡","💛","💚","💙","💜","🤍","🖤","💖","💗","💓","💞","💕","💝","✨"},
        // Activities & Objects
        {"🎵","🎶","🎸","🎮","⚽","🏀","🎯","🎁","🎉","🎊","🔥","💫","⚡","🌊","🎀"},
        // Vietnamese / Chat specific
        {"👍","👎","👏","🙌","✌️","🤞","💪","🤙","👋","🤝","🫶","❤️‍🔥","😤","💯","🆗"}
    };

    private final Context mContext;
    private final View mRootView;
    private final OnKeyListener mKeyListener;
    private final OnSpecialKeyListener mSpecialKeyListener;

    private LinearLayout mNumberRow;
    private LinearLayout mToneRow;
    private LinearLayout mVnRow;
    private LinearLayout mRow1, mRow2, mRow3, mRowBottom;
    private LinearLayout mEmojiContainer;
    private HorizontalScrollView mEmojiPanel;
    private List<TextView> mAllLetterKeys = new ArrayList<>();
    private boolean mShiftOn = false;

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
    }

    public void buildKeyboard(boolean showNumbers) {
        mAllLetterKeys.clear();

        buildNumberRow();
        buildToneRow();
        buildVnExtraRow();
        buildRow1();
        buildRow2();
        buildRow3();
        buildBottomRow();
        buildEmojiPanel();

        if (!showNumbers) {
            mNumberRow.setVisibility(View.GONE);
        }
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
    // Tone row (dấu thanh)
    // ──────────────────────────────────────────────────────────
    private void buildToneRow() {
        mToneRow.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lp.setMargins(3, 0, 3, 0);

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
            symView.setTextSize(15f);
            symView.setTextColor(Color.parseColor(color));
            symView.setTypeface(Typeface.DEFAULT_BOLD);
            symView.setGravity(Gravity.CENTER);

            TextView lblView = new TextView(mContext);
            lblView.setText(label);
            lblView.setTextSize(8f);
            lblView.setTextColor(Color.parseColor("#FFAAAACC"));
            lblView.setGravity(Gravity.CENTER);

            cell.addView(symView);
            cell.addView(lblView);

            // Emit tone action
            cell.setOnClickListener(v -> mKeyListener.onKey(action));
            mToneRow.addView(cell);
        }
    }

    // ──────────────────────────────────────────────────────────
    // Vietnamese extra letters row: ă, â, đ, ê, ô, ơ, ư
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
            key.getBackground().setTint(Color.parseColor("#FF202C45"));
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

        // Backspace
        TextView del = makeKey("⌫", 18, "#FF2D2D44", "#FFFF6B6B");
        del.setLayoutParams(lpDel);
        del.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_DELETE));
        // Long press = delete word
        del.setOnLongClickListener(v -> {
            for (int i = 0; i < 5; i++) {
                mSpecialKeyListener.onSpecialKey(KEY_DELETE);
            }
            return true;
        });
        mRow3.addView(del);
    }

    // ──────────────────────────────────────────────────────────
    // Bottom row: [😊][Số][  Khoảng trắng  ][↵]
    // ──────────────────────────────────────────────────────────
    private void buildBottomRow() {
        mRowBottom.removeAllViews();

        LinearLayout.LayoutParams lpSmall = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.2f);
        lpSmall.setMargins(3, 0, 3, 0);

        LinearLayout.LayoutParams lpSpace = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 4f);
        lpSpace.setMargins(3, 0, 3, 0);

        LinearLayout.LayoutParams lpEnter = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.5f);
        lpEnter.setMargins(3, 0, 3, 0);

        // Emoji toggle
        TextView emojiBtn = makeKey("😊", 18, "#FF1A1A2E", "#FFFFFFFF");
        emojiBtn.setLayoutParams(lpSmall);
        emojiBtn.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_TOGGLE_EMOJI));
        mRowBottom.addView(emojiBtn);

        // Number row toggle
        TextView numToggle = makeKey("123", 13, "#FF1A1A2E", "#FF4ECDC4");
        numToggle.setLayoutParams(lpSmall);
        numToggle.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_TOGGLE_NUMBERS));
        mRowBottom.addView(numToggle);

        // Space
        TextView space = makeKey("Khoảng trắng", 12, "#FF2D2D44", "#FFAAAACC");
        space.setLayoutParams(lpSpace);
        space.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_SPACE));
        mRowBottom.addView(space);

        // Enter/Return
        TextView enter = makeKey("⏎ Gửi", 13, "#FFE94560", "#FFFFFFFF");
        enter.setLayoutParams(lpEnter);
        enter.setOnClickListener(v -> mSpecialKeyListener.onSpecialKey(KEY_ENTER));
        mRowBottom.addView(enter);
    }

    // ──────────────────────────────────────────────────────────
    // Emoji Panel – 7 rows of emojis in horizontal scroll
    // ──────────────────────────────────────────────────────────
    private void buildEmojiPanel() {
        mEmojiContainer.removeAllViews();

        String[] categoryLabels = {
            "😊 Vui vẻ", "😢 Cảm xúc", "🌸 Thiên nhiên",
            "🍜 Đồ ăn", "❤️ Trái tim", "🎵 Hoạt động", "👍 Chat"
        };

        for (int r = 0; r < EMOJI_ROWS.length; r++) {
            LinearLayout row = new LinearLayout(mContext);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 4, 0, 4);

            // Category label
            TextView label = new TextView(mContext);
            label.setText(categoryLabels[r]);
            label.setTextSize(10f);
            label.setTextColor(Color.parseColor("#FF8888AA"));
            label.setPadding(8, 0, 8, 4);
            row.addView(label);

            // Emoji row
            LinearLayout emojiRow = new LinearLayout(mContext);
            emojiRow.setOrientation(LinearLayout.HORIZONTAL);

            for (String emoji : EMOJI_ROWS[r]) {
                TextView ev = new TextView(mContext);
                ev.setText(emoji);
                ev.setTextSize(22f);
                ev.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(52, 52);
                lp.setMargins(4, 2, 4, 2);
                ev.setLayoutParams(lp);
                ev.setOnClickListener(v -> mKeyListener.onKey(ev.getText().toString()));
                emojiRow.addView(ev);
            }

            row.addView(emojiRow);
            mEmojiContainer.addView(row);
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
    // Public control methods
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

    public void toggleNumberRow(boolean show) {
        mNumberRow.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void toggleEmojiPanel(boolean show) {
        mEmojiPanel.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
