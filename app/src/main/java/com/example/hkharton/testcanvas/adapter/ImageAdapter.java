package com.example.hkharton.testcanvas.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.hkharton.testcanvas.R;
import com.example.hkharton.testcanvas.provider.TileImage;

import java.lang.ref.WeakReference;

public class ImageAdapter extends BaseAdapter {
    private int thumbnail_size_dp = 0;
    private Context mContext;

    // Constructor
    public ImageAdapter(Context context) {
        super();
        mContext = context;
    }

    @Override
    public int getCount() {
        // TODO - please check
        return TileImage.imagePath.size();
        // return 10000;
    }

    @Override
    public Object getItem(int position) {
        // TODO - please check
        return TileImage.imagePath.get(position);
        // return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO - please check
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // set the custom layout
        View itemView = inflater.inflate(R.layout.item, null);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.grid_item_image);

        // display the bitmap
        ImageWorkerTask imageTask = new ImageWorkerTask(imageView);
        imageTask.execute(TileImage.imagePath.get(position));
        // BitmapFactory.decodeFile("/storage/emulated/0/S Note Export/Idea note_20140920_232308_01(1).jpg", bitmapOptions);
        // imageView.setImageBitmap(yourSelectedImage);

        return itemView;

        // set images source
        /*
        ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
        Bitmap yourSelectedImage = BitmapFactory.decodeFile("/storage/emulated/0/S Note Export/Idea note_20140920_232308_01(1).jpg");
        yourSelectedImage = Bitmap.createScaledBitmap(yourSelectedImage, 500, 500, false);
        imageView.setImageBitmap(yourSelectedImage);
        */

        //imageView.setImageResource(mThumbIds[position]);

        /*
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mThumbIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(70, 70));
        return imageView;
        */
    }

    class ImageWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String filePath = "";

        public ImageWorkerTask(ImageView imageView){
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // Decode image in background
            filePath = params[0];

            // this is the technique to retrieve the image dimension before allocating memory for it
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, bitmapOptions);

            // get the thumbnail size in pixel
            int thumbnailSizePixel = dipToPixels(mContext, thumbnail_size_dp);

            // Calculate the scaling down factor of the image decoder
            bitmapOptions.inSampleSize = calculateInSampleSize(bitmapOptions, thumbnailSizePixel, thumbnailSizePixel);
            bitmapOptions.inJustDecodeBounds = false;

            // load the scaled down image to the memory
            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath, bitmapOptions);
            return yourSelectedImage;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            // after decode is finished, see if the ImageView still around and set bitmap
            if(imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if(imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public void setThumbnailSize(int thumbnailSize) {
        if (thumbnailSize == thumbnail_size_dp) {
            return;
        }

        // set the thumbnailSize
        thumbnail_size_dp = thumbnailSize;
    }

    public static int dipToPixels(Context context, int dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics));
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}