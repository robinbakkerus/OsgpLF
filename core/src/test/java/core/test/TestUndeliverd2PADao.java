package core.test;

import static org.osgp.core.dbs.CoreCassandraClient.TABLE_UNDELIVERED_DLMS;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.osgp.core.dbs.CoreDao;
import org.osgp.core.dbs.CoreDaoFact;
import org.osgp.core.dbs.CoreDbsMgr;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.Database;
import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;

import com.aerospike.client.Key;
import com.alliander.osgp.shared.RequestResponseMsg;
public class TestUndeliverd2PADao {

	private static final String CORRELID = UUID.randomUUID().toString();
	
	@After
	public void afterEach() {
		CoreDbsMgr.INSTANCE.close();
		SystemPropertyHelper.clearProperties();
	}
	
	@Test
	public void testPerstDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.PERST);
		String key = CC.RK_CORE2PA_UNDELIVERED + CORRELID;
		testDao(new PK(key));
	}

	@Test
	public void testRedisDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Redis);
		byte[] key = RedisUtils.key(CC.RK_CORE2PA_UNDELIVERED, CORRELID);
		testDao(new PK(key));
	}

	@Test
	public void testAerospikeDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Aerospike);
		final Key key = new Key(CC.DBS_NAMESPACE_CORE, TABLE_UNDELIVERED_DLMS, CORRELID);
		testDao(new PK(key));
	}

	private void testDao(PK pk) {
		CoreDbsMgr.INSTANCE.open();
		CoreDaoFact.INSTANCE.reset();

		RequestResponseMsg reqRespMsg1 = RequestResponseMsg.newBuilder().setCorrelId(CORRELID).build();
		dao().saveUndeliveredRequest(reqRespMsg1);
		List<UndeliveredTuple> tuples = dao().getAllUndeliveredRequests();
		Assert.assertTrue(tuples.size() > 0 && getUndelivered(tuples, CORRELID));
	}

	private boolean getUndelivered(List<UndeliveredTuple> tuples, String correlid) {
		return tuples.stream().anyMatch(t -> t.getReqRespMsg().getCorrelId().equals(correlid));
	}
	
	private CoreDao dao() {
		return CoreDaoFact.INSTANCE.getDao();
	}
}
