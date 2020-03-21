package me.pugabyte.bncore.features.chatold.herochat;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HerochatAPI {
	public static List<Chatter> getRecipients(Chatter chatter, Channel channel) {
		List<Chatter> recipients = new ArrayList<>();

		String name = channel.getName();
		int distance = channel.getDistance();
		Player sender = chatter.getPlayer();


		for (Chatter loopChatter : channel.getMembers()) {
			Player loopPlayer = loopChatter.getPlayer();
			String world = loopPlayer.getWorld().getName().toLowerCase();

			if (loopPlayer != sender) {

				if (name.toLowerCase().contains("convo")) {
					recipients.add(loopChatter);

				} else if (name.matches("(Global|Broadcast|Staff|Operator|Admin)")) {
					recipients.add(loopChatter);

				} else if (name.equals("Local")) {
					if (loopPlayer.getWorld() == sender.getWorld()) {
						if (loopPlayer.getLocation().distance(sender.getPlayer().getLocation()) <= distance) {
							recipients.add(loopChatter);
						}
					}

				} else if (name.matches("(Minigames|Red|Blue|Green|Yellow|White)")) {
					if (world.matches("(gameworld|blockball)")) {
						recipients.add(loopChatter);
					}

				} else if (name.equals("Creative")) {
					if (loopPlayer.getWorld() == sender.getWorld()) {
						if (world.matches("(buildcontest|creative|buildadmin|jail)")) {
							recipients.add(loopChatter);
						}
					}

				} else if (name.equals("Skyblock")) {
					if (world.contains("skyblock")) {
						recipients.add(loopChatter);
					}
				}
			}
		}

		return recipients;
	}
}
