package gg.projecteden.nexus.models.banker;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.BigDecimalConverter;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Entity(value = "transactions", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, BigDecimalConverter.class, LocalDateTimeConverter.class})
public class Transactions implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Transaction> transactions = new CopyOnWriteArrayList<>();

	public List<Transaction> getUnreceivedTransactions() {
		return transactions.stream().filter(transaction -> !transaction.isReceived()).toList();
	}

}
