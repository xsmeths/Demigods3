package com.demigodsrpg.demigods.engine.data;

import com.demigodsrpg.demigods.engine.Demigods;
import com.demigodsrpg.demigods.engine.DemigodsPlugin;
import com.demigodsrpg.demigods.engine.battle.Battle;
import com.demigodsrpg.demigods.engine.conversation.Administration;
import com.demigodsrpg.demigods.engine.deity.Deity;
import com.demigodsrpg.demigods.engine.entity.player.DemigodsCharacter;
import com.demigodsrpg.demigods.engine.entity.player.DemigodsPlayer;
import com.demigodsrpg.demigods.engine.entity.player.attribute.Notification;
import com.demigodsrpg.demigods.engine.structure.DemigodsStructureType;
import com.demigodsrpg.demigods.engine.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TaskManager {
    private static final boolean SAVE_ALERT = Configs.getSettingBoolean("saving.console_alert");
    private static final Runnable SYNC, ASYNC, SAVE, FAVOR;

    static {
        SYNC = () -> {
            // Update online players
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Zones.inNoDemigodsZone(player.getLocation())) continue;
                DemigodsPlayer.of(player).updateCanPvp();
            }

            // Update Battle Particles
            Battle.updateBattleParticles();
        };
        ASYNC = () -> {
            // Update Timed Data
            TimedServerData.clearExpired();

            // Update Battles
            Battle.updateBattles();

            // Update Notifications
            Notification.updateNotifications();
        };
        SAVE = () -> {
            // Save time for reference after saving
            long time = System.currentTimeMillis();

            // Save data
            DataManager.saveAllData();

            // Send the save message to the console
            if (SAVE_ALERT)
                Messages.info(Bukkit.getOnlinePlayers().size() + " of " + Demigods.getServer().getAllPlayers().size() +
                        " total players saved in " + Times.getSeconds(time) + " seconds.");
        };
        FAVOR = () -> {
            // Update Favor
            DemigodsCharacter.updateFavor();

            // Update Sanctity
            DemigodsStructureType.Util.updateSanctity();
        };
    }

    public static void startThreads() {
        // Start sync demigods runnable
        Bukkit.getScheduler().scheduleSyncRepeatingTask(DemigodsPlugin.getInst(), SYNC, 20, 20);
        Administration.Util.sendDebug("Main Demigods SYNC runnable enabled...");

        // Start async demigods runnable
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(DemigodsPlugin.getInst(), ASYNC, 20, 20);
        Administration.Util.sendDebug("Main Demigods ASYNC runnable enabled...");

        // Start favor runnable
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(DemigodsPlugin.getInst(), FAVOR, 20,
                (Configs.getSettingInt("regeneration_rates.favor") * 20));
        Administration.Util.sendDebug("Favor regeneration runnable enabled...");

        // Start saving runnable TODO Should we move this?
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(DemigodsPlugin.getInst(), SAVE, 20,
                (Configs.getSettingInt("saving.freq") * 20));

        // Enable Deity runnables
        for (Deity deity : Demigods.getMythos().getDeities()) {
            deity.getAbilities().stream().filter(ability -> ability.getRunnable() != null).forEach(ability ->
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(DemigodsPlugin.getInst(), ability.getRunnable(),
                            ability.getDelay(), ability.getRepeat()));
        }

        // Triggers
        Threads.registerTriggers(DemigodsPlugin.getInst(), Demigods.getMythos().getTriggers());
    }

    public static void stopThreads() {
        DemigodsPlugin.getInst().getServer().getScheduler().cancelTasks(DemigodsPlugin.getInst());
        Threads.stopHooker(DemigodsPlugin.getInst());
    }
}
