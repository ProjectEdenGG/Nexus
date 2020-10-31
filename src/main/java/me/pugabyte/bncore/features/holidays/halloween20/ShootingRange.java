package me.pugabyte.bncore.features.holidays.halloween20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.pugabyte.bncore.features.holidays.halloween20.Halloween20.PREFIX;

public class ShootingRange implements Listener {
    //WorldEditUtils WEUtils = new WorldEditUtils(Halloween20.getWorld());
    private static final WorldGuardUtils WGUtils = new WorldGuardUtils(Halloween20.getWorld());
    @Getter
    private static final String gameRg = Halloween20.getRegion() + "_archery";
    //private static String targetsRg = gameRg + "_targets";
    private static final String itemLore = "Halloween20";
    private static final int targetSwitchTimeTicks = 60;
    private static int lastUpdateTicks = -1;
    private static final HashMap<UUID, Integer> points = new HashMap<>();

    private static final HashMap<Location, String> targets = new HashMap<>();

    public ShootingRange() {
        BNCore.registerListener(this);
        targets.put(new Location(Halloween20.getWorld(), 209, 13, -1844), "minecraft:skeleton_skull[rotation=14]");
        targets.put(new Location(Halloween20.getWorld(), 208, 14, -1842), "minecraft:zombie_head[rotation=0]");
        targets.put(new Location(Halloween20.getWorld(), 206, 13, -1845), "minecraft:skeleton_skull[rotation=2]");
        targetTask();
    }

    private void targetTask() {
        Tasks.repeat(0, 5, () -> {
            if(lastUpdateTicks==-1||lastUpdateTicks+targetSwitchTimeTicks < Bukkit.getServer().getCurrentTick()){
                clearTargets();
                Location nextTarget = getRandomTarget();
                placeTarget(nextTarget);
                lastUpdateTicks = Bukkit.getServer().getCurrentTick();
            }
        });
    }

    @EventHandler
    public void onRegionEnter(RegionEnteredEvent event) {
        if (!event.getRegion().getId().equalsIgnoreCase(gameRg)) return;
        giveItem(event.getPlayer(), Item.BOW);
        giveItem(event.getPlayer(), Item.ARROW);
        Utils.send(event.getPlayer(), PREFIX + "You received a bow. Hit the skull targets to get points!");
    }

    @EventHandler
    public void onRegionExit(RegionLeftEvent event) {
        if (!event.getRegion().getId().equalsIgnoreCase(gameRg)) return;
        removeItems(event.getPlayer());
        Utils.send(event.getPlayer(), PREFIX + "You got a total of &e" + getPoints(event.getPlayer()) + " &3points");
        removePoints(event.getPlayer());
    }

    @EventHandler
    public void onLogoutInRegion(PlayerQuitEvent event){
        //Remove item maybe?
        removePoints(event.getPlayer());
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent event){
        if(isInRegion(event.getPlayer()) && isHalloweenItem(event.getItemDrop().getItemStack())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Arrow)) return;
        Block block = event.getHitBlock();
        if (block == null) return;
        if(!targets.containsKey(block.getLocation())) return;
        if (!(projectile.getShooter() instanceof Player)) return;
        Player player = (Player) projectile.getShooter();

        projectile.remove();
        clearTargets();
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F);
        lastUpdateTicks = -1;
        addPoints(player, 1);
    }

    private void addPoints(Player player, int newPoints){
        UUID uuid = player.getUniqueId();
        if(!points.containsKey(uuid)){
            points.put(uuid, newPoints);
        } else {
            points.put(uuid, points.get(uuid) + newPoints);
        }
    }

    private int getPoints(Player player){
        if(points.containsKey(player.getUniqueId())){
            return points.get(player.getUniqueId());
        } else {
            return 0;
        }
    }

    private void removePoints(Player player){
        if(points.containsKey(player.getUniqueId())) points.remove(player.getUniqueId());
    }

    private void giveItem(Player player, Item item){ player.getInventory().addItem(item.getItem()); }

    private void removeItems(Player player){
        for(ItemStack item:player.getInventory()){
            if(isHalloweenItem(item)){
                player.getInventory().remove(item);
            }
        }
    }

    private void placeTarget(Location location) {
        Block block = location.getBlock();
        String blockDataString = targets.get(location);
        block.setBlockData(Bukkit.createBlockData(blockDataString));
    }

    private void clearTarget(Location location){
        Block block = location.getBlock();
        block.setType(Material.AIR);
    }

    private void clearTargets() {
        targets.keySet().forEach(this::clearTarget);
    }

    private Location getRandomTarget(){
        ArrayList<Location> locations = new ArrayList<Location>(targets.keySet());
        return RandomUtils.randomElement(locations);
    }

    private boolean isInRegion(Player player){
        return player.getLocation().getWorld().equals(Halloween20.getWorld()) && WGUtils.isInRegion(player.getLocation(), gameRg);
    }

    private boolean isHalloweenItem(ItemStack item){
        return (item != null && item.getLore() != null && item.getLore().get(0).contains(itemLore));
    }

    enum Item {

        BOW(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_INFINITE).lore(itemLore).unbreakable().build()),
        ARROW(new ItemBuilder(Material.ARROW).lore(itemLore).build());

        private ItemStack is;

        Item(ItemStack is) {
            this.is = is;
        }

        ItemStack getItem() {
            return is;
        }
    }

}
