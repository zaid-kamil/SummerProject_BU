package trainedge.bu_pro;


import android.content.Context;
import android.media.AudioManager;
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

        }

    }

    public void setToDefault(String key) {

    }
}
