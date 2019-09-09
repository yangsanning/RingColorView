package ysn.com.demo.ringcolorview;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import ysn.com.view.ringcolorview.ColorUtils;
import ysn.com.view.ringcolorview.RingColorView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View colorLayout = findViewById(R.id.main_activity_color_layout);
        final View colorView = findViewById(R.id.main_activity_color);
        final ImageView angleImageView = findViewById(R.id.main_activity_angle);
        RingColorView ringColorView = findViewById(R.id.main_activity_ring_color_view);

        ringColorView.setOnMultiChangeListener(new RingColorView.OnMultiChangeListener() {
            @Override
            public void colorChanged(int color) {
                colorView.setBackgroundColor(color);
                Log.d("test", "color: "+ ColorUtils.getHexCode(color));

            }

            @Override
            public void onAngle(float angle) {
                angleImageView.setRotation((float) (angle*(180/Math.PI)));
            }
        });
    }
}
