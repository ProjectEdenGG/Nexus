package me.pugabyte.nexus.models.easter21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.eventuser.EventUser;
import me.pugabyte.nexus.models.eventuser.EventUserService;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.vote.VotePoints;
import me.pugabyte.nexus.models.vote.VotePointsService;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity("easter21")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Easter21User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<Location> found = new HashSet<>();

	private static transient final String PREFIX = StringUtils.getPrefix("Easter21");

	public void found(Location location) {
		if (found.contains(location)) {
			send(PREFIX + "You have already found this egg!");
			return;
		}

		found.add(location);

		EventUserService eventUserService = new EventUserService();
		EventUser eventUser = eventUserService.get(uuid);
		eventUser.giveTokens(5);
		eventUserService.save(eventUser);

		VotePointsService votePointsService = new VotePointsService();
		VotePoints votePoints = votePointsService.get(uuid);

		BankerService bankerService = new BankerService();
		switch (found.size()) {
			case 5 -> {
				bankerService.deposit(TransactionCause.EVENT.of(null, getOfflinePlayer(), BigDecimal.valueOf(5000), ShopGroup.SURVIVAL, "Found 5 easter eggs"));
				send(PREFIX + "You have received &e$5,000 &3for finding &e5 easter eggs");
			}
			case 10 -> {
				votePoints.givePoints(25);
				votePointsService.save(votePoints);
				send(PREFIX + "You have received &e25 vote points &3for finding &e10 easter eggs");
			}
			case 20 -> {
				bankerService.deposit(TransactionCause.EVENT.of(null, getOfflinePlayer(), BigDecimal.valueOf(10000), ShopGroup.SURVIVAL, "Found 20 easter eggs"));
				send(PREFIX + "You have received &e$10,000 &3for finding &e20 easter eggs");
			}
			case 30 -> {
				votePoints.givePoints(50);
				votePointsService.save(votePoints);
				send(PREFIX + "You have received &e50 vote points &3for finding &e30 easter eggs");
			}
			case 35 -> {
				bankerService.deposit(TransactionCause.EVENT.of(null, getOfflinePlayer(), BigDecimal.valueOf(35000), ShopGroup.SURVIVAL, "Found 35 easter eggs"));
				send(PREFIX + "You have received &e$35,000 &3for finding &e35 easter eggs");
			}
		}
	}

}
