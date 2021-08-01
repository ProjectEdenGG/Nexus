package gg.projecteden.nexus.models.birthday21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2021.bearfair21.Quests;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.trophy.Trophy;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.features.events.y2021.birthday21.BirthdayEventCommand.MAX_CAKES;

@Data
@Builder
@Entity(value = "birthday21", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Birthday21User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<Location> found = new HashSet<>();

	private static transient final String PREFIX = StringUtils.getPrefix("Birthdays");

	public void found(Location location) {
		if (found.contains(location)) {
			sendMessage(PREFIX + "You have already found this cake!");
			return;
		}

		found.add(location);
		Quests.sound_obtainItem(getOnlinePlayer());
		JsonBuilder text = JsonBuilder.fromPrefix("Birthdays")
			.next("You found a ").next("&eBirthday Cake").next("! It's #" + found.size() + "/" + MAX_CAKES + ".");

		if (found.size() == 22 || found.size() == MAX_CAKES) {
			String nerd = found.size() == 22 ? "Griffin" : "Wakka";
			text.next(" It has a message written on top: ").next("&6Happy Birthday " + nerd + "!");
		}

		sendMessage(text);

		EventUserService eventUserService = new EventUserService();
		EventUser eventUser = eventUserService.get(uuid);
		eventUser.giveTokens(found.size() == 22 ? 20 : 10); // double for griffin age
		eventUserService.save(eventUser);

		if (found.size() == MAX_CAKES)
			Trophy.BIRTHDAY_PARTY_2021.give(this);

		VoterService voterService = new VoterService();
		Voter voter = voterService.get(uuid);

		BankerService bankerService = new BankerService();
		switch (found.size()) {
			case 5 -> {
				bankerService.deposit(Transaction.TransactionCause.EVENT.of(null, getOfflinePlayer(), BigDecimal.valueOf(5000), Shop.ShopGroup.SURVIVAL, "Found 5 birthday cakes"));
				sendMessage(PREFIX + "You have received &e$5,000 &3for finding &e5 birthday cakes");
			}
			case 10 -> {
				voter.givePoints(25);
				voterService.save(voter);
				sendMessage(PREFIX + "You have received &e25 vote points &3for finding &e10 birthday cakes");
			}
			case 20 -> {
				bankerService.deposit(Transaction.TransactionCause.EVENT.of(null, getOfflinePlayer(), BigDecimal.valueOf(10000), Shop.ShopGroup.SURVIVAL, "Found 20 birthday cakes"));
				sendMessage(PREFIX + "You have received &e$10,000 &3for finding &e20 birthday cakes");
			}
			case 24 -> {
				bankerService.deposit(Transaction.TransactionCause.EVENT.of(null, getOfflinePlayer(), BigDecimal.valueOf(15000), Shop.ShopGroup.SURVIVAL, "Found 24 birthday cakes"));
				voter.givePoints(25);
				voterService.save(voter);
				sendMessage(PREFIX + "You have received &e25 vote points &3and &e$15,000 &3for finding &eall 24 birthday cakes");
			}
		}
	}
}
