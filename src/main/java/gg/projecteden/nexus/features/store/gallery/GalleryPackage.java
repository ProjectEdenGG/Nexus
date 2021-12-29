package gg.projecteden.nexus.features.store.gallery;


import gg.projecteden.nexus.features.mobheads.MobHeadType;
import gg.projecteden.nexus.features.store.gallery.annotations.Category;
import gg.projecteden.nexus.features.store.gallery.annotations.Category.GalleryCategory;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmor;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmorService;
import gg.projecteden.nexus.models.rainbowbeacon.RainbowBeacon;
import gg.projecteden.nexus.models.rainbowbeacon.RainbowBeaconService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

import static gg.projecteden.utils.RandomUtils.randomElement;
import static gg.projecteden.utils.StringUtils.getUUID0;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum GalleryPackage {
	@Category(GalleryCategory.VISUALS)
	COSTUMES,

	@Category(GalleryCategory.VISUALS)
	WINGS,

	@Category(GalleryCategory.VISUALS)
	INVISIBLE_ARMOR,

	@Category(GalleryCategory.VISUALS)
	PLAYER_PLUSHIES,

	@Category(GalleryCategory.VISUALS)
	NPCS(4531) {
		private static final List<MobHeadType> disabledEntityTypes = List.of(MobHeadType.WITHER, MobHeadType.GHAST,
			MobHeadType.ENDER_DRAGON, MobHeadType.ELDER_GUARDIAN, MobHeadType.IRON_GOLEM, MobHeadType.RAVAGER,
			MobHeadType.HOGLIN, MobHeadType.ZOGLIN);

		private static final List<MobHeadType> availableTypes = EnumUtils.valuesExcept(MobHeadType.class, disabledEntityTypes.toArray(Enum<?>[]::new));

		@Override
		public void onNpcInteract(Player player) {
			if (!cooldown(TickTime.SECOND))
				return;

			final MobHeadType mob = randomElement(availableTypes);

			npc().setBukkitEntityType(mob.getEntityType());

			if (mob.getEntityType() == EntityType.PLAYER)
				CitizensUtils.updateSkin(npcId, player.getName());
			else if (mob.hasVariants())
				mob.getVariantSetter().accept(npc().getEntity(), mob.getRandomVariant().get());
		}
	},

	@Category(GalleryCategory.VISUALS)
	FIREWORKS {
		public Location getLaunchLocation() {
			return new Location(StoreGallery.getWorld(), 1057.5, 69, 983.5);
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
		static {
			if (false) // TODO
			Tasks.repeat(0, TickTime.SECOND.x(5), () -> {
				final int nearby = OnlinePlayers.where()
					.radius(CitizensUtils.locationOf(RAINBOW_ARMOR.getNpcId()), 50)
					.get().size();

				if (nearby == 0) {
					if (isActive())
						deactivate();
				} else {
					if (!isActive())
						activate();
				}
			});
		}

		private static RainbowArmor getUser() {
			return new RainbowArmorService().get(RAINBOW_ARMOR.npc().getUniqueId());
		}

		private static boolean isActive() {
			return getUser().isEnabled();
		}

		private static void activate() {
			getUser().startArmor();
		}

		private static void deactivate() {
			getUser().stopArmor();
		}
	},

	// TODO Activate faster?
	@Category(GalleryCategory.VISUALS)
	RAINBOW_BEACON {
		public Location getBeaconGlassLocation() {
			return new Location(StoreGallery.getWorld(), 1064, 67, 990);
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

			final RainbowBeacon user = getUser();
			user.setLocation(getBeaconGlassLocation());
			user.start();
			getIronBlockLocation().setType(Material.IRON_BLOCK);
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
	PLAYER_TIME,

	@Category(GalleryCategory.VISUALS)
	ENTITY_NAME,

	@Category(GalleryCategory.CHAT)
	PREFIXES,

	@Category(GalleryCategory.CHAT)
	NICKNAMES,

	@Category(GalleryCategory.CHAT)
	JOIN_QUIT,

	@Category(GalleryCategory.CHAT)
	EMOTES,

	@Category(GalleryCategory.PETS_DISGUISES)
	PETS,

	@Category(GalleryCategory.PETS_DISGUISES)
	DISGUISES,

	@Category(GalleryCategory.INVENTORY)
	AUTOSORT,

	@Category(GalleryCategory.INVENTORY)
	AUTOTORCH,

	@Category(GalleryCategory.INVENTORY)
	VAULTS,

	@Category(GalleryCategory.INVENTORY)
	WORKBENCHES,

	@Category(GalleryCategory.INVENTORY)
	ITEM_NAME,

	@Category(GalleryCategory.INVENTORY)
	HAT,

	@Category(GalleryCategory.INVENTORY)
	FIREWORK_BOW,

	@Category(GalleryCategory.INVENTORY)
	SKULL,

	@Category(GalleryCategory.MISC)
	CUSTOM_CONTRIBUTION,

	@Category(GalleryCategory.MISC)
	PLUS_5_HOMES,

	@Category(GalleryCategory.MISC)
	PLOTS,

	@Category(GalleryCategory.MISC)
	BOOSTS,
	;

	public int npcId;

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

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public GalleryCategory getCategory() {
		return getField().getAnnotation(Category.class).value();
	}

}
