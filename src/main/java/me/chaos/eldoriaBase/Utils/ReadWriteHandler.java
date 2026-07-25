package me.chaos.eldoriaBase.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;

public class ReadWriteHandler {
    public static void Write(JsonElement data, String Path){
        WriteJson (Path + ".json",data);
    }

    public static JsonElement Read(String Path){
        return ReadJson (Path + ".json");
    }

    private static void WriteJson(String filePath, JsonElement data){
        try (FileWriter writer = new FileWriter (filePath);
             BufferedWriter bufferedWriter = new BufferedWriter (writer) ){

            Gson gson = new GsonBuilder ().setPrettyPrinting ().create ();
            gson.toJson (data, bufferedWriter);

        } catch (IOException e) {
            throw new RuntimeException (e);
        }
    }

    private static JsonElement ReadJson(String filePath)  {
        File file = new File (filePath);


        try (FileReader reader = new FileReader (filePath);
             BufferedReader bufferedReader = new BufferedReader (reader)){
            if (!(file.exists ())) throw new IOException ("File nicht gefunden");

            return JsonParser.parseReader (bufferedReader);
    } catch (IOException e) {
            throw new RuntimeException (e);
        }
    }
}
