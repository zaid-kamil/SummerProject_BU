package trainedge.bu_pro;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FetchAddressService extends IntentService {

    private ResultReceiver mreceiver;

    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME =
                "trainedge.bu_pro";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
    }

    private Geocoder geocoder;

    public FetchAddressService() {
        super("AddressFetchingService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String errorMessage = "";
        mreceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if(mreceiver == null){
           // Toast.makeText(this, "No Receiver Registered", Toast.LENGTH_SHORT).show();
        }

        // Get the location passed to this service through an extra.
        LatLng location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            //Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            //Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
              //  Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }

    private void deliverResultToReceiver(int result, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY,message);
        mreceiver.send(result,bundle);
    }
}
