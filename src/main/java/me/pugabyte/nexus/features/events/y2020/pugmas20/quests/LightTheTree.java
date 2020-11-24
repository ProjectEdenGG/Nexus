package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Tasks.Countdown;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.WorldEditUtils.Paste;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.function.BiConsumer;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isAtPugmas;

@NoArgsConstructor
public class LightTheTree implements Listener {
	private static final String lighterRg = Pugmas20.getRegion() + "_lighter";
	//
	public static final ItemStack lighter_broken = Pugmas20.questItem(Material.FLINT_AND_STEEL).name("Broken Ceremonial Lighter").build();
	public static final ItemStack lighter = Pugmas20.questItem(Material.FLINT_AND_STEEL).name("Ceremonial Lighter").glow().build();
	public static final ItemStack steel_nugget = Pugmas20.questItem(Material.IRON_NUGGET).name("Steel Nugget").glow().build();

	public static final int timerTicks = Time.MINUTE.x(1);
	public static final int torches = 9;
	public static final int treeTorches = 7;

	@EventHandler
	public void onFindBrokenLighter(PlayerInteractEntityEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		if (!isAtPugmas(player)) return;

		Entity entity = event.getRightClicked();
		if (!entity.getType().equals(EntityType.ITEM_FRAME)) return;
		if (!Pugmas20.WGUtils.isInRegion(entity.getLocation(), lighterRg)) return;

		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);
		if (!user.getLightTreeStage().equals(QuestStage.STEP_ONE)) return;

		event.setCancelled(true);
		ItemUtils.giveItem(player, lighter_broken);
		user.setLightTreeStage(QuestStage.STEP_TWO);
		service.save(user);
	}

	public static void startTimer(Player player) {
		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);

		Countdown timer = Countdown.builder()
				.duration(timerTicks)
				.onStart(() -> {
					user.setLightingTorches(true);
					user.send("Timer started");
				})
				.onSecond(i -> ActionBarUtils.sendActionBar(player, "&e" + i))
				.onComplete(() -> {
					user.send("You ran out of time!");
					// TODO PUGMAS Click to teleport to start
					user.setLightingTorches(false);
					user.setTorchTimerTaskId(-1);
					user.setTorchesLit(0);
				})
				.start();

		user.setTorchTimerTaskId(timer.getTaskId());
	}

	@EventHandler
	public void onLightTorch(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (!isAtPugmas(player)) return;
		if (block == null) return;

		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);
		if (!user.getLightTreeStage().equals(QuestStage.STEPS_DONE)) return;
		if (!lighter.equals(player.getInventory().getItemInMainHand())) return;

		event.setCancelled(true);

		Block placed = block.getType() == Material.NETHERRACK ? block.getRelative(event.getBlockFace()) : block;

		Set<ProtectedRegion> regions = Pugmas20.WGUtils.getRegionsLikeAt("pugmas20_torch_[0-9]+", placed.getLocation());
		if (regions.isEmpty()) return;

		int torch = Integer.parseInt(regions.iterator().next().getId().split("_")[2]);
		user.send("Torch #" + torch);

		if (torch == 1) {
			user.setLightingTorches(true);
			if (user.getTorchTimerTaskId() == -1)
				startTimer(player);
		}

		if (torch > user.getTorchesLit() + 1) {
			user.send("You missed one!");
			return;
		} else if (torch == user.getTorchesLit() + 1) {
			user.send("Found next torch");
			user.setTorchesLit(torch);
			service.save(user);
		}

		fire(player, placed.getLocation());

		if (torch == 9) {
			user.send("Done");
			user.setLightTreeStage(QuestStage.FOUND_ALL);
			Tasks.cancel(user.getTorchTimerTaskId());
			user.setTorchTimerTaskId(-1);
			user.setLightingTorches(false);
			service.save(user);

			animateTreeLightBlocks(player);

			int wait = 0;
			for (int i = 1; i <= treeTorches; i++) {
				Location location = getLocation("treetorch", i);
				Tasks.wait(wait += Time.SECOND.get(), () -> fire(player, location));
			}

			Tasks.wait(wait + 1, () -> {
				user.setLightTreeStage(QuestStage.COMPLETE);
				service.save(user);
			});
		}
	}

	public static Location getLocation(String type, int i) {
		Region region = Pugmas20.WGUtils.getRegion("pugmas20_" + type + "_" + i);
		return Pugmas20.WGUtils.toLocation(region.getMinimumPoint());
	}

	public static void fire(Player player, Location location) {
		Tasks.wait(1, () -> player.sendBlockChange(location, Bukkit.createBlockData(Material.FIRE)));
	}

	public static void air(Player player, Location location) {
		Tasks.wait(1, () -> player.sendBlockChange(location, Bukkit.createBlockData(Material.AIR)));
	}

	static {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND, () -> {
			Pugmas20Service service = new Pugmas20Service();

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!isAtPugmas(player))
					continue;

				Pugmas20User user = service.get(player);

				switch (user.getLightTreeStage()) {
					case STEPS_DONE:
						updateFound(user);
						updateAllTreeTorches(player, LightTheTree::air);
						updateTreeLightBlocksAir(player);
						continue;
					case FOUND_ALL:
						updateFound(user);
						continue;
					case COMPLETE:
						updateAll(player, LightTheTree::fire);
						updateTreeLightBlocks(player);
						continue;
					default:
						updateAll(player, LightTheTree::air);
				}
			}
		});
	}

	private static void updateFound(Pugmas20User user) {
		for (int i = 1; i <= user.getTorchesLit(); i++)
			fire(user.getPlayer(), getLocation("torch", i));
		for (int i = (user.getTorchesLit() + 1); i <= 9; i++)
			air(user.getPlayer(), getLocation("torch", i));
	}

	private static void updateAll(Player player, BiConsumer<Player, Location> method) {
		updateAllTorches(player, method);
		updateAllTreeTorches(player, method);
	}

	private static void updateAllTorches(Player player, BiConsumer<Player, Location> method) {
		for (int i = 1; i <= torches; i++)
			method.accept(player, getLocation("torch", i));
	}

	private static void updateAllTreeTorches(Player player, BiConsumer<Player, Location> method) {
		for (int i = 1; i <= treeTorches; i++)
			method.accept(player, getLocation("treetorch", i));
	}

	private final static Paste treeLightPaster = Pugmas20.WEUtils.paster()
			.at(Pugmas20.location(936, 61, 483))
			.file("pugmas20/tree_light")
			.duration(Time.SECOND.x(7))
			.air(false)
			.computeBlocks();

	public static void updateTreeLightBlocks(Player player) {
		treeLightPaster.buildClientSide(player);
	}

	public static void updateTreeLightBlocksAir(Player player) {
		treeLightPaster.getComputedBlocks().forEach((location, blockData) -> air(player, location));
	}

	public static void animateTreeLightBlocks(Player player) {
		treeLightPaster.buildQueueClientSide(player);
	}

}
