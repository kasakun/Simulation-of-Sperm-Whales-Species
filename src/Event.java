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
    KillerWhalesHunt() {
        name = "Killer Whales Hunt";
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        kw.killSpermWhales(sw);
        kw.killMarineMammals(mm);
    }
}


// Sperm Whales
class SpermWhalesEat extends Event{
    SpermWhalesEat() {
        name = "Sperm Whales Eat";
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        sw.eat(mp);
    }
}


// Marine Mammals
class MarineMammalsEat extends Event{
    MarineMammalsEat() {
        name = "Marine Mammals Eat";
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        mm.eat(mp);
    }
}
