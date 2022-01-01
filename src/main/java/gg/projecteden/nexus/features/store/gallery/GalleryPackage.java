package gg.projecteden.nexus.features.store.gallery;


import com.destroystokyo.paper.ParticleBuilder;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Emotes;
import gg.projecteden.nexus.features.particles.providers.EffectSettingProvider;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.features.store.gallery.annotations.Category;
import gg.projecteden.nexus.features.store.gallery.annotations.Category.GalleryCategory;
import gg.projecteden.nexus.features.store.perks.CostumeCommand.CostumeMenu;
import gg.projecteden.nexus.features.store.perks.joinquit.JoinQuit;
import gg.projecteden.nexus.features.store.perks.workbenches.WorkbenchesCommand.WorkbenchesMenu;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmorTask;
import gg.projecteden.nexus.models.rainbowbeacon.RainbowBeacon;
import gg.projecteden.nexus.models.rainbowbeacon.RainbowBeaconService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.CitizensUtils.NPCRandomizer;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.utils.EnumUtils.random;
import static gg.projecteden.utils.RandomUtils.randomElement;
import static gg.projecteden.utils.StringUtils.getUUID0;

@Getter
@NoArgsConstructor
@AllArgsConstructor
// TODO Wakka - Sounds & other feedback
public enum GalleryPackage {
	@Category(GalleryCategory.VISUALS)
	COSTUMES(4307) {
		@NoArgsConstructor
		static class CostumeDisplayMenu extends CostumeMenu {

			public CostumeDisplayMenu(CostumeMenu previousMenu, CustomModelFolder folder) {
				super(previousMenu, folder);
			}

			@Override
			protected CostumeMenu newMenu(CostumeMenu previousMenu, CustomModelFolder subfolder) {
				return new CostumeDisplayMenu(previousMenu, subfolder);
			}

			@Override
			protected boolean isAvailableCostume(CostumeUser user, Costume costume) {
				return true;
			}

			@Override
			protected ClickableItem formatCostume(CostumeUser user, Costume costume, InventoryContents contents) {
				final ItemBuilder builder = new ItemBuilder(costume.getModel().getDisplayItem());
				if (costume.getId().equals(user.getActiveDisplayCostume()))
					builder.lore("", "&a&lActive").glow();

				return ClickableItem.from(builder.build(), e -> {
					user.setActiveDisplayCostume(costume.getId().equals(user.getActiveDisplayCostume()) ? null : costume);
					service.save(user);
					open(user.getOnlinePlayer(), contents.pagination().getPage());
				});
			}
		}

		@Override
		public void onNpcInteract(Player player) {
			new CostumeDisplayMenu().open(player);
		}
	},

	@Category(GalleryCategory.VISUALS)
	WINGS(4306) {
		@Override
		public void onNpcInteract(Player player) {
			new EffectSettingProvider(ParticleType.WINGS, entity()).open(player);
		}
	},

	@Category(GalleryCategory.VISUALS)
	INVISIBLE_ARMOR,

	@Category(GalleryCategory.VISUALS)
	PLAYER_PLUSHIES(4547) {
		private static boolean initialized = false;

		@Override
		public void init() {
			Tasks.repeat(TickTime.SECOND, TickTime.MINUTE, () -> {
				try {
					npc().setBukkitEntityType(EntityType.ITEM_FRAME);
					final ItemFrame itemFrame = entity();
					itemFrame.setFacingDirection(BlockFace.UP);
					itemFrame.setRotation(Rotation.FLIPPED_45);
					itemFrame.setVisible(false);
					itemFrame.setSilent(true);

					if (!initialized)
						itemFrame.setItem(getRandomPlushie());

					initialized = true;
				} catch (Exception ignore) {}
			});
		}

		@Override
		public void onNpcInteract(Player player) {
			final ItemFrame itemFrame = entity();
			itemFrame.setRotation(Rotation.FLIPPED);
			itemFrame.setItem(getRandomPlushie());
		}

		private ItemStack getRandomPlushie() {
			return new ItemBuilder(PlayerPlushieConfig.MATERIAL).customModelData(PlayerPlushieConfig.randomActive()).build();
		}
	},

