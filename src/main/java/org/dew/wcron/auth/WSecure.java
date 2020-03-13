package org.dew.wcron.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

/**
 * Name binding is a concept that allows to say to a JAX-RS runtime 
 * that a specific  filter or interceptor  will  be  executed  only 
 * for a specific resource method.
 * When a filter or an interceptor is limited only  to  a  specific 
 * resource method we say that it is name-bound.
 */

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface WSecure {

}
