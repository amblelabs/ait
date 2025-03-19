package dev.drtheo.mcecs.event;

import dev.drtheo.mcecs.MComponent;

public class HelloWorldEvent implements MEvent<HelloWorldEvent> {

    @Override
    public EventUid<HelloWorldEvent> getUid() {
        return null;
    }
}
