/*
 * Created by Zeyu Chen 03/10/2018
 * Main process for simulation
 *
 * Chengxi Yao
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
// import java.io.*;

enum curType {
    warmCurrents("warm currents", 1.2), coldWaterCurrents("cold currents", 0.85);
    private final String name;
    private final double rate;
    private curType(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    public static String getName(int id) {
        return (id == 0) ? curType.warmCurrents.name : curType.coldWaterCurrents.name;
    }

    public static double getRate(int id) {
        return (id == 0) ? curType.warmCurrents.rate : curType.coldWaterCurrents.rate;
    }
}

public class MainProc {
    int oceanCur;    // ocean current type
    int oceanTemp;   // ocean temperature

    double fishRate; // human dish rate
    double foodRes;  // food resource of sperm whales and other marine mammals

    // Lock
    Lock foodResl = new ReentrantLock();

    MainProc(int Cur, int Temp, double Fish, double Food) {
        this.oceanCur = Cur;
        this.oceanTemp = Temp;
        this.fishRate = Fish;
        this.foodRes = Food;
    }


    // public void consumeFood(Mammals mammals) {
    //     foodResl.lock();
    //     double eatrate = 0.0;
    //     if (mammals.name == "Sperm Whales")
    //         eatrate = 0.05;
    //     else if (mammals.name == "Marine Mammals")
    //         eatrate = 0.03;
    //     try {
    //         foodRes = foodRes - mammals.number*eatrate;
    //         System.out.println(mammals.name + " consumes food: " + mammals.number*eatrate + ". Remain:" + foodRes);
    //     } finally {
    //         foodResl.unlock();
    //     }
    // }


    public void huntWhale(SpermWhales sw) {
        if (Math.random() > 0.5) {
            sw.numberl.lock();
            try {
                sw.number = sw.number - 10;
                System.out.println("Main Proc: Hunts a sperm whale. Remain spem whales:" + sw.number);
            } finally {
                sw.numberl.unlock();
            }
        } else {
            sw.numberl.lock();
            try {
                sw.number = (int) (sw.number * 0.9);
                System.out.println("Main Proc: Hunts 2 sperm whales. Remain spem whales:" + sw.number);
            } finally {
                sw.numberl.unlock();
            }
        }
    }

    public void fish() {
        if (Math.random() > 0.3) {
            this.foodRes = (0.85 + 0.1 * Math.random()) * this.foodRes;
            System.out.println("Main Proc: Human fishing action. Remain food resource:" + this.foodRes);
        }
        // writeLog("log.txt", "hi");
        // writeLog("log.txt", "hihi");

    }

    public void naturalDisaster() {
        this.oceanTemp = (int) ((0.8 + 0.4 * Math.random()) * this.oceanTemp);
        if (Math.random() > 0.5) {
            oceanCur = 1 - oceanCur;
        }
        System.out.println("Main Proc: Climate Change, current temperature:" + this.oceanTemp
                + ", current ocean currents:" + this.oceanCur);
    }

    public void foodGrow() {
        this.foodRes = this.foodRes * 1.4 * (this.oceanTemp/85) * curType.getRate(oceanCur);
        System.out.println("Main Proc: Food resource grow, current food resource:" + this.foodRes);
    }

    public void winterToSpring() {
        this.oceanTemp += (int) (4 * Math.random() + 8);
        this.oceanCur = Math.random() > 0.5 ? 0 : 1;
        System.out.println("Main Proc: Season Change, current temperature:" + this.oceanTemp
                + ", current ocean currents:" + this.oceanCur);
    }

    public void springToSummer() {
        this.oceanTemp += (int) (6 * Math.random() + 6);
        this.oceanCur = 0;
        System.out.println("Main Proc: Season Change, current temperature:" + this.oceanTemp
                + ", current ocean currents:" + this.oceanCur);
    }

    public void summerToFall() {
        this.oceanTemp -= (int) (6 * Math.random() + 6);
        this.oceanCur = Math.random() > 0.5 ? 0 : 1;
        System.out.println("Main Proc: Season Change, current temperature:" + this.oceanTemp
                + ", current ocean currents:" + this.oceanCur);
    }

    public void fallToWinter() {
        this.oceanTemp -= (int) (4 * Math.random() + 8);
        this.oceanCur = 1;
        System.out.println("Main Proc: Season Change, current temperature:" + this.oceanTemp
                + ", current ocean currents:" + this.oceanCur);
    }

    public String toString() {
        return "Current condition: \n" +
                "ocean currents: " + curType.getName(this.oceanCur) + "\n" +
                "ocean temperature: " + this.oceanTemp + "\n" +
                "food resource: " + this.foodRes;
    }
}
