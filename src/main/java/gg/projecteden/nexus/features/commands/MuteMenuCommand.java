package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.mobheads.MobHeadType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mutemenu.MuteMenuService;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import joptsimple.internal.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.lexikiq.event.sound.EntitySoundEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@Description("Hide or mute certain parts of the server, such as chat, automatic broadcasts, and sounds")
public class MuteMenuCommand extends CustomCommand implements Listener {

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
			JUKEBOX("Custom Jukebox Songs", Material.JUKEBOX, List.of("Custom songs played via &c/jukebox")),
			CHAT_GAMES("Chat Games", Material.PAPER),
			TP_REQUESTS("Teleport Requests", Material.ENDER_PEARL),
			MESSAGES("Messages", Material.BOOK),
			BOOPS("Boops", Material.BELL),
			// Sounds
			FIRST_JOIN_SOUND("First Join", Material.GOLD_BLOCK, 50),
			JOIN_QUIT_SOUNDS("Join/Quit", Material.NOTE_BLOCK, 50),
			ALERTS("Alerts", Material.NAME_TAG, 50),
			RANK_UP("Rank Up", Material.EMERALD, 50),
			CHAT_GAMES_SOUND("Chat Games", Material.PAPER, 50);

			@NonNull
			private final String title;
			@NonNull
			private final Material material;
			private List<String> lore = new ArrayList<>();
			private String permission = null;
			private Integer defaultVolume = null;

			MuteMenuItem(@NotNull String title, @NotNull Material material, int defaultVolume) {
				this.title = title;
				this.material = material;
				this.defaultVolume = defaultVolume;
			}

			MuteMenuItem(@NotNull String title, @NotNull Material material, String permission) {
				this.title = title;
				this.material = material;
				this.permission = permission;
			}

			MuteMenuItem(@NotNull String title, @NotNull Material material, List<String> lore) {
				this.title = title;
				this.material = material;
				this.lore = lore.stream().map(line -> "&f" + line).collect(toList());
				if (!this.lore.isEmpty())
					this.lore.add(0, "");
			}
		}

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
				.title("&3" + StringUtils.camelCase(pageType.name()))
				.maxSize()
				.provider(this)
				.build()
				.open(viewer, page);
		}

		public void open(Player viewer, PageType pageType) {
			open(viewer, pageType, 0);
		}

		public void open(Player viewer, PageType pageType, int page) {
			this.pageType = pageType;
			open(viewer, page);
		}

		public void reopen(Player player, InventoryContents contents) {
			open(player, contents.pagination().getPage());
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			MuteMenuUser user = service.get(player.getUniqueId());
			final List<ClickableItem> items = new ArrayList<>();

			switch (pageType) {
				case MESSAGES -> {
					addCloseItem(contents);
					contents.set(0, 8, ClickableItem.of(nameItem(Material.COMMAND_BLOCK, "&dSounds"), e -> open(player, PageType.SOUNDS)));
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

						items.add(ClickableItem.of(stack, e -> {
							toggleMute(user, item);
							reopen(player, contents);
						}));
					}
				}
				case SOUNDS -> {
					addBackItem(contents, e -> open(player, PageType.MESSAGES));
					contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK)
						.name("&3Info")
						.lore("&eLeft Click - Increase volume", "&eRight Click - Decrease volume")
						.build()));

					if (Rank.of(player).isAdmin())
						items.add(ClickableItem.of(nameItem(Material.ZOMBIE_HEAD, "Mob Sounds"), e -> open(player, PageType.MOB_SOUNDS)));

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

						items.add(ClickableItem.of(stack, e -> {
							if (isRightClick(e))
								decreaseVolume(user, item);
							else if (isLeftClick(e))
								increaseVolume(user, item);
							service.save(user);
							reopen(player, contents);
						}));
					}
				}
				case MOB_SOUNDS -> {
					addBackItem(contents, e -> open(player, PageType.SOUNDS));
					contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK)
						.name("&3Info")
						.lore("&eLeft Click - Increase volume", "&eRight Click - Decrease volume")
						.build()));

					for (MobHeadType mobHeadType : MobHeadType.values()) {
						if (mobHeadType == MobHeadType.PLAYER)
							continue;

						if (mobHeadType.getSkull() == null)
							continue;

						int volume = user.getVolume(mobHeadType.getEntityType());
						final ItemBuilder skull = new ItemBuilder(mobHeadType.getSkull()).lore(volume == 0 ? "&c0%" : "&a" + volume + "%");
						items.add(ClickableItem.of(skull.build(), e -> {
							if (isRightClick(e))
								decreaseVolume(user, mobHeadType.getEntityType());
							else if (isLeftClick(e))
								increaseVolume(user, mobHeadType.getEntityType());
							service.save(user);
							reopen(player, contents);
						}));
					}
				}
			}

			paginator(player, contents, items);
		}

		private int increaseVolume(int volume) {
			return Math.min(volume + 10, 100);
		}

		private int decreaseVolume(int volume) {
			return Math.max(0, volume - 10);
		}

		private void increaseVolume(MuteMenuUser user, MuteMenuItem item) {
			user.setVolume(item, increaseVolume(user.getVolume(item)));
		}

		private void increaseVolume(MuteMenuUser user, EntityType entityType) {
			user.setVolume(entityType, increaseVolume(user.getVolume(entityType)));
		}

		private void decreaseVolume(MuteMenuUser user, MuteMenuItem item) {
			user.setVolume(item, decreaseVolume(user.getVolume(item)));
		}

		private void decreaseVolume(MuteMenuUser user, EntityType entityType) {
			user.setVolume(entityType, decreaseVolume(user.getVolume(entityType)));
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
					user.unmute(item);
				else
					user.mute(item);

				service.save(user);
			}
		}

		private enum PageType {
			MESSAGES,
			SOUNDS,
			MOB_SOUNDS,
		}
	}

	// TODO 1.18
//	@EventHandler
	public void onEntitySound(EntitySoundEvent event) {
		final Entity origin = event.getOrigin();
		if (!(origin instanceof LivingEntity))
			return;

		event.setCancelled(true);

		OnlinePlayers.where()
			.world(origin.getWorld())
			.radius(origin.getLocation(), 16 * event.getVolume())
			.get().forEach(player -> {
				final MuteMenuUser user = new MuteMenuService().get(player);
				final int volume = user.getVolume(origin.getType());
				new SoundBuilder(event.getSound())
					.location(origin.getLocation())
					.volume(volume)
					.receiver(player)
					.play();

				Nexus.debug("Played sound " + event.getSound() + " to " + player.getName() + " at volume " + volume);
			});
	}

}
