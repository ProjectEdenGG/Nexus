package me.pugabyte.nexus.features.store;

import com.google.common.base.Strings;
import eden.utils.Utils;
import lombok.SneakyThrows;
import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.features.chat.commands.EmotesCommand;
import me.pugabyte.nexus.features.commands.AutoTorchCommand;
import me.pugabyte.nexus.features.commands.HatCommand;
import me.pugabyte.nexus.features.commands.NicknameCommand;
import me.pugabyte.nexus.features.commands.PlayerTimeCommand;
import me.pugabyte.nexus.features.commands.WorkbenchCommand;
import me.pugabyte.nexus.features.commands.staff.admin.PermHelperCommand.NumericPermission;
import me.pugabyte.nexus.features.particles.WingsCommand;
import me.pugabyte.nexus.features.store.annotations.Category;
import me.pugabyte.nexus.features.store.annotations.Category.StoreCategory;
import me.pugabyte.nexus.features.store.annotations.Commands.Command;
import me.pugabyte.nexus.features.store.annotations.Display;
import me.pugabyte.nexus.features.store.annotations.ExpirationCommands.ExpirationCommand;
import me.pugabyte.nexus.features.store.annotations.ExpirationDays;
import me.pugabyte.nexus.features.store.annotations.Id;
import me.pugabyte.nexus.features.store.annotations.PermissionGroup;
import me.pugabyte.nexus.features.store.annotations.Permissions.Permission;
import me.pugabyte.nexus.features.store.annotations.World;
import me.pugabyte.nexus.features.store.perks.DonorSkullCommand;
import me.pugabyte.nexus.features.store.perks.EntityNameCommand;
import me.pugabyte.nexus.features.store.perks.InvisibleArmorCommand;
import me.pugabyte.nexus.features.store.perks.ItemNameCommand;
import me.pugabyte.nexus.features.store.perks.PrefixCommand;
import me.pugabyte.nexus.features.store.perks.RainbowArmorCommand;
import me.pugabyte.nexus.features.store.perks.RainbowBeaconCommand;
import me.pugabyte.nexus.features.store.perks.autosort.commands.AutoSortCommand;
import me.pugabyte.nexus.features.store.perks.fireworks.FireworkCommand;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.autotorch.AutoTorchService;
import me.pugabyte.nexus.models.autotorch.AutoTorchUser;
import me.pugabyte.nexus.models.contributor.Contributor;
import me.pugabyte.nexus.models.contributor.ContributorService;
import me.pugabyte.nexus.models.task.Task;
import me.pugabyte.nexus.models.task.TaskService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.LuckPermsUtils;
import me.pugabyte.nexus.utils.LuckPermsUtils.GroupChange;
import me.pugabyte.nexus.utils.LuckPermsUtils.PermissionChange;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static eden.utils.StringUtils.camelCase;

public enum Package {

	@Id("2589641")
	CUSTOM_DONATION,

	@Id("4425727")
	@Category(StoreCategory.CHAT)
	@Permission(NicknameCommand.PERMISSION)
	@Display(Material.NAME_TAG)
	NICKNAME_LIFETIME,

	@Id("4425728")
	@Category(StoreCategory.CHAT)
	@Permission(NicknameCommand.PERMISSION)
	@ExpirationDays(30)
	@ExpirationCommand("nickname expire [player]")
	@Display(Material.NAME_TAG)
	NICKNAME_ONE_MONTH,

	@Id("1922887")
	@Category(StoreCategory.CHAT)
	@Permission(PrefixCommand.PERMISSION)
	@Display(Material.OAK_SIGN)
	CUSTOM_PREFIX_LIFETIME,

	@Id("2730030")
	@Category(StoreCategory.CHAT)
	@Permission(PrefixCommand.PERMISSION)
	@ExpirationDays(30)
	@ExpirationCommand("prefix expire [player]")
	@Display(Material.OAK_SIGN)
	CUSTOM_PREFIX_ONE_MONTH,

	@Id("2019251")
	@Category(StoreCategory.INVENTORY)
	@Permission(AutoSortCommand.PERMISSION)
	@Display(Material.HOPPER)
	AUTO_SORT_LIFETIME,

