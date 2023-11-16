package scc.srv;

import java.util.HashSet;
import java.util.Set;

import scc.data.DataLayer;
import scc.utils.GenericExceptionMapper;

import jakarta.ws.rs.core.Application;

public class MainApplication extends Application
{
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> resources = new HashSet<Class<?>>();
	public MainApplication() {
		DataLayer dm = new DataLayer();
		singletons.add(dm);
		singletons.add(new HouseResource(dm));
		singletons.add(new MediaResource());
		singletons.add(new ControlResource());
		singletons.add(new UserResource(dm));
		resources.add(GenericExceptionMapper.class);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return resources;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
