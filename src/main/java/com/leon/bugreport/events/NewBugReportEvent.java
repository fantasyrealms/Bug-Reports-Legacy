package com.leon.bugreport.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class NewBugReportEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final String reportDetails;

    public NewBugReportEvent(String reportDetails) {
        this.reportDetails = reportDetails;
    }

    public String getReportDetails() {
        return reportDetails;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}