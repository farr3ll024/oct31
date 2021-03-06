package production;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author: Michael Gibler
 * @author: Sam Barth
 *
 * Date last modified: 12/7/2016
 *
 * The belt class creates a hashmap of variable size, which is the java
 * representation of a conveyor belt. The size is given to the belt class
 * through the master class. Item's can then be placed on the belt through the
 * addItem method. The belt moves one space every tick, represented in the code
 * by assigned each hashmap value to the next key (i.e the value at key = 0 is
 * reassigned with key = 1. This is done from the end of the belt to the front
 * in order to prevent overwriting anything. Once the reassignment reaches the
 * first spot in the hashmap, the program checks the Order class to see if a new
 * bin is ready to be added to the belt.
 */
public class Belt implements Clock, Document {

    private Bin newestBin;
    private boolean binDelivered;

    /*Initializing the HashMap belt*/
    static Map<Integer, Bin> belt = new HashMap<Integer, Bin>();
    /*Counter variables for constructing and moving respectively*/
    static int i, j;
    /*This is the x variable given by the masterclass decremented by two since there are two open*/
 /*floor squares in the belt's row*/
    static int beltLength;


    /*used for constructing*/
    public Belt(int x) {
        beltLength = x - 2;
        /*This will build the belt with a Hashmap. Each key corresponds*/
 /*to a position on the belt, with an arraylist value representing the bin in that location*/
        for (i = 0; i < beltLength; i++) {
            belt.put(i, null);
        }
        this.binDelivered = false;
    }

    /**
     *
     * @param b The bin item handed off to the Belt by the picker.
     */
    public void deliverBin(Bin b) {
        this.newestBin = b;
        this.binDelivered = true;
    }

    /* Ticker method that moves the bins on the belt along each tick, and then checks for new bins*/
    @Override
    public void tick(int iteration) {
        /* This variable j is decreasing from the last belt Key to 1 in order to move the ArrayLists by one*/
        j = beltLength - 1;
        /* Loop to move each ArrayList, starting at the end of the belt*/
        for (j = beltLength - 1; j > 0; j--) {
            belt.put(j, belt.get(j - 1));
        }
        /*Checks the Order Class to see if a bin is ready to be loaded onto belt position 0*/
        if (this.binDelivered) {
            belt.put(0, this.newestBin);
            this.binDelivered = false;
        }
    }

    /* Method to document what the belt is doing and to make sure it is working correctly*/
    @Override
    public void doc() {
        for (int z = 0; z <= beltLength - 1; z++) {
            try (FileWriter fw = new FileWriter("BeltLog", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                if (!(belt.get(z) == null)) {
                    out.println("At belt position" + (z + 1) + "there is a bin containing" + belt.get(z) + "\n");
                } else {
                    out.println("At belt position" + (z + 1) + "there is no bin \n");
                }
            } catch (IOException ex) {
                Logger.getLogger(Belt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
