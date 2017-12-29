package conse.nrc.org.co.consejo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import conse.nrc.org.co.consejo.Fragments.AlertAndCallFragment;
import conse.nrc.org.co.consejo.Fragments.SelectAvatarPiecesFragment;
import conse.nrc.org.co.consejo.Utils.ConseApp;
import conse.nrc.org.co.consejo.Utils.LocalConstants;
import conse.nrc.org.co.consejo.Utils.Models;
import conse.nrc.org.co.consejo.Utils.RequestTask;
import conse.nrc.org.co.consejo.Utils.ServerRequest;
import conse.nrc.org.co.consejo.R;
import conse.nrc.org.co.consejo.Utils.UtilsFunctions;

import static conse.nrc.org.co.consejo.Utils.ErrorCodes.VOLLEY_UPLOAD_FAIL;

public class Welcome extends AppCompatActivity implements View.OnClickListener, RequestTask.OnRequestCompleted{

    private Button mBtGoToVideo, mBtRegister, mBtSendAlert;
    TextView mTvToLoggin;
    ProgressDialog listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mBtGoToVideo = (Button)findViewById(R.id.bt_go_to_video);
        mBtGoToVideo.setOnClickListener(this);
        mTvToLoggin = (TextView)findViewById(R.id.tv_loggin);
        mTvToLoggin.setOnClickListener(this);
        mBtRegister = (Button) findViewById(R.id.bt_register);
        mBtRegister.setOnClickListener(this);
        mBtSendAlert = (Button) findViewById(R.id.bt_send_alert);
        mBtSendAlert.setOnClickListener(this);
        listener = new ProgressDialog(this);
        listener.setMessage(getString(R.string.getting_app_conf));

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        if (!UtilsFunctions.getSharedBoolean(this, LocalConstants.USER_IS_IN_DEVICE)){
            mBtSendAlert.setVisibility(View.GONE);
        } else {
            mBtGoToVideo.setVisibility(View.GONE);
        }

        new ServerRequest.GetAppConfiguration(this,listener, LocalConstants.GET_APP_CONF_TASK_ID).executeGet();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_go_to_video:
                goToVideo();
                break;
            case R.id.tv_loggin:
                goToLoggin();
                break;
            case R.id.bt_register:
                goToRegister();
                break;
            case R.id.bt_send_alert:
                sendAlert();
                break;
            default:
                break;
        }

    }

    private void sendAlert() {
        AlertAndCallFragment alertAndCallFragment = new AlertAndCallFragment();
        alertAndCallFragment.show(getSupportFragmentManager(), "tag");
    }

    private void goToVideo() {
        Intent tutorial = new Intent(this, VideoTutorial.class);
        startActivity(tutorial);
    }

    private void goToLoggin() {
        Intent loggin = new Intent(this, LogginActivity.class);
        startActivity(loggin);
    }

    private void goToRegister() {
        Intent register = new Intent(this, Register.class);
        startActivity(register);
    }

    @Override
    public void onRequestResponse(Object response, int taskId) {

        switch (taskId){
            case LocalConstants.GET_APP_CONF_TASK_ID:
                Models.ApplicationConfiguration appConf = (Models.ApplicationConfiguration) response;
                ConseApp.setAppConfiguration(this, appConf);
                if (UtilsFunctions.getSharedBoolean(this, LocalConstants.IS_USER_LOGGED_IN)){
                    if(ConseApp.getActualUser(this) != null){
                        goToNextactivity();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void goToNextactivity(){
        if (UtilsFunctions.getEmegencyContactsString(this).length() < 2){
            goToContact();
        } else if (UtilsFunctions.getSharedInteger(this, LocalConstants.AVATAR_SELECTED_PART_ + String.valueOf(1)) == 0){
            goToAvatar();
        } else{
            goToHome();
        }
    }

    private void goToAvatar() {
        Intent avatar = new Intent(this, Avatar.class);
        startActivity(avatar);
        this.finish();
    }

    private void goToContact() {
        Intent contact = new Intent(this, SelectContact.class);
        startActivity(contact);
        this.finish();
    }

    private void goToHome() {
        Intent tutorial = new Intent(this, MainActivity.class);
        startActivity(tutorial);
        this.finish();
    }

    @Override
    public void onRequestError(int errorCode, String errorMsg, int taskId) {

        switch (taskId){
            case LocalConstants.GET_APP_CONF_TASK_ID:
                if (ConseApp.getAppConfiguration(this) != null //Si hay configuraciòn,  usuario, està loggeado y el error es de red
                        && ConseApp.getActualUser(this) != null
                        && UtilsFunctions.getSharedBoolean(this, LocalConstants.IS_USER_LOGGED_IN)
                        && errorCode <= VOLLEY_UPLOAD_FAIL){
                    goToNextactivity();
                } else if(!UtilsFunctions.getSharedBoolean(this, LocalConstants.IS_USER_LOGGED_IN)
                        && ConseApp.getActualUser(this) != null
                        &&ConseApp.getAppConfiguration(this) != null
                        && UtilsFunctions.getSharedString(this, LocalConstants.USER_PSW) != null){
                    Toast.makeText(this, R.string.you_must_loggin_again, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }
}
