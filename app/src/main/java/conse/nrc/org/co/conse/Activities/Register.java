package conse.nrc.org.co.conse.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import Utils.ConseApp;
import Utils.LocalConstants;
import Utils.Models;
import Utils.RequestTask;
import Utils.ServerRequest;
import Utils.UtilsFunctions;
import conse.nrc.org.co.conse.R;

public class Register extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, RequestTask.OnRequestCompleted{


    EditText mEtBirthdate, mEtName, mEtLastname, mEtDocumentNumber, mEtEmail, mEtPassword;
    Spinner mSpGender, mSpDocumentType, mSpEthnicGroup, mSpGeographicLocation, mSpCondition, mSpOriginTown, mSpRole;
    CheckBox mCbIsNrcBeneficiary, mCbAcceptTermsConditions;
    Button mBtNext;
    ProgressDialog listener;
    List<String> gender_list, document_type_list, ethnic_group_list, geographic_location_list, condition_list, origin_town_list, role_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEtBirthdate = (EditText)findViewById(R.id.et_birthdate);
        mEtName = (EditText)findViewById(R.id.et_name);
        mEtLastname = (EditText)findViewById(R.id.et_last_name);
        mEtDocumentNumber = (EditText)findViewById(R.id.et_document_number);
        mEtEmail = (EditText)findViewById(R.id.et_email);
        mEtPassword = (EditText)findViewById(R.id.et_password);

        mSpGender = (Spinner)findViewById(R.id.sp_gender);
        mSpDocumentType = (Spinner)findViewById(R.id.sp_document_type);
        mSpEthnicGroup = (Spinner)findViewById(R.id.sp_ethnic_group);
        mSpGeographicLocation = (Spinner)findViewById(R.id.sp_geographic_location);
        mSpCondition = (Spinner)findViewById(R.id.sp_condition_type);
        mSpOriginTown = (Spinner)findViewById(R.id.sp_origin_town);
        mSpRole = (Spinner)findViewById(R.id.sp_user_profile);

        fillSpinnersData();

        fillSpinners();

        mCbAcceptTermsConditions = (CheckBox)findViewById(R.id.cb_accept_terms_conditions);
        mCbIsNrcBeneficiary = (CheckBox)findViewById(R.id.cb_is_nrc_beneficiary);

        mBtNext = (Button)findViewById(R.id.bt_register);

        mBtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        listener = new ProgressDialog(this);
        listener.setMessage(getString(R.string.registering_user));


