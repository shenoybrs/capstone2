package com.capstone.pixscramble.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class GsonParser<T> {

    private final Type type;

    public GsonParser(Type type) {
        this.type = type;
    }

    public T parse(String json) {
        T obj = null;

        Gson gson = getGson();
        obj = gson.fromJson(json, type);

        return obj;
    }

    public T parse(InputStream is) {
        InputStreamReader isr = new InputStreamReader(is);
        JsonReader reader = new JsonReader(isr);

        Gson gson = getGson();
        T obj = gson.fromJson(reader, type);

        return obj;
    }

    public T parse(File file) {
        T obj = null;

        FileReader fr = null;
        JsonReader reader = null;
        try {
            fr = new FileReader(file);
            reader = new JsonReader(fr);
            reader.setLenient(true);

            Gson gson = getGson();
            obj = gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                }
            }
        }

        return obj;
    }

    private Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(GregorianCalendar.class, new CalendarTypeAdapter())
                .registerTypeAdapter(Calendar.class, new CalendarTypeAdapter())
                .create();
    }
}
