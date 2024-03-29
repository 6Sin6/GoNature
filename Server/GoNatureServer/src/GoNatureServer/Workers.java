package GoNatureServer;

import CommonServer.ocsf.AbstractServer;
import CommonServer.ocsf.ConnectionToClient;
import DataBase.DBConnection;
import ServerUIPageController.ServerUIFrameController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * Provides methods to manage and schedule tasks for the GoNature server application.
 * This class includes functionality for starting threads to process client connections,
 * scheduling tasks for sending reminders, updating order statuses, and managing waitlisted orders.
 * It uses a list of ScheduledExecutorService instances to manage periodic execution of these tasks.
 */
public class Workers {
    /**
     * A list of ScheduledExecutorService instances.
     * Each instance in this list is responsible for managing and scheduling a specific task.
     */
    private static final List<ScheduledExecutorService> executors = new ArrayList<>();

    /**
     * Starts a thread to monitor and update the client connections in the server's UI.
     *
     * @param controller The server UI frame controller to update the UI components.
     * @param server The server instance to monitor the connections.
     */
    public static void startClientProcessingThread(ServerUIFrameController controller, AbstractServer server) {
        new Thread(() -> {
            try {
                while (server != null && server.isListening()) {
                    Thread[] clientConnections = server.getClientConnections();
                    controller.resetTableClients();
                    for (Thread clientThread : clientConnections) {
                        if (clientThread instanceof ConnectionToClient) {
                            ConnectionToClient client = (ConnectionToClient) clientThread;
                            controller.addRow(client.getInetAddress().getHostName(), client.getInetAddress().getHostAddress());
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("The client processing thread was interrupted.");
                    }
                }
            } catch (NullPointerException e) {

            }
        }, "Client Processing Thread").start();
    }

    /**
     * Schedules a task to send reminders and manage orders one day before their scheduled time.
     *
     * @param db The database connection object to access and update order data.
     * @param controller The server UI frame controller to log activities.
     */
    public static void SendReminderDayBeforeWorker(DBConnection db, ServerUIFrameController controller) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 24);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            try {
                db.updateOrderStatusForUpcomingVisits();
                db.cancelOrdersInWaitlist24HoursBefore();
            } catch (Exception e) {
                controller.addtolog("Sending Reminders for Orders in  " + calendar.getTime());
            }
        };

        long initialDelay = calculateInitialDelay();
        scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.HOURS.toMillis(1), TimeUnit.MILLISECONDS);
//        long initialDelay = 5000;
//        scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.SECONDS.toMillis(10), TimeUnit.MILLISECONDS);
        executors.add(scheduler);
    }

    /**
     * Schedules a task to cancel orders that haven't been confirmed 22 hours before the scheduled time.
     *
     * @param db The database connection object to update order statuses.
     * @param controller The server UI frame controller to log activities.
     */
    public static void CancelOrdersThatDidntConfirmWorker(DBConnection db, ServerUIFrameController controller) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, 22);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                db.ChangeLatePendingConfirmationToCancelled();
                controller.addtolog("Checking Confirmation for Orders in  " + calendar.getTime());
            } catch (Exception e) {
                controller.addtolog("failed CancelOrdersThatDidntConfirmWorker");
            }

        };

        long initialDelay = calculateInitialDelay();
        scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.HOURS.toMillis(1), TimeUnit.MILLISECONDS);
//        long initialDelay = 5000;
//        scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS);
        executors.add(scheduler);
    }

    /**
     * Schedules a task to process waitlisted orders 48 hours before the scheduled time.
     *
     * @param db The database connection object to manage orders.
     * @param controller The server UI frame controller to log activities.
     */
    public static void enterOrdersFromWaitList48HoursBeforeWorker(DBConnection db, ServerUIFrameController controller) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, 48);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                db.enterOrdersInWaitlist48HoursBefore();
                controller.addtolog("Entering orders from the waitlist  " + calendar.getTime());
            } catch (Exception e) {
                controller.addtolog("failed enterOrdersFromWaitList48HoursBeforeWorker");
            }
        };

        long initialDelay = calculateInitialDelay();
        scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.HOURS.toMillis(1), TimeUnit.MILLISECONDS);
//        long initialDelay = 5000;
//        scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS);
        executors.add(scheduler);
    }

    /**
     * Schedules a task to update the status of absent orders at the top of every hour.
     *
     * @param db The database connection object for order management.
     * @param controller The server UI frame controller to log activities.
     */
    public static void changeToAbsentOrders(DBConnection db, ServerUIFrameController controller) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                db.ChangeToAbsent();
                controller.addtolog("Update status for absent orders  " + calendar.getTime());
            } catch (Exception e) {
                controller.addtolog("failed changeToAbsentOrders");
            }
        };

        long initialDelay = calculateInitialDelay();
        scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.HOURS.toMillis(1), TimeUnit.MILLISECONDS);
//        long initialDelay = 5000;
//        scheduler.scheduleAtFixedRate(task, initialDelay, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS);
        executors.add(scheduler);
    }

    /**
     * Calculates the initial delay required to schedule the tasks at the top of the next hour.
     *
     * @return The initial delay in milliseconds.
     */
    private static long calculateInitialDelay() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();

        calendar.add(Calendar.HOUR, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long nextHour = calendar.getTimeInMillis();
        return nextHour - now;
    }

    /**
     * Shuts down all the scheduled executor services.
     */
    public static void shutdownExecutors() {
        executors.forEach(executor -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                        System.err.println("Executor service did not terminate");
                }
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        });
    }
}

