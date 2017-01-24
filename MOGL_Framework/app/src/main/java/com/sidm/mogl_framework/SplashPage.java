package com.sidm.mogl_framework;

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.MotionEvent;
        import android.view.Window;
        import android.view.WindowManager;

/**
 * Created by AFTERSHOCK on 24/11/2016.
 */

/*
Bundle - Bundles are generally used for passing data between various Android Activities.
It is up to you what type of values you want to pass, but Bundles can hold all types of values
and pass them to the new Activity.

You can use it like this:
Intent intent = new...
Intent(getApplicationContext(), SecondActivity.class);
intent.putExtra("myKey", AnyValue);
startActivity(intent);

You can get the passed values by doing:
Bundle extras = intent.getExtras();
String tmp = extras.getString("myKey");
*/

public class SplashPage extends Activity {

    //Variable(s)
    int displayTime = 3000;
    boolean skipSplashPage = false;
    Thread displayThread = new Thread() {
        @Override
        public void run() {
            try {
                int timePassed = 0;
                while(!skipSplashPage && (timePassed < displayTime)) {
                    //Sleep for 200ms
                    displayThread.sleep(200);
                    if(!skipSplashPage) {
                        timePassed += 200;
                    }
                }
            } catch (InterruptedException e) {
                //Do nothing.
            } finally {
                /*
                Calling finish() in onCreate(): onCreate() -> onDestroy()
                Calling finish() in onStart() : onCreate() -> onStart() -> onStop() -> onDestroy()
                Calling finish() in onResume(): onCreate() -> onStart() -> onResume() -> onPause() -> onStop() -> onDestroy()
                */
                finish();

                //Create a new Activity based on our Intent.
                Intent intent = new Intent(SplashPage.this, MainMenu.class);
                startActivity(intent);
            }
        }
    };

    //Overrides
    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        //Call our parent's function.
        super.onCreate(_savedInstanceState);

        //Hide Title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Hide Top (Fullscreen)
        //1st Parameter: Flags, 2nd Parameter: Mask
        //It just so happens that for fullscreen both flag and mask are the same.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Which layout we want to use.
        setContentView(R.layout.splashpage);

        //Start our displayThread which shows our splash screen.
        //This will call the run() function of our displayThread.
        displayThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent _event){
        //Check for the user tapping the screen.
        if(_event.getAction() == MotionEvent.ACTION_DOWN){
            //Skip the splash screen.
            skipSplashPage = true;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}