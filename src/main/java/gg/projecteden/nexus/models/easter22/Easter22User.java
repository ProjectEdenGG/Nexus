package gg.projecteden.nexus.models.easter22;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2022.easter22.Easter22;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem.EASTER_EGG;

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

		sendMessage(PREFIX + "You found an egg! &7(" + found.size() + "/" + Easter22.TOTAL_EASTER_EGGS + ")");
		PlayerUtils.giveItem(getOnlinePlayer(), EASTER_EGG.get());

		if (true) return; // TODO Easter22

		switch (found.size()) {
			case 5 -> {
				new BankerService().deposit(TransactionCause.EVENT.of(null, this, BigDecimal.valueOf(5000), ShopGroup.SURVIVAL, "Found 5 easter eggs"));
				sendMessage(PREFIX + "You have received &e$5,000 &3for finding &e5 easter eggs");
				new EventUserService().edit(uuid, eventUser -> eventUser.giveTokens(25));
			}
			case 10 -> {
				new VoterService().edit(uuid, voter -> voter.givePoints(25));
				sendMessage(PREFIX + "You have received &e25 vote points &3for finding &e10 easter eggs");
				new EventUserService().edit(uuid, eventUser -> eventUser.giveTokens(25));
			}
			case 15 -> {
				new BankerService().deposit(TransactionCause.EVENT.of(null, this, BigDecimal.valueOf(15000), ShopGroup.SURVIVAL, "Found 20 easter eggs"));
				sendMessage(PREFIX + "You have received &e$15,000 &3for finding &e15 easter eggs");
				new EventUserService().edit(uuid, eventUser -> eventUser.giveTokens(25));
			}
			case 20 -> new EventUserService().edit(uuid, eventUser -> eventUser.giveTokens(125));
			default -> new EventUserService().edit(uuid, eventUser -> eventUser.giveTokens(15));
		}
	}

}
