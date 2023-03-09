package gg.projecteden.nexus.models.party;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.party.Parties;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.parchment.HasPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "party_manager", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class PartyManager implements DatabaseObject {

	protected static final PartyService SERVICE = new PartyService();

	@Id
	@NonNull
	private UUID uuid;
	private List<Party> parties = new ArrayList<>();

	public static Party byPartyId(UUID uuid) {
		return SERVICE.get0().getParties().stream().filter(party -> party.getId().equals(uuid)).findFirst().orElse(null);
	}

	public static Party of(HasUniqueId player) {
		return of(player.getUniqueId());
	}

	public static Party of(UUID uuid) {
		return SERVICE.get0().getParties().stream().filter(party -> party.contains(uuid)).findFirst().orElse(null);
	}

	public static Party create(HasPlayer player) {
		Party party = new Party(player.getPlayer().getUniqueId());
		PartyManager manager = SERVICE.get0();
		manager.getParties().add(party);
		SERVICE.save();
		return party;
	}

	public static void disband(Party party, boolean silent) {
		if (!silent)
			party.broadcast(new JsonBuilder("&3The party has been disbanded"));
		party.getPendingInvites().forEach(uuid ->
				Nerd.of(uuid).sendMessage(new JsonBuilder(Parties.PREFIX + "&3Your invite to " +
					Nerd.of(party.getOwner()).getColoredName() + "'s &3party has been cancelled because the party was disbanded")));
		party.getAllMembers().forEach(p -> Chatter.of(p).leaveSilent(StaticChannel.PARTY.getChannel()));
		SERVICE.get0().getParties().remove(party);
		SERVICE.save();
	}

}
