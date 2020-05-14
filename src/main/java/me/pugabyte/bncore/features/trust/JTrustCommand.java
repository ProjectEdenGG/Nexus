package me.pugabyte.bncore.features.trust;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.trust.Trust;
import me.pugabyte.bncore.models.trust.Trust.Type;
import me.pugabyte.bncore.models.trust.TrustService;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JTrustCommand extends CustomCommand {
	Trust trust;
	TrustService service = new TrustService();

	public JTrustCommand(@NonNull CommandEvent event) {
		super(event);
		trust = service.get(player());
	}

	@Path
	void menu() {
		send("TODO");
	}

	@Path("lock <players...>")
	void lock(@Arg(type = Player.class) List<Player> players) {
		runCommand("cmodify " + players.stream().map(Player::getName).collect(Collectors.joining(" ")));
	}

	@Path("all <players...>")
	void all(@Arg(type = Player.class) List<Player> players) {
		locks(players);
		homes(players);
	}

	@Path("home <players...>")
	void home(@Arg(type = Player.class) List<Player> players) {
		// TODO homes service
	}

	@Path("locks <players...>")
	void locks(@Arg(type = Player.class) List<Player> players) {
		process(Type.LOCKS, players, trust.getLocks());
	}

	@Path("homes <players...>")
	void homes(@Arg(type = Player.class) List<Player> players) {
		process(Type.HOMES, players, trust.getHomes());
	}

	private void process(Trust.Type type, @Arg(type = Player.class) List<Player> players, List<UUID> list) {
		List<Player> skipped = new ArrayList<>();
		List<Player> added = new ArrayList<>();
		players.forEach(player -> {
			if (list.contains(player.getUniqueId()))
				skipped.add(player);
			else
				added.add(player);
		});

		list.addAll(added.stream().map(Player::getUniqueId).collect(Collectors.toList()));
		service.save(trust);
		send(skipped.stream().map(Player::getName).collect(Collectors.joining(", ")) + " already allowed to " + type.name().toLowerCase() + ", skipping");
		send("Added " + added.stream().map(Player::getName).collect(Collectors.joining(", ")) + " to " + type.name().toLowerCase());
	}

}