	@Id("2729981")
	@Category(StoreCategory.INVENTORY)
	@Permission(AutoSortCommand.PERMISSION)
	@ExpirationDays(30)
	@Display(Material.HOPPER)
	AUTO_SORT_ONE_MONTH,

	@Id("4471430")
	@Category(StoreCategory.INVENTORY)
	@Permission(AutoTorchCommand.PERMISSION)
	@Display(Material.TORCH)
	AUTO_TORCH {
		@Override
		public void handleApply(HasUniqueId uuid) {
			AutoTorchService service = new AutoTorchService();
			AutoTorchUser user = service.get(uuid);
			user.setEnabled(true);
			service.save(user);
		}
	},

	@Id("2965488")
	@Category(StoreCategory.CHAT)
	@Permission("jq.custom")
	@Display(Material.MAGENTA_GLAZED_TERRACOTTA)
	CUSTOM_JOIN_QUIT_MESSAGES_LIFETIME,

	@Id("2965489")
	@Category(StoreCategory.CHAT)
	@Permission("jq.custom")
	@ExpirationDays(30)
	@Display(Material.MAGENTA_GLAZED_TERRACOTTA)
	CUSTOM_JOIN_QUIT_MESSAGES_ONE_MONTH,

	@Id("3239567")
	@Category(StoreCategory.CHAT)
	@Permission(EmotesCommand.PERMISSION)
	EMOTES,

	@Id("3218615")
	@Category(StoreCategory.VISUALS)
	@Permission(WingsCommand.PERMISSION)
	@Permission("wings.style.*")
	@Display(Material.ELYTRA)
	PARTICLE_WINGS,

	@Id("2019259")
	@Category(StoreCategory.INVENTORY)
	@Command("/permhelper vaults add [player] 1")
	@Display(Material.ENDER_CHEST)
	VAULTS {
		@Override
		public int count(OfflinePlayer player) {
			return NumericPermission.VAULTS.getLimit(player);
		}

		@Override
		public boolean has(OfflinePlayer player) {
			return count(player) > 0;
		}
	},

	@Id("4365867")
	@Category(StoreCategory.INVENTORY)
	@Permission(WorkbenchCommand.PERMISSION)
	@Display(Material.CRAFTING_TABLE)
	WORKBENCH,

	@Id("2019261")
	@Category(StoreCategory.MISC)
	@Command("/permhelper homes add [player] 5")
	@Display(Material.CYAN_BED)
	EXTRA_SETHOMES {
		@Override
		public int count(OfflinePlayer player) {
			return NumericPermission.HOMES.getLimit(player);
		}

		@Override
		public boolean has(OfflinePlayer player) {
			return count(player) > 0;
		}
	},

	@Id("2559650")
	@Category(StoreCategory.VISUALS)
	@PermissionGroup("store.npc")
	@Command("/permhelper npcs add [player] 1")
	@Display(Material.ARMOR_STAND)
	NPC {
		@Override
		public int count(OfflinePlayer player) {
			return NumericPermission.NPCS.getLimit(player);
		}

		@Override
		public boolean has(OfflinePlayer player) {
			return count(player) > 0;
		}
	},

	@Id("2019264")
	@Category(StoreCategory.INVENTORY)
	@Permission(DonorSkullCommand.PERMISSION)
	@Display(Material.PLAYER_HEAD)
	DONOR_SKULL,

	@Id("2496109")
	@Category(StoreCategory.INVENTORY)
	@Permission(HatCommand.PERMISSION)
	@Display(Material.DIAMOND_HELMET)
	HAT,

	@Id("2019265")
	@Category(StoreCategory.VISUALS)
	@Permission(PlayerTimeCommand.PERMISSION)
	@Display(Material.DAYLIGHT_DETECTOR)
	PTIME,

	@Id("2559439")
	@Category(StoreCategory.INVENTORY)
	@Permission(ItemNameCommand.PERMISSION)
	@Display(Material.ANVIL)
	ITEM_NAME,

	@Id("4158709")
	@Category(StoreCategory.VISUALS)
	@Permission(EntityNameCommand.PERMISSION)
	@Display(Material.NAME_TAG)
	ENTITY_NAME,

	@Id("2495885")
	@Category(StoreCategory.VISUALS)
	@Permission(FireworkCommand.PERMISSION)
	@Display(Material.FIREWORK_ROCKET)
	FIREWORKS,

