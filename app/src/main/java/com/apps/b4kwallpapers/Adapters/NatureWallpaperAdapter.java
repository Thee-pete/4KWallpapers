package com.apps.b4kwallpapers.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.apps.b4kwallpapers.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.apps.b4kwallpapers.BuildConfig;
import com.apps.b4kwallpapers.R;
import com.apps.b4kwallpapers.UploadPages.NatureUpload;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class NatureWallpaperAdapter extends RecyclerView.Adapter<NatureWallpaperAdapter.ImageViewHolder> {
    private Context mContext;
    private List<NatureUpload> mUploads;
    private Bitmap bitmapImage;

    public NatureWallpaperAdapter (Context context, List<NatureUpload> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @NonNull
    @Override
    public NatureWallpaperAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.nature_recycler, parent, false);
        return new NatureWallpaperAdapter.ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NatureWallpaperAdapter.ImageViewHolder holder, int position) {
        NatureUpload uploadCurrent = mUploads.get(position);
        // holder.textViewName.setText(uploadCurrent.getName());
        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.drawable.placeholder4k)
                .fit()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // public TextView textViewName;
        public ImageView imageView;

        ImageButton buttonShare;
        ImageButton buttonDownload;
        ImageButton buttonSetAs;
        ImageButton buttonUp;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            //textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_nature);

            buttonShare = itemView.findViewById(R.id.naturebuttonShare);
            buttonDownload = itemView.findViewById(R.id.naturebuttonSave);
            buttonSetAs = itemView.findViewById(R.id.naturebuttonSet);
            buttonUp = itemView.findViewById(R.id.naturebuttonup);

            buttonShare.setOnClickListener(this);
            buttonDownload.setOnClickListener(this);
            buttonSetAs.setOnClickListener(this);
            buttonUp.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.naturebuttonShare:
                    shareWallpaper(mUploads.get(getAdapterPosition()));
                    break;

                case R.id.naturebuttonSave:
                     downloadWallpaper(mUploads.get(getAdapterPosition()));
                    break;

                case R.id.naturebuttonSet:
                     setWallpaper(mUploads.get(getAdapterPosition()));
                    break;
                case R.id.naturebuttonup:
                    natureUpOnclick(view);
                    break;

            }

        }
    }
    private void natureUpOnclick(View view) {
        Intent intent = new Intent(mContext, MainActivity.class );
        mContext.startActivity(intent);
        ((Activity) mContext).finish();


    }
    private void shareWallpaper(NatureUpload uploadCurrent) {

        //((Activity) mContext).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

        Glide.with(mContext)
                .asBitmap()
                .load(uploadCurrent.getImageUrl())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        // ((Activity) mContext).findViewById(R.id.progressbar).setVisibility(View.GONE);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/");
                        intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));

                        mContext.startActivity(Intent.createChooser(intent, "4K Wallpapers"));
                    }
                });
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;

        try {
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "4kwallpaper" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream((file));
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void downloadWallpaper(final NatureUpload wallpaper){

        //((Activity) mContext).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

        Glide.with(mContext)
                .asBitmap()
                .load(wallpaper.getImageUrl())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // ((Activity) mContext).findViewById(R.id.progressbar).setVisibility(View.GONE);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = saveWallpaperAndGetUri(resource,wallpaper.id);

                        if(uri!= null){
                            intent.setDataAndType(uri, "/image/*");
                            mContext.startActivity(Intent.createChooser(intent, "4K WALLPAPERS"));
                        }

                    }
                });

    }
    private Uri saveWallpaperAndGetUri(Bitmap bitmap, String id){
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat
                    .shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                intent.setData(uri);
                mContext.startActivity(intent);

            }else {
                ActivityCompat.requestPermissions((Activity) mContext,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            }
            return  null;
        }

        File folder = new File(Environment.getExternalStorageDirectory().toString()+"/Wallpapers_HD");
        folder.mkdirs();

        File file = new File(folder, id + ".jpg");

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.flush();
            out.close();

            return FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID +".provider", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    //change Upload to Abstract upload or whatever
    private void setWallpaper(NatureUpload wallset)
    {

        Glide.with(mContext)
                .asBitmap()
                .load(wallset.getImageUrl())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmapImage = resource;

                    }
                });
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
            wallpaperManager.setBitmap(bitmapImage);
            Toast.makeText(mContext, "Wallpaper set successfully",Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Wallpaper not set. Try again",Toast.LENGTH_SHORT).show();
        }




    }



}
