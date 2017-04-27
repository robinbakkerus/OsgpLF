package org.osgp.util.rpc;

import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.OsgpServiceGrpc.OsgpServiceBlockingStub;

import io.grpc.ManagedChannel;

public class OsgpServiceBlockingStubTuple {
	
	private final ManagedChannel channel;
	private final OsgpServiceGrpc.OsgpServiceBlockingStub blockingStub;
	
	public OsgpServiceBlockingStubTuple(ManagedChannel channel, OsgpServiceBlockingStub stub) {
		super();
		this.channel = channel;
		this.blockingStub = stub;
	}

	public ManagedChannel getChannel() {
		return channel;
	}

	public OsgpServiceGrpc.OsgpServiceBlockingStub getStub() {
		return blockingStub;
	}
	
	

}
