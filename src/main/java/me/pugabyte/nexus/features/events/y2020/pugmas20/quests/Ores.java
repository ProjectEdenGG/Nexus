package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.commands.staff.WorldGuardEditCommand;
import me.pugabyte.nexus.models.task.Task;
import me.pugabyte.nexus.models.task.TaskService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.SerializationUtils.JSON;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isAtPugmas;
import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.item;
import static me.pugabyte.nexus.utils.ItemUtils.isFuzzyMatch;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.SoundUtils.playSound;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@NoArgsConstructor
public class Ores implements Listener {

	public static String taskId = "pugmas-ore-regen";

	@Getter
	private static final ItemStack minersPickaxe = item(Material.IRON_PICKAXE).name("Miner's Pickaxe").build();
	@Getter
	private static final ItemStack minersSieve = item(Material.HOPPER).name("Miner's Sieve").build();
	@Getter
	private static final ItemStack flint = item(Material.FLINT).build();

	private static final int orePerCoal = 2;

	@EventHandler
	public void onOreBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!isAtPugmas(player, "cave"))
			return;

		if (event.getPlayer().hasPermission(WorldGuardEditCommand.getPermission()))
			return;

		event.setCancelled(true);

		Block block = event.getBlock();
		Material material = block.getType();
		OreType oreType = OreType.ofOre(material);
		if (oreType == null)
			return;

		if (!isFuzzyMatch(minersPickaxe, player.getInventory().getItemInMainHand()))
			return;

		playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, SoundCategory.BLOCKS);
		player.getInventory().addItem(oreType == OreType.COAL ? oreType.getIngot() : oreType.getOre());

		scheduleRegen(block);
		block.setType(Material.STONE);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!isAtPugmas(player, "cave"))
			return;

		if (!minersSieve.equals(player.getInventory().getItemInMainHand()))
			return;

		if (!ActionGroup.CLICK_BLOCK.applies(event) || event.getClickedBlock() == null)
			return;

		event.setCancelled(true);

		if (Action.LEFT_CLICK_BLOCK != event.getAction())
			return;

		Block block = event.getClickedBlock();
		if (block.getType() != Material.GRAVEL)
			return;

		playSound(player, Sound.ENTITY_HORSE_SADDLE, .5F, .5F);
		playSound(player, Sound.UI_STONECUTTER_TAKE_RESULT, .5F, .5F);
		Tasks.wait(5, () -> {
			playSound(player, Sound.ENTITY_HORSE_SADDLE, .5F, .5F);
			playSound(player, Sound.UI_STONECUTTER_TAKE_RESULT, .5F, .5F);
		});

		player.getInventory().addItem(flint);

		scheduleRegen(block);
		block.setType(Material.LIGHT_GRAY_CONCRETE_POWDER);
	}

	public void scheduleRegen(Block block) {
		new TaskService().save(new Task(taskId, new HashMap<String, Object>() {{
			put("location", JSON.serializeLocation(block.getLocation()));
			put("material", block.getType());
		}}, LocalDateTime.now().plusSeconds(RandomUtils.randomInt(3, 15))));
		// TODO PUGMAS Uncomment
//		}}, LocalDateTime.now().plusSeconds(RandomUtils.randomInt(3 * 60, 5 * 60))));
	}

	@EventHandler
	public void onSmelt(FurnaceSmeltEvent event) {
		if (!isAtPugmas(event.getBlock().getLocation()))
			return;

		if (isFuzzyMatch(event.getSource(), OreType.LIGHT_ANIMICA.getOre()))
			event.setResult(OreType.LIGHT_ANIMICA.getIngot());
	}

	@EventHandler
	public void onBurn(FurnaceBurnEvent event) {
		if (!isAtPugmas(event.getBlock().getLocation(), "cave"))
			return;

		if (!(event.getBlock().getState() instanceof BlastFurnace))
			return;

		BlastFurnace state = (BlastFurnace) event.getBlock().getState();
		if (state.getCookSpeedMultiplier() != 5) {
			state.setCookSpeedMultiplier(5);
			state.update();
		}

		if (isNullOrAir(event.getFuel()))
			return;

		if (!isFuzzyMatch(event.getFuel(), OreType.COAL.getIngot())) {
			state.setCookTimeTotal(0);
			state.update();
			event.setCancelled(true);
			return;
		}

		ItemStack smelting = state.getInventory().getSmelting();
		if (isNullOrAir(smelting)) {
			event.setCancelled(true);
			return;
		}

		OreType oreType = OreType.ofOre(smelting.getType());
		if (oreType == null || !isFuzzyMatch(oreType.getOre(), smelting)) {
			event.setCancelled(true);
			return;
		}

		ItemStack fuel = state.getInventory().getFuel();

		if (!isNullOrAir(fuel)) {
			fuel.setAmount(fuel.getAmount() - 1);
			state.getInventory().setFuel(fuel);
		}

		event.setBurnTime((int) (event.getBurnTime() / ((8 / orePerCoal) * state.getCookSpeedMultiplier())));
	}

	static {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND, () -> {
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
			this.ore = item(ore).name(camelCase(name() + " Ore")).build();
			this.ingot = item(ingot).name(camelCase(name())).build();
		}

		public static OreType ofOre(Material ore) {
			for (OreType oreType : OreType.values())
				if (oreType.getOre().getType() == ore)
					return oreType;
			return null;
		}

		public static OreType ofIngot(Material ingot) {
			for (OreType oreType : OreType.values())
				if (oreType.getIngot().getType() == ingot)
					return oreType;
			return null;
		}

		public ItemStack getIngot(int amount) {
			return new ItemBuilder(ingot).lore(ingot.getLore()).amount(amount).build();
		}

		public ItemStack getOre(int amount) {
			return new ItemBuilder(ore).lore(ingot.getLore()).amount(amount).build();
		}
	}
}
