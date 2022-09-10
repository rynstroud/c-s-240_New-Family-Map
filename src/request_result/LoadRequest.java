package request_result;

import model.Event;
import model.Person;
import model.User;

/**
 * holds the request to load the user's entire family history
 */
public class LoadRequest extends Base_Request {
    private User[] users;
    private Person[] persons;
    private Event[] events;

    public LoadRequest() {
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}