	@Category(GalleryCategory.VISUALS)
	NPCS(4531) {
		@Override
		public void onNpcInteract(Player player) {
			if (!cooldown(TickTime.SECOND.x(.5)))
				return;

			NPCRandomizer.randomize(npcId, player);
			npc().teleport(CitizensUtils.locationOf(npc()).clone().add(0, .25, 0), TeleportCause.PLUGIN);
		}
	},

	@Category(GalleryCategory.VISUALS)
	FIREWORKS {
		public Location getLaunchLocation() {
			return StoreGallery.location(1065.5, 69, 983.5);
		}

		@Override
		public void onImageInteract(Player player) {
			if (!cooldown(TickTime.SECOND.x(5)))
				return;

			FireworkLauncher.random(getLaunchLocation())
				.detonateAfter(13)
				.silent(true)
				.launch();
		}
	},

	@Category(GalleryCategory.VISUALS)
	RAINBOW_ARMOR(4530) {
		private static RainbowArmorTask task;

		@Override
		public void init() {
			Tasks.repeat(0, TickTime.SECOND.x(5), () -> {
				if (npc() == null)
					return;

				final int nearby = recipients().size();

				if (nearby == 0) {
					if (isActive())
						stop();
				} else {
					if (!isActive())
						start();
				}
			});
		}

		private List<Player> recipients() {
			return OnlinePlayers.where()
				.radius(CitizensUtils.locationOf(RAINBOW_ARMOR.getNpcId()), 35)
				.get();
		}

		private boolean isActive() {
			return task != null;
		}

		private void start() {
			task = RainbowArmorTask.builder()
				.entity(entity())
				.build()
				.start();
		}

		private void stop() {
			if (task != null) {
				task.stop();
				task = null;
			}
		}
	},

	// TODO Activate faster?
	@Category(GalleryCategory.VISUALS)
	RAINBOW_BEACON {
		public Location getBeaconGlassLocation() {
			return StoreGallery.location(1064, 67, 990);
		}

		@NotNull
		private Block getBeaconLocation() {
			return getBeaconGlassLocation().getBlock().getRelative(BlockFace.DOWN);
		}

		@NotNull
		private Block getIronBlockLocation() {
			return getBeaconGlassLocation().getBlock().getRelative(BlockFace.DOWN, 2);
		}

		private RainbowBeacon getUser() {
			return new RainbowBeaconService().get0();
		}

		@Override
		public void onImageInteract(Player player) {
			if (!cooldown(TickTime.SECOND.x(25)))
				return;

			getIronBlockLocation().setType(Material.IRON_BLOCK);
			final RainbowBeacon user = getUser();
			user.setLocation(getBeaconGlassLocation());
			user.start();
			Tasks.wait(TickTime.SECOND.x(20), () -> {
				user.stop();
				getIronBlockLocation().setType(Material.AIR);
			});
		}

		@Override
		public void shutdown() {
			getUser().stop();
			getIronBlockLocation().setType(Material.AIR);
		}
	},

	@Category(GalleryCategory.VISUALS)
	ENTITY_NAME,

	@Category(GalleryCategory.CHAT)
	PREFIX {
		@Override
		public void onImageInteract(Player player) {
			if (!cooldown(TickTime.MINUTE))
				return;

			PlayerUtils.send(player, "&c/prefix test <prefix...>");
			PlayerUtils.send(player, "&c/prefix test gradient <colors> <prefix...>");
			PlayerUtils.send(player, "&c/prefix test rainbow <prefix...>");
		}
	},

	@Category(GalleryCategory.CHAT)
	NICKNAME,

	@Category(GalleryCategory.CHAT)
	JOIN_QUIT {
		@Override
		public void onImageInteract(Player player) {
			if (!cooldown(TickTime.SECOND.x(3)))
				return;

			final boolean join = randomElement(true, false);
			String message = randomElement(join ? JoinQuit.getJoinMessages() : JoinQuit.getQuitMessages());
			message = join ? JoinQuit.formatJoin(player, message) : JoinQuit.formatQuit(player, message);
			PlayerUtils.send(player, "&6&l[Example] " + message);
		}
	},

