package com.leon.bugreport.API;

import com.leon.bugreport.BugReportPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class BugReportExpansion {

    public BugReportExpansion() {
    }

    public abstract String identifier();

    public abstract List<String> additionalDetails(Player player);

    public void register() {
        BugReportPlugin.getPlugin().getReportManager().registerExpansion(this);
    }

    public void unregister() {
        BugReportPlugin.getPlugin().getReportManager().unregisterExpansion(this);
    }
}
