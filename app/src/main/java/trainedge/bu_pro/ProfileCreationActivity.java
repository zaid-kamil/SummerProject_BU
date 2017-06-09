package trainedge.bu_pro;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import trainedge.bu_pro.models.SoundProfile;


public class ProfileCreationActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final int REQUEST_ADDRESS = 22;
    public static final String ADDRESS_EXTRAS = "trainedge.bu_pro.address_extra";
    public static final String PROFILE = "soundprofiles";
    private FloatingActionButton fabChoose;
    private Spinner spr_ringtone, spr_notification;
    public Map<String, String> list;

    private SeekBar seekbar1;


    private AudioManager audioManager = null;
    private Switch switch_vibrate;
    private Switch switch_silent;
    private Toolbar toolbar;
    private Bundle extras;
    private TextView tv_address;
    private TextView tv_lng;
    private TextView tv_lat;
    private TextView tv_pname;
    private Button btnConfirm;
    private EditText et_pname;
    private Map<String, String> myringtone;
    private Map<String, String> mynotification;
    private View container;
    private String ringtone;
    private String notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        container = findViewById(R.id.f_container);
        container.setVisibility(View.GONE);

        et_pname = (EditText) findViewById(R.id.et_pname);
        tv_lat = (TextView) findViewById(R.id.tv_lat);
        tv_lng = (TextView) findViewById(R.id.tv_lng);
        tv_address = (TextView) findViewById(R.id.tv_address);
        fabChoose = (FloatingActionButton) findViewById(R.id.fabChoose);
        spr_ringtone = (Spinner) findViewById(R.id.spr_ringtone);
        switch_vibrate = (Switch) findViewById(R.id.switch_vibrate);
        switch_silent = (Switch) findViewById(R.id.switch_silent);
        spr_notification = (Spinner) findViewById(R.id.spr_notification);
        seekbar1 = (SeekBar) findViewById(R.id.seekbar1);
        myringtone = getRingtones();
        mynotification = getNotifications();
        ArrayList<String> strings = new ArrayList<>(myringtone.keySet());
        ArrayList<String> strings1 = new ArrayList<>(mynotification.keySet());
        final ArrayAdapter<String> adapter_ringtone = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
        final ArrayAdapter<String> adapter_notification = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings1);
        fabChoose.setOnClickListener(this);
        adapter_ringtone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_notification.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_ringtone.setAdapter(adapter_ringtone);
        spr_notification.setAdapter(adapter_notification);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);
        spr_ringtone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ProfileCreationActivity.this, "Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spr_notification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ProfileCreationActivity.this, "Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        switch_silent.setOnCheckedChangeListener(this);

//        initControls();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabChoose:
                Intent i = new Intent(this, PlaceSelectionActivity.class);
                startActivityForResult(i, REQUEST_ADDRESS);
                break;
            case R.id.btnConfirm:
                validateData();
                break;
            default:
                break;
        }
    }

    private void validateData() {

       /* ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Wait for a while");
        dialog.show();
        dialog.setCancelable(false);
*/
        //show  progess
        String address = tv_address.getText().toString().trim();
        if (address.isEmpty()) {
            tv_address.setError("First select a Place");
            return;
        }
        String lat = tv_lat.getText().toString().trim();
        if (lat.isEmpty()) {
            tv_lat.setError("First select a Place");
            return;
        }
        String lng = tv_lng.getText().toString().trim();

        if (et_pname == null) {
            et_pname.setError("First select a Place");
            return;
        }
        final String pname = et_pname.getText().toString().trim();

        if (lng.isEmpty()) {
            tv_lng.setError("First select a Place");
            return;
        }
        if (switch_silent.isChecked()) {
            ringtone = "";
            notification = "";
            disableOtherOption();
            saveProfile(address, lat, lng, pname, ringtone, notification, seekbar1.getProgress());

        } else {
            String key = spr_ringtone.getSelectedItem().toString();
            ringtone = myringtone.get(key);
            String key2 = spr_notification.getSelectedItem().toString();
            notification = mynotification.get(key2);

            saveProfile(address, lat, lng, pname, ringtone, notification, seekbar1.getProgress());
        }


    }

    private void saveProfile(String address, String lat, String lng, final String pname, String ringtone, String notification, int volume) {
        container.setVisibility(View.VISIBLE);

        final double latVal = Double.parseDouble(lat);
        final double lngVal = Double.parseDouble(lng);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference dbref = db.getReference(PROFILE);
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbref.child(uid).push().setValue(new SoundProfile(
                address, ringtone, notification, pname, latVal, lngVal, switch_silent.isChecked(), switch_vibrate.isChecked(), false, volume), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                container.setVisibility(View.GONE);
                //if(dialog.isShowing()){
                //  dialog.dismiss();
                //}
                //hide progrss bar
                if (databaseError == null) {
                    Toast.makeText(ProfileCreationActivity.this, "profile created", Toast.LENGTH_SHORT).show();
                    //setup geofire for geofencing
                    DatabaseReference geofireRef = dbref.child(uid).child("geofire");
                    GeoFire geoFire = new GeoFire(geofireRef);
                    geoFire.setLocation(pname, new GeoLocation(latVal, lngVal), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error == null) {
                                Toast.makeText(ProfileCreationActivity.this, "geofence created", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Toast.makeText(ProfileCreationActivity.this, "Geofence could not be created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(ProfileCreationActivity.this, "Failure " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADDRESS) {
                extras = data.getExtras();
                tv_address.setText(extras.getString(PlaceSelectionActivity.ADDRESS_EXTRA));
                tv_lat.setText(String.valueOf(extras.getDouble(PlaceSelectionActivity.LAT_EXTRA)));
                tv_lng.setText(String.valueOf(extras.getDouble(PlaceSelectionActivity.LNG_EXTRA)));
            }
        }
    }

    public Map<String, String> getRingtones() {
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        list = new HashMap<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);

            list.put(notificationTitle, notificationUri);
        }

        return list;
    }

    public Map<String, String> getNotifications() {
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();
        list = new HashMap<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);

            list.put(notificationTitle, notificationUri);
        }

        return list;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.switch_silent:
                if (isChecked) {
                    disableOtherOption();
                } else {
                    enableOtherOption();
                }
                break;
            case R.id.switch_vibrate:
                break;
        }
    }

    private void disableOtherOption() {
        spr_notification.setEnabled(false);
        spr_ringtone.setEnabled(false);
        seekbar1.setEnabled(false);
        switch_vibrate.setEnabled(false);
    }

    private void enableOtherOption() {
        spr_notification.setEnabled(true);
        spr_ringtone.setEnabled(true);
        seekbar1.setEnabled(true);
        switch_vibrate.setEnabled(true);
    }
}
