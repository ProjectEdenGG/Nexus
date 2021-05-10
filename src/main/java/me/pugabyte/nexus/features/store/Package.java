package me.pugabyte.nexus.features.store;

import com.google.common.base.Strings;
import lombok.SneakyThrows;
import me.pugabyte.nexus.features.commands.AutoTorchCommand;
import me.pugabyte.nexus.features.store.annotations.Category;
import me.pugabyte.nexus.features.store.annotations.Commands.Command;
import me.pugabyte.nexus.features.store.annotations.ExpirationCommands.ExpirationCommand;
import me.pugabyte.nexus.features.store.annotations.ExpirationDays;
import me.pugabyte.nexus.features.store.annotations.Id;
import me.pugabyte.nexus.features.store.annotations.PermissionGroup;
import me.pugabyte.nexus.features.store.annotations.Permissions.Permission;
import me.pugabyte.nexus.models.task.Task;
import me.pugabyte.nexus.models.task.TaskService;
import me.pugabyte.nexus.utils.LuckPermsUtils.PermissionChange;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

public enum Package {

	@Id("2589641")
	CUSTOM_DONATION,

	@Id("4425727")
	@Permission("nickname.use")
	NICKNAME_LIFETIME,

	@Id("4425728")
	@Permission("nickname.use")
	@ExpirationDays(30)
	@ExpirationCommand("nickname expire [player]")
	NICKNAME_ONE_MONTH,

	@Id("1922887")
	@Permission("set.my.prefix")
	CUSTOM_PREFIX_LIFETIME,

	@Id("2730030")
	@Permission("set.my.prefix")
	@ExpirationDays(30)
	@ExpirationCommand("prefix expire [player]")
	CUSTOM_PREFIX_ONE_MONTH,

	@Id("2019251")
	@PermissionGroup("store.autosort")
	AUTO_SORT_LIFETIME,

	@Id("2729981")
	@PermissionGroup("store.autosort")
	@ExpirationDays(30)
	AUTO_SORT_ONE_MONTH,

	@Id("4471430")
	@Permission(AutoTorchCommand.PERMISSION)
	AUTO_TORCH,

	@Id("2965488")
	@Permission("jq.custom")
	CUSTOM_JOIN_QUIT_MESSAGES_LIFETIME,

	@Id("2965489")
	@Permission("jq.custom")
	@ExpirationDays(30)
	CUSTOM_JOIN_QUIT_MESSAGES_ONE_MONTH,

	@Id("3239567")
	@Permission("emoticons.use")
	EMOTES,

	@Id("3218615")
	@Permission("wings.use")
	@Permission("wings.style.*")
	PARTICLE_WINGS,

	@Id("2019259")
	@Command("/permhelper vaults add [player] 1")
	VAULTS,

	@Id("4365867")
	@Permission("workbench")
	WORKBENCH,

	@Id("2019261")
	@Command("/permhelper homes add [player] 5")
	FIVE_SETHOMES,

	@Id("2559650")
	@PermissionGroup("store.npc")
	@Command("/permhelper npcs add [player] 1")
	NPC,

	@Id("2019264")
	@Permission("essentials.skull")
	DONOR_SKULL,

	@Id("2496109")
	@Permission("essentials.hat")
	HAT,

	@Id("2019265")
	@Permission("essentials.ptime")
	PTIME,

	@Id("2559439")
	@Permission("itemname.use")
	ITEM_NAME,

	@Id("4158709")
	@Permission("entityname.use")
	ENTITY_NAME,

	@Id("2495885")
	@Permission("firework.launch")
	FIREWORKS,

	@Id("2678902")
	@Permission("fireworkbow.single")
	FIREWORK_BOW_SINGLE,

	@Id("2678893")
	@Permission("fireworkbow.infinite")
	FIREWORK_BOW_INFINITE,

	@Id("2495909")
	@Command("/permhelper plots add [player] 1")
	CREATIVE_PLOTS,

	@Id("2495900")
	@Permission("rainbowarmour.use")
	RAINBOW_ARMOUR,

	@Id("2886239")
	@Permission("invis.armour")
	INVISIBLE_ARMOUR,

	@Id("2856645")
	@Permission("rainbow.beacon")
	RAINBOW_BEACON,

