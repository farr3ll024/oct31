/**
 *
 * @author: Sam Barth
 *
 * date last modified: 11/30/2016
 *
 * The RobotMaster class acts as the go-between for the Robots and
 * the other parts of the warehouse.  After each tick, the RobotMaster
 * will check whether there is an available idle Robot, and if so,
 * send that Robot on a mission by making calls to either Inventory
 * or Orders.
 */
package production;

import java.awt.Point;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.IOException;

import testpackage.*;

// RobotMaster will implement clock interface
public class RobotMaster implements Clock, Document {

    private ArrayList<Robot> robots;
    private final int batteryRange = 50;
    private final static Point VOIDLOCATION = new Point(-1, -1);
    private boolean lastDeployedOrder;
    private MockFloor floor;
    private MockInventory inventory;
    private MockOrders orders;
    private Picker picker;

    /**
     *
     * @param numRobots the number of robots that will be used in the simulation
     * (RobotMaster currently only supports 1 robot)
     * @param f is the instance of the Floor that Master will use in conjunction
     * with this instance of RobotMaster
     * @param o is the instance of the Orders that Master will use in
     * conjunction with this instance of RobotMaster
     * @param i is the instance of the Inventory that Master will use in
     * conjunction with this instance of RobotMaster
     */
    public RobotMaster(int numRobots, MockFloor f, MockInventory i, MockOrders o, Picker p) {
        this.robots = new ArrayList<>(numRobots);
        this.lastDeployedOrder = false;
        //the following for loop will individually initialize each robot at a charge location
        int j = 0;
        for (; j < numRobots; j++) {
            Point start = new Point(0, j);
            Robot r = new Robot(start, f, i, o, p);
            this.robots.add(r);
        }
        this.floor = f;
        this.inventory = i;
        this.orders = o;
        this.picker = p;        
    }

    /**
     * The precondition for calling this method is that Robot r is idle
     *
     * @param r Robot r is an idle Robot in the simulation
     */
    private void deploy(Robot r) {
        if (this.lastDeployedOrder) {
            this.lastDeployedOrder = false;
            r.assignMission("Stock", inventory.shelfToFetch());
        } else {
            r.assignMission("Order", orders.shelfToFetch());
        }
    }

    public ArrayList getRobotLocations() {
        ArrayList<Point> robotLocations = new ArrayList<>();
        for (Robot r : robots) {
            Point location = new Point(r.getLocation());
            robotLocations.add(location);
        }
        return robotLocations;
    }

    /**
     * Increment time by one position. Robots deployed on missions may move a
     * maximum of one unit per tick.
     *
     * @param i The cumulative tick number of the simulation
     *
     */
    @Override
    public void tick(int i) {
        /**
         * robots currently on missions should continue those missions, and idle
         * robots should be deployed
         */
        for (Robot r : robots) {
            if (!r.isIdle()) {
                r.move(false);
            } else {
                deploy(r);
            }
        }
    }

    /**
     * Document movement of Robots on the Floor
     * 
     * Solution to the problem of writing to a txt file was found here:
     * http://stackoverflow.com/questions/2885173/how-to-create-a-file-and-write-to-a-file-in-java
     */
    @Override
    public void doc() {
        try {
            PrintWriter writer = new PrintWriter("RobotLog", "UTF-8");
            for(Robot r : this.robots){
            writer.println("Robot at" + r.getLocation().toString());
            writer.println(r.getSpecialActionLog());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
//need to call Order.getNextShelf, which will return Point or Null - same for Inventory
//need to call Order.shelfReady()
