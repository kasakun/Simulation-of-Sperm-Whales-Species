/**
 * This class is used to simulate the MarineMammals population variations
 * Created by Zeyu Chen 03/10/2018
 * @author Weihua Zhu
 * @version 0.9.0
 */

public class MarineMammals extends Mammals {

    MarineMammals(int num, int dem, double r, double d) {
        name = "Marine Mammals";
        number = num;
        demand = dem;
        reprorate = r;
        deathrate = d;
    }

    public void eat(MainProc mp) {
        mp.foodResl.lock();
        try {
            mp.foodRes = mp.foodRes - number*0.07;
            System.out.println(name + ": Consumes food: " + number*0.07 + ". Remain:" + mp.foodRes);
        } finally {
            mp.foodResl.unlock();
        }
    }

    public void accDeath() {
        numberl.lock();
        try {
            --number;
            System.out.println("Marine Mammals: Accidental death. Remain Marine Mammals:" + number);
        } finally {
            numberl.unlock();
        }
    }
}