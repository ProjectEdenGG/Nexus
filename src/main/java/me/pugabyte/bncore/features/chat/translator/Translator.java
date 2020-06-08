package me.pugabyte.bncore.features.chat.translator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.events.MinecraftChatEvent;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
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

	private static String apiKey = BNCore.getInstance().getConfig().getString("tokens.yandex");
	public static TranslatorHandler handler = new TranslatorHandler(apiKey);

	public static final String PREFIX = StringUtils.getPrefix("Translator");

	@EventHandler
	public void onChat(MinecraftChatEvent event) {
		Player sender = event.getChatter().getPlayer();

		Tasks.async(() -> {
			try {
				if (!map.containsKey(sender.getUniqueId())) return;

				Language language = handler.detect(event.getMessage());
				if (language == Language.EN) return;

				String translated = handler.translate(event.getMessage(), language, Language.EN);
				for (UUID uuid : map.get(sender.getUniqueId())) {
					Player translating = Utils.getPlayer(uuid).getPlayer();

					if (uuid == sender.getUniqueId()) continue;
					if (!event.wasSentTo(translating)) continue;

					Tasks.wait(1, () -> new JsonBuilder()
							.next(PREFIX + sender.getName() + " &e(&3" + language.name() + "&e) &3&l> &7" + translated)
							.hover(language.getName())
							.send(translating));
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
				for (UUID uuid : map.get(sender.getUniqueId())) {
					Player translating = Utils.getPlayer(uuid).getPlayer();
					translating.sendMessage(StringUtils.colorize(PREFIX + "Failed to translate message from " + sender.getDisplayName() + "."));
				}
			}
		});
	}

	@EventHandler
	public void onTranslatedDisconnect(PlayerQuitEvent event) {
		if (!map.containsKey(event.getPlayer().getUniqueId())) return;
		for (UUID uuid : map.get(event.getPlayer().getUniqueId()))
			Utils.getPlayer(uuid).getPlayer().sendMessage(PREFIX + event.getPlayer().getDisplayName() + " has logged out. Disabling translation.");

		map.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onTranslatorDisconnect(PlayerQuitEvent event) {
		map.keySet().forEach(uuid -> map.get(uuid).remove(event.getPlayer().getUniqueId()));
	}

}
