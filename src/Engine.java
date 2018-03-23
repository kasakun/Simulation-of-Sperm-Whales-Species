/*
 * Created by Zeyu Chen 03/23/2018
 *
 * Weihua Zhu
 *
 */

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class Engine {
    public int count = 0;

    public Queue<Event> eventList = new PriorityQueue<>(500, OrderIsdn);

    // Order
    public static Comparator<Event> OrderIsdn =  new Comparator<Event>(){
        public int compare(Event o1, Event o2) {
            // TODO Auto-generated method stub
            double numbera = o1.timestamp;
            double numberb = o2.timestamp;
            if(numberb < numbera)
                return 1;
            else if(numberb > numbera)
                return -1;
            else
                return 0;
        }
    };

    public void eventHandler(MainProc mp, KillerWhales kw, SpermWhales sw, MarineMammals mm) {
        Event e = eventList.poll();
        e.run(mp, kw, sw, mm);
    }

    public void schedule(Event e){
        eventList.add(e);
    }

}
