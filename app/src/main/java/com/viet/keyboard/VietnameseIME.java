package com.viet.keyboard;

import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class VietnameseIME extends InputMethodService {

    private View mKeyboardView;
    private VietnameseKeyboardManager mKeyboardManager;
    private boolean mShowNumbers = true;
    private boolean mShowEmoji = false;
    private boolean mShiftOn = false;

    @Override
    public View onCreateInputView() {
        mKeyboardView = LayoutInflater.from(this).inflate(R.layout.keyboard_view, null);
        mKeyboardManager = new VietnameseKeyboardManager(
                this, mKeyboardView, this::onKeyPressed, this::onSpecialKey
        );
        mKeyboardManager.buildKeyboard(mShowNumbers);
        return mKeyboardView;
    }

    private void onKeyPressed(String text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null || text == null || text.isEmpty()) return;

        // Check if this is a tone action from the Tone Bar
        if (text.startsWith("TONE:")) {
            handleToneAction(text, ic);
            return;
        }

        // Direct commit for text (ensures cursor stays exactly at current editing position)
        ic.commitText(text, 1);

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

        // Find the Vietnamese word immediately preceding the cursor at the current position
        CharSequence before = ic.getTextBeforeCursor(50, 0);
        if (before != null && before.length() > 0) {
            String beforeStr = before.toString();
            int i = beforeStr.length() - 1;
            while (i >= 0 && VietnameseEngine.isVietnameseWordChar(beforeStr.charAt(i))) {
                i--;
            }
            int wordStart = i + 1;
            if (wordStart < beforeStr.length()) {
                String lastWord = beforeStr.substring(wordStart);
                String tonedWord = VietnameseEngine.applyTone(lastWord, targetTone);
                ic.deleteSurroundingText(lastWord.length(), 0);
                ic.commitText(tonedWord, 1);
            }
        }
    }

    private void onSpecialKey(int keyCode) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (keyCode) {
            case VietnameseKeyboardManager.KEY_DELETE:
                // Delete single char at current cursor position or delete selection
                CharSequence selected = ic.getSelectedText(0);
                if (selected != null && selected.length() > 0) {
                    ic.commitText("", 1);
                } else {
                    ic.deleteSurroundingText(1, 0);
                }
                break;

            case VietnameseKeyboardManager.KEY_DELETE_WORD:
                // Delete whole word before cursor (held >400ms)
                CharSequence beforeWord = ic.getTextBeforeCursor(100, 0);
                if (beforeWord != null && beforeWord.length() > 0) {
                    int len = beforeWord.length();
                    int i = len - 1;
                    // Skip trailing whitespace/punctuations
                    while (i >= 0 && !VietnameseEngine.isVietnameseWordChar(beforeWord.charAt(i))) {
                        i--;
                    }
                    // Delete backwards across the word
                    while (i >= 0 && VietnameseEngine.isVietnameseWordChar(beforeWord.charAt(i))) {
                        i--;
                    }
                    int deleteCount = len - (i + 1);
                    if (deleteCount <= 0) {
                        deleteCount = 1;
                    }
                    ic.deleteSurroundingText(deleteCount, 0);
                } else {
                    ic.deleteSurroundingText(1, 0);
                }
                break;

            case VietnameseKeyboardManager.KEY_DELETE_ALL:
                // Delete all text in current input (held >3 seconds)
                CharSequence beforeAll = ic.getTextBeforeCursor(5000, 0);
                CharSequence afterAll = ic.getTextAfterCursor(5000, 0);
                int beforeLen = beforeAll != null ? beforeAll.length() : 0;
                int afterLen = afterAll != null ? afterAll.length() : 0;
                if (beforeLen > 0 || afterLen > 0) {
                    ic.deleteSurroundingText(beforeLen, afterLen);
                } else {
                    ic.deleteSurroundingText(10000, 10000);
                }
                break;

            case VietnameseKeyboardManager.KEY_SPACE:
                ic.commitText(" ", 1);
                break;

            case VietnameseKeyboardManager.KEY_ENTER:
                ic.performEditorAction(EditorInfo.IME_ACTION_GO);
                sendDefaultEditorAction(true);
                break;

            case VietnameseKeyboardManager.KEY_SHIFT:
                mShiftOn = !mShiftOn;
                mKeyboardManager.updateShiftState(mShiftOn);
                break;

            case VietnameseKeyboardManager.KEY_TOGGLE_NUMBERS:
                mKeyboardManager.toggleSymbols();
                break;

            case VietnameseKeyboardManager.KEY_TOGGLE_EMOJI:
                mKeyboardManager.toggleEmoji();
                break;
        }
    }
}
