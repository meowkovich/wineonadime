package com.example.wineonadime;

import android.view.View;

/*
 * This interface is used to help safely call a function in MainActivity to open Search Dialog.
 * This is needed because Search Dialogs cannot be opened from inside fragments.
 * Please see second response in this thread:
 *  https://stackoverflow.com/questions/12659747/call-an-activity-method-from-a-fragment
 */

public interface SearchListener
{
    public void openSearch( View v );
}
