package model;

/**
 * stores info about all people and users. abstract.
 */
public class Base_Person {
    protected String associatedUsername;
    protected String firstName;
    protected String lastName;
    protected char gender;
    protected String personID;

    /**
     *
     * @param associatedUsername the chosen associatedUsername of the user
     * @param firstName the user's supplied first name
     * @param lastName the user's supplied last name
     * @param gender the user's gender; can only be 'm' or 'f', sorry
     * @param personID the unique identifier for the person's object
     */
    public Base_Person(String associatedUsername, String firstName, String lastName, char gender, String personID) {
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
    }

    public Base_Person(String associatedUsername, String firstName, String lastName, char gender) {
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        personID = "";
    }

    public Base_Person() {}

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
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

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof Base_Person) {
            Base_Person oPerson = (Base_Person) o;
            return oPerson.getPersonID().equals(getPersonID()) &&
                    oPerson.getAssociatedUsername().equals(getAssociatedUsername()) &&
                    oPerson.getFirstName().equals(getFirstName()) &&
                    oPerson.getLastName().equals(getLastName()) &&
                    oPerson.getGender() == (getGender());
        } else {
            return false;
        }
    }
}
