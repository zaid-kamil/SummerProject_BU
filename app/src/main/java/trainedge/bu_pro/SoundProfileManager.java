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

    private  int count;
    Context context;

    private SoundProfile soundProfile;

    public SoundProfileManager(Context context) {
        count=0;
        this.context = context;

    }

    public void changeSoundProfile(final Context context, final String key, GeoLocation location)
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference profilesRef = FirebaseDatabase.getInstance().getReference("soundprofiles").child(uid).child(key);
        profilesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    boolean state = dataSnapshot.child("active").getValue(Boolean.class);
                    if(!state){
                        ProfileNotification.notify(context,"sound profile updated");
                        dataSnapshot.getRef().child("active").setValue(true);
                        soundProfile = new SoundProfile(dataSnapshot);
                        updateSoundProfile(soundProfile);
                    }


                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SoundProfileManager.this.context,databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updateSoundProfile(SoundProfile soundProfile) {
        boolean active=soundProfile.getActive();
        boolean isSilent = soundProfile.isSilent();
        boolean isVibrate = soundProfile.isVibrate();
        String name = soundProfile.getKey();
        final AudioManager profileMode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if(isSilent){
            profileMode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
        else if (isVibrate){
            profileMode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }else{

        }

    }
}
