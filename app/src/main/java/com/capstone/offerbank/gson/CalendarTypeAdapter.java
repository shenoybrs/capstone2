package com.capstone.offerbank.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class CalendarTypeAdapter implements JsonDeserializer<Calendar> {

    private static final String FORMAT_OUT ;
    private static final String FORMAT_OUT_B ;
    private static final String FORMAT_SHORT ;

    static{
        FORMAT_OUT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        FORMAT_OUT_B = "yyyy-MM-dd'T'HH:mm:ss";
        FORMAT_SHORT = "M/dd/yyyy";
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat();

    @Override
    public Calendar deserialize(JsonElement json, Type type, JsonDeserializationContext ctx)
            throws JsonParseException {
        Calendar cal = null;

        sdf.applyPattern(FORMAT_OUT);
        try {
            Date d = sdf.parse(json.getAsString());
            cal = Calendar.getInstance();
            cal.setTime(d);
            return cal;
        } catch (ParseException e) {
            // eat it
        }
        sdf.applyPattern(FORMAT_OUT_B);
        try {
            Date d = sdf.parse(json.getAsString());
            cal = Calendar.getInstance();
            cal.setTime(d);
            return cal;
        } catch (ParseException e) {
            // eat it
        }

        sdf.applyPattern(FORMAT_SHORT);
        try {
            Date d = sdf.parse(json.getAsString());
            cal = Calendar.getInstance();
            cal.setTime(d);
            return cal;
        } catch (ParseException e) {
            // eat it
        }

        return cal;
    }

}
