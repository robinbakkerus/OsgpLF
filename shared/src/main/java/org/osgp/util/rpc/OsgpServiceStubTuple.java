package org.osgp.util.rpc;

import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.OsgpServiceGrpc.OsgpServiceStub;

import io.grpc.ManagedChannel;

public class OsgpServiceStubTuple {
	
	private final ManagedChannel channel;
	private final OsgpServiceGrpc.OsgpServiceStub stub;
	
	public OsgpServiceStubTuple(ManagedChannel channel, OsgpServiceStub stub) {
		super();
		this.channel = channel;
		this.stub = stub;
	}

	public ManagedChannel getChannel() {
		return channel;
	}

	public OsgpServiceGrpc.OsgpServiceStub getStub() {
		return stub;
	}
	
	

}
