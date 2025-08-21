package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.profiles.ProfileSettingsUpdatedEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.profile.ProfileUserService;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange.PlayerRankChangeEvent;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.StringUtils.hexToInt;
import static gg.projecteden.nexus.utils.StringUtils.toHex;

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
			Tasks.wait(2, () -> updateColor(player())); // why the fuck wait 2?????
		}

		send(PREFIX + (state ? "&aEnabled" : "&cDisabled"));
	}

	@Path("color")
	void color() {
		send(PREFIX + "Change your &c/profile &3background color to set your locator bar color.");
	}

	@EventHandler
	public void on(ProfileSettingsUpdatedEvent event) {
		updateColor(event.getPlayer());
	}

	@EventHandler
	public void on(PlayerRankChangeEvent event) {
		var nerd = Nerd.of(event.getUuid());
		if (nerd.isOnline())
			updateColor(nerd.getOnlinePlayer());
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		var player = event.getPlayer();

		get(player, Attribute.WAYPOINT_TRANSMIT_RANGE).setBaseValue(Chat.getLocalRadius());

		var receive = get(player, Attribute.WAYPOINT_RECEIVE_RANGE);
		if (receive.getBaseValue() != 0)
			receive.setBaseValue(Chat.getLocalRadius());

		updateColor(player);
	}

	@SuppressWarnings("deprecation")
	private static void updateColor(Player player) {
		ChatColor color = Rank.of(player).getChatColor();

		ChatColor backgroundColor = new ProfileUserService().get(player).getBackgroundColor();
		// If player wants white, they will have to set it to slightly off-white
		if (backgroundColor != null && !"#ffffff".equals(toHex(backgroundColor)))
			color = backgroundColor;

		Optional<Integer> optional = Optional.of(hexToInt(color));

		mutateWaypoint(player, transmitter -> transmitter.waypointIcon().color = optional);
	}

	public static void mutateWaypoint(Player player, Consumer<WaypointTransmitter> consumer) {
		var manager = NMSUtils.toNMS(player.getWorld()).getWaypointManager();
		var nmsPlayer = NMSUtils.toNMS(player);
		manager.untrackWaypoint(nmsPlayer);
		if (nmsPlayer instanceof WaypointTransmitter transmitter)
			consumer.accept(transmitter);
		manager.trackWaypoint(nmsPlayer);
	}

	private static @NotNull AttributeInstance get(Player player, Attribute attribute) {
		var range = player.getAttribute(attribute);
		if (range == null)
			throw new NexusException("Player " + Nickname.of(player) + " has no " + attribute.key().value() + " attribute");
		return range;
	}

}
