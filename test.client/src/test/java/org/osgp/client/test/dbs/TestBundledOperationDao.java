package org.osgp.client.test.dbs;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.osgp.client.dao.ClientDao;
import org.osgp.client.dao.ClientDaoFact;
import org.osgp.client.dao.ClientDbsMgr;
import org.osgp.dlms.DC;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.Database;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;

import com.aerospike.client.Key;
import com.alliander.osgp.shared.RequestResponseMsg;

public class TestBundledOperationDao {

	private static final String CORRELID = UUID.randomUUID().toString();

	@After
	public void afterEach() {
		ClientDbsMgr.INSTANCE.close();
		SystemPropertyHelper.clearProperties();
	}

	@Test
	public void testPerstDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.PERST);
		String key = CC.RK_BUNDLED_DEVOP + CORRELID;
		testDao(new PK(key));
	}

	@Test
	public void testRedisDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Redis);
		byte[] key = RedisUtils.key(CC.RK_BUNDLED_DEVOP, CORRELID);
		testDao(new PK(key));
	}

	@Test
	public void testAerospikeDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Aerospike);
		final Key key = new Key(DC.DBS_NAMESPACE_SMINT, DC.DBS_BIN_BUNDLE_SEND, CORRELID);
		testDao(new PK(key));
	}

	private void testDao(PK pk) {
		ClientDbsMgr.INSTANCE.open();
		ClientDaoFact.INSTANCE.reset();

		RequestResponseMsg devopMsg1 = RequestResponseMsg.newBuilder()
				.setCorrelId(CORRELID).build();
		dao().saveBundledDeviceOperation(devopMsg1);
		
		List<RequestResponseMsg> allmsg = dao().getAllBundledDeviceOperations();
		Assert.assertTrue(allmsg.stream().anyMatch(t -> t.getCorrelId().equals(CORRELID)));
		
		dao().delete(pk);
		allmsg = dao().getAllBundledDeviceOperations();
		Assert.assertFalse(allmsg.stream().anyMatch(t -> t.getCorrelId().equals(CORRELID)));
	}

	private ClientDao dao() {
		return ClientDaoFact.INSTANCE.getDao();
	}
}
