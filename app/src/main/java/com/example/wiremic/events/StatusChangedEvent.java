package com.example.wiremic.events;

public class StatusChangedEvent extends Event {

    private boolean Status;
    public StatusChangedEvent(boolean status)
    {
        Status = status;
    }
    @Override
    public String getName() {
        return "StatusChangedEvent";
    }

    public boolean getStatus()
    {
        return Status;
    }
}
