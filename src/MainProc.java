/*
 * Created by Zeyu Chen 03/10/2018
 *
 * Chengxi Yao
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainProc {
    int oceanCur;    // ocean current type
    int oceanTemp;   // ocean temperature

    double fishRate; // human dish rate
    double foodRes;  // food resource of sperm whales and other marine mammals

    // Lock
    Lock foodResl = new ReentrantLock();

    MainProc(int Cur, int Temp, double Fish, double Food) {
        oceanCur = Cur;
        oceanTemp = Temp;
        fishRate = Fish;
        foodRes = Food;
    }


//    public void consumeFood(Mammals mammals) {
//        foodResl.lock();
//        double eatrate = 0.0;
//        if (mammals.name == "Sperm Whales")
//            eatrate = 0.05;
//        else if (mammals.name == "Marine Mammals")
//            eatrate = 0.03;
//        try {
//            foodRes = foodRes - mammals.number*eatrate;
//            System.out.println(mammals.name + " consumes food: " + mammals.number*eatrate + ". Remain:" + foodRes);
//        } finally {
//            foodResl.unlock();
//        }
//    }
}
