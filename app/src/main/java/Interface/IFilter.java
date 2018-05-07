package Interface;

import java.lang.reflect.Method;

public  interface IFilter {
	Object  Before(Object sender, Method method, Object[] args) throws Exception;
	Object  After(Object sender, Method method, Object[] args, Object retvalue) throws Exception;
}
