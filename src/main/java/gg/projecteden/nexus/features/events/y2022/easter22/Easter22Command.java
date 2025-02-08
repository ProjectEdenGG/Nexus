package gg.projecteden.nexus.features.events.y2022.easter22;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.easter22.Easter22User;
import gg.projecteden.nexus.models.easter22.Easter22UserService;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Disabled
@NoArgsConstructor
@Aliases("easter")
public class Easter22Command extends IEventCommand {

	public Easter22Command(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public EdenEvent getEdenEvent() {
		return Easter22.get();
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
		new Paginator<Easter22User>()
			.values(all)
			.formatter((user, index) -> json(index + " &e" + user.getNickname() + " &7- " + user.getFound().size()))
			.command("/easter top")
			.page(page)
			.send();
	}

	@Path("topLocations [page]")
	@Permission(Group.ADMIN)
	void topLocations(@Arg("1") int page) {
		Map<Location, Integer> counts = new Easter22UserService().getTopLocations();

		send(PREFIX + "Most found eggs");
		new Paginator<Location>()
			.values(Utils.sortByValueReverse(counts).keySet())
			.formatter((location, index) -> json(index + " &e" + StringUtils.getCoordinateString(location) + " &7- " + counts.get(location))
				.command(StringUtils.getTeleportCommand(location))
				.hover("&eClick to teleport")
			)
			.command("/easter topLocations")
			.page(page)
			.send();
	}

	@Path("store")
	void store() {
		new Easter22StoreProvider().open(player());
	}

	@Title("&3Easter 2022 Store")
	public static class Easter22StoreProvider extends InventoryProvider {

		@Override
		public void init() {
			addCloseItem();

			final List<ClickableItem> items = new ArrayList<>();

			final EventUser eventUser = new EventUserService().get(viewer);
			contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&e" + eventUser.getTokens() + " Event Tokens").build()));

//			final int MODEL_ID_START = CustomMaterial.EASTER22_EASTER_EGG.getModelId();
//			final int MODEL_ID_END = MODEL_ID_START + 20;
//			for (int i = MODEL_ID_START; i < MODEL_ID_END; i++) {
//				final ItemStack egg = new ItemBuilder(Material.PAPER).modelId(i).name("&eEgg #" + (i - MODEL_ID_START + 1)).build();
//				final ItemStack display = new ItemBuilder(egg).lore("", "&" + (eventUser.getTokens() >= 200 ? "e" : "c") + "200 Event Tokens").build();
//				items.add(ClickableItem.of(display, e -> {
//					if (!eventUser.hasTokens(200)) {
//						eventUser.sendMessage(StringUtils.getPrefix(Easter22.class) + "&cYou cannot afford that egg");
//						open(viewer, contents.pagination().getPage());
//					} else
//						ConfirmationMenu.builder()
//							.onConfirm(e2 -> {
//								final String PREFIX = Commands.getPrefix(Easter22Command.class);
//								try {
//									eventUser.charge(200);
//									Mail.fromServer(viewer.getUniqueId(), WorldGroup.SURVIVAL, egg).send();
//									PlayerUtils.send(viewer, PREFIX + "Your egg has been mailed to you in Survival");
//								} catch (InvalidInputException ex) {
//									MenuUtils.handleException(viewer, PREFIX, ex);
//								}
//							})
//							.onFinally(e2 -> open(viewer, contents.pagination().getPage()))
//							.open(viewer);
//				}));
//			}

			paginate(items);
		}

	}

}
