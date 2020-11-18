package me.pugabyte.bncore.features.events.y2020.pugmas20;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.task.Task;
import me.pugabyte.bncore.models.task.TaskService;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.SerializationUtils.JSON;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.bncore.features.events.y2020.pugmas20.Pugmas20.isAtPugmas;
import static me.pugabyte.bncore.features.events.y2020.pugmas20.Pugmas20.pugmasItem;
import static me.pugabyte.bncore.utils.ItemUtils.isFuzzyMatch;
import static me.pugabyte.bncore.utils.SoundUtils.playSound;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;

public class Ores implements Listener {

	public static String taskId = "pugmas-ore-regen";

	@Getter
	private static final ItemStack minersPickaxe = pugmasItem(Material.IRON_PICKAXE).name("Miner's Pickaxe").build();
	@Getter
	private static final ItemStack minersSieve = pugmasItem(Material.HOPPER).name("Miner's Sieve").build();

	public Ores() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onOreBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!isAtPugmas(player))
			return;

		// TODO PUGMAS Cave region check

		Block block = event.getBlock();
		Material material = block.getType();
		OreType oreType = OreType.ofOre(material);
		if (oreType == null)
			return;

		ItemStack mainHand = player.getInventory().getItemInMainHand();
		if (!isFuzzyMatch(minersPickaxe, mainHand))
			return;

		event.setCancelled(true);
		playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, SoundCategory.BLOCKS);
		player.getInventory().addItem(oreType.getOre());

		scheduleRegen(block);
		block.setType(Material.STONE);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!isAtPugmas(player))
			return;

		// TODO PUGMAS Cave region check

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null)
			return;

		Block block = event.getClickedBlock();
		if (block.getType() != Material.GRAVEL)
			return;

		ItemStack mainHand = player.getInventory().getItemInMainHand().clone();
		Damageable itemMeta = (Damageable) mainHand.getItemMeta();
		itemMeta.setDamage(0);
		mainHand.setItemMeta((ItemMeta) itemMeta);

		if (!minersSieve.equals(mainHand))
			return;

		event.setCancelled(true);

		playSound(player, Sound.ENTITY_HORSE_SADDLE, .5F, .5F);
		playSound(player, Sound.UI_STONECUTTER_TAKE_RESULT, .5F, .5F);
		Tasks.wait(5, () -> {
			playSound(player, Sound.ENTITY_HORSE_SADDLE, .5F, .5F);
			playSound(player, Sound.UI_STONECUTTER_TAKE_RESULT, .5F, .5F);
		});

		player.getInventory().addItem(new ItemStack(Material.FLINT));

		scheduleRegen(block);
		block.setType(Material.LIGHT_GRAY_CONCRETE_POWDER);
	}

	public void scheduleRegen(Block block) {
		new TaskService().save(new Task(taskId, new HashMap<String, Object>() {{
			put("location", JSON.serializeLocation(block.getLocation()));
			put("material", block.getType());
		}}, LocalDateTime.now().plusSeconds(RandomUtils.randomInt(3, 15))));
//		}}, LocalDateTime.now().plusSeconds(RandomUtils.randomInt(3 * 60, 5 * 60))));
	}

	static {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(1), () -> {
			TaskService service = new TaskService();
			service.process(taskId).forEach(task -> {
				Map<String, Object> data = task.getJson();

				Location location = JSON.deserializeLocation((String) data.get("location"));
				Material material = Material.valueOf((String) data.get("material"));

				Tasks.sync(() -> location.getBlock().setType(material));

				service.complete(task);
			});
		});
	}

	public enum OreType {
		LIGHT_ANIMICA(Material.DIAMOND_ORE, Material.DIAMOND),
		NECRITE(Material.EMERALD_ORE, Material.EMERALD),
		ADAMANTITE(Material.REDSTONE_ORE, Material.NETHER_BRICK),
		MITHRIL(Material.LAPIS_ORE, Material.LAPIS_LAZULI),
		IRON_NUGGET(Material.IRON_ORE, Material.IRON_NUGGET),
		COAL(Material.COAL_ORE, Material.CHARCOAL),
		LUMINITE_NUGGET(Material.GOLD_ORE, Material.GOLD_NUGGET);

		@Getter
		private final ItemStack ore;
		@Getter
		private final ItemStack ingot;

		OreType(Material ore, Material ingot) {
			this.ore = pugmasItem(ore).name(camelCase(name() + " Ore")).build();
			this.ingot = pugmasItem(ingot).name(camelCase(name())).build();
		}

		public static OreType ofOre(Material ore) {
			for (OreType oreType : OreType.values())
				if (oreType.getOre().getType() == ore)
					return oreType;
			return null;
		}
	}
}