        //Setting Calendar field
        setBirthdatePicker();

    }

    private void validateData() {

        boolean error = false;
        int gender=0, documentType=0, ethnicGroup=0, geographicLocation=0, condition=0, originTown=0, role=0;

        if(mEtBirthdate.getText().length() == 0){
            mEtBirthdate.setError(getString(R.string.must_fill_field));
            error = true;
        }
        if(mEtName.getText().length() == 0){
            mEtName.setError(getString(R.string.must_fill_field));
            error = true;
        }
        if(mEtLastname.getText().length() == 0){
            mEtLastname.setError(getString(R.string.must_fill_field));
            error = true;
        }
        if(mEtDocumentNumber.getText().length() == 0){
            mEtDocumentNumber.setError(getString(R.string.must_fill_field));
            error = true;
        }
        if(mEtEmail.getText().length() == 0){
            mEtEmail.setError(getString(R.string.must_fill_field));
            error = true;
        }
        if(mEtPassword.getText().length() == 0){
            mEtPassword.setError(getString(R.string.must_fill_field));
            error = true;
        }

        if (mSpGender.getSelectedItemPosition() > 0){
            gender = ConseApp.appConfiguration.gender_list.get(mSpGender.getSelectedItemPosition()-1).id;
        } else {
            ((TextView)((LinearLayout)(mSpGender.getChildAt(0))).getChildAt(0)).setError(getString(R.string.must_select_option));
            error = true;
        }

        if (mSpDocumentType.getSelectedItemPosition() > 0){
            documentType = ConseApp.appConfiguration.document_type_list.get(mSpDocumentType.getSelectedItemPosition()-1).id;
        } else {
            ((TextView)((LinearLayout)(mSpDocumentType.getChildAt(0))).getChildAt(0)).setError(getString(R.string.must_select_option));
            error = true;
        }

        if (mSpEthnicGroup.getSelectedItemPosition() > 0){
            ethnicGroup = ConseApp.appConfiguration.ethnic_group_list.get(mSpEthnicGroup.getSelectedItemPosition()-1).id;
        } else {
            ((TextView)((LinearLayout)(mSpEthnicGroup.getChildAt(0))).getChildAt(0)).setError(getString(R.string.must_select_option));
            error = true;
        }

        if (mSpGeographicLocation.getSelectedItemPosition() > 0){
            geographicLocation = ConseApp.appConfiguration.city_list.get(mSpGeographicLocation.getSelectedItemPosition()-1).id;
        } else {
            ((TextView)((LinearLayout)(mSpGeographicLocation.getChildAt(0))).getChildAt(0)).setError(getString(R.string.must_select_option));
            error = true;
        }

        if (mSpCondition.getSelectedItemPosition() > 0){
            condition = ConseApp.appConfiguration.condition_list.get(mSpCondition.getSelectedItemPosition()-1).id;
        } else {
            ((TextView)((LinearLayout)(mSpCondition.getChildAt(0))).getChildAt(0)).setError(getString(R.string.must_select_option));
            error = true;
        }

        if (mSpOriginTown.getSelectedItemPosition() > 0){
            originTown = ConseApp.appConfiguration.city_list.get(mSpOriginTown.getSelectedItemPosition()-1).id;
        } else {
            ((TextView)((LinearLayout)(mSpOriginTown.getChildAt(0))).getChildAt(0)).setError(getString(R.string.must_select_option));
            error = true;
        }

        if (mSpRole.getSelectedItemPosition() > 0){
            role = ConseApp.appConfiguration.role_list.get(mSpRole.getSelectedItemPosition()-1).id;
        } else {
            ((TextView)((LinearLayout)(mSpRole.getChildAt(0))).getChildAt(0)).setError(getString(R.string.must_select_option));
            error = true;
        }

        if(!mCbAcceptTermsConditions.isChecked()){
            mCbAcceptTermsConditions.setError(getString(R.string.you_must_accept_t_c));
            error = true;
        }

        if(!error){
            registerUser(gender,documentType,ethnicGroup,condition,role,geographicLocation,originTown);
        } else {
            Toast.makeText(this, getString(R.string.must_complete_information), Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(int gender, int documentType, int ethnicGroup,
                              int condition, int role, int geographicLocation, int originTown) {

        Models.RegisterUserProfileModel user = new Models.RegisterUserProfileModel();
        user.first_name = mEtName.getText().toString();
        user.last_name = mEtLastname.getText().toString();
        user.email = mEtEmail.getText().toString();
        user.password = mEtPassword.getText().toString();
        user.birthdate = mEtBirthdate.getText().toString();
        user.document_number = mEtDocumentNumber.getText().toString();
//        user.contact_phone = ;
//        user.address = ;
        user.role = role;
        user.gender = gender;
        user.ethnic_group = ethnicGroup;
        user.condition = condition;
        user.document_type = documentType;
        user.origin_city = originTown;
        user.actual_city = geographicLocation;
        user.isNRCBeneficiary = mCbIsNrcBeneficiary.isChecked();
//        user.latitude = ;
//        user.longitude = ;

        new ServerRequest.RegisterUser(this, listener, LocalConstants.REGISTER_USER_TASK_ID,user).executePost();

    }

    private void fillSpinnersData() {
        gender_list = new ArrayList<>();
        document_type_list = new ArrayList<>();
        ethnic_group_list = new ArrayList<>();
        geographic_location_list = new ArrayList<>();
        condition_list = new ArrayList<>();
        origin_town_list = new ArrayList<>();
        role_list = new ArrayList<>();

        gender_list.add(getString(R.string.select_hint));
        document_type_list.add(getString(R.string.select_hint));
        ethnic_group_list.add(getString(R.string.select_hint));
        geographic_location_list.add(getString(R.string.select_hint));
        condition_list.add(getString(R.string.select_hint));
        origin_town_list.add(getString(R.string.select_hint));
        role_list.add(getString(R.string.select_hint));

        if(ConseApp.appConfiguration.gender_list != null){
            for (Models.Gender gender : ConseApp.appConfiguration.gender_list){
                gender_list.add(gender.name);
            }
        }
        if(ConseApp.appConfiguration.document_type_list != null){
            for (Models.DocumentType documentType : ConseApp.appConfiguration.document_type_list){
                document_type_list.add(documentType.name);
            }
        }
        if(ConseApp.appConfiguration.ethnic_group_list != null){
            for (Models.EthnicGroup ethnicGroup : ConseApp.appConfiguration.ethnic_group_list){
                ethnic_group_list.add(ethnicGroup.name);
            }
        }
        if(ConseApp.appConfiguration.city_list != null){
            for (Models.City city: ConseApp.appConfiguration.city_list){
                geographic_location_list.add(city.name);
            }
        }
        if(ConseApp.appConfiguration.condition_list != null){
            for (Models.Condition condition: ConseApp.appConfiguration.condition_list){
                condition_list.add(condition.name);
            }
        }
        if(ConseApp.appConfiguration.city_list != null){
            for (Models.City city: ConseApp.appConfiguration.city_list){
                origin_town_list.add(city.name);
            }
        }
        if(ConseApp.appConfiguration.role_list!= null){
            for (Models.Role role: ConseApp.appConfiguration.role_list){
                role_list.add(role.name);
            }
        }

    }

    private void fillSpinners() {

        Log.d("Register", gender_list.size() + " - " + document_type_list.size());

        ArrayAdapter<String> spinerGenderArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_header_item, R.id.tv_direction, gender_list){

                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                };
        spinerGenderArrayAdapter.setDropDownViewResource(R.layout.spinner_direction_list_item);
        mSpGender.setAdapter(spinerGenderArrayAdapter);

        ArrayAdapter<String> spinerDocumentTypeArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_header_item, R.id.tv_direction, document_type_list){

                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                };
        spinerDocumentTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_direction_list_item);
        mSpDocumentType.setAdapter(spinerDocumentTypeArrayAdapter);

        ArrayAdapter<String> spinerEthnicGroupArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_header_item, R.id.tv_direction, ethnic_group_list){

                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                };
        spinerEthnicGroupArrayAdapter.setDropDownViewResource(R.layout.spinner_direction_list_item);
        mSpEthnicGroup.setAdapter(spinerEthnicGroupArrayAdapter);

        ArrayAdapter<String> spinerGeographicLocationArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_header_item, R.id.tv_direction, geographic_location_list){

                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                };
        spinerGeographicLocationArrayAdapter.setDropDownViewResource(R.layout.spinner_direction_list_item);
        mSpGeographicLocation.setAdapter(spinerGeographicLocationArrayAdapter);

        ArrayAdapter<String> spinerConditionArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_header_item, R.id.tv_direction, condition_list){

                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                };
        spinerConditionArrayAdapter.setDropDownViewResource(R.layout.spinner_direction_list_item);
        mSpCondition.setAdapter(spinerConditionArrayAdapter);

        ArrayAdapter<String> spinerOriginTownArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_header_item, R.id.tv_direction, origin_town_list){

                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                };
        spinerOriginTownArrayAdapter.setDropDownViewResource(R.layout.spinner_direction_list_item);
        mSpOriginTown.setAdapter(spinerOriginTownArrayAdapter);

        ArrayAdapter<String> spinerRoleArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.spinner_header_item, R.id.tv_direction, role_list){

                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                };
        spinerRoleArrayAdapter.setDropDownViewResource(R.layout.spinner_direction_list_item);
        mSpRole.setAdapter(spinerRoleArrayAdapter);

    }

    private void setBirthdatePicker(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        final DatePickerDialog picker = new DatePickerDialog(this, this, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        picker.getDatePicker().setMaxDate(System.currentTimeMillis());

        mEtBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int _birthYear = year;
        int _month = month + 1;
        int _day = dayOfMonth;
        mEtBirthdate.setText(_birthYear + "-" + _month +"-" + _day);
    }

    @Override
    public void onRequestResponse(Object response, int taskId) {

        switch (taskId){
            case LocalConstants.REGISTER_USER_TASK_ID:
                Models.RegisterUserResponse res = (Models.RegisterUserResponse) response;
                UtilsFunctions.saveSharedString(this, LocalConstants.USER_TOKEN, res.token);
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestError(int errorCode, String errorMsg, int taskId) {

        switch (taskId){
            case LocalConstants.REGISTER_USER_TASK_ID:
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }
}
