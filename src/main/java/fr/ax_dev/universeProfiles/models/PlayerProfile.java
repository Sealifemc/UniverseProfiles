package fr.ax_dev.universeProfiles.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerProfile {

    private final UUID uuid;
    private String playerName;
    private String bio;
    private boolean isPublic;
    private int stars;
    private Set<UUID> starGivers;
    private ItemStack[] inventory;
    private ItemStack[] armorContents;
    private ItemStack mainHand;
    private ItemStack offHand;
    private long lastSeen;

    public PlayerProfile(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.bio = "";
        this.isPublic = true;
        this.stars = 0;
        this.starGivers = new HashSet<>();
        this.lastSeen = System.currentTimeMillis();
    }

    public void updateFromPlayer(Player player) {
        this.playerName = player.getName();
        this.inventory = player.getInventory().getContents().clone();
        this.armorContents = player.getInventory().getArmorContents().clone();
        this.mainHand = player.getInventory().getItemInMainHand();
        this.offHand = player.getInventory().getItemInOffHand();
        this.lastSeen = System.currentTimeMillis();
    }

    public boolean canReceiveStarFrom(UUID giver) {
        return !starGivers.contains(giver);
    }

    public void addStar(UUID giver) {
        if (canReceiveStarFrom(giver)) {
            starGivers.add(giver);
            stars++;
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getBio() {
        return bio != null ? bio : "";
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public Set<UUID> getStarGivers() {
        return new HashSet<>(starGivers);
    }

    public void setStarGivers(Set<UUID> starGivers) {
        this.starGivers = new HashSet<>(starGivers);
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public void setArmorContents(ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }

    public ItemStack getMainHand() {
        return mainHand;
    }

    public void setMainHand(ItemStack mainHand) {
        this.mainHand = mainHand;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public void setOffHand(ItemStack offHand) {
        this.offHand = offHand;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}