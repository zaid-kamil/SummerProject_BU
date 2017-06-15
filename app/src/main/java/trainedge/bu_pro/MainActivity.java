package trainedge.bu_pro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import trainedge.bu_pro.models.SoundProfile;

public class MainActivity extends AppCompatActivity implements ValueEventListener {

    private RecyclerView r_view;
    private ArrayList<SoundProfile> mylist;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        r_view = (RecyclerView) findViewById(R.id.r_view);
        r_view.setLayoutManager(new LinearLayoutManager(this));
        mylist = new ArrayList<>();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference dbRef = db.getReference("soundprofiles").child(uid);
        dbRef.addValueEventListener(this);
        adapter = new MyAdapter();
        r_view.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent i = new Intent(MainActivity.this, ProfileCreationActivity.class);
                startActivity(i);
            }
        });
        startService(new Intent(this,LocationService.class));
    }

    public static Intent createIntent(Context context, IdpResponse response) {
        return new Intent(context, MainActivity.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_menu:
                Intent i = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(i);
                // Do whatever you want to do on logout click.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChildren()) {

            mylist.clear();
            // step 8
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                if (snapshot.getKey() == "geofire") {
                    continue;
                }
                mylist.add(snapshot.getValue(SoundProfile.class));

                adapter.notifyDataSetChanged();

            }
        } else {

            Toast.makeText(this, "no data found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        if (databaseError != null) {
            Toast.makeText(this, "could not get data " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    class Holder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_loc;

        public Holder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_loc = (TextView) itemView.findViewById(R.id.tv_loc);

        }


    }

    class MyAdapter extends RecyclerView.Adapter<Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.simple_card_layout, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {

            SoundProfile s = mylist.get(position);
            holder.tv_name.setText(s.getProfile());
            holder.tv_loc.setText(s.getAddress());


        }

        @Override
        public int getItemCount() {
            return mylist.size();
        }
    }
}