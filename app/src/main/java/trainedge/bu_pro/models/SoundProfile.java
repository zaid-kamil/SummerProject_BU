package trainedge.bu_pro.models;

/**
 * Created by CISE on 03/06/2017.
 */

public class

SoundProfile {
    private String address, ringtone, notification, profile;
    private double lat, lng;
    private boolean isSilent, isVibrate, isActive;
    private int volume;

    public SoundProfile() {

    }

    public SoundProfile(String address, String ringtone, String notification, String profile, double lat, double lng, boolean isSilent, boolean isVibrate, boolean isActive, int volume) {
        this.address = address;
        this.ringtone = ringtone;
        this.notification = notification;
        this.profile = profile;
        this.lat = lat;
        this.lng = lng;
        this.isSilent = isSilent;
        this.isVibrate = isVibrate;
        this.isActive = isActive;
        this.volume = volume;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isSilent() {
        return isSilent;
    }

    public void setSilent(boolean silent) {
        isSilent = silent;
    }

    public boolean isVibrate() {
        return isVibrate;
    }

    public void setVibrate(boolean vibrate) {
        isVibrate = vibrate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
