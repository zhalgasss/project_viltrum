package com.project.viltrum.events;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    private static final EventBus INSTANCE = new EventBus();

    private final List<GameEventListener> listeners = new ArrayList<>();

    private EventBus() {
    }

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public void subscribe(GameEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unsubscribe(GameEventListener listener) {
        listeners.remove(listener);
    }

    public void publish(GameEvent event) {
        List<GameEventListener> snapshot = new ArrayList<>(listeners);

        for (GameEventListener listener : snapshot) {
            listener.onGameEvent(event);
        }
    }
}
