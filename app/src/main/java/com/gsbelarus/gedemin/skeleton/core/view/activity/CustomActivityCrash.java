/*
 * Copyright 2015 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gsbelarus.gedemin.skeleton.core.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.BaseApplication;
import com.gsbelarus.gedemin.skeleton.core.util.CrashHelper;

public class CustomActivityCrash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_crash);

        //**IMPORTANT**
        //The custom error activity in this sample is uglier than the default one and just
        //for demonstration purposes, please don't copy it to your project!
        //We recommend taking the original library's DefaultActivityCrash as a basis.
        //Of course, you are free to implement it as you wish in your application.

        //These four methods are available for you to use:
        //CrashHelper.getStackTraceFromIntent(getIntent()): gets the stack trace as a string
        //CrashHelper.getAllErrorDetailsFromIntent(context, getIntent()): returns all error details including stacktrace as a string
        //CrashHelper.getRestartActivityClassFromIntent(getIntent()): returns the class of the restart activity to launch, or null if none
        //CrashHelper.getEventListenerFromIntent(getIntent()): returns the event listener that must be passed to restartApplicationWithIntent or closeApplication

        //Now, treat here the error as you wish. If you allow the user to restart or close the app,
        //don't forget to call the appropriate methods.
        //Otherwise, if you don't finish the activity, you will get the CustomActivityCrash on the activity stack and it will be visible again under some circumstances.
        //Also, you will get multiprocess problems in API<17.

        TextView errorDetailsText = (TextView) findViewById(R.id.error_details);
        errorDetailsText.setText(CrashHelper.getStackTraceFromIntent(getIntent()));

        Button restartButton = (Button) findViewById(R.id.restart_button);

        final Class<? extends Activity> restartActivityClass = CrashHelper.getRestartActivityClassFromIntent(getIntent());
        final CrashHelper.EventListener eventListener = CrashHelper.getEventListenerFromIntent(getIntent());

        if (restartActivityClass != null) {
            restartButton.setText(R.string.restart_app);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BaseApplication.getInstance().trackEvent(this.getClass().getSimpleName(), "restart_button", "");

                    Intent intent = new Intent(CustomActivityCrash.this, restartActivityClass);
                    CrashHelper.restartApplicationWithIntent(CustomActivityCrash.this, intent, eventListener);
                }
            });
        } else {
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BaseApplication.getInstance().trackEvent(this.getClass().getSimpleName(), "restart_button", "");

                    CrashHelper.closeApplication(CustomActivityCrash.this, eventListener);
                }
            });
        }
    }
}
