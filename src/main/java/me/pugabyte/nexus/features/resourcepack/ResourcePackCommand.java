package me.pugabyte.nexus.features.resourcepack;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.commands.staff.admin.BashCommand;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Aliases("rp")
@NoArgsConstructor
public class ResourcePackCommand extends CustomCommand implements Listener {
	private static final String URL = "http://cdn.bnn.gg/BearNationResourcePack.zip";
	private static String hash = Utils.createSha1(URL);

	public ResourcePackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private void resourcePack(Player player) {
		player.setResourcePack(URL, hash);
	}

	@Path
	void resourcePack() {
		if (hash == null)
			error("Resource pack hash is null");

		if (Status.DECLINED == player().getResourcePackStatus())
			error("You declined the original prompt for the resource pack. In order to use the resource pack, you must edit the server in your server list and change the \"Server Resource Packs\" option to either \"enabled\" or \"prompt\"");

		resourcePack(player());
	}

	@Permission("group.staff")
	@Path("getStatus [player]")
	void getStatus(@Arg("self") Player player) {
		send(PREFIX + "Resource pack status for " + player.getName() + ": &e" + (player.getResourcePackStatus() == null ? "null" : camelCase(player.getResourcePackStatus())));
	}

	@Permission("group.staff")
	@Path("getStatuses")
	void getStatuses() {
		send(PREFIX + "Statuses: ");
		new HashMap<Status, List<String>>() {{
			for (Player player : Bukkit.getOnlinePlayers()) {
				List<String> uuids = getOrDefault(player.getResourcePackStatus(), new ArrayList<>());
				uuids.add(player.getName());
				put(player.getResourcePackStatus(), uuids);
			}
		}}.forEach((status, names) -> send("&e" + camelCase(status) + "&3: " + String.join(", ", names)));
	}

	@Async
	@Path("update")
	@Permission("group.admin")
	void update() {
		send(BashCommand.tryExecute("/home/minecraft/git/Saturn/deploy.sh"));

		String newHash = Utils.createSha1(URL);
		if (hash != null && hash.equals(newHash))
			error("No resource pack update found");

		hash = newHash;

		if (hash == null)
			error("Resource pack hash is null");

//		TODO: Figure out a solution that actually works, this just disables the active resource pack for all players who click it
//		for (Player player : Bukkit.getOnlinePlayers())
//			if (Arrays.asList(Status.ACCEPTED, Status.SUCCESSFULLY_LOADED).contains(player.getResourcePackStatus()))
//				send(player, json(PREFIX + "There's an update to the resource pack available, click to update.").command("/rp"));
	}

	@Path("customModels")
	@Permission("group.staff")
	void customModels() {
		new CustomModelsMenu().open(player());
	}

	@Path("customModels [item]")
	@Permission("group.staff")
	void customModels(CustomModel customModel) {
		PlayerUtils.giveItem(player(), customModel.getItem());
		send(PREFIX + "Gave custom model &e" + camelCase(customModel));
	}

	@NoArgsConstructor
	public static class CustomModelsMenu extends MenuUtils implements InventoryProvider {

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
					.provider(this)
					.title(colorize("&0Custom Models"))
					.size(6, 9)
					.build()
					.open(viewer, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);

			List<ClickableItem> items = new ArrayList<>();

			for (CustomModel customModel : CustomModel.values()) {
				ItemStack item = customModel.getItem();
				items.add(ClickableItem.from(item, e -> PlayerUtils.giveItem(player, item)));
			}

			addPagination(player, contents, items);
		}

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Tasks.wait(Time.SECOND.x(2), () -> {
			resourcePack(player);

			// Try Again if failed
			Tasks.wait(Time.SECOND.x(5), () -> {
				if (Status.FAILED_DOWNLOAD == player.getResourcePackStatus())
					resourcePack(player);
			});
		});
	}

	@EventHandler
	public void onResourcePackEvent(PlayerResourcePackStatusEvent event) {
		Nexus.debug("Resource Pack Status Update: " + event.getPlayer().getName() + " = " + event.getStatus());
	}
}
