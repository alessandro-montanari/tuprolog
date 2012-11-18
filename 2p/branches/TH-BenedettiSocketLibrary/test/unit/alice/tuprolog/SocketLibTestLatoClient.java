package alice.tuprolog;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.Theories;

import alice.tuprolog.lib.SocketLib;

public class SocketLibTestLatoClient {

	private Prolog engine = null;
	
	@Before
	public void before()
	{
		engine = new Prolog();
		SolveInfo info=null;

		try {
			engine.loadLibrary("alice.tuprolog.lib.SocketLib");
		} catch (InvalidLibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	@Test
	public void testTcp_socket_client_open_2() throws PrologException, PrologError {
		String theory="client(X,Y,Z):-tcp_socket_client_open('127.0.0.1:4444',Sock).";
		engine.setTheory(new Theory(theory));
		
		SocketLib lib=(SocketLib) engine.loadLibrary("alice.tuprolog.lib.SocketLib");
		
		Struct Address=new Struct("127.0.0.1:4444");
		Term Socket=new Var();
		
		
		boolean t=lib.tcp_socket_client_open_2(Address, Socket);
		assertTrue(t);
	}

	@Test
	public void testWrite_to_socket_2() {
		fail("Not yet implemented");
	}

	@Test
	public void testRead_from_socket_3() {
		fail("Not yet implemented");
	}

	@Test
	public void testAread_from_socket_2() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAddress_2() {
		fail("Not yet implemented");
	}

}
