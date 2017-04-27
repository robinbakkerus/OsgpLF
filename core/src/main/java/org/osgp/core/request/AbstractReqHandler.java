package org.osgp.core.request;

import org.osgp.core.dbs.CoreDao;
import org.osgp.core.dbs.CoreDaoFact;
import org.osgp.util.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractReqHandler implements RequestHandler {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractReqHandler.class);

	protected CoreDao deviceDao() {
		return CoreDaoFact.INSTANCE.getDao();
	}
}
