package me.pugabyte.bncore.features.holidays.halloween20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShootingRange implements Listener {
    //WorldEditUtils WEUtils = new WorldEditUtils(Halloween20.getWorld());
    private static WorldGuardUtils WGUtils = new WorldGuardUtils(Halloween20.getWorld());
    private static String gameRg = Halloween20.getRegion() + "_archery";
    //private static String targetsRg = gameRg + "_targets";
    private static String itemLore = "Halloween20";
    private static int targetSwitchTimeTicks = 60;
    private static int lastUpdateTicks = -1;
    private static Location lastUsedTarget = null;

    private static HashMap<Location, String> targets = new HashMap<>();

    static {

        targets.put(new Location(Halloween20.getWorld(), 209,13,-1844), "minecraft:skeleton_skull[rotation=14]");
        targets.put(new Location(Halloween20.getWorld(), 208,14,-1842), "minecraft:zombie_head[rotation=0]");
        targets.put(new Location(Halloween20.getWorld(), 206,13,-1845), "minecraft:skeleton_skull[rotation=2]");

    }

    public ShootingRange() {
        BNCore.registerListener(this);
        targetTask();
    }

    private void targetTask() {
        Tasks.repeat(0, 5, () -> {
            if(lastUpdateTicks==-1||lastUpdateTicks+targetSwitchTimeTicks < Bukkit.getServer().getCurrentTick()){
                Location nextTarget = getRandomTarget();
                placeTarget(nextTarget);
                lastUpdateTicks = Bukkit.getServer().getCurrentTick();
                lastUsedTarget = nextTarget;
            }
        });
    }

    @EventHandler
    public void onRegionEnter(RegionEnteredEvent event) { //give bow
        if (!event.getRegion().getId().equalsIgnoreCase(gameRg)) return;
        Utils.vroom("You receive a bow.");
        giveItem(event.getPlayer(), Item.BOW);
        giveItem(event.getPlayer(), Item.ARROW);
    }

    @EventHandler
    public void onRegionExit(RegionLeftEvent event) { //take bow and announce points
        if (!event.getRegion().getId().equalsIgnoreCase(gameRg)) return;
        Utils.vroom("Your bow has been removed.");
        removeItems(event.getPlayer());
    }

    @EventHandler
    public void onLogoutInRegion(PlayerQuitEvent event){
        //Remove item maybe?
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
        clearTarget(block.getLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F);
        lastUpdateTicks = -1;

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

    private void clearTargets(){ targets.keySet().forEach((location) -> {clearTarget(location);}); }

    private Location getRandomTarget(){
        ArrayList<Location> validLocations = new ArrayList<Location>(targets.keySet());
        if(lastUsedTarget!=null)validLocations.remove(lastUsedTarget);
        return validLocations.get(new Random().nextInt()%validLocations.size());
    }

    private boolean isInRegion(Player player){
        return player.getLocation().getWorld().equals(Halloween20.getWorld()) && WGUtils.isInRegion(player.getLocation(), gameRg);
    }

    private boolean isHalloweenItem(ItemStack item){
        return (item != null && item.getLore() != null && item.getLore().get(0).contains(itemLore));
    }

    enum Item {

        BOW(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_INFINITE).lore(itemLore).unbreakable().build()), ARROW(new ItemBuilder(Material.ARROW).lore(itemLore).build());

        private ItemStack is;
        Item(ItemStack is){ this.is = is; }
        ItemStack getItem(){ return is; }

    }

}
