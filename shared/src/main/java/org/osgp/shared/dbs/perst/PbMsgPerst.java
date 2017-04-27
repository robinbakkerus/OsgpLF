package org.osgp.shared.dbs.perst;

import org.garret.perst.Persistent;

public class PbMsgPerst extends Persistent {

	public String strKey;
	public byte[] body;
	
	public PbMsgPerst(String strKey, byte[] body) {
		super();
		this.strKey = strKey;
		this.body = body;
	}
	
}
