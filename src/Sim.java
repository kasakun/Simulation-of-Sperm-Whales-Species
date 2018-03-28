/*
 * Created by Zeyu Chen 03/10/2018
 *
 * Main class
 */


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Sim {
    public static int barrier;
    public static int barrier_counter;
    public static void main(String[] args) {

        // Initialize
        barrier = 0;
        barrier_counter = 0;
        Lock barrierl = new ReentrantLock();

        MainProc mp = new MainProc(0, 85, 100, 100000000);
        KillerWhales kw = new KillerWhales(5000, 200, 0.15, 0.2);
        SpermWhales sw = new SpermWhales(10000, 200, 0.2, 0.1);
        MarineMammals mm = new MarineMammals(20000, 200, 0.4, 0.1);

        System.out.println("==================================================================================");
        System.out.println("Ocean Current: Type" + mp.oceanCur + ", Ocean Temp: " + mp.oceanTemp + "F, Human Fish Rate: "
                + mp.fishRate + ", Food Unit: " + mp.foodRes);

        System.out.println("Killer Whales: " + kw.number + ", Food Demand: " + kw.demand + ", Reproduce rate: " +
                kw.reprorate + ", Death Rate: " + kw.deathrate);

        System.out.println("Sperm Whales: " + sw.number + ", Food Demand: " + sw.demand + ", Reproduce rate: " +
                sw.reprorate + ", Death Rate: " + sw.deathrate);

        System.out.println("Other marine mammals: " + mm.number + ", Food Demand: " + mm.demand + ", Reproduce rate: " +
                mm.reprorate + ", Death Rate: " + mm.deathrate);


        System.out.println("==================================================================================");
        System.out.println("Simulation starts");

        MainProcThread mpthr = new MainProcThread("Main Process Thread") {
            @Override public void run() {
                //System.out.println("Thread:" + threadName + " ID: " + Thread.currentThread().getId() + " starts.");
                // Barrier
                barrierl.lock();
                try {
                    ++barrier;
                } finally {
                    barrierl.unlock();
                }
                while (barrier != 4)
                    //System.out.println("Main Process: is waiting.");
                    ++barrier_counter;
            }
        };

        KillerWhalesThread kwthr = new KillerWhalesThread("Killer Whales Thread") {
            @Override public void run() {
                //System.out.println("Thread:" + threadName + " ID: " + Thread.currentThread().getId() + " starts.");
                int count = 0;  // evemt up limit
                int huntcount =0;
                double now = 0.0;

                Engine kwengine = new Engine();
                Event hunt = new KillerWhalesHunt(now);

                // Season begin
                kwengine.eventList.add(hunt);
                ++count;
                while (!kwengine.eventList.isEmpty()) {
                    double temp = now;

                    kwengine.eventHandler(mp, kw, sw, mm);
                    // Use prob to determine whether to schedule
                    if (Math.random() > 0.1 && count < 50) {
                        now = Math.random()*1 +  temp;
                        Event huntTemp = new KillerWhalesHunt(now);
                        // schedule next event
                        kwengine.eventList.add(huntTemp);
                        ++count;
                    }
                    // Use prob to determine whether to schedule
                    if (Math.random() > 0.5 && count < 50) {
                        now = Math.random()*10 +  temp;
                        Event deathTemp = new KillerWhalesDeath(now);
                        // schedule next event
                        kwengine.eventList.add(deathTemp);
                        ++count;
                    }

                }
                // Barrier
                barrierl.lock();
                try {
                    ++barrier;
                } finally {
                    barrierl.unlock();
                }
                while (barrier != 4)
                   // System.out.println("Killer Whales is waiting.");
                    ++barrier_counter;
                /* Season calculate */
                kw.numberl.lock();

                try {
                    int temp = kw.number;
                    kw.number = kw.number  +  (int) (temp*kw.reprorate);
                    kw.number = kw.number - (int) (temp*kw.deathrate);
                    System.out.println(kw.name + ": " +(int)(temp*kw.deathrate) + " dies, " + (int)(temp*kw.deathrate) +
                            " reproduces. " + "Remain killer whales:" + kw.number);

                } finally {
                    kw.numberl.unlock();
                }

                // Calculate death for hunger
                kw.numberl.lock();

                try {
                    if (kw.food < kw.demand) {
                        kw.number -= (int)((kw.demand - kw.food)*2.5);
                        System.out.println(kw.name + ": " + (int)((kw.demand - kw.food)*2.5) + " dies for hunger.");
                    }
                } finally {
                    kw.numberl.unlock();
                }

                /* Season calculate */

                // Season ends
            }
        };
        // EXAMPLE HERE
        SpermWhalesThread swthr = new SpermWhalesThread("Sperm Whales Thread") {
            @Override public void run() {
                int count =0;//the number of event
                double now = 0.0;

                //System.out.println("Thread:" + threadName + " ID: " + Thread.currentThread().getId() + " starts.");
                Engine swengine = new Engine();
                Event e = new SpermWhalesEat(0.0);

                swengine.eventList.add(e);
                ++count;
                while (!swengine.eventList.isEmpty()) {
                    double temp =now;
                    swengine.eventHandler(mp, kw, sw, mm);

                    if (Math.random() > 0.8 && count < 50) {
                        now = Math.random()*40 + temp;
                        Event deathTemp = new SpermWhalesDeath(now);
                        swengine.eventList.add(deathTemp);
                        ++count;
                    }

                    if (Math.random() > 0.5 && count <50) {
                        now = Math.random()*0.5 + temp;
                        Event eat = new SpermWhalesEat(now);
                        swengine.eventList.add(eat);
                        ++count;
                    }

                    //kwengine.schedule(e);
                }

                // Barrier
                barrierl.lock();
                try {
                    ++barrier;
                } finally {
                    barrierl.unlock();
                }
                while (barrier != 4)
                    //System.out.println("Sperm Whales: is waiting.");
                    ++barrier_counter;

                sw.numberl.lock();

                try {
                    int temp = sw.number;
                    sw.number = sw.number  +  (int) (temp*sw.reprorate);
                    sw.number = sw.number - (int) (temp*sw.deathrate);
                    System.out.println(sw.name + ": " +(int)(temp*sw.deathrate) + " dies, " + (int)(temp*sw.deathrate) +
                            " reproduces. " + "Remain killer whales:" + sw.number);

                } finally {
                    sw.numberl.unlock();
                }

                // Calculate death for hunger
                sw.numberl.lock();

                try {
                    if (sw.food < sw.demand) {
                        sw.number -= (int)((sw.demand - sw.food)*2.5);
                        System.out.println(sw.name + ": " + (int)((sw.demand - sw.food)*2.5) + " dies for hunger.");
                    }
                } finally {
                    sw.numberl.unlock();
                }

            }
        };

        MarineMammalThread mmthr = new MarineMammalThread("Marine Mammals Thread") {
            @Override public void run() {
                //System.out.println("Thread" + threadName + ", ID: " + Thread.currentThread().getId() + " starts.");
                int count = 0;
                double now = 0.0;
                Engine mmengine = new Engine();
                // Food resource consume

                Event e = new MarineMammalsEat(0.0);
                mmengine.eventList.add(e);
                while (!mmengine.eventList.isEmpty()) {
                    mmengine.eventHandler(mp, kw, sw, mm);
                    //mmengine.schedule(e);
                }

                // Season begin
                mmengine.eventList.add(e);
                ++count;
                while (!mmengine.eventList.isEmpty()) {
                    double temp = now;

                    mmengine.eventHandler(mp, kw, sw, mm);

                    if (Math.random() > 0.1 && count < 50) {
                        now = Math.random()*1 + temp;
                        Event eatTemp = new MarineMammalsEat(now);
                        mmengine.eventList.add(eatTemp);
                        ++count;
                    }

                    if (Math.random() > 0.5 && count < 50) {
                        now = Math.random()*10 +  temp;
                        Event deathTemp = new MarineMammalsDeath(now);
                        mmengine.eventList.add(deathTemp);
                        ++count;
                    }

                }



                // Barrier
                barrierl.lock();
                try {
                    ++barrier;
                } finally {
                    barrierl.unlock();
                }
                while (barrier != 4)
                    //System.out.println("Marine Mammals: is waiting.");
                    ++barrier_counter;

                /* Season calculate */
                mm.numberl.lock();

                try {
                    int temp = mm.number;
                    mm.number = mm.number  +  (int) (temp*mm.reprorate);
                    mm.number = mm.number - (int) (temp*mm.deathrate);
                    System.out.println(mm.name + ": " +(int)(temp*mm.deathrate) + " dies, " + (int)(temp*mm.deathrate) +
                            " reproduces. " + "Remain Marine Mammals:" + mm.number);

                } finally {
                    mm.numberl.unlock();
                }

                // Calculate death for hunger
                mm.numberl.lock();

                try {
                    if (mm.food < mm.demand) {
                        mm.number -= (int)((mm.demand - mm.food)*5.0);
                        System.out.println(mm.name + ": " + (int)((mm.demand - mm.food)*5.0) + " dies for hunger.");
                    }
                } finally {
                    mm.numberl.unlock();
                }

                /* Season calculate */

                // Season ends
            }
        };

        mpthr.start();
        kwthr.start();
        swthr.start();
        mmthr.start();



    }
}


// Thread
class MainProcThread extends Thread{
    String threadName;

    public MainProcThread(String name) {
        threadName = name;
    }
}

class KillerWhalesThread extends Thread{
    String threadName;

    public KillerWhalesThread(String name) {
        threadName = name;
    }
}

class SpermWhalesThread extends Thread{
    String threadName;

    public SpermWhalesThread(String name) {
        threadName = name;
    }
}

class MarineMammalThread extends Thread{
    String threadName;

    public MarineMammalThread(String name) {
        threadName = name;
    }
}



