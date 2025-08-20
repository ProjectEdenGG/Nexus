package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class LocatorBarCommand extends CustomCommand implements Listener {

	public LocatorBarCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[state]")
	@Description("Toggle the locator bar on your screen. You will still show up on other player's locator bars.")
	void toggle(Boolean state) {
		var receiveRange = player().getAttribute(Attribute.WAYPOINT_RECEIVE_RANGE);
		if (receiveRange == null)
			error("Attribute &ewaypoint_receive_range &cnot found");

		if (WorldGroup.MINIGAMES == worldGroup())
			error("This command is disabled in minigames");

		if (state == null)
			state = receiveRange.getValue() == 0;

		if (state)
			receiveRange.setBaseValue(Chat.getLocalRadius());
		else {
			receiveRange.setBaseValue(0);
			dumbInvisibilityFix(player());
		}

		send(PREFIX + (state ? "&aEnabled" : "&cDisabled"));
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		var player = event.getPlayer();

		get(player, Attribute.WAYPOINT_TRANSMIT_RANGE).setBaseValue(Chat.getLocalRadius());

		var receive = get(player, Attribute.WAYPOINT_RECEIVE_RANGE);
		if (receive.getBaseValue() == 0)
			dumbInvisibilityFix(player);
		else
			receive.setBaseValue(Chat.getLocalRadius());
	}

	private static @NotNull AttributeInstance get(Player player, Attribute attribute) {
		var range = player.getAttribute(attribute);
		if (range == null)
			throw new NexusException("Player " + Nickname.of(player) + " has no " + attribute.key().value() + " attribute");
		return range;
	}

	private static void dumbInvisibilityFix(Player player) {
		// Required otherwise hidden from other players. Client bug? Fix with packets?
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2, 0));
	}
}
