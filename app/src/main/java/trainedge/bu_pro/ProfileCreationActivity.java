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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static trainedge.bu_pro.R.id.tv1;

public class ProfileCreationActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final int REQUEST_ADDRESS = 22;
    public static final String ADDRESS_EXTRAS = "trainedge.bu_pro.address_extra";
    private FloatingActionButton fabChoose;
    private Spinner spr_ringtone, spr_notification;
    public Map<String, String> list;

    private SeekBar seekbar1 = null;
    private AudioManager audioManager = null;
    private Switch switch_vibrate;
    private Switch switch_silent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_creation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabChoose = (FloatingActionButton) findViewById(R.id.fabChoose);
        spr_ringtone = (Spinner) findViewById(R.id.spr_ringtone);
        switch_vibrate = (Switch) findViewById(R.id.switch_vibrate);
        switch_silent = (Switch) findViewById(R.id.switch_silent);
        spr_notification = (Spinner) findViewById(R.id.spr_notification);
        seekbar1 = (SeekBar) findViewById(R.id.seekbar1);
        Map<String, String> myringtone = getRingtones();
        Map<String, String> mynotification = getNotifications();
        ArrayList<String> strings = new ArrayList<>(myringtone.keySet());
        ArrayList<String> strings1 = new ArrayList<>(mynotification.keySet());
        final ArrayAdapter<String> adapter_ringtone = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
        final ArrayAdapter<String> adapter_notification = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings1);
        fabChoose.setOnClickListener(this);
        adapter_ringtone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_notification.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spr_ringtone.setAdapter(adapter_ringtone);
        spr_notification.setAdapter(adapter_notification);
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
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//        initControls();
    }


    /*
    * private void initControls() {
          try
          {
              seekbar1 = (SeekBar)findViewById(R.id.seekbar1);
              audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
              seekbar1.setMax(audioManager
                      .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
              seekbar1.setProgress(audioManager
                      .getStreamVolume(AudioManager.STREAM_MUSIC));


              seekbar1.setOnSeekBarChangeListener(new OnseekBar1ChangeListener()
              {
                  @Override
                  public void onStopTrackingTouch(SeekBar arg0)
                  {
                  }

                  @Override
                  public void onStartTrackingTouch(SeekBar arg0)
                  {
                  }

                  @Override
                  public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                  {
                      audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                              progress, 0);
                  }
              });

      }

  */
    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, PlaceSelectionActivity.class);
        startActivityForResult(i, REQUEST_ADDRESS);
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
        Toast.makeText(this, "yyy", Toast.LENGTH_SHORT).show();
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
