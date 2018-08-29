package csv;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class CustomerDataHelper extends UserFileDataHelper {

	private static final Logger LOG = LogManager
			.getLogger(CustomerDataHelper.class);
	private static HashFunction recordIdHashFunction = Hashing.murmur3_128();

	/**
	 * Build an unique string by compose row key values
	 *
	 * @param gdr
	 * @param filesRecord
	 * @param rowCount
	 * @return
	 */
	public static NumberedDataRecord<CustomerDataRecord> buildGenericDataRecord(
			CustomerDataRecord gdr, FilesRecord filesRecord, Integer rowCount) {
		// build unique id
		StringBuilder b = new StringBuilder();

		b.append(filesRecord.getId());
		b.append(ID_SEPARATOR);
		// 0: private 1: business
		if (gdr.getType() == 0) {
			b.append(gdr.getEmail());
		} else {
			b.append(gdr.getCompanyName());
		}
		String uniqueId = b.toString();

		gdr.setId(rowCount.toString() + ID_SEPARATOR + uniqueId);
		System.out.println(gdr.getId());
		return new NumberedDataRecord<CustomerDataRecord>(rowCount, gdr);
	}

	public static Integer addIdWithIndex(
			ConcurrentHashMap<String, Integer> existingIds,
			NumberedDataRecord<CustomerDataRecord> nr) {
		UserFileDataRecordId recordId = getRecordRowNumberAndId(nr.getRecord()
				.getId());
		String hashCode = recordIdHashFunction.hashString(
				recordId.getUniqueId(), Charsets.UTF_8).toString();
		return existingIds.putIfAbsent(hashCode, nr.getRowNum());
	}

	public static void addOrUpdateGenericDataRecord(CustomerDataRecord record) {
		// addOrUpdateGenericDataRecords(Collections.singletonList(record));
	}

	// /**
	// * Here we run aggregation process on geo coded rows and insert aggregated
	// rows into database
	// *
	// * @param genericDataAggregation
	// * @return
	// */
	// public static Boolean aggregate(GenericDataAggregation
	// genericDataAggregation, String fileId) {
	// List<GenericDataAggregateRecord> aggregatedRecords =
	// genericDataAggregation.getAggregateRecords(fileId);
	// if (aggregatedRecords == null || aggregatedRecords.isEmpty()) {
	// LOG.error("No aggregated records generated");
	// return false;
	// }
	//
	// try {
	// genericDataAggregateTable.addOrUpdate(aggregatedRecords);
	// } catch (Exception e) {
	// LOG.error("Error while saving in the database: ", e);
	// return false;
	// }
	//
	// return true;
	// }

	// private static Configuration generateLazyInitConf() {
	// Configuration conf = new Configuration();
	// conf.set("mb.lazyConnect", "true");
	// return conf;
	// }
}
