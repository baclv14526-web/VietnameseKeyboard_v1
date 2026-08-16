package com.viet.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.HorizontalScrollView;

public class VietnameseIME extends InputMethodService {

    private View mKeyboardView;
    private VietnameseKeyboardManager mKeyboardManager;
    private boolean mShowNumbers = true;
    private boolean mShowEmoji = false;
    private boolean mShiftOn = false;
    private StringBuilder mComposing = new StringBuilder();

    @Override
    public View onCreateInputView() {
        mKeyboardView = LayoutInflater.from(this).inflate(R.layout.keyboard_view, null);
        mKeyboardManager = new VietnameseKeyboardManager(
                this, mKeyboardView, this::onKeyPressed, this::onSpecialKey
        );
        mKeyboardManager.buildKeyboard(mShowNumbers);
        return mKeyboardView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        mComposing.setLength(0);
    }

    private void onKeyPressed(String text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null || text == null || text.isEmpty()) return;

        // Check if this is a tone action from the Tone Bar
        if (text.startsWith("TONE:")) {
            handleToneAction(text, ic);
            return;
        }

        // Check if text is a single word character (Vietnamese letter or latin letter)
        boolean isSingleChar = text.length() == 1;
        char ch = text.charAt(0);
        boolean isWordChar = isSingleChar && VietnameseEngine.isVietnameseWordChar(ch);

        if (isWordChar) {
            // Process character through VietnameseEngine
            VietnameseEngine.ProcessResult result = VietnameseEngine.processKey(mComposing.toString(), text);
            mComposing.setLength(0);
            mComposing.append(result.word);
            ic.setComposingText(mComposing, 1);
        } else {
            // Non-word character (e.g., number, punctuation, emoji, multi-character symbol)
            if (mComposing.length() > 0) {
                ic.commitText(mComposing.toString(), 1);
                mComposing.setLength(0);
            }
            ic.commitText(text, 1);
        }

        if (mShiftOn) {
            mShiftOn = false;
            mKeyboardManager.updateShiftState(false);
        }
    }

    private void handleToneAction(String toneAction, InputConnection ic) {
        VietnameseEngine.Tone targetTone = VietnameseEngine.Tone.NONE;
        if (VietnameseKeyboardManager.TONE_ACUTE.equals(toneAction)) {
            targetTone = VietnameseEngine.Tone.ACUTE;
        } else if (VietnameseKeyboardManager.TONE_GRAVE.equals(toneAction)) {
            targetTone = VietnameseEngine.Tone.GRAVE;
        } else if (VietnameseKeyboardManager.TONE_HOOK.equals(toneAction)) {
            targetTone = VietnameseEngine.Tone.HOOK;
        } else if (VietnameseKeyboardManager.TONE_TILDE.equals(toneAction)) {
            targetTone = VietnameseEngine.Tone.TILDE;
        } else if (VietnameseKeyboardManager.TONE_DOT.equals(toneAction)) {
            targetTone = VietnameseEngine.Tone.DOT;
        } else if (VietnameseKeyboardManager.TONE_NONE.equals(toneAction)) {
            targetTone = VietnameseEngine.Tone.NONE;
        }

        if (mComposing.length() > 0) {
            String updated = VietnameseEngine.applyTone(mComposing.toString(), targetTone);
            mComposing.setLength(0);
            mComposing.append(updated);
            ic.setComposingText(mComposing, 1);
        } else {
            // If no active composing text, check the word immediately preceding the cursor
            CharSequence before = ic.getTextBeforeCursor(50, 0);
            if (before != null && before.length() > 0) {
                String beforeStr = before.toString();
                // Find the word at the end of the text
                int i = beforeStr.length() - 1;
                while (i >= 0 && VietnameseEngine.isVietnameseWordChar(beforeStr.charAt(i))) {
                    i--;
                }
                int wordStart = i + 1;
                if (wordStart < beforeStr.length()) {
                    String lastWord = beforeStr.substring(wordStart);
                    String tonedWord = VietnameseEngine.applyTone(lastWord, targetTone);
                    ic.deleteSurroundingText(lastWord.length(), 0);
                    mComposing.setLength(0);
                    mComposing.append(tonedWord);
                    ic.setComposingText(mComposing, 1);
                }
            }
        }
    }

    private void onSpecialKey(int keyCode) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (keyCode) {
            case VietnameseKeyboardManager.KEY_DELETE:
                if (mComposing.length() > 0) {
                    mComposing.deleteCharAt(mComposing.length() - 1);
                    if (mComposing.length() > 0) {
                        ic.setComposingText(mComposing, 1);
                    } else {
                        ic.commitText("", 1);
                    }
                } else {
                    ic.deleteSurroundingText(1, 0);
                }
                break;

            case VietnameseKeyboardManager.KEY_SPACE:
                if (mComposing.length() > 0) {
                    ic.commitText(mComposing.toString(), 1);
                    mComposing.setLength(0);
                }
                ic.commitText(" ", 1);
                break;

            case VietnameseKeyboardManager.KEY_ENTER:
                if (mComposing.length() > 0) {
                    ic.commitText(mComposing.toString(), 1);
                    mComposing.setLength(0);
                }
                ic.performEditorAction(EditorInfo.IME_ACTION_GO);
                sendDefaultEditorAction(true);
                break;

            case VietnameseKeyboardManager.KEY_SHIFT:
                mShiftOn = !mShiftOn;
                mKeyboardManager.updateShiftState(mShiftOn);
                break;

            case VietnameseKeyboardManager.KEY_TOGGLE_NUMBERS:
                mShowNumbers = !mShowNumbers;
                mKeyboardManager.toggleNumberRow(mShowNumbers);
                break;

            case VietnameseKeyboardManager.KEY_TOGGLE_EMOJI:
                mShowEmoji = !mShowEmoji;
                mKeyboardManager.toggleEmojiPanel(mShowEmoji);
                break;
        }
    }
}
