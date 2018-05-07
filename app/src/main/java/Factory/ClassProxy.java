package Factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import Annotation.Filter;

import Interface.*;


public class ClassProxy implements InvocationHandler  {
	//目标对象  
    private Object targetObject;  
    private Object[] CreateArgs;
    /** 
     * 创建动态代理类 
     * @return 
     * @return object(代理类) 
     */  
    public <T> T createProxy (Object targetObject,Object[] args){  
        this.targetObject = targetObject;   
        CreateArgs=args;
        return (T)Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
        		targetObject.getClass().getInterfaces(),this);
                  
    }  
    @Override  
    public Object invoke(Object proxy, Method method, Object[] args)  throws Exception {
		Object obj = null;
		Filter inject = method.getAnnotation(Filter.class);
		Annotation[] alist = method.getAnnotations();
		for (Annotation annotation : alist) {
			if (annotation.annotationType().isAssignableFrom(Filter.class)) {
				Class<?> aclass = ((Filter) annotation).value();
				IFilter filter = Factory.GetInstance(aclass, CreateArgs);
				Object ret = filter.Before(targetObject, method, args);
				if (ret != null) {
					return ret;
				}
			}
		}
		obj = method.invoke(targetObject, args);
		for (Annotation annotation : alist) {
			if (annotation.annotationType().isAssignableFrom(Filter.class)) {
				Class<?> aclass = ((Filter) annotation).value();
				IFilter filter = Factory.GetInstance(aclass, CreateArgs);
				Object ret = filter.After(targetObject, method, args, obj);
				if (ret != null) {
					return ret;
				}
			}
		}
		return obj;
	}

 
}
