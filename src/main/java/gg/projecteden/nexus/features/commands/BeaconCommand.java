package gg.projecteden.nexus.features.commands;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class BeaconCommand extends CustomCommand implements Listener {

	public BeaconCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("set effectRange <range>")
	@Description("Set the effect range of a beacon")
	void set_effectRange(double range) {
		Beacon beacon = getTargetBeacon();
		if (beacon == null)
			error("You must be looking at beacon");

		beacon.setEffectRange(range);
		beacon.update();
		send(PREFIX + "Set effect range to " + beacon.getEffectRange() + " blocks");
	}

	@Path("get effectRange")
	@Description("View the effect range of a beacon")
	void get_effectRange() {
		Beacon beacon = getTargetBeacon();
		if (beacon == null)
			error("You must be looking at beacon");

		send(PREFIX + "Effect range is " + beacon.getEffectRange() + " blocks");
	}

	@Path("set primaryEffect <effect>")
	void set_primaryEffect(PotionEffectType effect) {
		Beacon beacon = getTargetBeacon();
		if (beacon == null)
			error("You must be looking at beacon");

		beacon.setPrimaryEffect(effect);
		send(PREFIX + "Primary effect set to " + beacon.getPrimaryEffect().getType().getName());
	}

	@Path("set secondaryEffect <effect>")
	void set_secondaryEffect(PotionEffectType effect) {
		Beacon beacon = getTargetBeacon();
		if (beacon == null)
			error("You must be looking at beacon");

		beacon.setSecondaryEffect(effect);
		send(PREFIX + "Secondary effect set to " + beacon.getSecondaryEffect().getType().getName());
	}

	private @Nullable Beacon getTargetBeacon() {
		Block block = getTargetBlockRequired(Material.BEACON);
		if (!(block.getState() instanceof Beacon beacon))
			return null;

		return beacon;
	}

	@ConverterFor(PotionEffectType.class)
	PotionEffectType convertToPotionEffectType(String value) {
		return PotionEffectType.getByName(value);
	}

	@TabCompleterFor(PotionEffectType.class)
	List<String> tabCompletePotionEffectType(String filter) {
		return Arrays.stream(PotionEffectType.values())
			.map(effect -> effect.getKey().getKey())
			.filter(effect -> effect.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

	@EventHandler
	public void on(BeaconEffectEvent event) {
		var dom = StringUtils.xyzw(event.getBlock()).equals("6335 104 580 resource");
		if (!dom)
			return;

		event.setEffect(event.getEffect().withDuration(20 * 17).withAmplifier(1));
	}

}
