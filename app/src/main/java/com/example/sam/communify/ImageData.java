package com.example.sam.communify;

/**
 * Created by Sam on 2017-05-06.
 */
public class ImageData {
    double latitude;
    double longitude;
    String date;
    String desc;
    String dbKey;
    public ImageData(){

    }
    public ImageData(double latitude, double longitude, String date, String desc){
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.desc = desc;
    }

   public double getLatitude(){
       return latitude;
   }

   public double getLongitude(){
       return longitude;
   }
   public String getDate(){
       return date;
   }
   public String getDesc(){
       return desc;
   }
   public void changeDBKey(String dbKey){
       this.dbKey = dbKey;
   }
   public String myDbKey(){
       return dbKey;
   }

}
