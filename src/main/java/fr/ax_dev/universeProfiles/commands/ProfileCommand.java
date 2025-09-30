package fr.ax_dev.universeProfiles.commands;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import fr.ax_dev.universeProfiles.gui.ProfileGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class ProfileCommand implements CommandExecutor, TabCompleter {

    private final UniverseProfiles plugin;

    public ProfileCommand(UniverseProfiles plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (args.length == 0) {
            openProfile(player, player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "public" -> {
                if (!player.hasPermission("uprofiles.public")) {
                    plugin.getLanguageManager().sendMessage(player, "messages.no_permission");
                    return true;
                }
                plugin.getProfileManager().setPrivacy(player.getUniqueId(), true);
                plugin.getLanguageManager().sendMessage(player, "messages.profile_now_public");
            }
            case "private" -> {
                if (!player.hasPermission("uprofiles.private")) {
                    plugin.getLanguageManager().sendMessage(player, "messages.no_permission");
                    return true;
                }
                plugin.getProfileManager().setPrivacy(player.getUniqueId(), false);
                plugin.getLanguageManager().sendMessage(player, "messages.profile_now_private");
            }
            case "reload" -> {
                if (!player.hasPermission("uprofiles.reload")) {
                    plugin.getLanguageManager().sendMessage(player, "messages.no_permission");
                    return true;
                }
                try {
                    plugin.reload();
                    plugin.getLanguageManager().sendMessage(player, "messages.reload_success");
                } catch (Exception e) {
                    player.sendMessage("§c[UniverseProfiles] Erreur lors du rechargement: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "update-gui" -> {
                if (!player.hasPermission("uprofiles.admin")) {
                    plugin.getLanguageManager().sendMessage(player, "messages.no_permission");
                    return true;
                }
                plugin.getGuiConfigManager().updateFromResources();
                player.sendMessage("§a[UniverseProfiles] gui.yml a été mis à jour depuis les ressources du plugin");
            }
            case "bio" -> {
                if (args.length < 2) {
                    plugin.getLanguageManager().sendMessage(player, "commands.usage.bio");
                    return true;
                }
                String bio = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                int maxLength = plugin.getConfigManager().getInt("settings.bio.max_length", 200);
                if (bio.length() > maxLength) {
                    bio = bio.substring(0, maxLength);
                }
                String finalBio = bio;
                plugin.getProfileManager().getProfile(player.getUniqueId()).thenAccept(profile -> {
                    profile.setBio(finalBio);
                    plugin.getProfileManager().saveProfile(profile);
                    plugin.getLanguageManager().sendMessage(player, "messages.bio_updated");
                });
            }
            case "clearbio" -> {
                plugin.getProfileManager().getProfile(player.getUniqueId()).thenAccept(profile -> {
                    profile.setBio("");
                    plugin.getProfileManager().saveProfile(profile);
                    plugin.getLanguageManager().sendMessage(player, "messages.bio_cleared");
                });
            }
            case "admin" -> {
                if (!player.hasPermission("uprofiles.admin")) {
                    plugin.getLanguageManager().sendMessage(player, "messages.no_permission");
                    return true;
                }
                handleAdminCommand(player, Arrays.copyOfRange(args, 1, args.length));
            }
            default -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
                    Map<String, String> placeholders = Map.of("%player%", args[0]);
                    plugin.getLanguageManager().sendMessage(player, "messages.player_not_found", placeholders);
                    return true;
                }

                if (!plugin.getProfileManager().canViewProfile(player, target.getUniqueId())) {
                    plugin.getLanguageManager().sendMessage(player, "messages.profile_private");
                    return true;
                }

                openProfile(player, target);
            }
        }

        return true;
    }

    private void handleAdminCommand(Player player, String[] args) {
        if (args.length == 0) {
            plugin.getLanguageManager().sendMessage(player, "commands.usage.admin");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "open" -> {
                if (args.length < 2) {
                    plugin.getLanguageManager().sendMessage(player, "commands.usage.admin");
                    return;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
                    Map<String, String> placeholders = Map.of("%player%", args[1]);
                    plugin.getLanguageManager().sendMessage(player, "messages.player_not_found", placeholders);
                    return;
                }

                Player viewer = player;
                if (args.length >= 3) {
                    Player targetViewer = Bukkit.getPlayer(args[2]);
                    if (targetViewer == null || !targetViewer.isOnline()) {
                        Map<String, String> placeholders = Map.of("%player%", args[2]);
                        plugin.getLanguageManager().sendMessage(player, "messages.player_not_found", placeholders);
                        return;
                    }
                    viewer = targetViewer;
                }

                openProfile(viewer, target);
            }
            case "forcepublic" -> {
                if (args.length < 2) {
                    plugin.getLanguageManager().sendMessage(player, "commands.usage.admin");
                    return;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
                    Map<String, String> placeholders = Map.of("%player%", args[1]);
                    plugin.getLanguageManager().sendMessage(player, "messages.player_not_found", placeholders);
                    return;
                }

                plugin.getProfileManager().setPrivacy(target.getUniqueId(), true);
                Map<String, String> placeholders = Map.of("%player%", target.getName());
                plugin.getLanguageManager().sendMessage(player, "messages.profile_now_public", placeholders);
            }
            case "forceprivate" -> {
                if (args.length < 2) {
                    plugin.getLanguageManager().sendMessage(player, "commands.usage.admin");
                    return;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
                    Map<String, String> placeholders = Map.of("%player%", args[1]);
                    plugin.getLanguageManager().sendMessage(player, "messages.player_not_found", placeholders);
                    return;
                }

                plugin.getProfileManager().setPrivacy(target.getUniqueId(), false);
                Map<String, String> placeholders = Map.of("%player%", target.getName());
                plugin.getLanguageManager().sendMessage(player, "messages.profile_now_private", placeholders);
            }
            case "give" -> {
                if (args.length < 4 || !args[1].equalsIgnoreCase("star")) {
                    plugin.getLanguageManager().sendMessage(player, "commands.usage.admin");
                    return;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
                    Map<String, String> placeholders = Map.of("%player%", args[2]);
                    plugin.getLanguageManager().sendMessage(player, "messages.player_not_found", placeholders);
                    return;
                }

                try {
                    int amount = Integer.parseInt(args[3]);
                    plugin.getProfileManager().addStars(target.getUniqueId(), amount);
                    Map<String, String> placeholders = Map.of(
                            "%player%", target.getName(),
                            "%amount%", String.valueOf(amount)
                    );
                    plugin.getLanguageManager().sendMessage(player, "messages.star_given", placeholders);
                } catch (NumberFormatException e) {
                    plugin.getLanguageManager().sendMessage(player, "commands.usage.admin");
                }
            }
            default -> plugin.getLanguageManager().sendMessage(player, "commands.usage.admin");
        }
    }

    private void openProfile(Player viewer, OfflinePlayer target) {
        plugin.getProfileManager().getProfile(target.getUniqueId()).thenAccept(profile -> {
            fr.ax_dev.universeProfiles.util.SchedulerUtil.runTask(plugin, () -> {
                ProfileGUI gui = new ProfileGUI(plugin, viewer, profile);
                gui.open();
            });
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("public");
            completions.add("private");
            completions.add("bio");
            completions.add("clearbio");
            if (player.hasPermission("uprofiles.reload")) {
                completions.add("reload");
            }
            if (player.hasPermission("uprofiles.admin")) {
                completions.add("admin");
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            if (player.hasPermission("uprofiles.admin")) {
                completions.addAll(Arrays.asList("open", "forcepublic", "forceprivate", "give"));
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("admin")) {
            if (args[1].equalsIgnoreCase("give")) {
                completions.add("star");
            } else {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    completions.add(onlinePlayer.getName());
                }
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("open")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("give") && args[2].equalsIgnoreCase("star")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        } else if (args.length == 5 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("give") && args[2].equalsIgnoreCase("star")) {
            completions.addAll(Arrays.asList("1", "5", "10"));
        }

        return completions.stream()
                .filter(completion -> completion.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .sorted()
                .toList();
    }
}