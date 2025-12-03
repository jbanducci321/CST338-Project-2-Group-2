package com.example.TriviaBattler.database.typeConverters;

import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class StringListTypeConverter {

    /**
     * from list
     * @param list a list
     * @return [] or to string
     */
    @TypeConverter
    public String fromList(List<String> list) {
        if (list == null) {
            return "[]";
        }
        JSONArray arr = new JSONArray();
        for (String str : list) { //Adds in the contents of the list to the json array
            arr.put(str);
        }
        return arr.toString();
    }

    /**
     * to list
     * @param json file
     * @return list
     */
    @TypeConverter
    public List<String> toList(String json) {
        List<String> list = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return list;
        }
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); ++i) {
                list.add(arr.optString(i));
            }
        }
        catch (JSONException ignored) {}
        return list;
    }

}
