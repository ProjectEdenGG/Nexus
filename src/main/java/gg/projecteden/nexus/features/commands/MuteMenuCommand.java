package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.mobheads.MobHeadType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.mutemenu.MuteMenuService;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import joptsimple.internal.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.lexikiq.event.sound.EntitySoundEvent;
import me.lexikiq.event.sound.LocationNamedSoundEvent;
import me.lexikiq.event.sound.NamedSoundEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@AllArgsConstructor
	public static class MuteMenuProvider extends InventoryProvider {
		private final MuteMenuService service = new MuteMenuService();
		private PageType pageType;

		public MuteMenuProvider() {
			this.pageType = PageType.MESSAGES;
		}

		@Override
		public String getTitle() {
			return "&3" + StringUtils.camelCase(pageType.name());
		}

		@Override
		public void init() {
			MuteMenuUser user = service.get(player.getUniqueId());
			final List<ClickableItem> items = new ArrayList<>();

			switch (pageType) {
				case MESSAGES -> {
					addCloseItem();
					contents.set(0, 8, ClickableItem.of(Material.COMMAND_BLOCK, "&dSounds", e -> {
						pageType = PageType.SOUNDS;
						open(player);
					}));
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
							refresh();
						}));
					}
				}
				case SOUNDS -> {
					addBackItem(e -> {
						pageType = PageType.MESSAGES;
						open(player);
					});
					contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK)
						.name("&3Info")
						.lore("&eRight Click - Increase volume", "&eLeft Click - Decrease volume")
						.build()));

					items.add(ClickableItem.of(Material.ZOMBIE_HEAD, "Mob Sounds", e -> {
						pageType = PageType.MOB_SOUNDS;
						open(player);
					}));

					for (MuteMenuItem item : MuteMenuItem.values()) {
						if (item.getDefaultVolume() == null)
							continue;
						if (!Strings.isNullOrEmpty(item.getPermission()) && !player.hasPermission(item.getPermission()))
							continue;

						boolean muted = user.hasMuted(item);
						int volume = user.getVolume(item);

						ItemBuilder stack = new ItemBuilder(item.getMaterial()).name("&e" + item.getTitle()).lore(muted ? "&c0%" : "&a" + volume + "%");
						if (muted)
							stack.glow();

						items.add(ClickableItem.of(stack, e -> {
							if (e.isLeftClick())
								decreaseVolume(user, item);
							else if (e.isRightClick())
								increaseVolume(user, item);
							service.save(user);
							refresh();
						}));
					}
				}
				case MOB_SOUNDS -> {
					addBackItem(e -> {
						pageType = PageType.SOUNDS;
						open(player);
					});
					contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK)
						.name("&3Info")
						.lore("&eRight Click - Increase volume", "&eLeft Click - Decrease volume")
						.build()));

					for (MobHeadType mobHeadType : MobHeadType.values()) {
						if (mobHeadType == MobHeadType.PLAYER)
							continue;

						if (mobHeadType.getSkull() == null)
							continue;

						int volume = user.getVolume(mobHeadType.getEntityType());
						final ItemBuilder skull = new ItemBuilder(mobHeadType.getSkull()).lore(volume == 0 ? "&c0%" : "&a" + volume + "%");
						items.add(ClickableItem.of(skull.build(), e -> {
							if (e.isLeftClick())
								decreaseVolume(user, mobHeadType.getEntityType());
							else if (e.isRightClick())
								increaseVolume(user, mobHeadType.getEntityType());
							service.save(user);
							refresh();
						}));
					}
				}
			}

			paginator().items(items).build();
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

		private enum PageType {
			MESSAGES,
			SOUNDS,
			MOB_SOUNDS,
		}
	}

	@EventHandler
	public void onEntitySound(EntitySoundEvent event) {
		final Entity origin = event.getOrigin();
		if (!(origin instanceof LivingEntity))
			return;

		event.setCancelled(true);
		modifySound(event, origin.getType());
	}

	@EventHandler
	public void on(LocationNamedSoundEvent event) {
		final EntityType entityType = getEntityType(event.getSound());
		if (entityType == null || !entityType.isAlive())
			return;

		event.setCancelled(true);
		modifySound(event, entityType);
	}

	private static final String[] entityTypes = Arrays.stream(EntityType.values())
		.map(EntityType::name)
		.toArray(String[]::new);

	private static final Map<Sound, EntityType> cache = new HashMap<>();

	static {
		Arrays.sort(entityTypes, Comparator.comparingInt(String::length).reversed());
	}

	private EntityType getEntityType(Sound sound) {
		return cache.computeIfAbsent(sound, $ -> {
			for (String entityType : entityTypes)
				if (sound.name().matches("^ENTITY_" + entityType + "_.*"))
					return EntityType.valueOf(entityType);

			return null;
		});
	}

	public void modifySound(NamedSoundEvent event, EntityType entityType) {
		event.calculateRecipients().forEach(player -> {
			// calculate adjusted volume
			final MuteMenuUser user = new MuteMenuService().get(player);
			final double volumePercent = user.getVolume(entityType) / 100d;
			final double volume = event.getVolume() * volumePercent;
			if (volumePercent == 0) return;
			// play sound
			new SoundBuilder(event.getSound())
				.location(event.getLocation())
				.volume(volume)
				.pitch(event.getPitch())
				.category(event.getCategory())
				.receiver(player)
				.play();
			Nexus.debug("Played sound " + event.getSound() + " to " + player.getName() + " at volume " + volume);
		});
	}

}
