package com.example.wiremic;

import android.os.Build;

import com.example.wiremic.events.Event;
import com.example.wiremic.events.StatusChangedEvent;
import com.example.wiremic.utils.Utils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Store implements IModel {

    private boolean Status;
    private String Ip;
    private ArrayList<IListener<? extends Event>> listeners = new ArrayList<IListener<? extends Event>>();

    @Override
    public void setStatus(boolean status) {
        Status = status;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            for(IListener<StatusChangedEvent> listener :
                    Utils.<IListener<StatusChangedEvent>>filterObjectTypes(
                            listeners,
                            IListener.class,
                            StatusChangedEvent.class)
            )
            {
                listener.onEvent(new StatusChangedEvent(status));
            }
        }
    }

    @Override
    public String getIp() {
        return Ip;
    }

    @Override
    public boolean getStatus() {
        return Status;
    }

    @Override
    public void setIp(String ip) {
        Ip = ip;
    }

    @Override
    public <T extends Event> void addListener(IListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public <T extends Event> void removeListener(IListener<T> listener) {
        listeners.remove(listener);
    }

}
