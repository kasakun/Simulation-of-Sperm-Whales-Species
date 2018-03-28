/*
 * Created by Zeyu Chen 03/10/2018
 *
 * Taoyouwei Gao
 */

public class SpermWhales extends Mammals {
    double huntProb;

    SpermWhales(int num, int dem, double r, double d) {
        name = "Sperm Whales";
        number = num;
        demand = dem;
        reprorate = r;
        deathrate = d;
    }

    public void accDeath(){
        numberl.lock();
        try{
            --number;
            System.out.println("Sperm whales: Accidental death. Remain sperm whales:" + number);
        }
        finally{
            numberl.unlock();
        }
    }

    public void eat(MainProc mp) {
        mp.foodResl.lock();
        try {
            mp.foodRes = mp.foodRes - number*0.03;
            System.out.println(name + ": Consumes food: " + number*0.03 + ". Remain:" + mp.foodRes);
        } finally {
            mp.foodResl.unlock();
        }
    }
}
