package com.tafatalkstudent.Shared;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import com.tafatalkstudent.R;


public class CustomLoadDialogClass extends Dialog {

    public Activity c;
    public Dialog d;

    public CustomLoadDialogClass(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
    }

}


