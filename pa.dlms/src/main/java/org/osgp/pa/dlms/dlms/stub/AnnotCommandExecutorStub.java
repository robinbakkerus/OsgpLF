package org.osgp.pa.dlms.dlms.stub;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.alliander.osgp.dlms.RequestType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) 
public @interface AnnotCommandExecutorStub {
	public RequestType action();
}
