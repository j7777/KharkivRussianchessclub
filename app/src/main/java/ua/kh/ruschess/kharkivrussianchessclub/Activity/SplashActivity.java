package ua.kh.ruschess.kharkivrussianchessclub.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import ua.kh.ruschess.kharkivrussianchessclub.R;

public class SplashActivity extends AppCompatActivity {
    ImageView logoSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoSplash = (ImageView) findViewById(R.id.logoSplashImg);
        startAnimateSplash();
    }

    private void startAnimateSplash(){
        Animation fadeSplash = AnimationUtils.loadAnimation(this, R.anim.splash);
        logoSplash.startAnimation(fadeSplash);

        fadeSplash.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, EventsActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    protected void stopAnimateSplash(){
        logoSplash.clearAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAnimateSplash();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimateSplash();
    }
}
