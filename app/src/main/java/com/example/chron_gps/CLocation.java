package com.example.chron_gps;

import android.location.Location;

public class CLocation extends Location {

    private boolean bUseMetricUnits = false;

    public CLocation(Location locatioN){
        this(locatioN, true);
    }

    public CLocation(Location locatioN, boolean bUseMetricUnits){
        super(locatioN);
        this.bUseMetricUnits = bUseMetricUnits;
    }

    public boolean getUsMetricUnits(){
        return this.bUseMetricUnits;
    }

    public void setbUseMetricUnits(boolean bUseMetricUnits){
        this.bUseMetricUnits = bUseMetricUnits;
    }

    @Override
    public float distanceTo(Location dest) {
        float nDistance = super.distanceTo(dest);

        if (!this.getUsMetricUnits()){
            //Convert meters to feet
            nDistance = nDistance *3.28083989501312f;
        }
        return nDistance;
    }

    @Override
    public double getAltitude() {
        double nAltitude = super.getAltitude();
        if (!this.getUsMetricUnits()){
            //Convert meters to feet
            nAltitude = nAltitude *3.28083989501312d;
        }
        return nAltitude;
    }

    @Override
    public float getSpeed() {
        float nSpeed = super.getSpeed();
        if (!this.getUsMetricUnits()){
            //Convert meters/second to miles/hour
            nSpeed = nSpeed * 2.23693629f;
        }
        return nSpeed;
    }

    @Override
    public float getAccuracy() {
        float nAccuracy = super.getAccuracy();
        if (!this.getUsMetricUnits()){
            //Convert meters to feet
            nAccuracy = nAccuracy *3.28083989501312f;
        }
        return nAccuracy;
    }}
