package alice.tuprologx.eclipse.core;

import java.util.*;

public class PrologEngineFactory {
	
	@SuppressWarnings({ "rawtypes" })
	private Hashtable registry = null;
	private static PrologEngineFactory instance;

	@SuppressWarnings({ "rawtypes" })
	protected PrologEngineFactory() {
		registry = new Hashtable();
	}

	public static PrologEngineFactory getInstance() {
		if (instance == null)
			instance = new PrologEngineFactory();
		return instance;
	}

	@SuppressWarnings({ "rawtypes" })
	public Vector getProjectEngines(String projectName) {
		if (projectName != null)
			return (Vector) registry.get(projectName);
		else
			return null;
	}

	@SuppressWarnings({ "rawtypes" })
	public Vector getEngines() {
		return (Vector) registry.values();
	}

	@SuppressWarnings({ "rawtypes" })
	public PrologEngine getEngine(String projectName, int index) {
		Vector engines = getProjectEngines(projectName);
		if (engines != null)
			return (PrologEngine)engines.elementAt(index);
		else
			return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PrologEngine insertEntry(String projectName, String name) {
		if (projectName != null) {
			PrologEngine engine = new PrologEngine(projectName, name);
			Vector engines = new Vector();
			engines.add(engine);
			registry.put(projectName, engines);
			return engine;
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PrologEngine addEngine(String projectName, String name) {
		if (projectName != null) {
			PrologEngine engine = new PrologEngine(projectName, name);
			Vector engines = getProjectEngines(projectName);
			if (engines != null) {
				engines.add(engine);
				registry.put(projectName, engines);
			}
			return engine;
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void deleteEngine(String projectName, String name) {
		if (projectName != null) {
			Vector engines = getProjectEngines(projectName);
			for (int i = 0; i < engines.size(); i++) {
				if (name.equals(PrologEngineFactory.getInstance()
						.getEngine(projectName, i).getName())) {
					engines.removeElementAt(i);
				}
			}
			registry.remove(projectName);
			registry.put(projectName, engines);
		}
	}

}