	@Category(GalleryCategory.CHAT)
	EMOTES {
		@Override
		public void onImageInteract(Player player) {
			final Emotes emote = random(Emotes.class);
			String result = emote.getEmote();
			if (emote.getColors().size() > 0)
				result = randomElement(emote.getColors()) + result;

			PlayerUtils.send(player, "&6&l[Example] " + result);
		}
	},

	@Category(GalleryCategory.PETS_DISGUISES)
	PETS(4527) {
		@Override
		public void onNpcInteract(Player player) {
			if (!cooldown(TickTime.SECOND.x(.5)))
				return;

			NPCRandomizer.randomize(npcId);
			npc().teleport(CitizensUtils.locationOf(npc()).clone().add(0, .25, 0), TeleportCause.PLUGIN);
		}
	},

	@Category(GalleryCategory.PETS_DISGUISES)
	DISGUISES,

	@Category(GalleryCategory.INVENTORY)
	AUTO_INVENTORY,

	@Category(GalleryCategory.INVENTORY)
	AUTO_TORCH {
		private static final Map<UUID, ExampleTorcher> torchers = new HashMap<>();

		@Data
		static class ExampleTorcher {
			@NonNull
			protected Player player;
			private List<ExampleTorch> torches = new ArrayList<>();
			private int taskId;

			public void start() {
				stop();

				taskId = Tasks.repeat(TickTime.SECOND, TickTime.SECOND.x(2), () -> {
					if (!player.getWorld().equals(StoreGallery.getWorld()))
						stop();

					hideOldTorches();
					placeNewTorch();
				});

				Tasks.wait(TickTime.SECOND.x(30), this::stop);
			}

			private void stop() {
				Tasks.cancel(taskId);
				taskId = -1;
				torches.forEach(ExampleTorch::hide);
				torches.clear();
			}

			private void placeNewTorch() {
				final Block block = player.getLocation().getBlock();
				if (block.getType() != Material.AIR)
					return;

				if (!MaterialTag.BLOCKS.isTagged(block.getRelative(BlockFace.DOWN).getType()))
					return;

				torches.removeIf(torch -> torch.getBlock().getLocation().equals(block.getLocation()));
				torches.add(new ExampleTorch(block).show());
			}

			private void hideOldTorches() {
				new ArrayList<>(torches).forEach(torch -> {
					final LocalDateTime expiration = LocalDateTime.now().minusSeconds(10);
					if (torch.getCreatedAt().isAfter(expiration))
						return;

					torch.hide();
					torches.remove(torch);
				});
			}

			@Data
			class ExampleTorch {
				@NonNull
				private Block block;
				private LocalDateTime createdAt = LocalDateTime.now();

				public ExampleTorch show() {
					sendBlockChange(Material.TORCH);
					return this;
				}

				public void hide() {
					sendBlockChange(Material.AIR);
				}

				private void sendBlockChange(Material material) {
					player.sendBlockChange(block.getLocation(), Bukkit.createBlockData(material));
				}
			}
		}

		@Override
		public void onImageInteract(Player player) {
			if (!cooldown(TickTime.SECOND.x(35)))
				return;

			torchers.computeIfAbsent(player.getUniqueId(), $ -> new ExampleTorcher(player)).start();
		}
	},

	@Category(GalleryCategory.INVENTORY)
	FIREWORK_BOW {
		public Location getLaunchLocation() {
			return StoreGallery.location(943.5, 69, 972.5);
		}

		@Override
		public void onImageInteract(Player player) {
			if (!cooldown(TickTime.SECOND.x(5)))
				return;

			FireworkLauncher.random(getLaunchLocation())
				.detonateAfter(13)
				.silent(true)
				.launch();
		}
	},

	@Category(GalleryCategory.INVENTORY)
	HAT(4546) {
		@Override
		public void onNpcInteract(Player player) {
			inventory().setItem(EquipmentSlot.HEAD, new ItemStack(MaterialTag.ITEMS.random()));
		}
	},

