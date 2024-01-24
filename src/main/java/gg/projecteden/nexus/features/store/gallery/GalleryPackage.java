package gg.projecteden.nexus.features.store.gallery;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.chat.Emotes;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.particles.providers.EffectSettingProvider;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.features.store.Package;
import gg.projecteden.nexus.features.store.StoreCommand;
import gg.projecteden.nexus.features.store.annotations.Category.StoreCategory;
import gg.projecteden.nexus.features.store.gallery.annotations.Category;
import gg.projecteden.nexus.features.store.gallery.annotations.Category.GalleryCategory;
import gg.projecteden.nexus.features.store.gallery.annotations.RealCategory;
import gg.projecteden.nexus.features.store.perks.chat.joinquit.JoinQuit;
import gg.projecteden.nexus.features.store.perks.inventory.workbenches.WorkbenchesCommand.WorkbenchesMenu;
import gg.projecteden.nexus.features.store.perks.visuals.CostumeCommand.CostumeMenu;
import gg.projecteden.nexus.features.vaults.VaultCommand.VaultMenu;
import gg.projecteden.nexus.features.workbenches.DyeStation;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.Costume.CostumeType;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmorTask;
import gg.projecteden.nexus.models.rainbowbeacon.RainbowBeacon;
import gg.projecteden.nexus.models.rainbowbeacon.RainbowBeaconService;
import gg.projecteden.nexus.models.vaults.VaultUserService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.CitizensUtils.NPCRandomizer;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
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
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.line.ItemLine;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static gg.projecteden.api.common.utils.EnumUtils.random;
import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;
import static gg.projecteden.nexus.features.store.BuycraftUtils.ADD_TO_CART_URL;
import static gg.projecteden.nexus.features.store.BuycraftUtils.CATEGORY_URL;
import static gg.projecteden.nexus.utils.RandomUtils.randomElement;

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
			protected void init(CostumeUser user, InventoryContents contents) {
				for (CostumeType type : CostumeType.values()) {
					final Costume costume = user.getActiveDisplayCostume(type);
					if (costume != null) {
						final ItemBuilder builder = new ItemBuilder(user.getCostumeDisplayItem(costume))
							.lore("", "&a&lActive", "&cClick to deactivate")
							.glow();

						contents.set(0, type.getMenuHeaderSlot(), ClickableItem.of(builder.build(), e -> {
							user.setActiveDisplayCostume(type, null);
							service.save(user);
							open(user.getOnlinePlayer(), contents.pagination().getPage());
						}));

						if (MaterialTag.DYEABLE.isTagged(costume.getItem().getType())) {
							contents.set(0, type.getMenuHeaderSlot() + 1, ClickableItem.of(DyeStation.getWorkbench().build(), e ->
								new DyeStationMenu().openCostume(user, costume, data -> {
									user.dye(costume, data.getColor());
									service.save(user);
									open(user.getOnlinePlayer());
								})));
						}
					}
				}
			}

			@Override
			protected ClickableItem formatCostume(CostumeUser user, Costume costume, InventoryContents contents) {
				final ItemBuilder builder = new ItemBuilder(user.getCostumeDisplayItem(costume));
				if (user.hasDisplayCostumeActivated(costume))
					builder.lore("", "&a&lActive").glow();

				return ClickableItem.of(builder.build(), e -> {
					user.setActiveDisplayCostume(costume.getType(), user.hasDisplayCostumeActivated(costume) ? null : costume);
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
	PARTICLE_WINGS(4306) {
		@Override
		public void onNpcInteract(Player player) {
			new EffectSettingProvider(ParticleType.WINGS, entity()).open(player);
		}
	},

	@Category(GalleryCategory.VISUALS)
	INVISIBLE_ARMOR,

	@Category(GalleryCategory.VISUALS)
	PLAYER_PLUSHIES {
		@Override
		public void onEntityInteract(Player player, Entity entity) {
			if (!(entity instanceof ItemFrame itemFrame))
				return;

			itemFrame.setSilent(true);
			itemFrame.setItem(getRandomPlushie(), false);
			new SoundBuilder(Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM).location(itemFrame).play();
		}

		private ItemStack getRandomPlushie() {
			return new ItemBuilder(PlayerPlushieConfig.MATERIAL).modelId(PlayerPlushieConfig.random()).build();
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
				.detonateAfter(13L)
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
	@RealCategory(StoreCategory.PETS)
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
	@RealCategory(StoreCategory.DISGUISES)
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
				.detonateAfter(13L)
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
			new VaultMenu(player, new VaultUserService().get(player), 0);
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
			hologram = HologramsAPI.builder()
				.location(location)
				.lines("&eBob", new ItemStack(Material.DIAMOND_SWORD))
				.spawn();
		}

		@Override
		public void shutdown() {
			if (hologram != null)
				hologram.remove();
		}
	},

	@Category(GalleryCategory.INVENTORY)
	PLAYER_HEAD {
		private Hologram hologram;

		@Override
		public void init() {
			final Location location = StoreGallery.location(1048.5, 70.25, 991.5);
			final ItemBuilder builder = new ItemBuilder(Material.PLAYER_HEAD);

			// TODO Save last player?
			final List<Player> players = OnlinePlayers.getAll();
			builder.skullOwner(players.isEmpty() ? randomElement(EnumUtils.valuesExcept(Dev.class, Dev.SPIKE)) : randomElement(players));

			hologram = HologramsAPI.builder()
				.lines(builder.build())
				.location(location).build();

			final ItemLine itemLine = (ItemLine) hologram.getLines().get(0);
			itemLine.setItemTransform(ItemDisplay.ItemDisplayTransform.GROUND);
			itemLine.setClickListener(player -> {
				itemLine.setItem(new ItemBuilder(Material.PLAYER_HEAD).skullOwner((Player) player).build());
				itemLine.getHologram().update();
			});
			hologram.update();
		}

		@Override
		public void shutdown() {
			if (hologram != null)
				hologram.remove();
		}
	},

	@Category(GalleryCategory.MISC)
	STORE_CREDIT {
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
	@RealCategory(StoreCategory.BOOSTS)
	BOOSTS,
	;

	public int npcId;

	public void init() {}

	public void onNpcInteract(Player player) {}

	public void onImageInteract(Player player) {}

	public void onEntityInteract(Player player, Entity entity) {}

	public void onClickCart(Player player) {
		String url;
		try {
			url = ADD_TO_CART_URL.formatted(getStorePackage().getId());
		} catch (IllegalArgumentException ex) {
			url = CATEGORY_URL.formatted(getCategoryId());
		}

		new JsonBuilder(StoreCommand.PREFIX + "&eClick here &3to open the store").url(url).send(player);
	}

	@NotNull
	private Package getStorePackage() {
		return Package.valueOf(name());
	}

	public void shutdown() {}

	public String getRegionId() {
		return "store_gallery__" + getCategory().name().toLowerCase() + "__" + name().toLowerCase();
	}

	protected boolean cooldown(TickTime time) {
		return cooldown(time.get());
	}

	protected boolean cooldown(long ticks) {
		return new CooldownService().check(UUID0, getRegionId(), ticks);
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

	public String getCategoryId() {
		final RealCategory annotation = getField().getAnnotation(RealCategory.class);
		final GalleryCategory galleryCategory = getCategory();
		final StoreCategory storeCategory = annotation == null ? null : annotation.value();
		if (storeCategory == null)
			return galleryCategory.name().toLowerCase();
		else
			return storeCategory.name().toLowerCase();
	}

	public static void onStop() {
		for (GalleryPackage galleryPackage : GalleryPackage.values())
			galleryPackage.shutdown();
	}

	public static void onStart() {
		for (GalleryPackage galleryPackage : GalleryPackage.values())
			galleryPackage.init();
	}

	public static GalleryPackage of(NPC npc) {
		for (GalleryPackage galleryPackage : GalleryPackage.values())
			if (galleryPackage.getNpcId() == npc.getId())
				return galleryPackage;

		return null;
	}

}
