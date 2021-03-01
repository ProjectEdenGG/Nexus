package me.pugabyte.nexus.features.wither.models;

import lombok.Data;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.warps.Warps;
import me.pugabyte.nexus.features.wither.BeginningCutscene;
import me.pugabyte.nexus.features.wither.WitherChallenge;
import me.pugabyte.nexus.utils.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public abstract class WitherFight implements Listener {

	public OfflinePlayer host;
	public List<UUID> party;
	public List<UUID> alivePlayers;
	public Wither wither;
	public List<Location> playerPlacedBlocks = new ArrayList<>();

	public abstract WitherChallenge.Difficulty getDifficulty();

	public abstract void spawnWither(Location location);

	public abstract boolean shouldGiveStar();

	public abstract List<ItemStack> getAlternateDrops();

	public void start() {
		alivePlayers = party;
		Nexus.registerListener(this);
		new BeginningCutscene().run().thenAccept(this::spawnWither);
	}

	public void broadcastToParty(String message) {
		for (UUID player : party)
			PlayerUtils.send(player, WitherChallenge.PREFIX + message);
	}

	public void giveItems() {
		Player itemReceiver = host.getPlayer();
		if (shouldGiveStar()) {
			ItemUtils.giveItem(itemReceiver, new ItemStack(Material.NETHER_STAR));
			broadcastToParty("&3Congratulations! You have gotten a wither star for this fight!");
		} else {
			broadcastToParty("&cUnfortunately, you did not get a star this time. You can try a harder difficulty for a higher chance");
			if (getAlternateDrops() != null)
				ItemUtils.giveItems(itemReceiver, getAlternateDrops());
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (WitherChallenge.currentFight == null) return;
		Player player = event.getEntity();
		if (!alivePlayers.contains(player.getUniqueId())) return;
		alivePlayers.remove(player.getUniqueId());
		event.setCancelled(true);
		Warps.spawn(player);
		if (alivePlayers.size() == 0) {
			Chat.broadcastIngame(WitherChallenge.PREFIX + host.getName() + " has lost to the Wither in " +
					getDifficulty().getTitle() + " &3mode");
			Chat.broadcastDiscord("**[Wither]** " + host.getName() + " has lost to the Wither in " +
					StringUtils.camelCase(getDifficulty().name()) + " mode");
			WitherChallenge.reset();
		} else
			WitherChallenge.currentFight.broadcastToParty("&e" + player.getName() + " &chas died and is out of the fight!");
	}

	@EventHandler
	public void onKillWither(EntityDamageByEntityEvent event) {
		if (WitherChallenge.currentFight == null) return;
		if (!(event.getEntity() instanceof Wither)) return;

		Wither wither = (Wither) event.getEntity();

		if (wither != this.wither) return;

		double newHealth = wither.getHealth() - event.getFinalDamage();
		if (newHealth > 0) return;

		Chat.broadcastIngame(WitherChallenge.PREFIX + "&3" + host.getName() + " has successfully beaten the Wither in " +
				getDifficulty().getTitle() + " &3mode");
		Chat.broadcastDiscord("**[Wither]** " + host.getName() + " has successfully beaten the Wither in " +
				StringUtils.camelCase(getDifficulty().name()) + " mode");

		giveItems();
		Tasks.wait(Time.SECOND.x(10), () -> {
			WitherChallenge.currentFight.getAlivePlayers().forEach(uuid -> {
				OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid);
				if (offlinePlayer.getPlayer() != null)
					Warps.spawn(offlinePlayer.getPlayer());
			});
			WitherChallenge.reset();
		});
	}

	public boolean isInRegion(Location location) {
		return new WorldGuardUtils(location.getWorld()).isInRegion(location, "witherarena");
	}

	@EventHandler
	public void onFallingBlockLand(EntityChangeBlockEvent event) {
		if (!(event.getEntity() instanceof FallingBlock)) return;
		if (!isInRegion(event.getEntity().getLocation())) return;
		event.setCancelled(true);
		event.getEntity().remove();
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		if (!isInRegion(event.getBlock().getLocation())) return;
		if (!alivePlayers.contains(event.getPlayer().getUniqueId())) return;
		playerPlacedBlocks.add(event.getBlock().getLocation());
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		if (!isInRegion(event.getBlock().getLocation())) return;
		if (!alivePlayers.contains(event.getPlayer().getUniqueId())) return;
		if (playerPlacedBlocks.contains(event.getBlock().getLocation())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		if (!isInRegion(event.getBlock().getLocation())) return;
		if (playerPlacedBlocks.contains(event.getBlock().getLocation())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onWitherExplodeBlock(EntityChangeBlockEvent event) {
		if (!isInRegion(event.getBlock().getLocation())) return;
		if (playerPlacedBlocks.contains(event.getBlock().getLocation())) return;
		if (event.getTo() != Material.AIR) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onRemoveMapFromFrame(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof ItemFrame)) return;
		if (!isInRegion(event.getEntity().getLocation())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		if (!(event.getEntity() instanceof ItemFrame)) return;
		if (!isInRegion(event.getEntity().getLocation())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onItemFrameBreakByEntity(HangingBreakByEntityEvent event) {
		if (!(event.getEntity() instanceof ItemFrame)) return;
		if (!isInRegion(event.getEntity().getLocation())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent event) {
		if (!isInRegion(event.getEntity().getLocation())) return;
		event.blockList().removeIf(block -> !playerPlacedBlocks.contains(block.getLocation()));
	}

}
