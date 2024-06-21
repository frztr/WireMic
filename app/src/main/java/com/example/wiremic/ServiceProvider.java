package com.example.wiremic;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ServiceProvider {

    private static ServiceProvider provider;
    private Context Context;
    private ServiceProvider()
    {

    }

    public ServiceProvider AddContext(Context context)
    {
        Context = context;
        return provider;
    }

    public Context getContext()
    {
        return Context;
    }

    public static ServiceProvider getProvider()
    {
        if(provider == null)
        {
            provider = new ServiceProvider();
        }
        return provider;
    }

    private HashMap<Class,Class> serviceCollection = new HashMap<Class, Class>();
    private HashMap<Class,Object> instances = new HashMap<Class, Object>();

    public <T,K extends T> ServiceProvider AddService(Class<T> I, Class<K> C)
    {
        serviceCollection.put(I,C);
        return provider;
    }

    <T> T getService(Class<T> I)
    {
        Class C =  serviceCollection.get(I);
        try {
            return (T)C.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    <T> T getSingleton(Class<T> I)
    {
        Object o = instances.get(I);
        if(o == null)
        {
            o = getService(I);
            instances.put(I,o);
        }
        if(o != null) {
            return (T) o;
        }
        return null;
    }
}
