package me.spec.eris.event.client;

import me.spec.eris.event.Event;
import me.spec.eris.utils.Helper;

public class EventClientTick extends Event {

    @Override
    public void call() {
        Helper.onTick();
    }
}
