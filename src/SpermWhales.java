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
            System.out.println("Sperm Whales: Accidental death. Remain sperm whales:" + number);
        }
        finally{
            numberl.unlock();
        }
    }

    public void eat(MainProc mp) {
        if(mp.foodRes<=0)
            return;
        mp.foodResl.lock();
        try {
            mp.foodRes = mp.foodRes - number;
            System.out.println(name + ": Consumes food: " + number + ".");
            food += number;
        } finally {
            mp.foodResl.unlock();
        }
    }
}
