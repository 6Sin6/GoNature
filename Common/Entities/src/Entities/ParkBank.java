package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ParkBank implements Serializable, Iterable<Park> {
    private ArrayList<Park> parks;

    public ParkBank() {
        parks = new ArrayList<>();
    }

    public ParkBank(Park park) {
        parks = new ArrayList<>();
        parks.add(park);
    }

    public Boolean insertPark(Park park) {
        return parks.add(park);
    }


    public void deletePark(Park park) {
        if (parks.contains(park))
            parks.remove(park);
    }

    public Boolean isExists(Park park) {
        return parks.contains(park);
    }

    public ParkBank GetParksByDepartmentID(Integer departmentID) {
        ParkBank parkBank = new ParkBank();
        for (Park park : parks) {
            if (park.getDepartment() == departmentID)
                parkBank.insertPark(park);
        }
        return parkBank;
    }

    @Override
    public Iterator<Park> iterator() {
        return new ParkBank.ParkIterator();
    }

    private class ParkIterator implements Iterator<Park> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < parks.size();
        }

        @Override
        public Park next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the ParkBank");
            }
            return parks.get(currentIndex++);
        }
    }
}
