/*
 * Created by Zeyu Chen 03/10/2018
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Mammals {
    String name;      // type of the animal
    int number;       // number of the species
    int demand;       // demand of the food
    int food;         // actually food
    double reprorate; // reproduce rate
    double deathrate; // death rate

    // Lock

    Lock numberl = new ReentrantLock();
    Lock demandl = new ReentrantLock();
    Lock reproratel = new ReentrantLock();
    Lock deathratel = new ReentrantLock();

    public Mammals() {
    }

    public String getName() {
        return name;
    }
    public int getNumber() {
        return number;
    }

    public int getDemand() {
        return demand;
    }

    public double getReprorate() {
        return reprorate;
    }

    public double getDeathrate() {
        return deathrate;
    }
}

