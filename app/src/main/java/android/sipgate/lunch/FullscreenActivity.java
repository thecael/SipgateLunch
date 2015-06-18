package android.sipgate.lunch;

import android.os.Bundle;
import android.sipgate.lunch.sipgatelunch.R;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import com.ortiz.touch.TouchImageView;

import android.schafm.helper.UiHelper;

/**
 * Zoomable fullscreen view of a meal photo.
 * @author schafm
 */
public class FullscreenActivity  extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen);
        String url = getIntent().getExtras().getString("image_url");

        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "Kein Foto gefunden!", Toast.LENGTH_SHORT).show();
            finish();
        }

        TouchImageView imageView = (TouchImageView) findViewById(R.id.image);

        int height = UiHelper.getDisplayHeight();
        int width = UiHelper.getDisplayWidth();
        Picasso.with(this).load(url)
                .placeholder(R.drawable.meal_template)
                .error(R.drawable.meal_template)
                .resize(width, height).centerInside()
                .into(imageView);
    }
}
