package me.pugabyte.bncore.features.chat.translator;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.herochat.HerochatAPI;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Translator implements Listener {

	public Translator() {
		BNCore.registerListener(this);
	}

	public HashMap<UUID, ArrayList<UUID>> translatorMap = new HashMap<>();

	public String apiKey = BNCore.getInstance().getConfig().getConfigurationSection("yandex").getString("apiKey");
	public TranslatorHandler translatorHandler = new TranslatorHandler(apiKey);

	String PREFIX = Utils.getPrefix("Translator");

	@EventHandler
	public void onChat(ChannelChatEvent event) {
		Utils.wait(1, () -> {
			try {
				if (!translatorMap.containsKey(event.getSender().getPlayer().getUniqueId())) return;
				Language language = Language.valueOf(translatorHandler.getLanguage(event.getMessage()));
				String translatedMessage = translatorHandler.translate(event.getMessage(), language, Language.en);
				List<Chatter> chatters = HerochatAPI.getRecipients(event.getSender(), event.getChannel());
				for (UUID uuid : translatorMap.get(event.getSender().getPlayer().getUniqueId())) {
					if (!chatters.contains(Herochat.getChatterManager().getChatter(Utils.getPlayer(uuid).getPlayer())) || uuid != event.getSender().getPlayer().getUniqueId())
						continue;
					Utils.getPlayer(uuid).getPlayer().sendMessage(Utils.colorize(
							PREFIX + event.getSender().getPlayer().getDisplayName() + " &e(&3" + language.toString().toUpperCase() + "&e) &3&l> &7" + translatedMessage));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				for (UUID uuid : translatorMap.get(event.getSender().getPlayer().getUniqueId())) {
					Utils.getPlayer(uuid).getPlayer().sendMessage(Utils.colorize(
							PREFIX + "Failed to translate message from " + event.getSender().getPlayer().getDisplayName() + "."));
				}
			}
		});
	}

	@EventHandler
	public void onTranslatedDisconnect(PlayerQuitEvent event) {
		if (!translatorMap.containsKey(event.getPlayer().getUniqueId())) return;
		for (UUID uuid : translatorMap.get(event.getPlayer().getUniqueId())) {
			Utils.getPlayer(uuid).getPlayer().sendMessage(PREFIX + event.getPlayer().getDisplayName() + " has logged out. Turning off translation for them.");
		}
		translatorMap.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onTranslatorDisconnect(PlayerQuitEvent event) {
		for (UUID uuid : translatorMap.keySet()) {
			translatorMap.get(uuid).remove(event.getPlayer().getUniqueId());
		}
	}

}
