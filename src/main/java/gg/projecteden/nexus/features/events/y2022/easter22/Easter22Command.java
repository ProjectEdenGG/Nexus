package gg.projecteden.nexus.features.events.y2022.easter22;

import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22Entity;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22NPC;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestTask;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.easter22.Easter22User;
import gg.projecteden.nexus.models.easter22.Easter22UserService;
import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BlockRegenJob;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.RandomUtils.randomInt;
import static gg.projecteden.nexus.utils.StringUtils.getCoordinateString;
import static gg.projecteden.nexus.utils.StringUtils.getTeleportCommand;
import static gg.projecteden.utils.RandomUtils.chanceOf;

@NoArgsConstructor
@Aliases("easter")
public class Easter22Command extends CustomCommand implements Listener {
	public static final LocalDateTime START = LocalDate.of(2021, 4, 10).atStartOfDay();
	public static final LocalDateTime END = LocalDate.of(2021, 4, 25).atStartOfDay();

	public Easter22Command(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg("self") Easter22User user) {
		send(PREFIX + (isSelf(user) ? "You have found" : user.getNickname() + " has found") + " &e" +
			user.getFound().size() + "/" + Easter22.TOTAL_EASTER_EGGS + plural(" easter egg", user.getFound().size()));
	}

	@Path("top [page]")
	void top(@Arg("1") int page) {
		final List<Easter22User> all = new Easter22UserService().getTop();
		final int sum = all.stream().mapToInt(user -> user.getFound().size()).sum();

		send(PREFIX + "Top egg hunters  &3|  Total: &e" + sum);
		paginate(all, (user, index) -> json(index + " &e" + user.getNickname() + " &7- " + user.getFound().size()), "/easter top", page);
	}

	@Path("topLocations [page]")
	@Permission(Group.ADMIN)
	void topLocations(@Arg("1") int page) {
		Map<Location, Integer> counts = new Easter22UserService().getTopLocations();

		send(PREFIX + "Most found eggs");
		BiFunction<Location, String, JsonBuilder> formatter = (location, index) ->
				json(index + " &e" + getCoordinateString(location) + " &7- " + counts.get(location))
						.command(getTeleportCommand(location))
						.hover("&eClick to teleport");
		paginate(Utils.sortByValueReverse(counts).keySet(), formatter, "/easter topLocations", page);
	}

	@EventHandler
	public void onEggInteract(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		if (!isEgg(itemFrame.getItem()))
			return;

		event.setCancelled(true);

		if (LocalDateTime.now().isBefore(START))
			return;

		if (LocalDateTime.now().isAfter(END))
			return;

		Location location = itemFrame.getLocation();

		Easter22UserService service = new Easter22UserService();
		Easter22User user = service.get(event.getPlayer());
		user.found(location);
		service.save(user);
	}

	private static boolean isEgg(ItemStack item) {
		if (isNullOrAir(item))
			return false;

		if (item.getType() != Material.PAPER)
			return false;

		final int modelId = CustomModelData.of(item);
		return modelId >= 2001 && modelId <= 2020;
	}

	@Path("quest debug <task>")
	void quest_debug(Easter22QuestTask task) {
		send(String.valueOf(task.get()));
	}

	@Path("quest start")
	void quest_start() {
		Quest.builder()
			.tasks(Easter22QuestTask.MAIN)
			.assign(player())
			.start();

		send(PREFIX + "Quest activated");
	}

	@Path("quest npc tp <quest>")
	void quest_npc_tp(Easter22NPC npc) {
		final Location location = CitizensUtils.locationOf(npc.getNpcId());
		if (location == null)
			error("Could not determine location of NPC");

		player().teleportAsync(location, TeleportCause.COMMAND);
	}

	@Path("quest item <item> [amount]")
	void quest_item(Easter22QuestItem item, @Arg("1") int amount) {
		giveItems(item.get(), amount);
	}

	@EventHandler
	public void on(NPCRightClickEvent event) {
		final Easter22NPC npc = Easter22NPC.of(event.getNPC());
		if (npc == null)
			return;

		new QuesterService().edit(event.getClicker(), quester -> quester.interact(npc));
		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractAtEntityEvent event) {
		final Easter22Entity entity = Easter22Entity.of(event.getRightClicked());
		if (entity == null)
			return;

		new QuesterService().edit(event.getPlayer(), quester -> quester.interact(entity));
		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!Easter22.isAtEasterIsland(player))
			return;

		final Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		switch (event.getAction()) {
			case LEFT_CLICK_BLOCK:
				switch (block.getType()) {
					case OXEYE_DAISY, CORNFLOWER -> {
						new BlockRegenJob(block.getLocation(), block.getType()).schedule(randomInt(3 * 60, 6 * 60));
						block.breakNaturally();
					}
				}
				break;
			case RIGHT_CLICK_BLOCK:
				switch (block.getType()) {
					case OAK_LEAVES -> {
						if (!new CooldownService().check(player, "easter22-stick-" + StringUtils.getFlooredCoordinateString(block.getLocation()).replace(" ", "-"), TickTime.MINUTE))
							return;

						if (chanceOf(20)) {
							PlayerUtils.giveItem(player, Material.STICK);
							new SoundBuilder(Sound.ITEM_BONE_MEAL_USE).receiver(player).play();
						} else {
							PlayerUtils.send(player, "&7Hmm... no sticks in this bush");
							new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(player).volume(.15).play();
						}
					}
				}
				break;
		}

	}

}
