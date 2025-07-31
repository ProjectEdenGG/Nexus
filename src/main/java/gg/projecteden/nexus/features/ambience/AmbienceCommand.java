package gg.projecteden.nexus.features.ambience;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.ambience.effects.birds.BirdSound;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlacedEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.BirdHouse;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.ambience.AmbienceConfig;
import gg.projecteden.nexus.models.ambience.AmbienceConfig.Ambience;
import gg.projecteden.nexus.models.ambience.AmbienceConfig.Ambience.AmbienceType;
import gg.projecteden.nexus.models.ambience.AmbienceConfigService;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.nexus.models.ambience.AmbienceUserService;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@HideFromWiki // TODO
@NoArgsConstructor
@Permission(Group.STAFF)
public class AmbienceCommand extends CustomCommand implements Listener {
	final AmbienceConfigService service = new AmbienceConfigService();
	final AmbienceConfig config = service.get0();
	final AmbienceUserService userService = new AmbienceUserService();
	AmbienceUser user;

	public AmbienceCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent())
			user = userService.get(player());
	}

	@Path("sounds [enable]")
	void toggleSounds(Boolean enable) {
		if (enable == null)
			enable = !user.isSounds();

		user.setSounds(enable);
		userService.save(user);
		send(PREFIX + "Sounds " + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@Path("particles [enable]")
	void toggleParticles(Boolean enable) {
		if (enable == null)
			enable = !user.isParticles();

		user.setParticles(enable);
		userService.save(user);
		send(PREFIX + "Particles " + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@Path("wind [enable]")
	void toggleWind(Boolean enable) {
		if (enable == null)
			enable = !Wind.isBlowing();

		Wind.setBlowing(enable);
		send(PREFIX + "Wind " + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@Path("debug [enable]")
	void toggleDebug(Boolean enable) {
		if (enable == null)
			enable = !user.isDebug();

		user.setDebug(enable);
		userService.save(user);
		send(PREFIX + "Debug " + (enable ? "&aEnabled" : "&cDisabled"));
	}

	@Path("types [page]")
	void types(@Arg("1") int page) {
		final List<AmbienceType> types = List.of(AmbienceType.values());
		if (types.isEmpty())
			error("No ambience types defined");

		send(PREFIX + "Ambience types:");

		BiFunction<AmbienceType, String, JsonBuilder> formatter = (type, index) ->
			json(index + " &e" + camelCase(type) + " &7- " + config.get(type).size())
				.command("/ambience list " + type.name().toLowerCase())
				.hover("&eClick to view");

		new Paginator<AmbienceType>()
			.values(types)
			.formatter(formatter)
			.command("/ambience types")
			.page(page)
			.send();
	}

	@Path("list <type> [page]")
	void list(AmbienceType type, @Arg("1") int page) {
		final List<Ambience> ambiences = config.get(type);
		if (ambiences.isEmpty())
			error("No " + camelCase(type) + " ambience found");

		send(PREFIX + camelCase(type) + " ambience:");
		final BiFunction<Ambience, String, JsonBuilder> formatter = (ambience, index) ->
			json(index + " &e" + StringUtils.getLocationString(ambience.getLocation()))
				.command(StringUtils.tppos(ambience.getLocation()))
				.hover("&eClick to teleport");

		new Paginator<Ambience>()
			.values(ambiences)
			.formatter(formatter)
			.command("/ambience list " + type.name().toLowerCase())
			.page(page)
			.send();
	}

	@Path("near [type] [page] [--radius]")
	void near(AmbienceType type, @Arg("1") int page, @Switch @Arg("20") int radius) {
		List<Ambience> ambiences;
		if (type == null)
			ambiences = config.getAmbiences();
		else
			ambiences = config.get(type);

		ambiences = ambiences.stream()
			.filter(ambience -> ambience.getLocation().getWorld().equals(world()))
			.filter(ambience -> distanceTo(ambience).lte(radius))
			.toList();

		if (ambiences.isEmpty())
			error("No " + (type == null ? "" : camelCase(type) + " ") + "ambience found within " + radius + " blocks");

		send(PREFIX + "Nearby " + (type == null ? "" : camelCase(type) + " ") + "ambience:");
		final BiFunction<Ambience, String, JsonBuilder> formatter = (ambience, index) ->
			json(index + " &e" + StringUtils.getLocationString(ambience.getLocation()))
				.command(StringUtils.tppos(ambience.getLocation()))
				.hover("&eClick to teleport");

		new Paginator<Ambience>()
			.values(ambiences)
			.formatter(formatter)
			.command("/ambience near " + (type == null ? "" : type.name().toLowerCase()) + " --radius=" + radius)
			.page(page)
			.send();
	}

	@Path("play")
	void play() {
		playAll();
	}

	@Path("birds play <sound>")
	void sound(BirdSound sound) {
		sound.play(location());
	}

	@Path("fixSpawn")
	void fixSpawn() {
		for (var entity : ClientSideConfig.getEntities(Survival.getWorld())) {
			if (entity.getType() == ClientSideEntityType.ITEM_FRAME) {
				ClientSideItemFrame clientSideItemFrame = (ClientSideItemFrame) entity;
				ItemStack itemStack = clientSideItemFrame.content();
				Location location = clientSideItemFrame.location();

				if (config.getAmbienceMap().containsKey(location))
					continue;

				if (WindChime.isWindchime(itemStack)) {
					config.add(new AmbienceConfig.Ambience(location.toBlockLocation(), AmbienceType.METAL_WINDCHIMES));
				} else if (BirdHouse.isBirdHouse(itemStack)) {
					config.add(new AmbienceConfig.Ambience(location.toBlockLocation(), AmbienceType.BIRDHOUSE));
				}
			}
		}

		service.save(config);
	}

	static {
		Tasks.repeat(TickTime.MINUTE, TickTime.SECOND.x(RandomUtils.randomInt(45, 90)), AmbienceCommand::playAll);
	}

	static void playAll() {
		final AmbienceConfigService service = new AmbienceConfigService();
		final AmbienceConfig config = service.get0();

		// TODO These should tick, utilize cooldowns + randomness
		for (AmbienceConfig.Ambience ambience : new ArrayList<>(config.getAmbiences())) {
			if (ambience == null || ambience.getLocation() == null || ambience.getLocation().getWorld() == null)
				continue;

			if (!ambience.getLocation().isChunkLoaded())
				continue;

			if (!ambience.validate())
				continue;

			ambience.play();
		}

		service.save(config);
	}

	@EventHandler
	public void on(DecorationDestroyEvent event) {
		removeAmbience(event.getPlayer(), event.getDecoration().getItemFrame());
	}

	@EventHandler
	public void onRemoveAmbience(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player))
			return;

		if (!(event.getEntity() instanceof ItemFrame itemFrame))
			return;

		removeAmbience(player, itemFrame);
	}

	private void removeAmbience(Player player, ItemFrame itemFrame) {
		if (itemFrame == null)
			return;

		final AmbienceConfigService service = new AmbienceConfigService();
		final AmbienceConfig config = service.get0();

		final Ambience ambience = config.get(itemFrame.getLocation());
		if (ambience == null)
			return;

		config.delete(ambience);
		service.save(config);
		PlayerUtils.debug(player, PREFIX + "Removed " + camelCase(ambience.getType()));
	}

	@EventHandler
	public void on(DecorationPlacedEvent event) {
		addAmbience(event.getPlayer(), event.getLocation(), event.getItem());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		final Player player = event.getPlayer();
		final ItemStack item = player.getInventory().getItem(event.getHand());

		addAmbience(player, itemFrame.getLocation(), item);
	}

	private void addAmbience(Player player, Location location, ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return;

		final AmbienceConfigService service = new AmbienceConfigService();
		final AmbienceConfig config = service.get0();

		for (AmbienceType ambienceType : AmbienceType.values()) {
			if (!ambienceType.matches(item))
				continue;

			config.add(new AmbienceConfig.Ambience(location.toBlockLocation(), ambienceType));
			service.save(config);
			PlayerUtils.debug(player, PREFIX + "Added " + camelCase(ambienceType));
			break;
		}
	}

}
