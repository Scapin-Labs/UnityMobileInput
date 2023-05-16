// ----------------------------------------------------------------------------
// The MIT License
// UnityMobileInput https://github.com/mopsicus/UnityMobileInput
// Copyright (c) 2018 Mopsicus <mail@mopsicus.ru>
// ----------------------------------------------------------------------------

package ru.mopsicus.mobileinput;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mopsicus.common.Common;

public class KeyboardListener implements KeyboardObserver {

    private boolean isPreviousState = false;
    private Common common = new Common();

    @Override
    public void onKeyboardHeight(float height, int keyboardHeight, int orientation, int keyboardHeight2) {
        boolean isShow = (keyboardHeight > 0);
        JSONObject json = new JSONObject();
        
        try {
            json.put("msg", Plugin.KEYBOARD_ACTION);
            json.put("show", isShow);
            json.put("height", height);
            json.put("keyboard_height_without_bottom", keyboardHeight);
            json.put("keyboard_bottom", keyboardHeight2);
            json.put("orientation", orientation);
        } catch (JSONException e) {}
        if (isPreviousState != isShow) {
            isPreviousState = isShow;
            common.sendData(Plugin.name, json.toString());
        }
    }

}

