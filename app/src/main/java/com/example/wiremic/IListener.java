package com.example.wiremic;

import com.example.wiremic.events.Event;

public interface IListener<T extends Event> {
    void onEvent(T event);
}
