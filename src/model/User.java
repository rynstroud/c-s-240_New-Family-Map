package model;

/**
 * the class storing information about the user
 * table in the database. It is only in use when the dao's temporarily
 * store information from the database
 */
public class User extends Base_Person {
    private String password;
    private String email;


    /**
     *
     * @param username the unique identifier of the user
     * @param firstName the supplied first name of the user
     * @param lastName the supplied last name of the user
     * @param gender the supplied gender of the user
     * @param personID the unique identifier of the person
     * @param password the supplied password of the user
     * @param email the supplied email of the user
     */
    public User(String username, String password, String email, String firstName,
                String lastName, char gender, String personID) {
        super(username, firstName, lastName, gender, personID);
        this.password = password;
        this.email = email;
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

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof User) {
            User oUser = (User) o;
            return super.equals(oUser) &&
                    oUser.getPassword().equals(getPassword()) &&
                    oUser.getEmail().equals(getEmail());
        } else {
            return false;
        }
    }
}
