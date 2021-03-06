package me.pugabyte.nexus.features.commands;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import joptsimple.internal.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.mutemenu.MuteMenuService;
import me.pugabyte.nexus.models.mutemenu.MuteMenuUser;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class MuteMenuCommand extends CustomCommand {

	public MuteMenuCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void muteMenu() {
		new MuteMenuProvider().open(player());
	}

	public static class MuteMenuProvider extends MenuUtils implements InventoryProvider {

		private final MuteMenuService service = new MuteMenuService();

		@Getter
		@AllArgsConstructor
		@RequiredArgsConstructor
		public enum MuteMenuItem {
			CHANNEL_GLOBAL("Global and Discord Chat", Material.GREEN_WOOL),
			CHANNEL_LOCAL("Local Chat", Material.YELLOW_WOOL),
			CHANNEL_MINIGAMES("Minigames Chat", Material.CYAN_WOOL, "chat.use.minigames"),
			CHANNEL_CREATIVE("Creative Chat", Material.LIGHT_BLUE_WOOL, "chat.use.creative"),
			CHANNEL_SKYBLOCK("Skyblock Chat", Material.ORANGE_WOOL, "chat.use.skyblock"),
			REMINDERS("Reminders", Material.REPEATER),
			AFK("AFK Broadcasts", Material.REDSTONE_LAMP),
			FIRST_JOIN_SOUNDS("First Join Sounds", Material.GOLD_BLOCK),
			JOIN_QUIT_SOUNDS("Join/Quit Sounds", Material.NOTE_BLOCK),
			JOIN_QUIT("Join/Quit Messages", Material.OAK_FENCE_GATE),
			EVENTS("Event Broadcasts", Material.BEACON),
			MINIGAMES("Minigame Broadcasts", Material.DIAMOND_SWORD); // TODO

			@NonNull
			public String title;
			@NonNull
			public Material material;
			public String permission;
		}

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
					.title(colorize("&3Mute Menu"))
					.size(getRows(MuteMenuItem.values().length, 2, 7), 9)
					.provider(this)
					.build()
					.open(viewer);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);

			MuteMenuUser user = service.get(player.getUniqueId());
			int row = 1;
			int column = 1;
			for (MuteMenuItem item : MuteMenuItem.values()) {
				if (!Strings.isNullOrEmpty(item.getPermission()) && !player.hasPermission(item.getPermission()))
					continue;

				boolean muted = user.hasMuted(item);
				ItemStack stack = nameItem(item.getMaterial(), "&e" + item.getTitle(), muted ? "&cMuted" : "&aUnmuted");
				if (muted)
					addGlowing(stack);

				contents.set(row, column, ClickableItem.from(stack, e -> {
					toggleMute(user, item);
					open(player);
				}));

				if (column == 7) {
					column = 1;
					row++;
				} else
					column++;
			}
		}

		public void toggleMute(MuteMenuUser user, MuteMenuItem item) {
			Player player = user.getPlayer();
			if (item.name().startsWith("CHANNEL_"))
				if (user.hasMuted(item))
					PlayerUtils.runCommand(player, "ch join " + item.name().replace("CHANNEL_", "").toLowerCase());
				else
					PlayerUtils.runCommand(player, "ch leave " + item.name().replace("CHANNEL_", "").toLowerCase());
			else {
				if (user.hasMuted(item))
					user.getMuted().remove(item);
				else
					user.getMuted().add(item);
				service.save(user);
			}
		}

		@Override
		public void update(Player player, InventoryContents contents) {
		}
	}
}