	@Category(GalleryCategory.INVENTORY)
	VAULTS {
		@Override
		public void onImageInteract(Player player) {
			player.openInventory(Bukkit.createInventory(null, 3 * 9, new JsonBuilder("&4Vault #1").build()));
		}
	},

	@Category(GalleryCategory.INVENTORY)
	WORKBENCHES {
		@Override
		public void onImageInteract(Player player) {
			new WorkbenchesMenu().open(player);
		}
	},

	@Category(GalleryCategory.INVENTORY)
	ITEM_NAME {
		private Hologram hologram;

		@Override
		public void init() {
			final Location location = StoreGallery.location(950.5, 70.5, 972.5);
			hologram = HologramsAPI.createHologram(Nexus.getInstance(), location);
			hologram.appendTextLine(colorize("&eBob"));
			hologram.appendItemLine(new ItemStack(Material.DIAMOND_SWORD));
		}

		@Override
		public void shutdown() {
			hologram.delete();
		}
	},

	@Category(GalleryCategory.INVENTORY)
	PLAYER_HEAD {
		private Hologram hologram;

		@Override
		public void init() {
			final Location location = StoreGallery.location(1048.5, 70.25, 991.5);
			hologram = HologramsAPI.createHologram(Nexus.getInstance(), location);

			final ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD);

			// TODO Save last player?
			final List<Player> players = OnlinePlayers.getAll();
			builder.skullOwner(players.isEmpty() ? randomElement(EnumUtils.valuesExcept(Dev.class, Dev.SPIKE)) : randomElement(players));

			final ItemLine itemLine = hologram.appendItemLine(builder.build());
			itemLine.setTouchHandler(player -> itemLine.setItemStack(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player).build()));
		}

		@Override
		public void shutdown() {
			hologram.delete();
		}
	},

	@Category(GalleryCategory.MISC)
	CUSTOM_DONATION {
		public Location getLocation() {
			return StoreGallery.location(967.5, 71.5, 992.5);
		}

		@Override
		public void onImageInteract(Player player) {
			if (!cooldown(TickTime.SECOND))
				return;

			new ParticleBuilder(Particle.HEART)
				.receivers(player)
				.location(getLocation())
				.offset(1, 0.75, 0.2)
				.extra(0)
				.count(15)
				.spawn();
		}
	},

	@Category(GalleryCategory.MISC)
	PLUS_FIVE_HOMES {
		@Override
		public void onImageInteract(Player player) {
			PlayerUtils.runCommand(player, "homes limit");
		}
	},

	// TODO Doesn't work (see listener)
	@Category(GalleryCategory.MISC)
	CREATIVE_PLOTS {
		@Override
		public void onImageInteract(Player player) {
			PlayerUtils.runCommand(player, "plots limit");
		}
	},

	@Category(GalleryCategory.MISC)
	BOOSTS,
	;

	public int npcId;

	public void init() {}

	public void onNpcInteract(Player player) {}

	public void onImageInteract(Player player) {}

	public void shutdown() {}

	public String getRegionId() {
		return "store_gallery__" + getCategory().name().toLowerCase() + "__" + name().toLowerCase();
	}

	protected boolean cooldown(TickTime time) {
		return cooldown(time.get());
	}

	protected boolean cooldown(int ticks) {
		return new CooldownService().check(getUUID0(), getRegionId(), ticks);
	}

	public NPC npc() {
		if (npcId > 0)
			return CitizensUtils.getNPC(npcId);
		return null;
	}

	public <T extends Entity> T entity() {
		final NPC npc = npc();
		if (npc == null || npc.getEntity() == null)
			return null;
		return (T) npc.getEntity();
	}

	public HumanEntity humanEntity() {
		return entity();
	}

	public PlayerInventory inventory() {
		return humanEntity().getInventory();
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public GalleryCategory getCategory() {
		return getField().getAnnotation(Category.class).value();
	}

	public static void onStop() {
		for (GalleryPackage galleryPackage : GalleryPackage.values())
			galleryPackage.shutdown();
	}

	public static void onStart() {
		for (GalleryPackage galleryPackage : GalleryPackage.values())
			galleryPackage.init();
	}

}
