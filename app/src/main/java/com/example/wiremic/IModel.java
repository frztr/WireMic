package com.example.wiremic;

import com.example.wiremic.events.Event;

public interface IModel {
    void setStatus(boolean status);
    String getIp();
    boolean getStatus();
    void setIp(String ip);
    <T extends Event> void addListener(IListener<T> listener);
    <T extends Event> void removeListener(IListener<T> listener);
}
