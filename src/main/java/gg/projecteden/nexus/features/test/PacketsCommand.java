package gg.projecteden.nexus.features.test;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.PrettyPrinter;
import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Permission(Group.ADMIN)
public class PacketsCommand extends CustomCommand {
	private static final Map<UUID, PacketAdapter> listeners = new HashMap<>();

	public PacketsCommand(@NonNull CommandEvent event) {
		super(event);
	}
	
	@Path("listen <classes>")
	@Description("Listen to and dump information about certain packets")
	void listen(@ErasureType(PacketClass.class) List<PacketClass> classes) {
		if (listeners.containsKey(uuid())) {
			Nexus.getProtocolManager().removePacketListener(listeners.remove(uuid()));
			send(PREFIX + "Cancelled previous listeners");
		}

		final PacketAdapter listener = createListener(classes);
		Nexus.getProtocolManager().addPacketListener(listener);
		listeners.put(uuid(), listener);

		send(PREFIX + "Now listening to the following packets:");
		classes.forEach(packetClass -> send(" &7" + packetClass.getSimpleName()));
	}

	@Path("listen stop")
	@Description("Stop listening for packets")
	void stop() {
		if (!listeners.containsKey(uuid()))
			error("No packet listeners found");

		Nexus.getProtocolManager().removePacketListener(listeners.remove(uuid()));
		send(PREFIX + "Stopped listening for packets");
	}

	@Path("search <filter> [page]")
	@Description("Search for packets by name")
	void search(String filter, @Optional("1") int page) {
		final List<String> matches = tabCompletePacketClass(filter);

		if (!matches.isEmpty())
			error("No matches found for &e" + filter);

		send(PREFIX + "Matches for &e" + filter);
		paginate(matches, (index, packet) -> json("&3" + index + " &7" + packet), "/packets search " + filter, page);
	}

	@NotNull
	private static PacketAdapter createListener(List<PacketClass> classes) {
		final List<PacketType> types = classes.stream().map(packetClass -> PacketType.fromClass(packetClass.getPacketClass())).toList();

		return new PacketAdapter(Nexus.getInstance(), types) {
			@Override
			public void onPacketSending(PacketEvent event) {
				print("Server -> " + Nickname.of(event.getPlayer()) + ": ", event);
			}

			@Override
			public void onPacketReceiving(PacketEvent event) {
				print(Nickname.of(event.getPlayer()) + " -> Server: ", event);
			}

			private void print(String prefix, PacketEvent event) {
				try {
					Nexus.log(prefix + event.getPacket().getHandle().getClass().getSimpleName() + PrettyPrinter.printObject(event.getPacket().getHandle()));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
	}

	@Data
	private static class PacketClass {
		private final Class<? extends Packet> packetClass;

		public String getSimpleName() {
			return packetClass.getSimpleName();
		}
	}

	private static final List<PacketClass> classes = new ArrayList<>();

	static {
		Tasks.async(() -> {
			for (Class<? extends Packet> clazz : ReflectionUtils.subTypesOf(Packet.class, "net.minecraft.network.protocol.game"))
				classes.add(new PacketClass(clazz));

			Nexus.log("Found " + classes.size() + " packet classes");
		});
	}

	@TabCompleterFor(PacketClass.class)
	List<String> tabCompletePacketClass(String filter) {
		return classes.stream()
			.map(PacketClass::getSimpleName)
			.filter(packetName -> packetName.toLowerCase().contains(filter.toLowerCase()))
			.toList();
	}

	@ConverterFor(PacketClass.class)
	PacketClass convertToPacketClass(String value) {
		return classes.stream()
			.filter(clazz -> clazz.getSimpleName().equalsIgnoreCase(value))
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("Packet &e" + value + " &cnot found"));
	}

}
