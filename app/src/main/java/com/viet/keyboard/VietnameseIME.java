package com.viet.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
        if (getCurrentInputConnection() == null) return;

        if (mComposing.length() > 0) {
            // We have composing text - just commit and type
            getCurrentInputConnection().commitText(mComposing.toString(), 1);
            mComposing.setLength(0);
        }
        getCurrentInputConnection().commitText(text, 1);

        if (mShiftOn) {
            mShiftOn = false;
            mKeyboardManager.updateShiftState(false);
        }
    }

    private void onSpecialKey(int keyCode) {
        if (getCurrentInputConnection() == null) return;

        switch (keyCode) {
            case VietnameseKeyboardManager.KEY_DELETE:
                if (mComposing.length() > 0) {
                    mComposing.deleteCharAt(mComposing.length() - 1);
                    getCurrentInputConnection().setComposingText(mComposing, 1);
                } else {
                    getCurrentInputConnection().deleteSurroundingText(1, 0);
                }
                break;

            case VietnameseKeyboardManager.KEY_SPACE:
                if (mComposing.length() > 0) {
                    getCurrentInputConnection().commitText(mComposing.toString(), 1);
                    mComposing.setLength(0);
                }
                getCurrentInputConnection().commitText(" ", 1);
                break;

            case VietnameseKeyboardManager.KEY_ENTER:
                if (mComposing.length() > 0) {
                    getCurrentInputConnection().commitText(mComposing.toString(), 1);
                    mComposing.setLength(0);
                }
                getCurrentInputConnection().performEditorAction(EditorInfo.IME_ACTION_GO);
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
