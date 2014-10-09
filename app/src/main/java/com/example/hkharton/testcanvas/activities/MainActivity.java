package com.example.hkharton.testcanvas.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hkharton.testcanvas.view.DrawTest;
import com.example.hkharton.testcanvas.R;


public class MainActivity extends Activity {

    final int ACTIVITY_SELECT_PATTERN = 1234;
    DrawTest drawTest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        // set the view to be the custom view for the color tiles
        drawTest = new DrawTest(this);
        setContentView(drawTest);

        /*
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#CD5C5C"));
        Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bg);
        canvas.drawRect(50, 50, 200, 200, paint);
        canvas.drawRect(100, 100, 500, 500, paint);

        LinearLayout ll = (LinearLayout) findViewById(R.id.rect);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));
        */
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case ACTIVITY_SELECT_PATTERN:
                if(resultCode == RESULT_OK){
                    // decode the pattern & add color tile to the view
                    String imagePath = data.getStringExtra("imagePath");
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(imagePath);
                    yourSelectedImage = Bitmap.createScaledBitmap(yourSelectedImage, 500, 500, false);
                    drawTest.addNewColorTile(yourSelectedImage);

                    /*
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    // decode the pattern & add color tile to the view
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    yourSelectedImage = Bitmap.createScaledBitmap(yourSelectedImage, 500, 500, false);
                    drawTest.addNewColorTile(yourSelectedImage);
                    */
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_add){
            addNewTile();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewTile(){
        //Uri uri = Uri.parse(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI.toString() + "/myalbum");
        /*
        Uri uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        Intent patternIntent = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(patternIntent, ACTIVITY_SELECT_PATTERN);
        */

        Intent patternIntent = new Intent(MainActivity.this, LibraryActivity.class);
        startActivityForResult(patternIntent, ACTIVITY_SELECT_PATTERN);
    }
}
