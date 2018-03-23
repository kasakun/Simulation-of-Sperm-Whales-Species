/*
 * Created by Zeyu Chen 03/10/2018
 *
 * Main class
 */



public class Sim {
    public static void main(String[] args) {

        // Initialize
        MainProc mp = new MainProc(0, 85, 100, 1000);
        KillerWhales kw = new KillerWhales(1000, 200, 0.3, 0.2);
        SpermWhales sw = new SpermWhales(1000, 200, 0.2, 0.1);
        MarineMammals mm = new MarineMammals(2000, 200, 0.4, 0.1);



        MainProcThread mpthr = new MainProcThread("Main Process Thread") {
            @Override public void run() {
                System.out.println("Thread:" + threadName + " ID: " + Thread.currentThread().getId() + " starts.");
            }
        };

        KillerWhalesThread kwthr = new KillerWhalesThread("Killer Whales Thread") {
            @Override public void run() {
                System.out.println("Thread:" + threadName + " ID: " + Thread.currentThread().getId() + " starts.");
                Engine kwengine = new Engine();
                Event e = new KillerWhalesHunt();

                kwengine.eventList.add(e);
                while (!kwengine.eventList.isEmpty()) {
                    kwengine.eventHandler(mp, kw, sw, mm);
                    //kwengine.schedule(e);
                }
            }
        };
        // EXAMPLE HERE
        SpermWhalesThread swthr = new SpermWhalesThread("Sperm Whales Thread") {
            @Override public void run() {
                System.out.println("Thread:" + threadName + " ID: " + Thread.currentThread().getId() + " starts.");
                Engine swengine = new Engine();

                Event e = new SpermWhalesEat();

                swengine.eventList.add(e);
                while (!swengine.eventList.isEmpty()) {
                    swengine.eventHandler(mp, kw, sw, mm);
                    //kwengine.schedule(e);
                }

            }
        };

        MarineMammalThread mmthr = new MarineMammalThread("Marine Mammals Thread") {
            @Override public void run() {
                System.out.println("Thread:" + threadName + " ID: " + Thread.currentThread().getId() + " starts.");
                Engine mmengine = new Engine();
                // Food resource consume

                Event e = new MarineMammalsEat();
                mmengine.eventList.add(e);
                while (!mmengine.eventList.isEmpty()) {
                    mmengine.eventHandler(mp, kw, sw, mm);
                    //kwengine.schedule(e);
                }
            }
        };

        mpthr.start();
        kwthr.start();
        mmthr.start();
        swthr.start();


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



