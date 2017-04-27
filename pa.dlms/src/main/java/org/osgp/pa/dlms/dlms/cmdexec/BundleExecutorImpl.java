package org.osgp.pa.dlms.dlms.cmdexec;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgp.pa.dlms.dlms.CommandExecutor;
import org.osgp.pa.dlms.service.BundleExecutor;
import org.osgp.pa.dlms.service.ExecutorNotFoundException;
import org.osgp.util.ScanClassesHelper;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.RequestType;

public class BundleExecutorImpl implements BundleExecutor {

	private static Map<RequestType, CommandExecutor> cmdExecutorMap = null;

	@Override
	public CommandExecutor getExecutor(DlmsActionMsg action) throws ExecutorNotFoundException {
		if (getMap().containsKey(action.getRequestType())) {
			return getMap().get(action.getRequestType());
		} else {
			throw new ExecutorNotFoundException("executor not found for " + action.getRequestType());
		}
	}

	//todo dit gaan naar een helper class.
	private static synchronized Map<RequestType, CommandExecutor> getMap() {
		if (cmdExecutorMap == null) {
			cmdExecutorMap = new HashMap<>();
			Set<Class<?>> annotated = 
					ScanClassesHelper.findAnnotatedClasses("org.osgp.pa.dlms.dlms.cmdexec", AnnotCommandExecutor.class);

			for (Class<?> clz : annotated) {
				CommandExecutor cmdExec=null;
				try {
					AnnotCommandExecutor cmdAnno = clz.getAnnotation(AnnotCommandExecutor.class);
					cmdExec = (CommandExecutor) clz.newInstance();
					cmdExecutorMap.put(cmdAnno.action(), cmdExec);
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		return cmdExecutorMap;
	}
}
