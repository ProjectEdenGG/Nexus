package me.pugabyte.nexus.features.events.y2021.bearfair21.quests;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.Quests;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.TrashLoot;
import me.pugabyte.nexus.features.particles.ParticleUtils;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class Recycler implements Listener {
	private static final String title = "&aRecycler";
	private static final Location composterLoc = new Location(BearFair21.getWorld(), 14, 138, -146);
	private static final Location soundLoc = new Location(BearFair21.getWorld(), 12, 136, -145);
	private static boolean active = false;

	public Recycler() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onClickRecycler(PlayerInteractEvent event) {
		String[] lines = Quests.getMenuBlockLines(event);
		if (lines == null)
			return;

		if (!StringUtils.stripColor(lines[0]).equals("[Recycler]"))
			return;

		if (active) {
			PlayerUtils.send(event.getPlayer(), Errors.inUse);
			return;
		}

		event.setCancelled(true);
		openRecycler(event.getPlayer());
	}

	private void openRecycler(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27, colorize(title));
		player.openInventory(inv);
	}

	@EventHandler
	public void onSellCrateClose(InventoryCloseEvent event) {
		String _title = stripColor(event.getView().getTitle());
		if (!_title.contains(stripColor(title)))
			return;

		Player player = (Player) event.getPlayer();

		List<ItemStack> trash = new ArrayList<>();
		List<ItemStack> giveBack = new ArrayList<>();
		for (ItemStack itemStack : event.getInventory().getContents()) {
			if (!ItemUtils.isNullOrAir(itemStack)) {
				if (FishingLoot.isTrash(player, itemStack))
					trash.add(itemStack);
				else
					giveBack.add(itemStack);
			}
		}

		if (!giveBack.isEmpty())
			PlayerUtils.giveItems(player, giveBack);

		if (trash.isEmpty())
			return;

		if (active) {
			PlayerUtils.send(player, Errors.inUse);
			PlayerUtils.giveItems(player, trash);
			return;
		}

		recycle(player, trash);
	}

	private void recycle(Player player, List<ItemStack> trash) {
		int count = getCount(trash);
		active = true;
		AtomicInteger wait = new AtomicInteger(0);
		Block composter = composterLoc.getBlock();
		Levelled levelled = (Levelled) composter.getBlockData();

		Block above = composterLoc.getBlock().getRelative(BlockFace.UP);
		Openable openable = (Openable) above.getBlockData();

		// Increase level of composter
		int maxLevel = levelled.getMaximumLevel() - 2;
		for (int i = 0; i <= maxLevel; i++) {
			int finalI = i;
			Tasks.wait(wait.addAndGet(5), () -> {
				levelled.setLevel(finalI);
				composter.setBlockData(levelled);
				SoundUtils.playSound(composterLoc, Sound.BLOCK_COMPOSTER_FILL);
			});
		}

		// Close trapdoor
		Tasks.wait(wait.addAndGet(10), () -> {
			openable.setOpen(false);
			above.setBlockData(openable);
			SoundUtils.playSound(composterLoc, Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE);

		});

		// Play piston sounds
		Tasks.wait(wait.addAndGet(10), () -> soundLoc.getBlock().setType(Material.REDSTONE_BLOCK));
		wait.addAndGet(40);

		// Play composter sounds and effects
		for (int i = 0; i < count; i++) {
			Tasks.wait(wait.addAndGet(5), () -> {
				SoundUtils.playSound(composterLoc, Sound.BLOCK_COMPOSTER_READY);
				ParticleUtils.display(Particle.VILLAGER_HAPPY, LocationUtils.getCenteredLocation(above.getLocation()), 15, 0.5, 0.5, 0.5, 0.1);
			});
		}

		wait.addAndGet(10);

		// Play done sound
		Tasks.wait(wait.addAndGet(10), () -> {
			SoundUtils.playSound(composterLoc, Sound.BLOCK_NOTE_BLOCK_BELL);
			levelled.setLevel(0);
			composter.setBlockData(levelled);
		});

		wait.addAndGet(10);

		// Open trapdoor
		Tasks.wait(wait.addAndGet(10), () -> {
			openable.setOpen(true);
			above.setBlockData(openable);
			SoundUtils.playSound(composterLoc, Sound.BLOCK_WOODEN_TRAPDOOR_OPEN);
		});

		Tasks.wait(wait.addAndGet(10), () -> {
			List<ItemStack> rewards = new ArrayList<>();
			for (ItemStack itemStack : trash) {
				List<ItemStack> trashLoot = TrashLoot.from(player, itemStack);
				if (trashLoot != null && !trashLoot.isEmpty())
					rewards.addAll(trashLoot);
			}

			PlayerUtils.giveItems(player, rewards);

			active = false;
		});
	}

	private int getCount(List<ItemStack> items) {
		int result = 0;
		for (ItemStack item : items)
			result += item.getAmount();

		if (result > 128)
			result = 128;

		return result;

	}

}
