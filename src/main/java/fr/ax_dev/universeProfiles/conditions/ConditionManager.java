package fr.ax_dev.universeProfiles.conditions;

import fr.ax_dev.universeProfiles.UniverseProfiles;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ConditionManager {

    private final UniverseProfiles plugin;

    public ConditionManager(UniverseProfiles plugin) {
        this.plugin = plugin;
    }

    public boolean checkConditions(ConfigurationSection conditions, Player viewer, Player target) {
        if (conditions == null) return true;

        for (String key : conditions.getKeys(false)) {
            ConfigurationSection condition = conditions.getConfigurationSection(key);
            if (condition == null) continue;

            if (!evaluateCondition(condition, viewer, target)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateCondition(ConfigurationSection condition, Player viewer, Player target) {
        String type = condition.getString("type", "placeholder");
        String checkTarget = condition.getString("target", "viewer");
        Player playerToCheck = "target".equalsIgnoreCase(checkTarget) ? target : viewer;

        if (playerToCheck == null) return false;

        return switch (type.toLowerCase()) {
            case "permission" -> checkPermission(condition, playerToCheck);
            case "placeholder" -> checkPlaceholder(condition, playerToCheck, viewer, target);
            default -> true;
        };
    }

    private boolean checkPermission(ConfigurationSection condition, Player player) {
        String permission = condition.getString("permission", "");
        if (permission.isEmpty()) return true;

        boolean negate = condition.getBoolean("negate", false);
        boolean hasPermission = player.hasPermission(permission);

        return negate != hasPermission;
    }

    private boolean checkPlaceholder(ConfigurationSection condition, Player player, Player viewer, Player target) {
        String placeholder = condition.getString("placeholder", "");
        if (placeholder.isEmpty()) return true;

        String operation = condition.getString("operation", "equals");
        String value = condition.getString("value", "");

        placeholder = processPlaceholders(placeholder, viewer, target);
        value = processPlaceholders(value, viewer, target);

        String actualValue = "";
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            actualValue = PlaceholderAPI.setPlaceholders(player, placeholder);
        } else {
            actualValue = placeholder;
        }

        return switch (operation.toLowerCase()) {
            case "equals" -> actualValue.equals(value);
            case "not_equals", "!=" -> !actualValue.equals(value);
            case "contains" -> actualValue.contains(value);
            case "not_contains" -> !actualValue.contains(value);
            case "starts_with" -> actualValue.startsWith(value);
            case "ends_with" -> actualValue.endsWith(value);
            case "greater_than", ">" -> compareNumeric(actualValue, value) > 0;
            case "less_than", "<" -> compareNumeric(actualValue, value) < 0;
            case "greater_or_equal", ">=" -> compareNumeric(actualValue, value) >= 0;
            case "less_or_equal", "<=" -> compareNumeric(actualValue, value) <= 0;
            default -> actualValue.equals(value);
        };
    }

    private String processPlaceholders(String text, Player viewer, Player target) {
        if (viewer != null) {
            text = text.replace("%viewer_name%", viewer.getName());
        }
        if (target != null) {
            text = text.replace("%player_name%", target.getName());
        }
        return text;
    }

    private int compareNumeric(String a, String b) {
        try {
            double numA = Double.parseDouble(a);
            double numB = Double.parseDouble(b);
            return Double.compare(numA, numB);
        } catch (NumberFormatException e) {
            return a.compareTo(b);
        }
    }
}
