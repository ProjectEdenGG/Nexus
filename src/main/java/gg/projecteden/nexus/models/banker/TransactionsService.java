package gg.projecteden.nexus.models.banker;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoPlayerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@ObjectClass(Transactions.class)
public class TransactionsService extends MongoPlayerService<Transactions> {
	private final static Map<UUID, Transactions> cache = new ConcurrentHashMap<>();

	public Map<UUID, Transactions> getCache() {
		return cache;
	}

	private final static int LIMIT = 40000;

	@Override
	protected void beforeSave(Transactions transactions) {
		try {
			final List<Transaction> txns = new CopyOnWriteArrayList<>(transactions.getTransactions());
			txns.removeIf(transaction -> transaction.getTimestamp().isBefore(LocalDateTime.now().minusDays(60)));

			if (txns.size() > LIMIT)
				transactions.setTransactions(new CopyOnWriteArrayList<>(txns.subList(txns.size() - LIMIT, txns.size())));
			else
				transactions.setTransactions(new CopyOnWriteArrayList<>(txns));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
