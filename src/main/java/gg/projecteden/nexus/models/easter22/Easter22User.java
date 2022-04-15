package gg.projecteden.nexus.models.easter22;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "easter22", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Easter22User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<Location> found = new HashSet<>();

	private static transient final String PREFIX = StringUtils.getPrefix("Easter22");

	public void found(Location location) {
		if (found.contains(location)) {
			sendMessage(PREFIX + "You have already found this egg!");
			return;
		}

		found.add(location);

		PlayerUtils.giveItem(getOnlinePlayer(), Easter22QuestItem.EASTER_EGG.get());

		/*
		new EventUserService().edit(uuid, eventUser -> eventUser.giveTokens(15));

		switch (found.size()) {
			case 5 -> {
				new BankerService().deposit(TransactionCause.EVENT.of(null, this, BigDecimal.valueOf(5000), ShopGroup.SURVIVAL, "Found 5 easter eggs"));
				sendMessage(PREFIX + "You have received &e$5,000 &3for finding &e5 easter eggs");
			}
			case 10 -> {
				new VoterService().edit(uuid, voter -> voter.givePoints(25));
				sendMessage(PREFIX + "You have received &e25 vote points &3for finding &e10 easter eggs");
			}
			case 20 -> {
				new BankerService().deposit(TransactionCause.EVENT.of(null, this, BigDecimal.valueOf(10000), ShopGroup.SURVIVAL, "Found 20 easter eggs"));
				sendMessage(PREFIX + "You have received &e$10,000 &3for finding &e20 easter eggs");
			}
			case 30 -> {
				new VoterService().edit(uuid, voter -> voter.givePoints(50));
				sendMessage(PREFIX + "You have received &e50 vote points &3for finding &e30 easter eggs");
			}
			case 35 -> {
				new BankerService().deposit(TransactionCause.EVENT.of(null, this, BigDecimal.valueOf(35000), ShopGroup.SURVIVAL, "Found 35 easter eggs"));
				sendMessage(PREFIX + "You have received &e$35,000 &3for finding &e35 easter eggs");
			}
		}
		*/
	}

}
