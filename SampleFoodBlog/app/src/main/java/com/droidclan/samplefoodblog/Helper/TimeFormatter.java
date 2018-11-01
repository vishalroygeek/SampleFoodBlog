package com.droidclan.samplefoodblog.Helper;


import java.text.SimpleDateFormat;

public class TimeFormatter {

    String timeString = "";

    public String getTime(long time){

        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime-time;

        SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");

        if (timeDifference <= 600000){
            return "Just now";
        }else if (timeDifference <= 3.6e+6){
            timeString = sdfMinute.format(timeDifference);
            return timeString.replaceFirst("0","") + " minutes ago";
        }else if (timeDifference <= 8.64e+7){
            timeString = sdfHour.format(timeDifference);
            return timeString.replaceFirst("0","") + " hours ago";
        }else if (timeDifference <= 2.592e+9){
            timeString = sdfDay.format(timeDifference);
            return timeString.replaceFirst("0","") + " days ago";
        }else if (timeDifference > 2.592e+9){
            timeString = sdf.format(time);
            return timeString;
        }


        return "";
    }

}