	@Id("2678902")
	@Category(StoreCategory.INVENTORY)
	@Permission("fireworkbow.single")
	@Display(Material.BOW)
	FIREWORK_BOW_SINGLE,

	@Id("2678893")
	@Category(StoreCategory.INVENTORY)
	@Permission("fireworkbow.infinite")
	@Display(Material.BOW)
	FIREWORK_BOW_INFINITE,

	@Id("2495909")
	@Category(StoreCategory.MISC)
	@Command("/permhelper plots add [player] 1")
	@World("creative")
	@Display(Material.WOODEN_AXE)
	CREATIVE_PLOTS {
		@Override
		public int count(OfflinePlayer player) {
			return NumericPermission.PLOTS.getLimit(player);
		}

		@Override
		public boolean has(OfflinePlayer player) {
			return count(player) > 0;
		}
	},

	@Id("2495900")
	@Category(StoreCategory.INVENTORY)
	@Permission(RainbowArmorCommand.PERMISSION)
	@Display(Material.LEATHER_CHESTPLATE)
	RAINBOW_ARMOR,

	@Id("2886239")
	@Category(StoreCategory.INVENTORY)
	@Permission(InvisibleArmorCommand.PERMISSION)
	@Display(Material.CHAINMAIL_CHESTPLATE)
	INVISIBLE_ARMOR,

	@Id("2856645")
	@Category(StoreCategory.VISUALS)
	@Permission(RainbowBeaconCommand.PERMISSION)
	@Display(Material.BEACON)
	RAINBOW_BEACON,

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
	DISGUISES_OTHER;

	public void handleApply(HasUniqueId uuid) {}

	public void handleExpire(HasUniqueId uuid) {}

	public int count(OfflinePlayer player) {
		return has(player) ? 1 : 0;
	}

	public boolean has(OfflinePlayer player) {
		if (this == CUSTOM_DONATION) {
			ContributorService contributorService = new ContributorService();
			Contributor contributor = contributorService.get(player);
			return contributor.getPurchases().stream().anyMatch(purchase -> purchase.getPackageId().equals(getId()));
		}

		List<String> permissions = getPermissions();
		if (!Utils.isNullOrEmpty(permissions))
			return LuckPermsUtils.hasPermission(player, permissions.get(0), getWorld());

		String permissionGroup = getPermissionGroup();
		if (!StringUtils.isNullOrEmpty(permissionGroup))
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
	public ItemBuilder getDisplayItem() {
		Display annotation = getField().getAnnotation(Display.class);
		Material material = Material.PAPER;;
		int customModelData = 0;;
		if (annotation != null) {
			material = annotation.value();
			customModelData = annotation.customModelData();
		}

		return new ItemBuilder(material).customModelData(customModelData).name(camelCase(name()));
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

	public void apply(OfflinePlayer player) {
		getPermissions().forEach(permission -> PermissionChange.set().player(player).permission(permission).run());

		String permissionGroup = getPermissionGroup();
		if (!isNullOrEmpty(permissionGroup))
			PlayerUtils.runCommandAsConsole("lp user " + player.getName() + " parent add " + permissionGroup);

		getCommands().stream()
				.map(command -> command.replaceAll("\\[player]", Objects.requireNonNull(player.getName())))
				.forEach(PlayerUtils::runCommandAsConsole);

		handleApply(player);

		if (getExpirationDays() > 0)
			new TaskService().save(new Task("package-expire", new HashMap<>() {{
				put("uuid", player.getUniqueId().toString());
				put("packageId", getId());
			}}, LocalDateTime.now().plusDays(getExpirationDays())));
	}

	public void expire(OfflinePlayer player) {
		getPermissions().forEach(permission -> PermissionChange.unset().player(player).permission(permission).run());

		String permissionGroup = getPermissionGroup();
		if (!Strings.isNullOrEmpty(permissionGroup))
			GroupChange.remove().player(player).group(permissionGroup).run();

		handleExpire(player);

		getExpirationCommands().stream()
				.map(StringUtils::trimFirst)
				.map(command -> command.replaceAll("\\[player]", Objects.requireNonNull(player.getName())))
				.forEach(command -> Tasks.sync(() -> PlayerUtils.runCommandAsConsole(command)));
	}

}
