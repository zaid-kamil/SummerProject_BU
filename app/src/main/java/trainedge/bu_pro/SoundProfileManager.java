package trainedge.bu_pro;


import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import trainedge.bu_pro.models.SoundProfile;


class SoundProfileManager {

    private int count;
    Context context;

    private SoundProfile soundProfile;

    public SoundProfileManager(Context context) {
        count = 0;
        this.context = context;

    }

    public void changeSoundProfile(final Context context, final String key, GeoLocation location) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference profilesRef = FirebaseDatabase.getInstance().getReference("soundprofiles").child(uid);
        profilesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equalsIgnoreCase("geofire")) {
                            continue;
                        }
                        SoundProfile profile = snapshot.getValue(SoundProfile.class);
                        if (profile.getProfile().equalsIgnoreCase(key)) {
                            if (!profile.isActive()) {
                                snapshot.child("active").getRef().setValue(true);
                                updateSoundProfile(profile);
                            }
                        }
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null)
                    Toast.makeText(SoundProfileManager.this.context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateSoundProfile(SoundProfile soundProfile) {
        boolean active = soundProfile.isActive();
        boolean isSilent = soundProfile.isSilent();
        boolean isVibrate = soundProfile.isVibrate();
        String name = soundProfile.getProfile();
        final AudioManager profileMode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (isSilent) {
            profileMode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else if (isVibrate) {
            profileMode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        } else {
            int volume = soundProfile.getVolume();
            String msgtone = soundProfile.getRingtone();
            String ringtone = soundProfile.getRingtone();
            profileMode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(
                        context,
                        RingtoneManager.TYPE_RINGTONE,
                        Uri.parse(ringtone)
                );
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, Uri.parse(msgtone));
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            profileMode.setStreamVolume(AudioManager.STREAM_RING, volume, 0);

        }
        ProfileNotification.notify(context, "Sound Profile loaded ->" + name);


    }

    public void setToDefault(String key) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("soundprofiles").child(uid).child(key);
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    dataSnapshot.getRef().child("state").setValue(false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(SoundProfileManager.this.context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}
