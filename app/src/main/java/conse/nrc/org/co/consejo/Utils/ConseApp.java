package conse.nrc.org.co.consejo.Utils;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import conse.nrc.org.co.consejo.Activities.MainActivity;
import conse.nrc.org.co.consejo.R;
import io.fabric.sdk.android.Fabric;

/**
 * Created by apple on 11/18/17.
 */

public class ConseApp extends Application {

    public static Models.ApplicationConfiguration appConfiguration;
    public static Models.RegisterUserResponse user;

    public static MainActivity mainActivity;

    private com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    DisplayImageOptions options;

    @Override
    public void onCreate(){

        imageLoader = imageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)
                .showImageOnFail(null)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        Fabric.with(this, new Crashlytics());

    }

    public static void setAppConfiguration(Context ctx, Models.ApplicationConfiguration conf){
        UtilsFunctions.saveSharedObject(ctx, LocalConstants.APP_CONFIGURATION, conf);
        appConfiguration = conf;
    }

    public static void setActualUser(Context ctx, Models.RegisterUserResponse userResponse){
        UtilsFunctions.saveSharedObject(ctx, LocalConstants.USER_DATA, userResponse);
        user = userResponse;
    }

    public static void setMainActivity(MainActivity mainActivityToSet){
        mainActivity = mainActivityToSet;
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }

    public static Models.RegisterUserResponse getActualUser(Context ctx){
        user = UtilsFunctions.getSavedObjectFromPreference(ctx, LocalConstants.USER_DATA, Models.RegisterUserResponse.class);
        return user;
    }

    public static Models.ApplicationConfiguration getAppConfiguration(Context ctx){
        appConfiguration = UtilsFunctions.getSavedObjectFromPreference(ctx, LocalConstants.APP_CONFIGURATION, Models.ApplicationConfiguration.class);
        return appConfiguration;
    }

    public static List<Models.UserAvatar> getUserAvatarList(Context ctx){
        List<Models.UserAvatar> userAvatarsList = new ArrayList<>();

        for(int i : LocalConstants.AVATAR_BODY_PARTS_ORDER){
            try {
                userAvatarsList.add(new Models.UserAvatar(user.user.id,
                        UtilsFunctions.getSharedInteger(ctx, LocalConstants.AVATAR_SELECTED_PART_ + String.valueOf(i))));
            } catch (Exception ea){
                userAvatarsList.add(null);
                ea.printStackTrace();
            }
        }

        return userAvatarsList;

    }

    public static FrameLayout getAvatarFrame(Context ctx, ImageLoader imgLdr, DisplayImageOptions options){


        List<Models.UserAvatar> userAvatars = getUserAvatarList(ctx);
        final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View avatarLayer = inflater.inflate(R.layout.avatar_layout, null, false);
        FrameLayout frameLayout = (FrameLayout) avatarLayer.findViewById(R.id.fl_avatar);
        int i = 0;
        for (Models.UserAvatar avatar: userAvatars){
            if (avatar != null && avatar.avatar_piece != 0) {
                Models.AvatarPiece avatarPiece = ConseApp.appConfiguration.getAvatarPieceById(avatar.avatar_piece);
                Log.d("Avatar", "Avatar id: " + avatar.avatar_piece);
                imgLdr.displayImage(ConseApp.appConfiguration.getAvatarPieceById(avatar.avatar_piece).icon,
                        (ImageView) frameLayout.findViewWithTag(String.valueOf(avatarPiece.body_part)), options);
            }
            i++;
        }

        return frameLayout;
    }

    public static void clearAvatar(Context ctx){

        for(int i = 0; i<=15; i++){
            UtilsFunctions.deleteKeySharedPreferences(ctx, LocalConstants.AVATAR_SELECTED_PART_ + String.valueOf(i));
            Log.d("Clear avatar", "Clear id: " + LocalConstants.AVATAR_SELECTED_PART_ + String.valueOf(i));
        }

    }

    public static int getAvatarGender(Context ctx){
        return UtilsFunctions.getSharedInteger(ctx, LocalConstants.AVATAR_GENDER_ID_);
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean isLocationEnabled(Context ctx) {
        return ((LocationManager)
                ctx.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                ((LocationManager)
                        ctx.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static boolean validateGpsStatus(boolean show_alert, Context ctx) {
        if (isLocationEnabled(ctx)){
            return true;
        } else if (show_alert){
            showGpsAlert(ctx);
        }
        return false;
    }


    public static boolean validateNetworkStatus(boolean show_alert, Context ctx) {
        if (isNetworkAvailable(ctx)){
            return true;
        } else if (show_alert){
            showNetworkConnectionAlert(ctx);
        }
        return false;
    }


    public static void showGpsAlert(Context ctx) {
        final Context mctx = ctx;
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(mctx);
        dialog.setTitle(ctx.getString(R.string.inactive_geolocation))
                .setMessage(ctx.getString(R.string.conse_need_gps))
                .setPositiveButton(ctx.getString(R.string.config), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mctx.startActivity(myIntent);
                    }
                })
                .setNegativeButton(ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public static void showNetworkConnectionAlert(Context ctx) {
        final Context mctx = ctx;
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(mctx);
        dialog.setTitle(ctx.getString(R.string.not_network_conection))
                .setMessage(ctx.getString(R.string.conse_need_network))
                .setPositiveButton(ctx.getString(R.string.config), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                        mctx.startActivity(myIntent);
                    }
                })
                .setNegativeButton(ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

}
