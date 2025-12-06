package gg.projecteden.nexus.features.store;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.commands.EmotesCommand;
import gg.projecteden.nexus.features.commands.staff.admin.PermHelperCommand;
import gg.projecteden.nexus.features.commands.staff.admin.PermHelperCommand.NumericPermission;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.store.annotations.Category;
import gg.projecteden.nexus.features.store.annotations.Category.StoreCategory;
import gg.projecteden.nexus.features.store.annotations.Commands.Command;
import gg.projecteden.nexus.features.store.annotations.Display;
import gg.projecteden.nexus.features.store.annotations.ExpirationCommands.ExpirationCommand;
import gg.projecteden.nexus.features.store.annotations.ExpirationDays;
import gg.projecteden.nexus.features.store.annotations.Id;
import gg.projecteden.nexus.features.store.annotations.PermissionGroup;
import gg.projecteden.nexus.features.store.annotations.Permissions.Permission;
import gg.projecteden.nexus.features.store.annotations.World;
import gg.projecteden.nexus.features.store.perks.chat.NicknameCommand;
import gg.projecteden.nexus.features.store.perks.chat.PrefixCommand;
import gg.projecteden.nexus.features.store.perks.inventory.AutoTorchCommand;
import gg.projecteden.nexus.features.store.perks.inventory.HatCommand;
import gg.projecteden.nexus.features.store.perks.inventory.InvisibleArmorCommand;
import gg.projecteden.nexus.features.store.perks.inventory.ItemNameCommand;
import gg.projecteden.nexus.features.store.perks.inventory.PlayerHeadCommand;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventory;
import gg.projecteden.nexus.features.store.perks.inventory.workbenches._WorkbenchCommand;
import gg.projecteden.nexus.features.store.perks.visuals.EntityNameCommand;
import gg.projecteden.nexus.features.store.perks.visuals.FireworkCommand;
import gg.projecteden.nexus.features.store.perks.visuals.PlayerTimeCommand;
import gg.projecteden.nexus.features.store.perks.visuals.RainbowArmorCommand;
import gg.projecteden.nexus.features.store.perks.visuals.RainbowBeaconCommand;
import gg.projecteden.nexus.features.store.perks.visuals.WingsCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.autotorch.AutoTorchService;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.BoosterService;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfigService;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUserService;
import gg.projecteden.nexus.models.scheduledjobs.jobs.PackageExpireJob;
import gg.projecteden.nexus.models.store.Contributor;
import gg.projecteden.nexus.models.store.ContributorService;
import gg.projecteden.nexus.models.vaults.VaultUser;
import gg.projecteden.nexus.models.vaults.VaultUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.SneakyThrows;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public enum Package {

	@Id("5246465")
	@Category(StoreCategory.MISC)
	STORE_CREDIT {
		@Override
		public boolean has(Contributor player) {
			ContributorService contributorService = new ContributorService();
			Contributor contributor = contributorService.get(player);
			return contributor.getPurchases().stream().anyMatch(purchase -> purchase.getPackageId().equals(getId()));
		}
	},

	@Id("4425727")
	@Category(StoreCategory.CHAT)
	@Permission(NicknameCommand.PERMISSION)
	@Display(Material.NAME_TAG)
	NICKNAME,

	@Id("4425728")
	@Category(StoreCategory.CHAT)
	@Permission(NicknameCommand.PERMISSION)
	@ExpirationDays(30)
	@ExpirationCommand("nickname reset [player]")
	@Display(Material.NAME_TAG)
	NICKNAME_ONE_MONTH,

	@Id("1922887")
	@Category(StoreCategory.CHAT)
	@Permission(PrefixCommand.PERMISSION)
	@Display(Material.OAK_SIGN)
	PREFIX,

	@Id("2730030")
	@Category(StoreCategory.CHAT)
	@Permission(PrefixCommand.PERMISSION)
	@ExpirationDays(30)
	@ExpirationCommand("prefix reset [player]")
	@Display(Material.OAK_SIGN)
	CUSTOM_PREFIX_ONE_MONTH,

	@Id("2965488")
	@Category(StoreCategory.CHAT)
	@Permission("jq.custom")
	@Display(Material.MAGENTA_GLAZED_TERRACOTTA)
	JOIN_QUIT,

	@Id("2965489")
	@Category(StoreCategory.CHAT)
	@Permission("jq.custom")
	@ExpirationDays(30)
	@Display(Material.MAGENTA_GLAZED_TERRACOTTA)
	JOIN_QUIT_ONE_MONTH,

	@Id("3239567")
	@Category(StoreCategory.CHAT)
	@Permission(EmotesCommand.PERMISSION)
	@Display(Material.TOTEM_OF_UNDYING)
	EMOTES,

	@Id("4496330")
	@Category(StoreCategory.BOOSTS)
	EXPERIENCE {
		public Boostable getType() {
			return Boostable.valueOf(name());
		}

		@Override
		public void handleApply(UUID uuid) {
			new BoosterService().edit(uuid, booster -> booster.add(getType(), 2, TickTime.DAY));
		}

		@Override
		public @NotNull ItemBuilder getDisplayItem(UUID uuid) {
			return getType().getDisplayItem();
		}

		@Override
		public int count(Contributor player) {
			return new BoosterService().get(player).count(getType());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("4496331")
	@Category(StoreCategory.BOOSTS)
	MCMMO_EXPERIENCE {
		public Boostable getType() {
			return Boostable.valueOf(name());
		}

		@Override
		public void handleApply(UUID uuid) {
			new BoosterService().edit(uuid, booster -> booster.add(getType(), 1.5, TickTime.DAY));
		}

		@Override
		public @NotNull ItemBuilder getDisplayItem(UUID uuid) {
			return getType().getDisplayItem();
		}

		@Override
		public int count(Contributor player) {
			return new BoosterService().get(player).count(getType());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Disabled
	@Id("4496334")
	@Category(StoreCategory.BOOSTS)
	MARKET_SELL_PRICES {
		public Boostable getType() {
			return Boostable.valueOf(name());
		}

		@Override
		public void handleApply(UUID uuid) {
			new BoosterService().edit(uuid, booster -> booster.add(getType(), 1.5, TickTime.DAY));
		}

		@Override
		public @NotNull ItemBuilder getDisplayItem(UUID uuid) {
			return getType().getDisplayItem();
		}

		@Override
		public int count(Contributor player) {
			return new BoosterService().get(player).count(getType());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("4496336")
	@Category(StoreCategory.BOOSTS)
	MYSTERY_CRATE_KEY {
		public Boostable getType() {
			return Boostable.valueOf(name());
		}

		@Override
		public void handleApply(UUID uuid) {
			new BoosterService().edit(uuid, booster -> booster.add(getType(), 2.5, TickTime.DAY));
		}

		@Override
		public @NotNull ItemBuilder getDisplayItem(UUID uuid) {
			return getType().getDisplayItem();
		}

		@Override
		public int count(Contributor player) {
			return new BoosterService().get(player).count(getType());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("4640794")
	@Category(StoreCategory.BOOSTS)
	MOB_HEADS {
		public Boostable getType() {
			return Boostable.valueOf(name());
		}

		@Override
		public void handleApply(UUID uuid) {
			new BoosterService().edit(uuid, booster -> booster.add(getType(), 1.25, TickTime.DAY));
		}

		@Override
		public @NotNull ItemBuilder getDisplayItem(UUID uuid) {
			return getType().getDisplayItem();
		}

		@Override
		public int count(Contributor player) {
			return new BoosterService().get(player).count(getType());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("4496339")
	@Category(StoreCategory.BOOSTS)
	VOTE_POINTS {
		public Boostable getType() {
			return Boostable.valueOf(name());
		}

		@Override
		public void handleApply(UUID uuid) {
			new BoosterService().edit(uuid, booster -> booster.add(getType(), 1.5, TickTime.DAY));
		}

		@Override
		public @NotNull ItemBuilder getDisplayItem(UUID uuid) {
			return getType().getDisplayItem();
		}

		@Override
		public int count(Contributor player) {
			return new BoosterService().get(player).count(getType());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("4496340")
	@Category(StoreCategory.BOOSTS)
	KILLER_MONEY {
		public Boostable getType() {
			return Boostable.valueOf(name());
		}

		@Override
		public void handleApply(UUID uuid) {
			new BoosterService().edit(uuid, booster -> booster.add(getType(), 2, TickTime.DAY));
		}

		@Override
		public @NotNull ItemBuilder getDisplayItem(UUID uuid) {
			return getType().getDisplayItem();
		}

		@Override
		public int count(Contributor player) {
			return new BoosterService().get(player).count(getType());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("4496341")
	@Category(StoreCategory.BOOSTS)
	MINIGAME_DAILY_TOKENS {
		public Boostable getType() {
			return Boostable.valueOf(name());
		}

		@Override
		public void handleApply(UUID uuid) {
			new BoosterService().edit(uuid, booster -> booster.add(getType(), 2, TickTime.DAY));
		}

		@Override
		public @NotNull ItemBuilder getDisplayItem(UUID uuid) {
			return getType().getDisplayItem();
		}

		@Override
		public int count(Contributor player) {
			return new BoosterService().get(player).count(getType());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("4714837")
	@Category(StoreCategory.BOOSTS)
	HALLOWEEN_CANDY {
		public Boostable getType() {
			return Boostable.valueOf(name());
		}

		@Override
		public void handleApply(UUID uuid) {
			new BoosterService().edit(uuid, booster -> booster.add(getType(), 2, TickTime.DAY));
		}

		@Override
		public @NotNull ItemBuilder getDisplayItem(UUID uuid) {
			return getType().getDisplayItem();
		}

		@Override
		public int count(Contributor player) {
			return new BoosterService().get(player).count(getType());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("7060626")
	@Category(StoreCategory.BOOSTS)
	HALLOWEEN_CRATE_KEY {
		public Boostable getType() {
			return Boostable.valueOf(name());
		}

		@Override
		public void handleApply(UUID uuid) {
			new BoosterService().edit(uuid, booster -> booster.add(getType(), 2, TickTime.DAY));
		}

		@Override
		public @NotNull ItemBuilder getDisplayItem(UUID uuid) {
			return getType().getDisplayItem();
		}

		@Override
		public int count(Contributor player) {
			return new BoosterService().get(player).count(getType());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("7155397")
	@Category(StoreCategory.VISUALS)
	@Display(model = ItemModelType.CRATE_KEY_MYSTERY)
	MYSTERY_CRATE_KEY_1 {
		@Override
		public void handleApply(UUID uuid) {
			var keys = new ItemBuilder(CrateType.MYSTERY.getKey()).amount(5);
			PlayerUtils.giveItemAndMailExcess(Nerd.of(uuid), keys.build(), "1 Mystery Crate Key", WorldGroup.SURVIVAL);
		}
	},

	@Id("7155399")
	@Category(StoreCategory.VISUALS)
	@Display(model = ItemModelType.CRATE_KEY_MYSTERY)
	MYSTERY_CRATE_KEY_5 {
		@Override
		public void handleApply(UUID uuid) {
			var keys = new ItemBuilder(CrateType.MYSTERY.getKey()).amount(5);
			PlayerUtils.giveItemAndMailExcess(Nerd.of(uuid), keys.build(), "5 Mystery Crate Keys", WorldGroup.SURVIVAL);
		}
	},

	@Id("4610203")
	@Category(StoreCategory.VISUALS)
	@Display(model = ItemModelType.COSTUMES_GG_HAT)
	COSTUMES {
		@Override
		public void handleApply(UUID uuid) {
			new CostumeUserService().edit(uuid, user -> user.addVouchers(1));
		}

		@Override
		public boolean has(Contributor player) {
			final CostumeUser user = new CostumeUserService().get(player);
			return user.getOwnedCostumes().size() + user.getVouchers() >= 1;
		}
	},

	@Id("4610206")
	@Category(StoreCategory.VISUALS)
	@Display(model = ItemModelType.COSTUMES_GG_HAT)
	COSTUMES_5 {
		@Override
		public void handleApply(UUID uuid) {
			new CostumeUserService().edit(uuid, user -> user.addVouchers(5));
		}

		@Override
		public boolean has(Contributor player) {
			final CostumeUser user = new CostumeUserService().get(player);
			return user.getOwnedCostumes().size() + user.getVouchers() >= 5;
		}
	},

	@Id("3218615")
	@Category(StoreCategory.VISUALS)
	@Permission(WingsCommand.PERMISSION)
	@Permission("wings.style.*")
	@Display(Material.ELYTRA)
	PARTICLE_WINGS,

	@Id("2559650")
	@Category(StoreCategory.VISUALS)
	@PermissionGroup("store.npc")
	@Command("/permhelper add npcs [player] 1")
	@Display(Material.ARMOR_STAND)
	NPCS {
		@Override
		public int count(Contributor player) {
			return NumericPermission.NPCS.getLimit(player.getUniqueId());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("2886239")
	@Category(StoreCategory.VISUALS)
	@Permission(InvisibleArmorCommand.PERMISSION)
	@Display(Material.CHAINMAIL_CHESTPLATE)
	INVISIBLE_ARMOR,

	@Id("2495900")
	@Category(StoreCategory.VISUALS)
	@Permission(RainbowArmorCommand.PERMISSION)
	@Display(Material.LEATHER_CHESTPLATE)
	RAINBOW_ARMOR,

	@Id("2856645")
	@Category(StoreCategory.VISUALS)
	@Permission(RainbowBeaconCommand.PERMISSION)
	@Display(Material.BEACON)
	RAINBOW_BEACON,

	@Id("2495885")
	@Category(StoreCategory.VISUALS)
	@Permission(FireworkCommand.PERMISSION)
	@Display(Material.FIREWORK_ROCKET)
	FIREWORKS,

	@Id("4158709")
	@Category(StoreCategory.VISUALS)
	@Permission(EntityNameCommand.PERMISSION)
	@Display(Material.NAME_TAG)
	ENTITY_NAME,

	@Id("2019265")
	@Category(StoreCategory.VISUALS)
	@Permission(PlayerTimeCommand.PERMISSION)
	@Display(Material.DAYLIGHT_DETECTOR)
	PTIME,

	@Id("5614793")
	@Category(StoreCategory.VISUALS)
	@Display(model = ItemModelType.VOUCHER)
	PLAYER_PLUSHIES {
		@Override
		public void handleApply(UUID uuid) {
			new PlayerPlushieConfigService().edit0(config -> config.addOwner(uuid));
			new PlayerPlushieUserService().edit(uuid, user -> user.addVouchers(1));
		}

		@Override
		public int count(Contributor player) {
			return new PlayerPlushieUserService().get(player).getVouchers();
		}

		@Override
		public boolean has(Contributor player) {
			return PlayerPlushieConfig.get().isOwner(player);
		}
	},

	@Id("5614796")
	@Category(StoreCategory.VISUALS)
	@Display(model = ItemModelType.VOUCHER, amount = 5)
	PLAYER_PLUSHIES_5 {
		@Override
		public void handleApply(UUID uuid) {
			new PlayerPlushieConfigService().edit0(config -> config.addOwner(uuid));
			new PlayerPlushieUserService().edit(uuid, user -> user.addVouchers(5));
		}

		@Override
		public int count(Contributor player) {
			return new PlayerPlushieUserService().get(player).getVouchers();
		}

		@Override
		public boolean has(Contributor player) {
			return PlayerPlushieConfig.get().isOwner(player);
		}
	},

	@Id("2019251")
	@Category(StoreCategory.INVENTORY)
	@Permission(AutoInventory.PERMISSION)
	@Display(Material.HOPPER)
	AUTO_INVENTORY,

	@Id("2729981")
	@Category(StoreCategory.INVENTORY)
	@Permission(AutoInventory.PERMISSION)
	@ExpirationDays(30)
	@Display(Material.HOPPER)
	AUTO_INVENTORY_ONE_MONTH,

	@Id("4471430")
	@Category(StoreCategory.INVENTORY)
	@Permission(AutoTorchCommand.PERMISSION)
	@Display(Material.TORCH)
	AUTO_TORCH {
		@Override
		public void handleApply(UUID uuid) {
			new AutoTorchService().edit(uuid, user -> user.setEnabled(true));
		}
	},

	@Id("2559439")
	@Category(StoreCategory.INVENTORY)
	@Permission(ItemNameCommand.PERMISSION)
	@Display(Material.ANVIL)
	ITEM_NAME,

	@Id("4365867")
	@Category(StoreCategory.INVENTORY)
	@Permission(_WorkbenchCommand.PERMISSION)
	@Display(Material.CRAFTING_TABLE)
	WORKBENCHES,

	@Id("2019259")
	@Category(StoreCategory.INVENTORY)
	@Display(Material.ENDER_CHEST)
	VAULTS {
		@Override
		public void handleApply(UUID uuid) {
			new VaultUserService().edit(uuid, VaultUser::increaseLimit);
		}

		@Override
		public int count(Contributor player) {
			return new VaultUserService().get(player).getLimit();
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("2019264")
	@Category(StoreCategory.INVENTORY)
	@Permission(PlayerHeadCommand.PERMISSION)
	@Display(Material.PLAYER_HEAD)
	PLAYER_HEAD,

	@Id("2496109")
	@Category(StoreCategory.INVENTORY)
	@Permission(HatCommand.PERMISSION)
	@Display(Material.DIAMOND_HELMET)
	HAT,

	@Id("2678893")
	@Category(StoreCategory.INVENTORY)
	@Permission("fireworkbow.infinite")
	@Display(Material.BOW)
	FIREWORK_BOW,

	@Id("2678902")
	@Category(StoreCategory.INVENTORY)
	@Permission("fireworkbow.single")
	@Display(Material.BOW)
	FIREWORK_BOW_SINGLE,

	@Id("2019261")
	@Category(StoreCategory.MISC)
	@Display(Material.CYAN_BED)
	PLUS_FIVE_HOMES {
		@Override
		public void handleApply(UUID uuid) {
			new HomeService().edit(uuid, user -> user.addExtraHomes(5));
		}

		@Override
		public int count(Contributor player) {
			return new HomeService().get(player).getExtraHomes();
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("2495909")
	@Category(StoreCategory.MISC)
	@Command("/permhelper add plots [player] 1")
	@World("creative")
	@Display(Material.WOODEN_AXE)
	CREATIVE_PLOTS {
		@Override
		public void handleApply(UUID uuid) {
			PermHelperCommand.add(NumericPermission.PLOTS, uuid, 1);
		}

		@Override
		public int count(Contributor player) {
			return NumericPermission.PLOTS.getLimit(player.getUniqueId());
		}

		@Override
		public boolean has(Contributor player) {
			return count(player) > 0;
		}
	},

	@Id("2495867")
	@Category(StoreCategory.PETS)
	@PermissionGroup("store.pets.farm")
	@Display(Material.CHICKEN_SPAWN_EGG)
	PETS_FARM,

	@Id("2495869")
	@Category(StoreCategory.PETS)
	@PermissionGroup("store.pets.cuties")
	@Display(Material.WOLF_SPAWN_EGG)
	PETS_CUTIES,

	@Id("2495876")
	@Category(StoreCategory.PETS)
	@PermissionGroup("store.pets.natives")
	@Display(Material.VILLAGER_SPAWN_EGG)
	PETS_NATIVES,

	@Id("3919092")
	@Category(StoreCategory.PETS)
	@PermissionGroup("store.pets.aquatic")
	@Display(Material.DOLPHIN_SPAWN_EGG)
	PETS_AQUATIC,

	@Id("2495873")
	@Category(StoreCategory.PETS)
	@PermissionGroup("store.pets.nether")
	@Display(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG)
	PETS_NETHER,

	@Id("2495872")
	@Category(StoreCategory.PETS)
	@PermissionGroup("store.pets.monsters")
	@Display(Material.ZOMBIE_SPAWN_EGG)
	PETS_MONSTERS,

	@Id("2495871")
	@Category(StoreCategory.PETS)
	@PermissionGroup("store.pets.mounts")
	@Display(Material.HORSE_SPAWN_EGG)
	PETS_MOUNTS,

	@Id("2495870")
	@Category(StoreCategory.PETS)
	@PermissionGroup("store.pets.other")
	@Display(Material.SLIME_SPAWN_EGG)
	PETS_OTHER,

	@Id("2496219")
	@Category(StoreCategory.PETS)
	@Display(Material.ARMOR_STAND)
	@Permission("miniaturepets.pet.BB8")
	@Permission("miniaturepets.pet.Bee")
	@Permission("miniaturepets.pet.Boxer")
	@Permission("miniaturepets.pet.Camera")
	@Permission("miniaturepets.pet.Chimp")
	@Permission("miniaturepets.pet.Duck")
	@Permission("miniaturepets.pet.earth")
	@Permission("miniaturepets.pet.FacebookLogo")
	@Permission("miniaturepets.pet.Frog")
	@Permission("miniaturepets.pet.Giraffe")
	@Permission("miniaturepets.pet.Gorilla")
	@Permission("miniaturepets.pet.hamster")
	@Permission("miniaturepets.pet.InstagramLogo")
	@Permission("miniaturepets.pet.King")
	@Permission("miniaturepets.pet.Koala")
	@Permission("miniaturepets.pet.LionCub")
	@Permission("miniaturepets.pet.Lion")
	@Permission("miniaturepets.pet.Milker")
	@Permission("miniaturepets.pet.Milk")
	@Permission("miniaturepets.pet.MiniMe")
	@Permission("miniaturepets.pet.Moon")
	@Permission("miniaturepets.pet.Pug")
	@Permission("miniaturepets.pet.Panda")
	@Permission("miniaturepets.pet.Penguin")
	@Permission("miniaturepets.pet.PerryThePlatipus")
	@Permission("miniaturepets.pet.PolarBearCub")
	@Permission("miniaturepets.pet.Princess")
	@Permission("miniaturepets.pet.Pug")
	@Permission("miniaturepets.pet.Rapina")
	@Permission("miniaturepets.pet.Robo")
	@Permission("miniaturepets.pet.Snowglobe")
	@Permission("miniaturepets.pet.Soccer")
	@Permission("miniaturepets.pet.Summerglobe")
	@Permission("miniaturepets.pet.Sun")
	@Permission("miniaturepets.pet.TigerCub")
	@Permission("miniaturepets.pet.Tiger")
	@Permission("miniaturepets.pet.Turtle")
	@Permission("miniaturepets.pet.TwitchLogo")
	@Permission("miniaturepets.pet.TwitterLogo")
	@Permission("miniaturepets.pet.YouTubeLogo")
	@Permission("miniaturepets.pet.ZombieHead")
	@Permission("miniaturepets.pet.ZombieStatue")
	@Permission("miniaturepets.pet.ZombieStatueAir")
	@Permission("miniaturepets.pet.footballplayer")
	@Permission("miniaturepets.pet.footballplayermini")
	@Permission("pokeblocks.mob.magifish")
	@Permission("pokeblocks.mob.bulbasaur")
	@Permission("minime.edsheeran")
	@Permission("quickpets.ownerpet")
	@Permission("MinaturePets.Giraffe")
	@Permission("puffle.pet.blue")
	@Permission("minecraft.steve")
	@Permission("MinaturePets.Witherboss")
	@Permission("minaturepets.voltorb")
	@Permission("MiniaturePets.Koala")
	@Permission("pokeblocks.mob.pikachu")
	@Permission("miniaturepets.BearCub")
	@Permission("miniaturepets.Cat")
	@Permission("miniaturepets.Dipper")
	@Permission("miniaturepets.Tech")
	@Permission("miniaturepets.FrostySnowman")
	@Permission("miniaturepets.FrosytFriend")
	@Permission("miniaturepets.GrunkleStan")
	@Permission("miniaturepets.Jake")
	@Permission("miniaturepets.King")
	@Permission("miniaturepets.Mabel")
	@Permission("miniaturepets.Ocelot")
	@Permission("miniaturepets.Princess")
	@Permission("miniaturepets.Squirrel")
	MINIATURE_PETS,

	// Defaults:
	// setSleeping.setUpsideDown.setSitting.setArrowsSticking.setEnraged.setSelfDisguiseVisible.setBaby.setBurning

	@Id("2495938")
	@Category(StoreCategory.DISGUISES)
	@PermissionGroup("store.disguises.farm")
	@Display(Material.CHICKEN_SPAWN_EGG)
	DISGUISES_FARM,

	@Id("2495940")
	@Category(StoreCategory.DISGUISES)
	@PermissionGroup("store.disguises.cuties")
	@Display(Material.WOLF_SPAWN_EGG)
	DISGUISES_CUTIES,

	@Id("2495948")
	@Category(StoreCategory.DISGUISES)
	@PermissionGroup("store.disguises.natives")
	@Display(Material.VILLAGER_SPAWN_EGG)
	DISGUISES_NATIVES,

	@Id("3919103")
	@Category(StoreCategory.DISGUISES)
	@PermissionGroup("store.disguises.aquatic")
	@Display(Material.DOLPHIN_SPAWN_EGG)
	DISGUISES_AQUATIC,

	@Id("2495945")
	@Category(StoreCategory.DISGUISES)
	@PermissionGroup("store.disguises.nether")
	@Display(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG)
	DISGUISES_NETHER,

	@Id("2495944")
	@Category(StoreCategory.DISGUISES)
	@PermissionGroup("store.disguises.monsters")
	@Display(Material.ZOMBIE_SPAWN_EGG)
	DISGUISES_MONSTERS,

	@Id("2495942")
	@Category(StoreCategory.DISGUISES)
	@PermissionGroup("store.disguises.mounts")
	@Display(Material.HORSE_SPAWN_EGG)
	DISGUISES_MOUNTS,

	@Id("2495941")
	@Category(StoreCategory.DISGUISES)
	@PermissionGroup("store.disguises.other")
	@Display(Material.SLIME_SPAWN_EGG)
	DISGUISES_OTHER,

	;

	public void handleApply(UUID uuid) {}

	public void handleExpire(UUID uuid) {}

	public int count(Contributor player) {
		return has(player) ? 1 : 0;
	}

	public boolean has(Contributor player) {
		if (isDisabled())
			return false;

		List<String> permissions = getPermissions();
		if (!Nullables.isNullOrEmpty(permissions)) {
			org.bukkit.World world = getWorld();
			return LuckPermsUtils.hasPermission(player, permissions.get(0), world == null ? ImmutableContextSet.empty() : ImmutableContextSet.of("world", world.getName()));
		}

		String permissionGroup = getPermissionGroup();
		if (!Nullables.isNullOrEmpty(permissionGroup))
			return LuckPermsUtils.hasGroup(player, permissionGroup);

		throw new InvalidInputException("Could not determine if a player has the " + name().toLowerCase() + " package");
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	@NotNull
	public String getId() {
		return getField().getAnnotation(Id.class).value();
	}

	@NotNull
	public ItemBuilder getDisplayItem(UUID uuid) {
		Material material = Material.PAPER;
		String modelId = null;
		int amount = 1;

		Display annotation = getField().getAnnotation(Display.class);
		if (annotation != null) {
			amount = annotation.amount();
			if (!Nullables.isNullOrAir(annotation.value())) {
				material = annotation.value();
			} else if (annotation.model() != ItemModelType.INVISIBLE) {
				material = annotation.model().getMaterial();
				modelId = annotation.model().getModel();
			} else {
				Nexus.warn("Invalid @Display on Package." + name());
			}
		}

		return new ItemBuilder(material).model(modelId).name(StringUtils.camelCase(name())).amount(amount);
	}

	@Nullable
	public org.bukkit.World getWorld() {
		World annotation = getField().getAnnotation(World.class);
		return annotation == null ? null : Bukkit.getWorld(annotation.value());
	}

	@Nullable
	public StoreCategory getCategory() {
		Category annotation = getField().getAnnotation(Category.class);
		return annotation == null ? null : annotation.value();
	}

	@NotNull
	public List<String> getPermissions() {
		return Arrays.stream(getField().getAnnotationsByType(Permission.class))
				.map(Permission::value)
				.collect(Collectors.toList());
	}

	@Nullable
	public String getPermissionGroup() {
		if (getField().getAnnotation(PermissionGroup.class) != null)
			return getField().getAnnotation(PermissionGroup.class).value();
		return null;
	}

	public boolean isDisabled() {
		return getField().isAnnotationPresent(Disabled.class);
	}

	public boolean isEnabled() {
		return !isDisabled();
	}

	@NotNull
	public List<String> getCommands() {
		return Arrays.stream(getField().getAnnotationsByType(Command.class))
				.map(Command::value)
				.map(StringUtils::trimFirst)
				.collect(Collectors.toList());
	}

	public int getExpirationDays() {
		if (getField().getAnnotation(ExpirationDays.class) != null)
			return getField().getAnnotation(ExpirationDays.class).value();
		return -1;
	}

	@NotNull
	public List<String> getExpirationCommands() {
		return Arrays.stream(getField().getAnnotationsByType(ExpirationCommand.class))
				.map(ExpirationCommand::value)
				.map(StringUtils::trimFirst)
				.collect(Collectors.toList());
	}

	@Nullable
	public static Package getPackage(String id) {
		for (Package value : values())
			if (value.getId().equals(id))
				return value;
		return null;
	}

	public void apply(UUID uuid) {
		PermissionChange.set().uuid(uuid).permissions(getPermissions()).runAsync();

		String permissionGroup = getPermissionGroup();
		if (!Nullables.isNullOrEmpty(permissionGroup))
			GroupChange.add().uuid(uuid).group(permissionGroup).runAsync();

		getCommands().stream()
				.map(command -> command.replaceAll("\\[player]", Objects.requireNonNull(Name.of(uuid))))
				.forEach(PlayerUtils::runCommandAsConsole);

		handleApply(uuid);

		if (getExpirationDays() > 0)
			new PackageExpireJob(uuid, getId()).schedule(LocalDateTime.now().plusDays(getExpirationDays()));
	}

	public void expire(UUID uuid) {
		PermissionChange.unset().uuid(uuid).permissions(getPermissions()).runAsync();

		String permissionGroup = getPermissionGroup();
		if (!Nullables.isNullOrEmpty(permissionGroup))
			GroupChange.remove().uuid(uuid).group(permissionGroup).runAsync();

		handleExpire(uuid);

		getExpirationCommands().stream()
				.map(StringUtils::trimFirst)
				.map(command -> command.replaceAll("\\[player]", Objects.requireNonNull(Name.of(uuid))))
				.forEach(command -> Tasks.sync(() -> PlayerUtils.runCommandAsConsole(command)));
	}

}
