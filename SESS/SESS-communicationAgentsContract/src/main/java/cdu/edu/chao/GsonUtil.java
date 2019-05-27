package cdu.edu.chao;

import com.google.gson.*;
import java.util.Base64;
import java.lang.reflect.Type;




public class GsonUtil {
    public static Gson gson = null;
    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private GsonUtil() {
    }



    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder().setLenient();

        builder.registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter()).create();
        Gson gson = builder.create();
        return gson;
    }

    public static Gson getPrettyGson() {
        GsonBuilder builder = new GsonBuilder().setLenient().setPrettyPrinting();
        builder.registerTypeHierarchyAdapter(byte[].class, new ByteArrayTypeAdapter()).create();
        Gson gson = builder.create();
        return gson;
    }


}



class ByteArrayTypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {

        String base64 = Base64.getEncoder().encodeToString(src);
        return new JsonPrimitive(base64);
    }

    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }
        try {
            byte[] base64 = Base64.getDecoder().decode(json.getAsString());
            return base64;
        } catch (Exception ex) {
        }
        return null;
    }


}