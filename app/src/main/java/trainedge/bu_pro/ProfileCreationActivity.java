package trainedge.bu_pro;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static trainedge.bu_pro.R.id.tv1;

public class ProfileCreationActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_ADDRESS = 22;
    public static final String ADDRESS_EXTRAS = "trainedge.bu_pro.address_extra";
    private FloatingActionButton fabChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        fabChoose = (FloatingActionButton) findViewById(R.id.fabChoose);
        fabChoose.setOnClickListener(this);
        String value1 = super.getIntent().getExtras().getString(ADDRESS_EXTRAS);
        //tv1.setText(value1);

    }


    @Override
    public void onClick(View v) {
        Intent i = new Intent(this,PlaceSelectionActivity.class);
        startActivityForResult(i, REQUEST_ADDRESS);
    }
}
