package crysoftdynamics.timeline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class SaveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        final String fullimagePath = getIntent().getStringExtra("fullimagePath");
        final String thumbnailPath = getIntent().getStringExtra("thumbnailPath");
        ImageView preview = (ImageView) findViewById(R.id.SavedPreviewImage);
        View parent = (View)preview.getParent();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(fullimagePath, bmOptions);
       // bitmap = Bitmap.createScaledBitmap(bitmap, parent.getWidth(), parent.getHeight(), true);
        preview.setImageBitmap(bitmap);

        FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText caption = (EditText) findViewById(R.id.captionTxt);
                savePost(fullimagePath,thumbnailPath, String.valueOf(caption.getText()));
               Intent i = new Intent(getApplicationContext(), HomeActivity.class);
               startActivityForResult(i, 0);
            }
        });
   }
    public void savePost(String imageURL,String thumbnailURL, String caption){
        Log.i("Saved Details:","Image=" + imageURL +" Thumbnail="+ thumbnailURL + " Caption=" + caption);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
