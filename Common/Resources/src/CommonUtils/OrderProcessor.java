package CommonUtils;

import Entities.Order;
import Entities.OrderStatus;
import Entities.OrderType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderProcessor {

    public static List<Order> findBestCombination(List<Order> orders, int target) {
        Result result = findBestCombination(orders, 0, target, new ArrayList<>(), 0, 0, new ArrayList<>());
        return result.bestCombination;
    }

    private static Result findBestCombination(List<Order> orders, int start, int target, List<Order> currentCombination, int currentSum, int closestSum, List<Order> bestCombination) {
        if (currentSum > target) {
            return new Result(closestSum, bestCombination);
        }
        if (currentSum > closestSum) {
            closestSum = currentSum;
            bestCombination = new ArrayList<>(currentCombination);
        }
        for (int i = start; i < orders.size(); i++) {
            Order order = orders.get(i);
            currentCombination.add(order);
            Result result = findBestCombination(orders, i + 1, target, currentCombination, currentSum + order.getNumOfVisitors(), closestSum, bestCombination);
            closestSum = result.closestSum;
            bestCombination = result.bestCombination;
            currentCombination.remove(currentCombination.size() - 1);
        }
        return new Result(closestSum, bestCombination);
    }

    private static class Result {
        int closestSum;
        List<Order> bestCombination;

        Result(int closestSum, List<Order> bestCombination) {
            this.closestSum = closestSum;
            this.bestCombination = bestCombination;
        }
    }

    // Example usage
    public static void main(String[] args) {
        List<Order> orders = Arrays.asList(
                new Order("1", "Park1", new Timestamp(System.currentTimeMillis()), "email1@test.com", "111", OrderStatus.STATUS_WAITLIST, null, null, "O1", OrderType.ORD_TYPE_SINGLE, 9),
                new Order("2", "Park2", new Timestamp(System.currentTimeMillis()), "email2@test.com", "222", OrderStatus.STATUS_WAITLIST, null, null, "O2", OrderType.ORD_TYPE_SINGLE, 2),
                new Order("4", "Park2", new Timestamp(System.currentTimeMillis()), "email2@test.com", "222", OrderStatus.STATUS_WAITLIST, null, null, "O5", OrderType.ORD_TYPE_SINGLE, 2),
                new Order("5", "Park2", new Timestamp(System.currentTimeMillis()), "email2@test.com", "222", OrderStatus.STATUS_WAITLIST, null, null, "7", OrderType.ORD_TYPE_SINGLE, 2),
                new Order("6", "Park2", new Timestamp(System.currentTimeMillis()), "email2@test.com", "222", OrderStatus.STATUS_WAITLIST, null, null, "9", OrderType.ORD_TYPE_SINGLE, 2),
                new Order("3", "Park3", new Timestamp(System.currentTimeMillis()), "email3@test.com", "333", OrderStatus.STATUS_WAITLIST, null, null, "O3", OrderType.ORD_TYPE_SINGLE, 2)
        );

        int target = 10;
        List<Order> bestCombination = findBestCombination(orders, target);
        System.out.println("Best combination of orders:");
        for (Order order : bestCombination) {
            System.out.println("OrderID: " + order.getOrderID() + ", NumOfVisitors: " + order.getNumOfVisitors());
        }
    }
}
