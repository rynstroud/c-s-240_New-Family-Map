package serialize_deserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import request_result.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class responsible for conversion between json and objects
 */
public final class Json_Converter {

    private static Logger logger;

    public Json_Converter() {
        logger = Logger.getLogger("familymapserver");
    }

    /**
     * converts a string to an output stream
     * @param str string to be converted
     * @param outputStream outputstream to which this string is added
     * @return the outputstream with string converted
     * @throws IOException
     */
    public OutputStream convertStringToOutputStream(String str, OutputStream outputStream) throws IOException {
        logger.log(Level.FINEST, "Converting json string to OutputStream");
        outputStream.write(str.getBytes(StandardCharsets.UTF_8));
        return outputStream;
    }

    /**
     * converts an inputstream to an object of the provided return type
     * @param reqBody the inputstream containing the object
     * @param returnType the type to which the inputstream is to be converted
     * @param <T>
     * @return the object created through conversion
     * @throws IOException
     */
    public static <T> T deserialize(InputStream reqBody, Class<T> returnType) throws IOException {
        logger.log(Level.FINEST, "Deserializing " + returnType.getName());
        String jsonStr = convertInputStreamToStr(reqBody);
        Gson gson = new Gson();
        logger.log(Level.FINEST, "Converting string to " + returnType.getName() + " object");
        return gson.fromJson(jsonStr, returnType);
    }

    /**
     * converts an inputstream to a string
     * @param inputStream the inputstream which is to be converted
     * @return the string converted from the inputstream
     */
    public static String convertInputStreamToStr(InputStream inputStream) throws IOException {
        logger.log(Level.FINEST, "Converting input stream to string");
        String inputStreamString = new Scanner(inputStream,"UTF-8").useDelimiter("\\A").next();
        inputStream.close();
        return inputStreamString;
    }

    /**
     * converts an object to a json string
     * @param r the object to be converted
     * @param returnType the type of object that is being converted
     * @param <T>
     * @return the json string created from the object conversion
     */
    public static <T> String serialize(Base_Result r, Class<T> returnType) {
        logger.log(Level.FINEST, "Serializing " + returnType.getName());
        Gson gson = new Gson();
        return gson.toJson(r);
    }
}
