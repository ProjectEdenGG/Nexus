package me.pugabyte.nexus.features.chat.translator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.events.MinecraftChatEvent;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
public class Translator implements Listener {

	@Getter
	private static HashMap<UUID, ArrayList<UUID>> map = new HashMap<>();

	@Getter
	private static final String apiKey = Nexus.getInstance().getConfig().getString("tokens.yandex");
	public static TranslatorHandler handler = new TranslatorHandler();

	public static final String PREFIX = StringUtils.getPrefix("Translator");

	@EventHandler
	public void onChat(MinecraftChatEvent event) {
		Player sender = event.getChatter().getOnlinePlayer();

		Tasks.async(() -> {
			try {
				if (!map.containsKey(sender.getUniqueId())) return;

				Language language = event.getChatter().getLanguage();
				if (language == null) {
					language = handler.detect(event.getMessage());
					if (language == null || language == Language.EN) return;
					event.getChatter().setLanguage(language);
				}

				String translated = handler.translate(event.getMessage(), language, Language.EN);
				for (UUID uuid : map.get(sender.getUniqueId())) {
					Player translating = PlayerUtils.getPlayer(uuid).getPlayer();

					if (uuid == sender.getUniqueId()) continue;
					if (!event.wasSentTo(translating)) continue;

					Language finalLanguage = language;
					Tasks.wait(1, () -> new JsonBuilder()
							.next(PREFIX + sender.getName() + " &e(&3" + finalLanguage.name() + "&e) &3&l> &7" + translated)
							.hover(finalLanguage.getName())
							.send(translating));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				for (UUID uuid : map.get(sender.getUniqueId())) {
					PlayerUtils.send(uuid, PREFIX + "Failed to translate message from " + sender.getDisplayName() + ".");
				}
			}
		});
	}

	@EventHandler
	public void onTranslatedDisconnect(PlayerQuitEvent event) {
		if (!map.containsKey(event.getPlayer().getUniqueId())) return;
		for (UUID uuid : map.get(event.getPlayer().getUniqueId()))
			PlayerUtils.send(uuid, PREFIX + event.getPlayer().getDisplayName() + " has logged out. Disabling translation.");

		map.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onTranslatorDisconnect(PlayerQuitEvent event) {
		map.keySet().forEach(uuid -> map.get(uuid).remove(event.getPlayer().getUniqueId()));
	}

}
