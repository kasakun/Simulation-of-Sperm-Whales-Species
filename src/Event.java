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
class humanHunt extends Event {
    humanHunt(double time) {
        name = "Human Hunt";
        timestamp = time;
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        mp.huntWhale(sw);
    }
}


class humanFish extends Event {
    humanFish(double time) {
        name = "Human Fish";
        timestamp = time;
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        mp.fish();
    }
}


class naturalDisaster extends Event {
    naturalDisaster(double time) {
        name = "Natural Disaster";
        timestamp = time;
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        mp.naturalDisaster();
    }
}


class foodGrow extends Event {
    foodGrow(double time) {
        name = "Food Grow";
        timestamp = time;
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        mp.foodGrow();
    }
}


class seasonChange extends Event {
    seasonChange(double time) {
        name = "season Change";
        timestamp = time;
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        if (timestamp % 360 < 90) {
            mp.winterToSpring();
        } else if (timestamp % 360 < 180) {
            mp.springToSummer();
        } else if (timestamp % 360 < 270) {
            mp.summerToFall();
        } else {
            mp.fallToWinter();
        }
    }
}

// Killer Whales
class KillerWhalesHunt extends Event {
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

class KillerWhalesDeath extends Event {
    KillerWhalesDeath(double time) {
        name = "Killer Whales Accidental Death";
        timestamp = time;
    }

    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        kw.accDeath();
    }
}

// Sperm Whales
class SpermWhalesDeath extends Event {
    SpermWhalesDeath(double time){
        name = "Sperm Whales Accidental Death";
        timestamp = time;
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        sw.accDeath();
    }
}

class SpermWhalesEat extends Event {
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
class MarineMammalsEat extends Event {
    MarineMammalsEat(double time) {
        name = "Marine Mammals Eat";
        timestamp = time;
    }
    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        mm.eat(mp);
    }
}

class MarineMammalsDeath extends Event {
    MarineMammalsDeath(double time) {
        name = "Marine Mammals Accidental Death";
        timestamp = time;
    }

    @Override
    public void run(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        mm.accDeath();
    }
}