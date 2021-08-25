package gg.projecteden.nexus.features.placeholderapi;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.Tab;
import gg.projecteden.nexus.features.listeners.Tab.NameplateUtils;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@NoArgsConstructor
public class NexusPlaceholders extends PlaceholderExpansion {

	@Override
	public @NotNull String getIdentifier() {
		return "nexus";
	}

	public String getId() {
		return getClass().getSimpleName().replace("Placeholder", "").toLowerCase();
	}

	@Override
	public @NotNull String getAuthor() {
		return String.join(", ", Nexus.getInstance().getDescription().getAuthors());
	}

	@Override
	public @NotNull String getVersion() {
		return Nexus.getInstance().getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String id) {
		Nexus.debug("Handling placeholder request for " + player.getName() + ": " + id);

		final NexusPlaceholder placeholder = NexusPlaceholder.of(id);
		if (placeholder == null) {
			Nexus.warn("Unknown placeholder " + id + " requested for " + player.getName());
			return null;
		}

		final String result = placeholder.apply(player);
		Nexus.debug("  Result: " + StringUtils.decolorize(result));
		return result;
	}

	@AllArgsConstructor
	private enum NexusPlaceholder {
		NAMEPLATE(player -> {
			final Minigamer minigamer = PlayerManager.get(player);
			// TODO When hex is supported .getChatFormat()
			String nameplate = Nerd.of(player).getNameplateFormat();

			if (minigamer.isPlaying())
				nameplate = minigamer.getColoredName();

			nameplate = Tab.addStateTags(player, nameplate);

			// TODO Should be fixed on Nameplate's side
			if (NameplateUtils.NameplateType.hasEquippedNameplate(player.getUniqueId()))
				if (nameplate.contains("[") && nameplate.contains("]"))
					nameplate += " ";

			return nameplate;
		}),
		;

		private final Function<Player, String> function;

		public String apply(Player player) {
			return function.apply(player);
		}

		public static NexusPlaceholder of(String id) {
			try {
				return valueOf(id.toUpperCase());
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}
	}

}
