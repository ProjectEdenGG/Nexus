package gg.projecteden.nexus.models.banker;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(Transactions.class)
public class TransactionsService extends MongoService<Transactions> {
	private final static Map<UUID, Transactions> cache = new ConcurrentHashMap<>();

	public Map<UUID, Transactions> getCache() {
		return cache;
	}

	private final static int LIMIT = 40000;

	@Override
	protected void beforeSave(Transactions transactions) {
		final List<Transaction> txns = transactions.getTransactions();
		txns.removeIf(transaction -> transaction.getTimestamp().isBefore(LocalDateTime.now().minusDays(60)));

		if (txns.size() > LIMIT)
			transactions.setTransactions(txns.subList(txns.size() - LIMIT, txns.size()));
	}

}
