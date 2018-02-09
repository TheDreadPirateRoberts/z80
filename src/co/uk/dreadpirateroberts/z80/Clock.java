package co.uk.dreadpirateroberts.z80;

import java.util.Observable;
import java.util.TimerTask;

public class Clock extends Observable {
    private String state = "high";

    public Clock() {
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setChanged();
                notifyObservers(state);
                if(state.equals("high"))
                    state = "low";
                else
                    state = "high";
                clearChanged();
            }
        }, 1000, 1000);
    }
}
