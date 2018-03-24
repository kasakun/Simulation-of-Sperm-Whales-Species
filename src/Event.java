/*
 * Created by Zeyu Chen 03/23/2018
 *
 */


public class Event {
    String name;
    public double timestamp;

    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
    }
}


// Main Process


// Killer Whales
class KillerWhalesHunt extends Event{
    KillerWhalesHunt(double time) {
        name = "Killer Whales Hunt";
        timestamp = time;
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        kw.killSpermWhales(sw);
        kw.killMarineMammals(mm);
    }
}


// Sperm Whales
class SpermWhalesEat extends Event{
    SpermWhalesEat(double time) {
        name = "Sperm Whales Eat";
        timestamp = time;
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        sw.eat(mp);
    }
}


// Marine Mammals
class MarineMammalsEat extends Event{
    MarineMammalsEat(double time) {
        name = "Marine Mammals Eat";
        timestamp = time;
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        mm.eat(mp);
    }
}