	@Id("2495867")
	@Category("Pets")
	@PermissionGroup("store.pets.farm")
	PETS_FARM,

	@Id("2495869")
	@Category("Pets")
	@PermissionGroup("store.pets.cuties")
	PETS_CUTIES,

	@Id("2495876")
	@Category("Pets")
	@PermissionGroup("store.pets.natives")
	PETS_NATIVES,

	@Id("3919092")
	@Category("Pets")
	@PermissionGroup("store.pets.aquatic")
	PETS_AQUATIC,

	@Id("2495873")
	@Category("Pets")
	@PermissionGroup("store.pets.nether")
	PETS_NETHER,

	@Id("2495872")
	@Category("Pets")
	@PermissionGroup("store.pets.monsters")
	PETS_MONSTERS,

	@Id("2495871")
	@Category("Pets")
	@PermissionGroup("store.pets.mounts")
	PETS_MOUNTS,

	@Id("2495870")
	@Category("Pets")
	@PermissionGroup("store.pets.other")
	PETS_OTHER,

	@Id("2496219")
	@Category("Pets")
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
	@Category("Disguises")
	@PermissionGroup("store.pets.farm")
	DISGUISES_FARM,

	@Id("2495940")
	@Category("Disguises")
	@PermissionGroup("store.pets.cuties")
	DISGUISES_CUTIES,

	@Id("2495948")
	@Category("Disguises")
	@PermissionGroup("store.pets.natives")
	DISGUISES_NATIVES,

	@Id("3919103")
	@Category("Disguises")
	@PermissionGroup("store.pets.aquatic")
	DISGUISES_AQUATIC,

	@Id("2495945")
	@Category("Disguises")
	@PermissionGroup("store.pets.nether")
	DISGUISES_NETHER,

	@Id("2495944")
	@Category("Disguises")
	@PermissionGroup("store.pets.monsters")
	DISGUISES_MONSTERS,

	@Id("2495942")
	@Category("Disguises")
	@PermissionGroup("store.pets.mounts")
	DISGUISES_MOUNTS,

	@Id("2495941")
	@Category("Disguises")
	@PermissionGroup("store.pets.other")
	DISGUISES_OTHER;

	@SneakyThrows
	public Field getField() {
		return getClass().getDeclaredField(name());
	}

	public String getId() {
		return getField().getAnnotation(Id.class).value();
	}

	public String getCategory() {
		Category annotation = getField().getAnnotation(Category.class);
		return annotation == null ? null : annotation.value();
	}

	public List<String> getPermissions() {
		return Arrays.stream(getField().getAnnotationsByType(Permission.class))
				.map(Permission::value)
				.collect(Collectors.toList());
	}

	public String getPermissionGroup() {
		if (getField().getAnnotation(PermissionGroup.class) != null)
			return getField().getAnnotation(PermissionGroup.class).value();
		return null;
	}

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

	public List<String> getExpirationCommands() {
		return Arrays.stream(getField().getAnnotationsByType(ExpirationCommand.class))
				.map(ExpirationCommand::value)
				.map(StringUtils::trimFirst)
				.collect(Collectors.toList());
	}

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
				.map(command -> command.replaceAll("\\[player]", player.getName()))
				.forEach(PlayerUtils::runCommandAsConsole);

		if (getExpirationDays() > 0)
			new TaskService().save(new Task("package-expire", new HashMap<>() {{
				put("uuid", player.getUniqueId().toString());
				put("packageId", String.valueOf(getId()));
			}}, LocalDateTime.now().plusDays(getExpirationDays())));
	}

	public void expire(OfflinePlayer player) {
		getPermissions().forEach(permission -> PermissionChange.unset().player(player).permission(permission).run());

		String permissionGroup = getPermissionGroup();
		if (!Strings.isNullOrEmpty(permissionGroup))
			PlayerUtils.runCommandAsConsole("lp user " + player.getName() + " parent remove " + permissionGroup);

		getExpirationCommands().stream()
				.map(StringUtils::trimFirst)
				.map(command -> command.replaceAll("\\[player]", player.getName()))
				.forEach(command -> Tasks.sync(() -> PlayerUtils.runCommandAsConsole(command)));
	}

}
