package serialize_deserialize;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request_result.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class Json_ConverterTest {

    private InputStream inputStream;
    Json_Converter converter;
    RegisterRequest hardCodedRequest;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        inputStream = new FileInputStream("test/testing_files/register.txt");
        converter = new Json_Converter();
        hardCodedRequest = new RegisterRequest("ashyke", "rynee",
                "jace_hamilton@fake_email.net", "Jace", "Hamilton", 'm');
    }

    @AfterEach
    void tearDown() throws IOException {
        if (inputStream != null) inputStream.close();
    }

    @Test
    void deserializeNullRegisterFail() {
        inputStream = null;
        assertThrows(NullPointerException.class, () -> Json_Converter.deserialize(inputStream, RegisterRequest.class));
    }

    @Test
    void serializeRegisterPass() {
        RegisterResult result = new RegisterResult(null, "associatedUsername", "personID", true);
        String reStr = Json_Converter.serialize(result, RegisterResult.class);
        assertEquals("{" +
                "\"username\":\"associatedUsername\"," +
                "\"personID\":\"personID\"," +
                "\"success\":true" +
                "}", reStr);
    }

}