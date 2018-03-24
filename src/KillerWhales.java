/*
 * Created by Zeyu Chen 03/10/2018
 *
 * Zeyu Chen
 */

public class KillerWhales extends Mammals {
    double preySperm;  // prey rate on sperm whales
    double preyMarin;  // prey rate on other marine mammals


    KillerWhales(int num, int dem, double r, double d) {
        name = "Killer Whales";
        number = num;
        demand = dem;
        reprorate = r;
        deathrate = d;
    }

    public void killSpermWhales(SpermWhales sw) {
        if (Math.random() > 0.5) {
            sw.numberl.lock();
            try {
                --sw.number;
                System.out.println("Killer Whales: Kills sperm whales. Remain sperm whales: " + sw.number);
            } finally {
                sw.numberl.unlock();
            }
        }
    }

    public void killMarineMammals(MarineMammals mm) {
        if (Math.random() > 0.5) {
            mm.numberl.lock();
            try {
                --mm.number;
                System.out.println("Killer Whales: Kills  marine mammals. Remain marine mammals:" + mm.number);
            } finally {
                mm.numberl.unlock();
            }
        }

    }

    public void accDeath() {
        numberl.lock();
        try {
            --number;
            System.out.println("Killer Whales: Accidental death. Remain killer whales:" + number);
        } finally {
            numberl.unlock();
        }
    }

}
