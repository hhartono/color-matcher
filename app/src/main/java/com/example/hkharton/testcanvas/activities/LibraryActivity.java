package com.example.hkharton.testcanvas.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.hkharton.testcanvas.R;
import com.example.hkharton.testcanvas.adapter.ImageAdapter;
import com.example.hkharton.testcanvas.adapter.TabsPagerAdapter;
import com.example.hkharton.testcanvas.provider.MaterialsDataSource;
import com.example.hkharton.testcanvas.provider.TileImage;

import java.util.ArrayList;

public class LibraryActivity extends FragmentActivity implements ActionBar.TabListener {
    final int ACTIVITY_SELECT_PATTERN = 1234;
    private static ImageAdapter imageGridAdapter;

    // Tab element initialization
    public static final int WOOD_TYPE = 0;
    public static String[] materialTabs = {"Wood", "Wallpaper", "Paint", "Hardware", "Flooring"};
    private ViewPager viewPager;
    private TabsPagerAdapter tabPagerAdapter;
    private ActionBar actionBar;

    // Database initialization
    private static MaterialsDataSource materialsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //final ImageButton myButton = (ImageButton) findViewById(R.id.imageButton);

        /*
        Uri uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        Intent patternIntent = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(patternIntent, ACTIVITY_SELECT_PATTERN);
        */

        /*
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TEST - add new thumbnail to the grid view
                TileImage.imagePath.add("/storage/emulated/0/S Note Export/Idea note_20140920_232308_01(1).jpg");

                // Tell the image adapter the the data set has been updated
                mAdapter.notifyDataSetChanged();
                return;
            }
        });
        */

        // get the image adapter
        imageGridAdapter = new ImageAdapter(this);
        imageGridAdapter.setThumbnailSize(150); // set the thumbnail size in dp

        // Initialize the database
        materialsDataSource = new MaterialsDataSource(this);
        materialsDataSource.open();

        // Initialization of the tabs
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        tabPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        // Set tab adapter for the pager
        viewPager.setAdapter(tabPagerAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String eachTabName : materialTabs) {
            actionBar.addTab(actionBar.newTab().setText(eachTabName).setTabListener(this));
        }

        // on swiping the viewpager make respective tab selected
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);

                // update the image list
                // updateTileImageList(position);

                // Tell the image adapter the the data set has been updated
                // imageGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        // get the grid view
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(imageGridAdapter);

        // set listener for the gridview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.e("TEST", "PRESSED: " + TileImage.imagePath.get(position));

                Intent returnIntent = new Intent();
                returnIntent.putExtra("imagePath", TileImage.imagePath.get(position));
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        // TEST - database
        ArrayList<String> testString = new ArrayList<String>();
        testString.add("test1");

        ArrayList<String> testSmoothString = new ArrayList<String>();
        testSmoothString.add("woodSmooth");

        ArrayList<String> testRoughString = new ArrayList<String>();
        testRoughString.add("woodRough");

        ArrayList<String> testSuperRoughString = new ArrayList<String>();
        testSuperRoughString.add("woodSuperRough");

        // WARNING - very dangerous - will wipe all the data
        materialsDataSource.truncateMaterialDatabase();

        boolean insertStatus = materialsDataSource.createMaterial("wood1", 0, "/storage/emulated/0/DCIM/Camera/wood1.jpg", testRoughString);
        insertStatus = materialsDataSource.createMaterial("wood2", 0, "/storage/emulated/0/DCIM/Camera/wood2.jpg", testSmoothString);
        insertStatus = materialsDataSource.createMaterial("wood3", 0, "/storage/emulated/0/DCIM/Camera/wood3.jpg", testSuperRoughString);

        insertStatus = materialsDataSource.createMaterial("wallpaper1", 1, "/storage/emulated/0/DCIM/Camera/wallpaper1.jpg", testString);
        insertStatus = materialsDataSource.createMaterial("wallpaper2", 1, "/storage/emulated/0/DCIM/Camera/wallpaper2.jpg", testString);
        insertStatus = materialsDataSource.createMaterial("wallpaper3", 1, "/storage/emulated/0/DCIM/Camera/wallpaper3.jpg", testString);

        insertStatus = materialsDataSource.createMaterial("paint1", 2, "/storage/emulated/0/DCIM/Camera/paint1.jpg", testString);
        insertStatus = materialsDataSource.createMaterial("paint2", 2, "/storage/emulated/0/DCIM/Camera/paint2.jpg", testString);
        insertStatus = materialsDataSource.createMaterial("paint3", 2, "/storage/emulated/0/DCIM/Camera/paint3.jpg", testString);

        insertStatus = materialsDataSource.createMaterial("hardware1", 3, "/storage/emulated/0/DCIM/Camera/hardware1.jpg", testString);
        insertStatus = materialsDataSource.createMaterial("hardware2", 3, "/storage/emulated/0/DCIM/Camera/hardware2.jpg", testString);
        insertStatus = materialsDataSource.createMaterial("hardware3", 3, "/storage/emulated/0/DCIM/Camera/hardware3.jpg", testString);

        insertStatus = materialsDataSource.createMaterial("flooring1", 4, "/storage/emulated/0/DCIM/Camera/flooring1.jpg", testString);
        insertStatus = materialsDataSource.createMaterial("flooring2", 4, "/storage/emulated/0/DCIM/Camera/flooring2.jpg", testString);
        insertStatus = materialsDataSource.createMaterial("flooring3", 4, "/storage/emulated/0/DCIM/Camera/flooring3.jpg", testString);
    }

    @Override
    protected void onResume(){
        super.onResume();
        materialsDataSource.open();
    }

    @Override
    protected void onPause(){
        super.onPause();
        materialsDataSource.close();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case ACTIVITY_SELECT_PATTERN:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    Log.w("TEST", "TEST: " + filePath);
                    //cursor.close();

                    // decode the pattern & add color tile to the view
                    //Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    //yourSelectedImage = Bitmap.createScaledBitmap(yourSelectedImage, 500, 500, false);
                    //drawTest.addNewColorTile(yourSelectedImage);
                }
        }
    }

    public static void updateTileImageList(int type, ArrayList<String> requestedCharacteristic){
        // remove the material list
        TileImage.imagePath.clear();

        // add the material list
        materialsDataSource.updateMaterialFilepath(type, requestedCharacteristic);

        // Tell the image adapter the the data set has been updated
        imageGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());

        // update the image list
        updateTileImageList(tab.getPosition(), null);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
