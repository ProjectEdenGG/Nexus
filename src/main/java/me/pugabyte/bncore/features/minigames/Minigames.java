package me.pugabyte.bncore.features.minigames;

import lombok.Getter;
import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.features.minigames.commands.MinigamesCommands;
import me.pugabyte.bncore.features.minigames.listeners.MatchListener;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Loadout;
import me.pugabyte.bncore.features.minigames.models.Lobby;
import me.pugabyte.bncore.features.minigames.models.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class Minigames {
	public static final String PREFIX = Utils.getPrefix("Minigames");
	@Getter
	private static World gameworld;
	@Getter
	private static Location gamelobby;

	public Minigames() {
		new MinigamesCommands();
		new MatchListener();

		registerConfigurationTypes();

		gameworld = Bukkit.getWorld("gameworld");
		gamelobby = new Location(gameworld, 1861.5, 38.1, 247.5, 0, 0);

		ArenaManager.read();
	}

	private void registerConfigurationTypes() {
		ConfigurationSerialization.registerClass(Arena.class, "Arena");
		ConfigurationSerialization.registerClass(Lobby.class, "Lobby");
		ConfigurationSerialization.registerClass(Team.class, "Team");
		ConfigurationSerialization.registerClass(Loadout.class, "Loadout");
	}

}
