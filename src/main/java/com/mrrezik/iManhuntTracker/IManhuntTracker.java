package com.mrrezik.iManhuntTracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

// ИЗМЕНЕНИЕ ЗДЕСЬ: Класс теперь называется IManhuntTracker
public class IManhuntTracker extends JavaPlugin implements CommandExecutor {

    private final Map<UUID, UUID> targets = new HashMap<>();
    private final Map<UUID, Boolean> wasInDifferentWorld = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getCommand("tracker") != null) {
            getCommand("tracker").setExecutor(this);
        }

        startTrackingTask();
        getLogger().info("AdvancedManhuntTracker enabled (Java 8 Compatible Mode)");
    }

    // Остальной код остается без изменений...
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize(getConfig().getString("messages.only-players")));
            return true;
        }

        Player hunter = (Player) sender;

        if (args.length == 0) {
            targets.remove(hunter.getUniqueId());
            wasInDifferentWorld.remove(hunter.getUniqueId());
            hunter.setCompassTarget(hunter.getWorld().getSpawnLocation());
            hunter.sendMessage(colorize(getConfig().getString("messages.target-reset")));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            hunter.sendMessage(colorize(getConfig().getString("messages.target-offline")));
            return true;
        }

        targets.put(hunter.getUniqueId(), target.getUniqueId());
        wasInDifferentWorld.put(hunter.getUniqueId(), false);
        giveTrackerCompass(hunter, target.getName());

        hunter.sendMessage(colorize(getConfig().getString("messages.target-set")
                .replace("%player%", target.getName())));

        return true;
    }

    private void startTrackingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (targets.isEmpty()) return;

                for (Map.Entry<UUID, UUID> entry : targets.entrySet()) {
                    Player hunter = Bukkit.getPlayer(entry.getKey());
                    if (hunter == null || !hunter.isOnline()) continue;

                    Player target = Bukkit.getPlayer(entry.getValue());
                    if (target == null || !target.isOnline()) continue;

                    updateCompassLogic(hunter, target);
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    private void updateCompassLogic(Player hunter, Player target) {
        World hunterWorld = hunter.getWorld();
        World targetWorld = target.getWorld();
        UUID hunterUUID = hunter.getUniqueId();

        boolean isDifferentWorld = !hunterWorld.equals(targetWorld);
        boolean wasDifferent = wasInDifferentWorld.getOrDefault(hunterUUID, false);

        if (isDifferentWorld) {
            hunter.setCompassTarget(hunterWorld.getSpawnLocation());
            if (!wasDifferent) {
                hunter.sendMessage(colorize(getConfig().getString("messages.target-diff-world")));
                wasInDifferentWorld.put(hunterUUID, true);
            }
        } else {
            hunter.setCompassTarget(target.getLocation());
            if (wasDifferent) {
                wasInDifferentWorld.put(hunterUUID, false);
            }
        }
    }

    private void giveTrackerCompass(Player p, String targetName) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        FileConfiguration config = getConfig();

        if (meta != null) {
            String name = config.getString("compass-name", "&cTracker");
            meta.setDisplayName(colorize(name.replace("%player%", targetName)));

            List<String> lore = config.getStringList("compass-lore");
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(colorize(line.replace("%player%", targetName)));
            }
            meta.setLore(coloredLore);

            compass.setItemMeta(meta);
        }
        p.getInventory().addItem(compass);
    }

    private String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}