package ke.co.scedar.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTime {

    /**
     *
     * @return current UNIX timestamp
     * of type Long
     */
    public static long getCurrentUnixTimestamp(){
        return System.currentTimeMillis();
    }

    public static Timestamp getCurrentSqlTimestamp(){
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     *
     * @param date java.util.Date object
     * @return equivalent UNIX timestamp
     * of type Long
     */
    public static long getTimestamp(Date date){
        try{
            return date.getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     *
     * @param date of type String
     * @return equivalent UNIX timestamp
     * of type Long
     */
    public static long getTimestamp(String date){
        try{
            Date d = convertDateStringToDate(date);
            if(d != null){
                return d.getTime();
            }
            return 0L;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     *
     * @param format Desired date or date & time format
     *               Type String
     * @return current date
     * Type String
     */
    public static String getCurrentDate(String format){
        try{
            return new SimpleDateFormat(format)
                    .format(new java.sql.Date(
                            getCurrentUnixTimestamp()));

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     * Type java.util.Date
     * Current DateTime
     */
    public static Date getCurrentJavaUtilDateTime(){
        return new Date();
    }

    /**
     *
     * @return
     * Type java.util.Date
     * Current Date
     */
    public static Date getCurrentJavaUtilDate(){
        return new Date();
    }

    /**
     *
     * @return current date with system default format
     * Type String
     */
    public static String getCurrentDate(){
        return new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT)
                .format(new java.sql.Date(getCurrentUnixTimestamp()));
    }

    /**
     *
     * @return current date and time with system default format
     * Type String
     */
    public static String getCurrentDateTime(){
        return new SimpleDateFormat(Constants.DEFAULT_DATE_TIME_FORMAT)
                .format(new java.sql.Date(getCurrentUnixTimestamp()));
    }

    /**
     *
     * @param timestamp type String
     * @return equivalent date & time
     * Type String
     */
    public static String getDateFromTimestamp(String timestamp){
        try{
            return new SimpleDateFormat(Constants.DEFAULT_DATE_TIME_FORMAT)
                    .format(new java.sql.Date(Long.parseLong(timestamp)));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param timestamp type long
     * @return equivalent date & time
     * Type String
     */
    public static String getDateFromTimestamp(long timestamp){
        try{
            return new SimpleDateFormat(Constants.DEFAULT_DATE_TIME_FORMAT)
                    .format(new java.sql.Date(timestamp));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param date
     *      String value of Date to be converted
     *      Format = yyyy-M-dd HH:mm:ss (2017-10-25 18:02:25)
     * @return
     *      null, if exception occurs and java.util.Date Object if not
     */
    public static Date convertDateStringToDate(String date){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(Constants.DEFAULT_DATE_TIME_FORMAT);
        try{
            return simpleDateFormat.parse(date);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Date addDaysToNow(int days){
        Date now = convertDateStringToDate(getCurrentDateTime());
        Calendar cal = Calendar.getInstance();
        assert now != null;
        cal.setTime(now);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static Date convertDateStringToDate(String date, String format){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(format);
        try{
            return simpleDateFormat.parse(date);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public static java.sql.Date convertDateStringToSqlDate(String date, String format){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(format);
        try{
            return new java.sql.Date(simpleDateFormat.parse(date).getTime());
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public static java.sql.Time convertTimeStringToSqlTime(String time, String format){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(format);
        try{
            return new java.sql.Time(simpleDateFormat.parse(time).getTime());
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param date
     *      java.util.Date Object to convert to String
     * @return
     *     String value of Date
     *     Format = yyyy-M-dd HH:mm:ss (2017-10-25 18:02:25)
     */
    public static String convertDateToDateString(Date date){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(Constants.DEFAULT_DATE_TIME_FORMAT);
        try{
            return simpleDateFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String convertDateToDateString(Date date, String format){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(format);
        try{
            return simpleDateFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param startDate
     *      Starting Date
     * @param endDate
     *      Ending Date
     * @return
     *      Pretty and intelligent time difference
     *      Resolution = seconds for short time diffs
     */
    public static String getPrettyDateTimeDifference(Date startDate, Date endDate){

        String seconds = "second";
        String minutes = "minute";
        String hours = "hour";
        String days = "day";

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedSeconds>1) seconds = seconds+"s";
        if (elapsedMinutes>1) minutes = minutes+"s";
        if (elapsedHours>1) hours = hours+"s";
        if (elapsedDays>1) days = days+"s";

        if (elapsedDays<=0){
            if(elapsedHours<=0){
                if(elapsedMinutes<=0){
                    if(elapsedSeconds<=0){
                        return "Just now";
                    }else{
                        return String.format("%d "+seconds+"%n", elapsedSeconds);
                    }
                }else{
                    if(elapsedSeconds>0){
                        return String.format("%d "+minutes+", %d "+seconds+"%n",
                                elapsedMinutes, elapsedSeconds);
                    }else{
                        return String.format("%d "+minutes+"%n", elapsedMinutes);
                    }
                }
            }else{
                if(elapsedMinutes>0){
                    return String.format("%d "+hours+", %d "+minutes+"%n",
                            elapsedHours, elapsedMinutes);
                }else{
                    return String.format("%d "+hours+"%n", elapsedHours);
                }
            }
        }else{
            if(elapsedHours>0){
                return String.format("%d "+days+", %d "+hours+"%n",
                        elapsedDays, elapsedHours);
            }else{
                return String.format("%d "+days+"%n", elapsedDays);
            }
        }
    }

}
