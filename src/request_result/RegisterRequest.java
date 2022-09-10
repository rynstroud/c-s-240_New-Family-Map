package request_result;


/**
 * holds the client's request to register a new user
 */
public class RegisterRequest extends Base_Request {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private char gender;

    public RegisterRequest() {

    }

    public RegisterRequest(String u, String p, String e, String f, String l, char g) {
        this.username = u;
        this.password = p;
        this.email = e;
        this.firstName = f;
        this.lastName = l;
        this.gender = g;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof RegisterRequest) {
            RegisterRequest oRegisterRequest = (RegisterRequest) o;
            return (oRegisterRequest.getUsername().equals(getUsername()) &&
                    oRegisterRequest.getPassword().equals(getPassword()) &&
                    oRegisterRequest.getEmail().equals(getEmail()) &&
                    oRegisterRequest.getFirstName().equals(getFirstName()) &&
                    oRegisterRequest.getLastName().equals(getLastName()) &&
                    oRegisterRequest.getGender() == (getGender()));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
