package gg.projecteden.nexus.features.events.y2022.easter22;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22NPC;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestTask;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.easter22.Easter22User;
import gg.projecteden.nexus.models.easter22.Easter22UserService;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.getCoordinateString;
import static gg.projecteden.nexus.utils.StringUtils.getTeleportCommand;

@NoArgsConstructor
@Aliases("easter")
public class Easter22Command extends CustomCommand implements Listener {

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

	@Permission(Group.ADMIN)
	@Path("quest debug <task>")
	void quest_debug(Easter22QuestTask task) {
		send(String.valueOf(task.get()));
	}

	@Permission(Group.ADMIN)
	@Path("quest start")
	void quest_start() {
		Quest.builder()
			.tasks(Easter22QuestTask.MAIN)
			.assign(player())
			.start();

		send(PREFIX + "Quest activated");
	}

	@Permission(Group.ADMIN)
	@Path("quest npc tp <quest>")
	void quest_npc_tp(Easter22NPC npc) {
		final Location location = CitizensUtils.locationOf(npc.getNpcId());
		if (location == null)
			error("Could not determine location of NPC");

		player().teleportAsync(location, TeleportCause.COMMAND);
	}

	@Permission(Group.ADMIN)
	@Path("quest item <item> [amount]")
	void quest_item(Easter22QuestItem item, @Arg("1") int amount) {
		giveItems(item.get(), amount);
	}

	@Path("store")
	void store() {
		new Easter22StoreProvider().open(player());
	}

	public static class Easter22StoreProvider extends MenuUtils implements InventoryProvider {

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
				.provider(this)
				.title(colorize("&3Easter 2022 Store"))
				.size(6, 9)
				.build()
				.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);

			final List<ClickableItem> items = new ArrayList<>();

			final EventUser eventUser = new EventUserService().get(player);
			contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&e" + eventUser.getTokens() + " Event Tokens").build()));

			for (int i = 2001; i <= 2020; i++) {
				final ItemStack egg = new ItemBuilder(Material.PAPER).customModelData(i).name("&eEgg #" + (i - 2000)).build();
				final ItemStack display = new ItemBuilder(egg).lore("", "&" + (eventUser.getTokens() >= 200 ? "e" : "c") + "200 Event Tokens").build();
				items.add(ClickableItem.from(display, e -> {
					if (!eventUser.hasTokens(200)) {
						eventUser.sendMessage(StringUtils.getPrefix(Easter22.class) + "&cYou cannot afford that egg");
						open(player, contents.pagination().getPage());
					} else
						ConfirmationMenu.builder()
							.onConfirm(e2 -> {
								final String PREFIX = Commands.getPrefix(Easter22Command.class);
								try {
									eventUser.charge(200);
									Mail.fromServer(player.getUniqueId(), WorldGroup.SURVIVAL, egg).send();
									PlayerUtils.send(player, PREFIX + "Your egg has been mailed to you in Survival");
								} catch (InvalidInputException ex) {
									handleException(player, PREFIX, ex);
								}
							})
							.onFinally(e2 -> open(player, contents.pagination().getPage()))
							.open(player);
				}));
			}

			paginator(player, contents, items);
		}

	}

}
