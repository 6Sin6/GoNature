package Entities;

import java.io.Serializable;
import java.util.*;

/**
 * The ParkBank class represents a collection of Park objects.
 * It implements the Serializable and Iterable interfaces, allowing it to be serialized and iterated over.
 * The class also provides methods to insert a park, delete a park, check if a park exists, and get parks by department ID.
 */
public class ParkBank implements Serializable, Iterable<Park> {
    /**
     * An ArrayList of Park objects.
     */
    private ArrayList<Park> parks;

    /**
     * A static map of park names to their corresponding IDs.
     */
    private static final Map<String, String> ParkMap;

    /**
     * Static initializer block to populate the ParkMap.
     */
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("Tel-Aviv Central Park", "1");
        aMap.put("Be'er Sheva City Park", "2");
        aMap.put("Haifa Downtown Park", "3");
        aMap.put("Karmi'el Zoo Park", "4");
        // Additional entries can be added here
        ParkMap = Collections.unmodifiableMap(aMap);
    }

    /**
     * Returns the park name for a given park ID.
     *
     * @param value The park ID.
     * @return The park name, or null if the ID is not found.
     */
    public static String getParkNameByID(String value) {
        for (Map.Entry<String, String> entry : ParkMap.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        // If the value is not found, return null or throw an exception as needed
        return null;
    }

    /**
     * Returns an unmodifiable view of the ParkMap.
     *
     * @return An unmodifiable map of park names to their corresponding IDs.
     */
    public static Map<String, String> getUnmodifiableMap() {
        return ParkMap;
    }

    /**
     * Default constructor that initializes the parks ArrayList.
     */
    public ParkBank() {
        parks = new ArrayList<>();
    }

    /**
     * Constructor that initializes the parks ArrayList and adds a park to it.
     *
     * @param park The park to add.
     */
    public ParkBank(Park park) {
        parks = new ArrayList<>();
        parks.add(park);
    }

    /**
     * Inserts a park into the parks ArrayList.
     *
     * @param park The park to insert.
     * @return True if the park was added, false otherwise.
     */
    public Boolean insertPark(Park park) {
        return parks.add(park);
    }

    /**
     * Deletes a park from the parks ArrayList.
     *
     * @param park The park to delete.
     */
    public void deletePark(Park park) {
        if (parks.contains(park))
            parks.remove(park);
    }

    /**
     * Checks if a park exists in the parks ArrayList.
     *
     * @param park The park to check.
     * @return True if the park exists, false otherwise.
     */
    public Boolean isExists(Park park) {
        return parks.contains(park);
    }

    /**
     * Returns a ParkBank containing parks that belong to a specific department.
     *
     * @param departmentID The department ID.
     * @return A ParkBank containing parks that belong to the specified department.
     */
    public ParkBank GetParksByDepartmentID(Integer departmentID) {
        ParkBank parkBank = new ParkBank();
        for (Park park : parks) {
            if (park.getDepartment() == departmentID)
                parkBank.insertPark(park);
        }
        return parkBank;
    }

    /**
     * Returns an iterator over the parks ArrayList.
     *
     * @return An iterator over the parks ArrayList.
     */
    @Override
    public Iterator<Park> iterator() {
        return new ParkBank.ParkIterator();
    }

    /**
     * The ParkIterator class provides an iterator over the parks ArrayList.
     */
    private class ParkIterator implements Iterator<Park> {
        /**
         * The current index of the iterator.
         */
        private int currentIndex = 0;

        /**
         * Checks if there is a next element in the parks ArrayList.
         *
         * @return True if there is a next element, false otherwise.
         */
        @Override
        public boolean hasNext() {
            return currentIndex < parks.size();
        }

        /**
         * Returns the next element in the parks ArrayList.
         *
         * @return The next Park object.
         * @throws NoSuchElementException If there is no next element.
         */
        @Override
        public Park next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the ParkBank");
            }
            return parks.get(currentIndex++);
        }

    }
}
