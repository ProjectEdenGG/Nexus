package gg.projecteden.nexus.features.commands;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mutemenu.MuteMenuService;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import joptsimple.internal.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static java.util.stream.Collectors.toList;

@Description("Hide or mute certain parts of the server, such as chat, automatic broadcasts, and sounds")
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
		private PageType pageType = PageType.MESSAGES;

		@Getter
		@AllArgsConstructor
		@RequiredArgsConstructor
		public enum MuteMenuItem {
			CHANNEL_GLOBAL("Global and Discord Chat", Material.GREEN_WOOL),
			CHANNEL_LOCAL("Local Chat", Material.YELLOW_WOOL),
			CHANNEL_MINIGAMES("Minigames Chat", Material.CYAN_WOOL, "chat.use.minigames"),
			CHANNEL_CREATIVE("Creative Chat", Material.LIGHT_BLUE_WOOL, "chat.use.creative"),
			CHANNEL_SKYBLOCK("Skyblock Chat", Material.ORANGE_WOOL, "chat.use.skyblock"),
			REMINDERS("Reminders", Material.REPEATER, List.of("Periodic reminders about", "features and events")),
			AFK("AFK Broadcasts", Material.REDSTONE_LAMP),
			JOIN_QUIT("Join/Quit Messages", Material.OAK_FENCE_GATE),
			DEATH_MESSAGES("Death Messages", Material.PLAYER_HEAD),
			BOSS_FIGHT("Boss Fight Broadcasts", Material.NETHER_STAR),
			CRATES("Crate Broadcasts", Material.CHEST, List.of("Broadcasts when players win", "rare items from crates")),
			EVENTS("Event Broadcasts", Material.BEACON),
			BOOSTS("Boost Broadcasts", Material.EXPERIENCE_BOTTLE),
			MINIGAMES("Minigame Broadcasts", Material.DIAMOND_SWORD),
			QUEUP("QueUp Song Updates", Material.MUSIC_DISC_MALL),
			// Sounds
			FIRST_JOIN_SOUND("First Join", Material.GOLD_BLOCK, 50),
			JOIN_QUIT_SOUNDS("Join/Quit", Material.NOTE_BLOCK, 50),
			ALERTS("Alerts", Material.NAME_TAG, 50),
			RANK_UP("Rank Up", Material.EMERALD, 50);

			@NonNull
			private final String title;
			@NonNull
			private final Material material;
			private List<String> lore = new ArrayList<>();
			private String permission = null;
			private Integer defaultVolume = null;

			MuteMenuItem(String title, Material material, int defaultVolume) {
				this.title = title;
				this.material = material;
				this.defaultVolume = defaultVolume;
			}

			MuteMenuItem(String title, Material material, String permission) {
				this.title = title;
				this.material = material;
				this.permission = permission;
			}

			MuteMenuItem(String title, Material material, List<String> lore) {
				this.title = title;
				this.material = material;
				this.lore = lore.stream().map(line -> "&f" + line).collect(toList());
				if (!this.lore.isEmpty())
					this.lore.add(0, "");
			}
		}

		@Override
		public void open(Player player, int page) {
			open(player, PageType.MESSAGES);
		}

		public void open(Player viewer, PageType pageType) {
			this.pageType = pageType;
			String title = "&3" + StringUtils.camelCase(pageType.name());
			SmartInventory.builder()
					.title(colorize(title))
					.size(getRows(getViewableItems(viewer, pageType), 2, 7), 9)
					.provider(this)
					.build()
					.open(viewer);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			MuteMenuUser user = service.get(player.getUniqueId());
			int row = 1;
			int column = 1;

			if (pageType.equals(PageType.MESSAGES)) {
				addCloseItem(contents);
				contents.set(0, 8, ClickableItem.from(nameItem(Material.COMMAND_BLOCK, "&dSounds"), e -> open(player, PageType.SOUNDS)));

				for (MuteMenuItem item : MuteMenuItem.values()) {
					if (item.getDefaultVolume() != null)
						continue;
					if (!Strings.isNullOrEmpty(item.getPermission()) && !player.hasPermission(item.getPermission()))
						continue;

					boolean muted = user.hasMuted(item);
					ItemStack stack = new ItemBuilder(item.getMaterial()).name("&e" + item.getTitle())
							.lore(muted ? "&cMuted" : "&aUnmuted")
							.lore(item.getLore())
							.glow(muted)
							.itemFlags(ItemFlag.HIDE_ATTRIBUTES)
							.build();

					contents.set(row, column, ClickableItem.from(stack, e -> {
						toggleMute(user, item);
						open(player, PageType.MESSAGES);
					}));

					if (column == 7) {
						column = 1;
						row++;
					} else
						column++;
				}
			} else {
				addBackItem(contents, e -> open(player, PageType.MESSAGES));

				ItemStack info = new ItemBuilder(Material.BOOK)
						.name("&3Info")
						.lore("&eLClick - Increase volume", "&eRClick - Decrease volume")
						.build();
				contents.set(0, 8, ClickableItem.empty(info));

				for (MuteMenuItem item : MuteMenuItem.values()) {
					if (item.getDefaultVolume() == null)
						continue;
					if (!Strings.isNullOrEmpty(item.getPermission()) && !player.hasPermission(item.getPermission()))
						continue;

					boolean muted = user.hasMuted(item);
					int volume = user.getVolume(item);
					ItemStack stack = nameItem(item.getMaterial(), "&e" + item.getTitle(), muted ? "&c0%" : "&a" + volume + "%");
					if (muted)
						addGlowing(stack);

					contents.set(row, column, ClickableItem.from(stack, e -> {
						InventoryClickEvent clickEvent = ((InventoryClickEvent) e.getEvent());
						if (clickEvent.isRightClick())
							decreaseVolume(user, item);
						else if (clickEvent.isLeftClick())
							increaseVolume(user, item);
						open(player, PageType.SOUNDS);
					}));

					if (column == 7) {
						column = 1;
						row++;
					} else
						column++;
				}
			}

		}

		private void increaseVolume(MuteMenuUser user, MuteMenuItem item) {
			int previous = user.getVolume(item);
			int current = Math.min(previous + 10, 100);
			user.setVolume(item, current);
			service.save(user);
		}

		private void decreaseVolume(MuteMenuUser user, MuteMenuItem item) {
			int previous = user.getVolume(item);
			int current = Math.max(0, previous - 10);
			user.setVolume(item, current);
			service.save(user);
		}

		public void toggleMute(MuteMenuUser user, MuteMenuItem item) {
			Player player = user.getOnlinePlayer();
			if (item.name().startsWith("CHANNEL_"))
				if (user.hasMuted(item))
					PlayerUtils.runCommand(player, "ch join " + item.name().replace("CHANNEL_", "").toLowerCase());
				else
					PlayerUtils.runCommand(player, "ch leave " + item.name().replace("CHANNEL_", "").toLowerCase());
			else {
				if (user.hasMuted(item))
					user.unMute(item);
				else
					user.mute(item);

				service.save(user);
			}
		}

		private int getViewableItems(Player player, PageType pageType) {
			int count = 0;
			if (pageType.equals(PageType.SOUNDS)) {
				for (MuteMenuItem item : MuteMenuItem.values()) {
					if (item.getDefaultVolume() != null)
						count++;
				}

			} else {
				for (MuteMenuItem item : MuteMenuItem.values()) {
					if (item.getDefaultVolume() != null)
						continue;
					if (Strings.isNullOrEmpty(item.getPermission()) || player.hasPermission(item.getPermission()))
						count++;
				}
			}
			return count;
		}

		private enum PageType {
			MESSAGES,
			SOUNDS
		}
	}


}
