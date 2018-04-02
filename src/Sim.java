/*
 * Created by Zeyu Chen 03/10/2018
 *
 * Main class
 */


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("Duplicates") // No more duplicated warning OK?
public class Sim {
    private static CyclicBarrier startBarrier = new CyclicBarrier(4);
    private static CyclicBarrier endBarrier = new CyclicBarrier(4);


    public static double timeLimit = 3600;  // The unit is day. 360 is onw year, 90 is one season, change it to control the time limit.

    public static void main(String[] args) {
        if (args.length  == 0) {
            System.out.println("Running by default 3600 days, 40 seasons.");
        }
        else if (args.length == 1) {
            System.out.println("Running by " + args[0] + " days, " + (Integer.parseInt(args[0])/90 + 1) + " seasons.");
            timeLimit = Integer.parseInt(args[0]);
        }
        else {
            System.out.println("Please input the correct number of days.");
            return;
        }

        MainProc mp = new MainProc(0, 85, 100000000);
        KillerWhales kw = new KillerWhales(3000, 350, 0.03, 0.01);
        SpermWhales sw = new SpermWhales(10000, 10000, 0.03, 0.002);
        MarineMammals mm = new MarineMammals(20000, 20000, 0.03, 0.018);

        System.out.println("Ocean Current: Type" + mp.oceanCur + ", Ocean Temp: " + mp.oceanTemp + "F, Total Food: "
                + mp.totalFood + ", Food Resource: " + mp.foodRes);

        System.out.println("Killer Whales: " + kw.number + ", Food Demand: " + kw.demand + ", Reproduce rate: " +
                kw.reprorate + ", Death Rate: " + kw.deathrate);

        System.out.println("Sperm Whales: " + sw.number + ", Food Demand: " + sw.demand + ", Reproduce rate: " +
                sw.reprorate + ", Death Rate: " + sw.deathrate);

        System.out.println("Other marine mammals: " + mm.number + ", Food Demand: " + mm.demand + ", Reproduce rate: " +
                mm.reprorate + ", Death Rate: " + mm.deathrate);


        System.out.println("Simulation starts");

        MainProcThread mpthr = new MainProcThread("Main Process Thread") {
            @Override public void run() {
                //System.out.println("Thread:" + threadName + " ID: " + Thread.currentThread().getId() + " starts.");
                double timeHelper = 0.0;

                FileOutputStream mainProcLog = null;
                try {
                    mainProcLog = new FileOutputStream("mainProcLog.txt");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                PrintStream mainProcPrint = new PrintStream(mainProcLog);

                double now = 0.0;
                Engine mpengine = new Engine();
                Event season = new seasonChange(now);
                mpengine.eventList.add(season);


                while(timeHelper < timeLimit) {
                	now = timeHelper;
                    mp.foodRes = 2700000;
                    // Start Barrier
                    try{
                        startBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(threadName + "Season: " + (int)(timeHelper/90) + " begins");
                    mainProcPrint.println(threadName + "Season: " + (int)(timeHelper/90) + " begins");

                    mp.foodResl.lock();
                    try {
                        System.out.println("Main Process: Start food Unit: " + mp.foodRes);

                        mainProcPrint.println("Main Process: Start food Unit: " + mp.foodRes);

                        System.out.println("Main Process: Start Ocean Temperature: " + mp.oceanTemp);

                        mainProcPrint.println("Main Process: Start Ocean Temperature: " + mp.oceanTemp);


                    } finally {
                        mp.foodResl.unlock();
                    }
                    // Season begins
                    /**************************************** Season Begins *******************************************/
                    while (now <= timeHelper) {
                        double temp = now;

                        // Under construction
//                        while (!mpengine.eventList.isEmpty()) {
//	                        mpengine.eventHandler(mp, kw, sw, mm);
//	                    }
//
//                        Event season1 = new seasonChange(temp + 90);
//                        Event food = new foodGrow(temp + 90);
//                        mpengine.eventList.add(season1);
//                        mpengine.eventList.add(food);
//                        mainProcPrint.println(threadName + ": Food grow at " + now);
//
//
//                        if (Math.random() < 0.01) {
//                           Event disaster = new naturalDisaster(temp + 90);
//                           mpengine.eventList.add(disaster);
//	                       mainProcPrint.println(threadName + ": Natural disaster at " + now);
//
//                        }
//
//                        if (temp % 360 == 0) {
//                           Event humanHunt = new humanHunt(temp + 90);
//                           mpengine.eventList.add(humanHunt);
//	                       mainProcPrint.println(threadName + ": Human hunt event at " + now);
//                        }
//
//                        if (Math.random() > 0.5) {
//                           Event humanFish = new humanFish(temp + 90);
//                           mpengine.eventList.add(humanFish);
//	                       mainProcPrint.println(threadName + ": Human fish event at " + now);
//                        }
                        now = temp + 90;
                    }

                    /**************************************** Season Ends *********************************************/
                    try{
                        endBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(threadName + "Season: " + (int)(timeHelper/90) + " ends");
                    mainProcPrint.println(threadName + "Season: " + (int)(timeHelper/90) + " ends");
                    /*************************************** Season Checkout ******************************************/
                    mp.foodResl.lock();
                    try {
                        System.out.println("Main Process: End food Unit: " + mp.foodRes);

                        mainProcPrint.println("Main Process: End food Unit: " + mp.foodRes);

                        System.out.println("Main Process: End Ocean Temperature: " + mp.oceanTemp);

                        mainProcPrint.println("Main Process: End Ocean Temperature: " + mp.oceanTemp);


                    } finally {
                        mp.foodResl.unlock();
                    }
                    timeHelper += 90;
                    mainProcPrint.println("====================================================================================================");
                    /*************************************** Season Complete ******************************************/
                }
                // Close file
                mainProcPrint.close();
            }

        };

        KillerWhalesThread kwthr = new KillerWhalesThread("Killer Whales Thread") {
            @Override public void run() {
                // Killer Whales
                double timeHelper = 0.0;
                FileOutputStream killerWhaleLog = null;
                try {
                    killerWhaleLog = new FileOutputStream("killerWhaleLog.txt");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                PrintStream killerWhalePrint = new PrintStream(killerWhaleLog);

                while(timeHelper < timeLimit) {
                    double now = 0.0;
                    int eatcounter = 1;
                    int deathcounter = 0;

                    Engine kwengine = new Engine();
                    Event hunt = new KillerWhalesHunt(now);
                    kwengine.eventList.add(hunt);

                    try{
                        startBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(threadName + "Season: " + (int)(timeHelper/90) + " begins");
                    killerWhalePrint.println(threadName + "Season: " + (int)(timeHelper/90) + " begins");
                    /**************************************** Season Begins *******************************************/
                    kw.food = 0.0;
                    kw.demand = kw.number*0.12;

                    while (!kwengine.eventList.isEmpty()) {
                        double temp = now;
                        kwengine.eventHandler(mp, kw, sw, mm);
                        // Use prob to determine whether to schedule
                        if (now < 90) {
                            now = Math.random()*15 +  temp;
                            Event huntTemp = new KillerWhalesHunt(now);
                            ++eatcounter;
                            // schedule next event
                            kwengine.eventList.add(huntTemp);
                        }
                        // Use prob to determine whether to schedule
                        if (Math.random() > 0.95 && now < 90) {
                            now = Math.random()*2 +  temp;
                            Event deathTemp = new KillerWhalesDeath(now);
                            ++deathcounter;
                            // schedule next event
                            kwengine.eventList.add(deathTemp);
                        }

                    }

                    /**************************************** Season Ends *********************************************/
                    try{
                        endBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(threadName + "Season: " + (int)(timeHelper/90) + " ends");
                    killerWhalePrint.println(threadName + "Season: " + (int)(timeHelper/90) + " ends");
                    /*************************************** Season Checkout ******************************************/
                    // Calculate natural death and reproduce
                    killerWhalePrint.println(threadName + " eat: " + eatcounter);
                    killerWhalePrint.println(threadName + " accidental death: " + deathcounter);
                    kw.numberl.lock();
                    try {
                        int temp = kw.number;
                        kw.number = kw.number  +  (int) (temp*kw.reprorate);
                        kw.number = kw.number - (int) (temp*kw.deathrate);
                        System.out.println(kw.name + ": " +(int)(temp*kw.deathrate) + " dies, " + (int)(temp*kw.reprorate)
                                + " reproduces.");

                        killerWhalePrint.println(kw.name + ": " +(int)(temp*kw.deathrate) + " dies, " +
                                (int)(temp*kw.reprorate) + " reproduces.");

                    } finally {
                        kw.numberl.unlock();
                    }
                    killerWhalePrint.println("Killer Whales total demands: " + kw.demand);
                    killerWhalePrint.println("Killer Whales total consumes: " + kw.food);

                    // Calculate death for hunger
                    kw.numberl.lock();
                    try {
                        if (kw.food < kw.demand) {
                            kw.reprorate = 0.02*((kw.demand - kw.food)/kw.demand);
                            kw.number -= (int)((kw.demand - kw.food)*0.1);
                            System.out.println(kw.name + ": " + (int)((kw.demand - kw.food)*0.1) + " dies for hunger.");

                            killerWhalePrint.println(kw.name + ": "
                                    + (int)((kw.demand - kw.food)*0.1) + " dies for hunger.");
                        } else {
                            kw.reprorate = 0.025*(1 + (kw.food - kw.demand)/kw.demand);
                        }
                    } finally {
                        kw.numberl.unlock();
                    }

                    killerWhalePrint.println("Remain killer whales:" + kw.number);
                    timeHelper += 90;
                    killerWhalePrint.println("====================================================================================================");
                    /*************************************** Season Complete ******************************************/
                }
                killerWhalePrint.close();
            }
        };
        // EXAMPLE HERE
        SpermWhalesThread swthr = new SpermWhalesThread("Sperm Whales Thread") {
            @Override public void run() {
                // Sperm Whales
                double timeHelper = 0.0;

                FileOutputStream spermWhaleLog = null;
                try {
                    spermWhaleLog = new FileOutputStream("spermWhaleLog.txt");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                PrintStream spermWhalePrint = new PrintStream(spermWhaleLog);

                while(timeHelper < timeLimit) {
                    double now = 0.0; // now is in the season scope
                    int eatcounter = 1;
                    int deathcounter = 0;

                    Engine swengine = new Engine();

                    Event e = new SpermWhalesEat(0.0);
                    swengine.eventList.add(e);

                    try{
                        startBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(threadName + "Season: " + (int)(timeHelper/90) + " begins");
                    spermWhalePrint.println(threadName + "Season: " + (int)(timeHelper/90) + " begins");
                    /**************************************** Season Begins *******************************************/
                    sw.food = 0.0;
                    sw.demand = sw.number*70;
                    while (!swengine.eventList.isEmpty()) {
                        double temp =now;
                        swengine.eventHandler(mp, kw, sw, mm);

                        if (Math.random() > 0.95 && now < 90) {
                            now = Math.random()*0.5 + temp;
                            Event deathTemp = new SpermWhalesDeath(now);
                            deathcounter++;
                            swengine.eventList.add(deathTemp);
                        }

                        if (now < 90) {
                            now = Math.random()*2 + temp;
                            Event eat = new SpermWhalesEat(now);
                            eatcounter++;
                            if (sw.food < sw.demand*1.02)
                                swengine.eventList.add(eat);
                        }
                    }

                    /**************************************** Season Ends *********************************************/
                    try{
                        endBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(threadName + "Season: " + timeHelper/90 + "ends");
                    System.out.println(threadName + "Season: " + (int)(timeHelper/90) + " ends");


                    spermWhalePrint.println(threadName + "Season: " + (int)(timeHelper/90) + " ends");
                    /*************************************** Season Checkout ******************************************/
                    // Calculate natural death and reproduce
                    spermWhalePrint.println(threadName + " eat: " + eatcounter);
                    spermWhalePrint.println(threadName + " accidental death: " + deathcounter);
                    sw.numberl.lock();
                    try {
                        int temp = sw.number;
                        sw.number = sw.number  +  (int) (temp*sw.reprorate);
                        sw.number = sw.number - (int) (temp*sw.deathrate);
                        System.out.println(sw.name + ": " +(int)(temp*sw.deathrate) + " dies, " + (int)(temp*sw.reprorate)
                                + " reproduces.");
                        //spermWhalePrint.println(sw.food);

                        spermWhalePrint.println(sw.name + ": " +(int)(temp*sw.deathrate) + " dies, "
                                + (int)(temp*sw.reprorate) + " reproduces.");

                    } finally {
                        sw.numberl.unlock();
                    }
                    spermWhalePrint.println("Sperm Whales total demands: " + sw.demand);
                    spermWhalePrint.println("Sperm Whales total consumes: " + sw.food);
                    // Calculate death for hunger
                    sw.numberl.lock();
                    try {
                        if (sw.food < sw.demand ) {
                            if (sw.food != sw.demand)
                                sw.reprorate = 0.02*((sw.demand - sw.food)/sw.demand);
                            sw.number -= (int)((sw.demand - sw.food)*0.01);

                            if (mm.number <= 0)
                                mm.number = 0;
                            System.out.println(sw.name + ": " + (int)((sw.demand - sw.food)*0.01) + " dies for hunger.");

                            spermWhalePrint.println(sw.name + ": " + (int)((sw.demand - sw.food)*0.01)
                                    + " dies for hunger.");
                        } else {
                            if (sw.food != sw.demand)
                                sw.reprorate = 0.02*(1 + (sw.food - sw.demand)/sw.demand);
                        }
                    } finally {
                        sw.numberl.unlock();
                    }

                    System.out.println("Remain sperm whales:" + sw.number);
                    spermWhalePrint.println("Remain sperm whales:" + sw.number);
                    timeHelper += 90;
                    spermWhalePrint.println("====================================================================================================");
                    /*************************************** Season Complete ******************************************/
                }
                spermWhalePrint.close();
            }
        };

        MarineMammalThread mmthr = new MarineMammalThread("Marine Mammals Thread") {
            @Override public void run() {
                double timeHelper = 0.0;

                FileOutputStream marineMammalLog = null;
                try {
                    marineMammalLog = new FileOutputStream("marineMammalLog.txt");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                PrintStream marineMammalPrint = new PrintStream(marineMammalLog);

                while(timeHelper < timeLimit) {
                    double now = 0.0;
                    int eatcounter = 1;
                    int deathcounter = 0;
                    Engine mmengine = new Engine();
                    // Food resource consume

                    Event e = new MarineMammalsEat(0.0);
                    mmengine.eventList.add(e);

                    try{
                        startBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(threadName + "Season: " + (int)(timeHelper/90) + " begins");
                    marineMammalPrint.println(threadName + "Season: " + (int)(timeHelper/90) + " begins");
                    /**************************************** Season Begins *******************************************/
                    mm.food = 0.0;
                    mm.demand = mm.number*70;
                    while (!mmengine.eventList.isEmpty()) {
                        double temp = now;

                        mmengine.eventHandler(mp, kw, sw, mm);

                        if (now < 90) {
                            now = Math.random()*2 + temp;
                            Event eatTemp = new MarineMammalsEat(now);
                            ++eatcounter;
                            if (mm.food < mm.demand*1.05)
                                mmengine.eventList.add(eatTemp);
                        }

                        if (Math.random() > 0.95 && now < 90) {
                            now = Math.random()*2 +  temp;
                            Event deathTemp = new MarineMammalsDeath(now);
                            ++deathcounter;
                            mmengine.eventList.add(deathTemp);
                        }

                    }
                    /**************************************** Season Ends *********************************************/
                    try{
                        endBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(threadName + "Season: " + (int)(timeHelper/90) + " ends");
                    marineMammalPrint.println(threadName + "Season: " + (int)(timeHelper/90) + " ends");
                    /*************************************** Season Checkout ******************************************/
                    // Calculate natural death and reproduce
                    marineMammalPrint.println(threadName + " eat: " + eatcounter);
                    marineMammalPrint.println(threadName + " accidental death: " + deathcounter);
                    mm.numberl.lock();
                    try {
                        int temp = mm.number;
                        mm.number = mm.number  +  (int) (temp*mm.reprorate);
                        mm.number = mm.number - (int) (temp*mm.deathrate);
                        System.out.println(mm.name + ": " +(int)(temp*mm.deathrate) + " dies, " + (int)(temp*mm.reprorate)
                                + " reproduces.");
                        marineMammalPrint.println(mm.name + ": " +(int)(temp*mm.deathrate) + " dies, "
                                + (int)(temp*mm.reprorate) + " reproduces.");

                    } finally {
                        mm.numberl.unlock();
                    }
                    marineMammalPrint.println("Marine Whales total demands: " + mm.demand);
                    marineMammalPrint.println("Marine Mammals total consumes: " + mm.food);
                    // Calculate death for hunger
                    mm.numberl.lock();
                    try {
                        if (mm.food < mm.demand) {
                            if (mm.food != mm.demand)
                                mm.reprorate = 0.03*((mm.demand - mm.food)/sw.demand);;
                            mm.number -= (int) ((mm.demand - mm.food) * 0.01);
                            if (mm.number <= 0) mm.number = 0;
                            System.out.println(mm.name + ": " + (int) ((mm.demand - mm.food) * 0.01) + " dies for hunger.");
                            marineMammalPrint.println(mm.name + ": "
                                    + (int) ((mm.demand - mm.food) * 0.01) + " dies for hunger.");
                        } else {
                            if (mm.food != mm.demand)
                                mm.reprorate = 0.03*(1 + (mm.food - mm.demand)/sw.demand);
                        }
                    } finally {
                        mm.numberl.unlock();
                    }
                    marineMammalPrint.println("Remain Marine Mammals:" + mm.number);
                    timeHelper += 90;
                    marineMammalPrint.println("====================================================================================================");
                    /*************************************** Season Complete ******************************************/
                }
                marineMammalPrint.close();
            }
        };

        mpthr.start();
        kwthr.start();
        swthr.start();
        mmthr.start();

        //System.out.println("Simulation ends");

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



