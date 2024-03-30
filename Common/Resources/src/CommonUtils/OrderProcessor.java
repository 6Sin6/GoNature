package CommonUtils;

import Entities.Order;

import java.util.ArrayList;
import java.util.List;
/**
 * The {@code OrderProcessor} class provides functionality to process orders and
 * determine the optimal combination of them to meet a specified target number of visitors.
 * It utilizes a recursive approach to explore all possible combinations of orders
 * and selects the one that most closely matches the target number of visitors without exceeding it.
 *
 * <p>The primary method {@code findBestCombination} initiates the process by taking a list of orders
 * and the target number of visitors as inputs. It then delegates the task to a private recursive method
 * that iteratively builds combinations of orders, tracks their sum of visitors, and compares them against
 * the target number. The process aims to find the combination of orders whose sum of visitors is closest to,
 * but not exceeding, the target number.</p>

 */
public class OrderProcessor {
    /**
     * Returns the best combination of orders that sum up to the target number of visitors.
     *
     * @param orders The list of orders to filter.
     * @param target The target number of visitors.
     * @return The filtered list of orders.
     */
    public static List<Order> findBestCombination(List<Order> orders, int target) {
        Result result = findBestCombination(orders, 0, target, new ArrayList<>(), 0, 0, new ArrayList<>());
        return result.bestCombination;
    }

    /**
     * Finds the best combination of orders that sum up to the target number of visitors.
     *
     * @param orders             The list of orders to consider.
     * @param start              The index to start from in the list of orders.
     * @param target             The target number of visitors.
     * @param currentCombination The current combination of orders.
     * @param currentSum         The current sum of visitors in the combination.
     * @param closestSum         The closest sum of visitors to the target.
     * @param bestCombination    The best combination of orders found so far.
     * @return The best combination of orders that sum up to the target number of visitors.
     */
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

    /**
     * Represents the result of the findBestCombination method.
     */
    private static class Result {
        int closestSum;
        List<Order> bestCombination;

        /**
         * Constructs a new Result object.
         *
         * @param closestSum      The closest sum of visitors to the target.
         * @param bestCombination The best combination of orders found so far.
         */
        Result(int closestSum, List<Order> bestCombination) {
            this.closestSum = closestSum;
            this.bestCombination = bestCombination;
        }
    }
}
