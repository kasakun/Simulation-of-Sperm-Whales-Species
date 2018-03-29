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


    public static double timeLimit = 3600;

    public static void main(String[] args) throws FileNotFoundException {

        MainProc mp = new MainProc(0, 85, 100, 100000000);
        KillerWhales kw = new KillerWhales(50000, 200, 0.15, 0.2);
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
                double timeHelper = 0.0;

                FileOutputStream mainProcLog = null;
                try {
                    mainProcLog = new FileOutputStream("mainProcLog.txt");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                PrintStream mainProcPrint = new PrintStream(mainProcLog);

                while(timeHelper < timeLimit) {
                    double now = 0.0;

                    Engine mpengine = new Engine();
                    Event season = new seasonChange(now);
                    mpengine.eventList.add(season);

                    // Start Barrier
                    try{
                        startBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(threadName + "Season: " + timeHelper/90 + "begins");
                    mainProcPrint.println(threadName + "Season: " + timeHelper/90 + "begins");
                    // Season begins
                    /**************************************** Season Begins *******************************************/
                    while (!mpengine.eventList.isEmpty() && now < mpengine.eventList.peek().timestamp) {
                        double temp = now;
                        // Why run all event in the list?
                        mpengine.eventHandler(mp, kw, sw, mm);


                        Event season1 = new seasonChange(temp + 90);
                        Event food = new foodGrow(temp + 90);
                        mpengine.eventList.add(season1);
                        mpengine.eventList.add(food);


                        if (Math.random() > 0.01) {
                           Event disaster = new naturalDisaster(temp + 90);
                           mpengine.eventList.add(disaster);
                        }

                        if (temp % 360 == 0) {
                           Event humanHunt = new humanHunt(temp + 90);
                           mpengine.eventList.add(humanHunt);
                        }

                        if (Math.random() > 0.5) {
                           Event humanFish = new humanFish(temp + 90);
                           mpengine.eventList.add(humanFish);
                        }
                        now = temp + 90;
                    }

                    /**************************************** Season Ends *********************************************/
                    try{
                        endBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(threadName + "Season: " + timeHelper/90 + "ends");
                    mainProcPrint.println(threadName + "Season: " + timeHelper/90 + "ends");
                    /*************************************** Season Checkout ******************************************/
                    timeHelper += 90;
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

                    Engine kwengine = new Engine();
                    Event hunt = new KillerWhalesHunt(now);
                    kwengine.eventList.add(hunt);

                    try{
                        startBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(threadName + "Season: " + timeHelper/90 + "begins");
                    killerWhalePrint.println(threadName + "Season: " + timeHelper/90 + "begins");
                    /**************************************** Season Begins *******************************************/
                    while (!kwengine.eventList.isEmpty()) {
                        double temp = now;

                        kwengine.eventHandler(mp, kw, sw, mm);
                        // Use prob to determine whether to schedule
                        if (Math.random() > 0.1 && now < 30) {
                            now = Math.random()*1 +  temp;
                            Event huntTemp = new KillerWhalesHunt(now);
                            // schedule next event
                            kwengine.eventList.add(huntTemp);
                        }
                        // Use prob to determine whether to schedule
                        if (Math.random() > 0.5 && now < 30) {
                            now = Math.random()*10 +  temp;
                            Event deathTemp = new KillerWhalesDeath(now);
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
                    /*************************************** Season Checkout ******************************************/
                    // Calculate natural death and reproduce
                    kw.numberl.lock();
                    try {
                        int temp = kw.number;
                        kw.number = kw.number  +  (int) (temp*kw.reprorate);
                        kw.number = kw.number - (int) (temp*kw.deathrate);
                        System.out.println(kw.name + ": " +(int)(temp*kw.deathrate) + " dies, " + (int)(temp*kw.deathrate)
                                + " reproduces. " + "Remain killer whales:" + kw.number);

                        killerWhalePrint.println(kw.name + ": " +(int)(temp*kw.deathrate) + " dies, " +
                                (int)(temp*kw.deathrate) + " reproduces. " + "Remain killer whales:" + kw.number);

                    } finally {
                        kw.numberl.unlock();
                    }

                    // Calculate death for hunger
                    kw.numberl.lock();
                    try {
                        if (kw.food < kw.demand) {
                            kw.number -= (int)((kw.demand - kw.food)*2.5);
                            System.out.println(kw.name + ": " + (int)((kw.demand - kw.food)*2.5) + " dies for hunger.");

                            killerWhalePrint.println(kw.name + ": "
                                    + (int)((kw.demand - kw.food)*2.5) + " dies for hunger.");
                        }
                    } finally {
                        kw.numberl.unlock();
                    }


                    timeHelper += 90;
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

                    Engine swengine = new Engine();
                    Event e = new SpermWhalesEat(0.0);
                    swengine.eventList.add(e);

                    try{
                        startBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }

                    /**************************************** Season Begins *******************************************/
                    while (!swengine.eventList.isEmpty()) {
                        double temp =now;
                        swengine.eventHandler(mp, kw, sw, mm);

                        if (Math.random() > 0.5 && now < 90) {
                            now = Math.random()*0.5 + temp;
                            Event deathTemp = new SpermWhalesDeath(now);
                            swengine.eventList.add(deathTemp);
                        }

                        if (Math.random() > 0.1 && now < 90) {
                            now = Math.random()*0.5 + temp;
                            Event eat = new SpermWhalesEat(now);
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
                    /*************************************** Season Checkout ******************************************/
                    // Calculate natural death and reproduce
                    sw.numberl.lock();
                    try {
                        int temp = sw.number;
                        sw.number = sw.number  +  (int) (temp*sw.reprorate);
                        sw.number = sw.number - (int) (temp*sw.deathrate);
                        System.out.println(sw.name + ": " +(int)(temp*sw.deathrate) + " dies, " + (int)(temp*sw.deathrate)
                                + " reproduces. " + "Remain killer whales:" + sw.number);

                        spermWhalePrint.println(sw.name + ": " +(int)(temp*sw.deathrate) + " dies, "
                                + (int)(temp*sw.deathrate) + " reproduces. " + "Remain killer whales:" + sw.number);

                    } finally {
                        sw.numberl.unlock();
                    }

                    // Calculate death for hunger
                    sw.numberl.lock();
                    try {
                        if (sw.food < sw.demand) {
                            sw.number -= (int)((sw.demand - sw.food)*2.5);
                            System.out.println(sw.name + ": " + (int)((sw.demand - sw.food)*2.5) + " dies for hunger.");

                            spermWhalePrint.println(sw.name + ": " + (int)((sw.demand - sw.food)*2.5)
                                    + " dies for hunger.");
                        }
                    } finally {
                        sw.numberl.unlock();
                    }
                    
                    timeHelper += 90;
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
                    Engine mmengine = new Engine();
                    // Food resource consume

                    Event e = new MarineMammalsEat(0.0);
                    mmengine.eventList.add(e);

                    try{
                        startBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println(threadName + "Season: " + timeHelper/90 + "begins");
                    marineMammalPrint.println(threadName + "Season: " + timeHelper/90 + "begins");
                    /**************************************** Season Begins *******************************************/
                    while (!mmengine.eventList.isEmpty()) {
                        double temp = now;

                        mmengine.eventHandler(mp, kw, sw, mm);

                        if (Math.random() > 0.1 && now < 90) {
                            now = Math.random()*0.5 + temp;
                            Event eatTemp = new MarineMammalsEat(now);
                            mmengine.eventList.add(eatTemp);
                        }

                        if (Math.random() > 0.5 && now < 90) {
                            now = Math.random()*0.5 +  temp;
                            Event deathTemp = new MarineMammalsDeath(now);
                            mmengine.eventList.add(deathTemp);
                        }

                    }
                    /**************************************** Season Ends *********************************************/
                    try{
                        endBarrier.await();
                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(threadName + "Season: " + timeHelper/90 + "ends");
                    marineMammalPrint.println(threadName + "Season: " + timeHelper/90 + "ends");
                    /*************************************** Season Checkout ******************************************/
                    // Calculate natural death and reproduce
                    mm.numberl.lock();
                    try {
                        int temp = mm.number;
                        mm.number = mm.number  +  (int) (temp*mm.reprorate);
                        mm.number = mm.number - (int) (temp*mm.deathrate);
                        System.out.println(mm.name + ": " +(int)(temp*mm.deathrate) + " dies, " + (int)(temp*mm.deathrate)
                                + " reproduces. " + "Remain Marine Mammals:" + mm.number);

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

                    timeHelper += 90;
                    /*************************************** Season Complete ******************************************/
                }
                marineMammalPrint.close();
            }
        };

        mpthr.start();
        kwthr.start();
        swthr.start();
        mmthr.start();
        





        System.out.println("Simulation ends");

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



