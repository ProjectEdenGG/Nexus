package me.pugabyte.nexus.models.purchase;

import me.pugabyte.nexus.models.MySQLService;

import java.util.List;

public class PurchaseService extends MySQLService {

	public List<Purchase> getRecent(int count) {
		return database.sql(
				"select * " +
				"from purchase " +
				"where price > 0 " +
				"and transactionId IN ( " +
						"select transactionId from ( " +
								"select distinct uuid, transactionId, timestamp " +
								"from purchase " +
								"where price > 0 " +
								"group by uuid " +
						") as data " +
				") order by timestamp " +
				"limit " + count
		).results(Purchase.class);
	}

}
