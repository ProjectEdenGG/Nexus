package me.pugabyte.nexus.features.ambience;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.ambience.sounds.BirdSound;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.ambience.AmbienceConfig;
import me.pugabyte.nexus.models.ambience.AmbienceConfig.Ambience;
import me.pugabyte.nexus.models.ambience.AmbienceConfig.Ambience.AmbienceType;
import me.pugabyte.nexus.models.ambience.AmbienceConfigService;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.models.ambience.AmbienceUserService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
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

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

@NoArgsConstructor
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
	@Permission("group.staff")
	void types(@Arg("1") int page) {
		final List<AmbienceType> types = List.of(AmbienceType.values());
		if (types.isEmpty())
			error("No ambience types defined");

		send(PREFIX + "Ambience types:");

		BiFunction<AmbienceType, String, JsonBuilder> formatter = (type, index) ->
			json(" &3" + index + " &e" + camelCase(type) + " &7- " + config.get(type).size())
				.command("/ambience list " + type.name().toLowerCase())
				.hover("&eClick to view");

		paginate(types, formatter, "/ambience types", page);
	}

	@Path("list <type> [page]")
	@Permission("group.staff")
	void list(AmbienceType type, @Arg("1") int page) {
		final List<Ambience> ambiences = config.get(type);
		if (ambiences.isEmpty())
			error("No " + camelCase(type) + " ambience found");

		send(PREFIX + camelCase(type) + " ambience:");
		final BiFunction<Ambience, String, JsonBuilder> formatter = (ambience, index) ->
			json(" &3" + index + " &e" + StringUtils.getLocationString(ambience.getLocation()))
				.command(StringUtils.getTeleportCommand(ambience.getLocation()))
				.hover("&eClick to teleport");

		paginate(ambiences, formatter, "/ambience list " + type.name().toLowerCase(), page);
	}

	@Path("play")
	@Permission("group.staff")
	void play() {
		playAll();
	}

	@Path("birds play <sound>")
	@Permission("group.staff")
	void sound(BirdSound sound) {
		sound.play(location());
	}

	static {
		Tasks.repeat(Time.MINUTE, Time.SECOND.x(RandomUtils.randomInt(45, 90)), AmbienceCommand::playAll);
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
