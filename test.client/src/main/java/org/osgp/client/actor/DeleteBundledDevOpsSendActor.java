package org.osgp.client.actor;

import org.osgp.client.dao.ClientDao;
import org.osgp.client.dao.ClientDaoFact;
import org.osgp.dlms.DC;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.Database;
import org.osgp.util.ConfigHelper;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;

import com.aerospike.client.Key;

import akka.actor.UntypedActor;

public class DeleteBundledDevOpsSendActor extends UntypedActor {

//	public static final String DELETE_DEVOPS = "delete devops";

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof DeleteDeviceOperationsWrapper) {
			deleteRequestsSend((DeleteDeviceOperationsWrapper) msg);
			context().stop(self());
		}
	}

	private void deleteRequestsSend(DeleteDeviceOperationsWrapper wrapper) {
//		long startTime = System.nanoTime();
//		if (Database.PERST == ConfigHelper.getDatabaseImpl()) {
//			Iterator<?> i = ClientDbsMgr.INSTANCE.dbsMgr().getPerstRoot().bundleIndex.iterator();
//			while (i.hasNext()) {
//				PerstBundleMsg obj = (PerstBundleMsg) i.next();
//				i.remove();
//				obj.deallocate();
//			}
//			long seconds = NANOSECONDS.toMillis(System.nanoTime() - startTime);
//			System.out.println("del took " + seconds);
//		} else {
//			for (RequestResponseMsg request : clientDao().getAllBundledDeviceOperations()) {
//			clientDao().delete(getBundlePK(request.getCorrelId()));
//			}
//		}
		
		wrapper.getRequests().forEach(req -> clientDao().delete(getBundlePK(req.getCorrelId())));
	}

	private PK getBundlePK(final String correlid) {
		if (Database.Redis == ConfigHelper.getDatabaseImpl()) {
			return new PK(RedisUtils.key(CC.RK_BUNDLED_DEVOP, correlid));
		} else if (Database.PERST == ConfigHelper.getDatabaseImpl()) {
			return new PK(CC.RK_BUNDLED_DEVOP + correlid);
		} else {
			Key key = new Key(DC.DBS_NAMESPACE_SMINT, DC.DBS_BIN_BUNDLE_SEND, correlid);
			return new PK(key);
		}
	}

	private ClientDao clientDao() {
		return ClientDaoFact.INSTANCE.getDao();
	}

}
