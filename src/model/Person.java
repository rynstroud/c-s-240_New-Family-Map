package model;

import dao.DataAccessException;
import dao.Database;

import java.util.UUID;
import java.util.logging.Level;

/**
 * the class storing information about the person
 * table in the database. It is only in use when the dao's temporarily
 * store information from the database
 */
public class Person extends Base_Person {
    private String fatherID;
    private String motherID;
    private String spouseID;

    /**
     * @param personID the unique identifier of the person
     * @param associatedUsername the unique identifier of the user
     * @param firstName the supplied first name of the user
     * @param lastName the supplied last name of the user
     * @param gender the user's gender; can only be 'm' or 'f', sorry
     * @param father the person's father's identifier; could be null
     * @param mother the person's mother's identifier; could be null
     * @param spouse the person's spouse's identifier; could be null
     */
    public Person(String personID, String associatedUsername, String firstName, String lastName, char gender,
                  String father, String mother, String spouse) {
        super(associatedUsername, firstName, lastName, gender, personID);
        fatherID = father;
        motherID = mother;
        spouseID = spouse;
    }
    public Person() {}

    public String getFather() {
        return fatherID;
    }

    public void setFather(String id) {
        fatherID = id;
    }

    public String getMother() {
        return motherID;
    }

    public void setMother(String id) {
        motherID = id;
    }

    public String getSpouse() {
        return spouseID;
    }

    public void setSpouse(String id) {
        spouseID = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof Person) {
            Person oPerson = (Person) o;
            return super.equals(oPerson) &&
                    oPerson.getFather().equals(getFather()) &&
                    oPerson.getMother().equals(getMother()) &&
                    oPerson.getSpouse().equals(getSpouse());
        } else {
            return false;
        }
    }

}
