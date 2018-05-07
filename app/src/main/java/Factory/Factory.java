package Factory;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import Interface.ICard;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * Created by 王彦鹏 on 2017-09-06.
 */

public class Factory {
    public static <T> T GetInstance(Class<?> classType,Object[] args)
    {
        try
        {

            Constructor c1;
            if (args !=null)
            {
                Class[] classlist=new Class[args.length];
                for (int i=0;i<args.length;i++)
                {
                    classlist[i]=args[i].getClass();
                }

                c1=classType.getConstructor(classlist);
            }
            else
            {
                c1=classType.getDeclaredConstructor();
            }
            c1.setAccessible(true);



            ClassProxy proxy = new ClassProxy();
            return  (T) proxy.createProxy(c1.newInstance(args),args);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Class<?>>  GetInstances(Context ctx, Class<?> classType, Object[] args)
    {
        List<Class<?>> clist=new ArrayList<Class<?>>();
        try {



//            DexFile df = new DexFile(ctx.getPackageCodePath());//通过DexFile查找当前的APK中可执行文件
//            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
//            while (enumeration.hasMoreElements()) {//遍历
//                String className = (String) enumeration.nextElement();
//                Class c = Class.forName(className);
//                if (c.isInstance(classType)){
//                    clist.add(c);
//                }
//            }
            Class c = Class.forName("Class.EnjoyCard");
            clist.add(c);
        }
        catch (Exception e)
        {

        }

        return clist;
    }



    public static List<Class<?>> getAllAssignedClass(Class<?> cls) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Class<?> c : getClasses(cls)) {
            if (cls.isAssignableFrom(c) && !cls.equals(c)) {
                classes.add(c);
            }
        }
        return classes;
    }


    public static List<Class<?>> getClasses(Class<?> cls) throws IOException, ClassNotFoundException {
        String pk = cls.getPackage().getName();
        String path = pk.replace('.', '/');
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is= classloader.getResourceAsStream(path);
        is.skip(1);
        URL url = classloader.getResource(path);
        String newUrlStr=url.toString().substring(0,url.toString().lastIndexOf("/"));
        URL newUrl = new URL(newUrlStr);
        return getClasses(new File(newUrl.getFile()), pk);
    }


    private static List<Class<?>> getClasses(File dir, String pk) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!dir.exists()) {
            return classes;
        }

        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                classes.addAll(getClasses(f, f.getName()));
            }
            String name = f.getName();
            int start = f.getPath().indexOf("debug");
            int end = f.getPath().lastIndexOf("\\");
            String url = "";
            if (start+6 < end){
                url = f.getPath().substring(start+6,end);
            }else{
                url = "";
            }
            if (name.endsWith(".class")) {
                classes.add(Class.forName(url.replace("\\",".") + "." + name.substring(0, name.length() - 6)));
            }
        }
        return classes;
    }

    public static void main(String[] args) {
        try {
            System.out.println("获取所有子类和实现类：");
            for (Class<?> c : getAllAssignedClass(ICard.class)) {
                System.out.println(c.getName());
            }
            System.out.println("获取所有类：");
            for (Class<?> c : getClasses(ICard.class)) {
                System.out.println(c.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
