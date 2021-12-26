package gg.projecteden.nexus.features.ambience;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.ambience.effects.birds.BirdSound;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.ambience.AmbienceConfig;
import gg.projecteden.nexus.models.ambience.AmbienceConfig.Ambience;
import gg.projecteden.nexus.models.ambience.AmbienceConfig.Ambience.AmbienceType;
import gg.projecteden.nexus.models.ambience.AmbienceConfigService;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.nexus.models.ambience.AmbienceUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
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

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

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

		paginate(types, formatter, "/ambience types", page);
	}

	@Path("list <type> [page]")
	void list(AmbienceType type, @Arg("1") int page) {
		final List<Ambience> ambiences = config.get(type);
		if (ambiences.isEmpty())
			error("No " + camelCase(type) + " ambience found");

		send(PREFIX + camelCase(type) + " ambience:");
		final BiFunction<Ambience, String, JsonBuilder> formatter = (ambience, index) ->
			json(index + " &e" + StringUtils.getLocationString(ambience.getLocation()))
				.command(StringUtils.getTeleportCommand(ambience.getLocation()))
				.hover("&eClick to teleport");

		paginate(ambiences, formatter, "/ambience list " + type.name().toLowerCase(), page);
	}

	@Path("play")
	void play() {
		playAll();
	}

	@Path("birds play <sound>")
	void sound(BirdSound sound) {
		sound.play(location());
	}

	static {
		Tasks.repeat(TickTime.MINUTE, TickTime.SECOND.x(RandomUtils.randomInt(45, 90)), AmbienceCommand::playAll);
	}

	static void playAll() {
		final AmbienceConfigService service = new AmbienceConfigService();
		final AmbienceConfig config = service.get0();

		// TODO These should tick, utilize cooldowns + randomness
		for (AmbienceConfig.Ambience ambience : new ArrayList<>(config.getAmbiences())) {
			if (!ambience.getLocation().isChunkLoaded())
				continue;

			if (!ambience.validate())
				continue;

			ambience.play();
		}

		service.save(config);
	}

	@EventHandler
	public void onRemoveAmbience(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player))
			return;

		if (!(event.getEntity() instanceof ItemFrame itemFrame))
			return;

		final AmbienceConfigService service = new AmbienceConfigService();
		final AmbienceConfig config = service.get0();

		final AmbienceConfig.Ambience ambience = config.get(itemFrame.getLocation());
		if (ambience == null)
			return;

		config.delete(ambience);
		service.save(config);
		if (Nexus.isDebug())
			PlayerUtils.send(player, PREFIX + "Removed " + camelCase(ambience.getType()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAddAmbience(PlayerInteractEntityEvent event) {
		final Player player = event.getPlayer();

		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		final Location location = itemFrame.getLocation();
		final ItemStack item = player.getInventory().getItem(event.getHand());

		if (isNullOrAir(item))
			return;

		final AmbienceConfigService service = new AmbienceConfigService();
		final AmbienceConfig config = service.get0();

		for (AmbienceType ambienceType : AmbienceType.values()) {
			if (!ambienceType.equals(item))
				continue;

			config.add(new AmbienceConfig.Ambience(location.toBlockLocation(), ambienceType));
			service.save(config);
			if (Nexus.isDebug())
				PlayerUtils.send(player, PREFIX + "Added " + camelCase(ambienceType));
			break;
		}
	}


}
