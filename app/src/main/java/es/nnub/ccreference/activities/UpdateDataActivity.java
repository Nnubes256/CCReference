package es.nnub.ccreference.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;

import es.nnub.ccreference.MainActivity;
import es.nnub.ccreference.R;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;

public class UpdateDataActivity extends AppCompatActivity {

    private int stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        final ProgressBar updateProgressBar = findViewById(R.id.progressBar);
        Button cancelButton = findViewById(R.id.cancelButton);

        stage = 0;

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("success", false);
                setResult(RESULT_CANCELED, result);
                finish();
            }
        });

        Thread splashThread = new Thread() {
            public void run() {
                while (stage < 4) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (Math.random() > 0.87) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Intent result = new Intent();
                                    result.putExtra("success", false);
                                    setResult(RESULT_OK, result);
                                    finish();
                                }
                            });
                            return;
                        }
                        stage++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ObjectAnimator animator = ObjectAnimator.ofInt(updateProgressBar, "progress", 250 * stage);
                                animator.setDuration(300);
                                animator.setInterpolator(new DecelerateInterpolator());
                                animator.start();
                            }
                        });
                        if (stage >= 4) {
                            try {
                                sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Intent result = new Intent();
                                        result.putExtra("success", true);
                                        setResult(RESULT_OK, result);
                                        finish();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        };
        splashThread.start();
    }
}
