package alice.tuprolog;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import alice.tuprolog.AbstractSocket;
import alice.tuprolog.Client_Socket;
import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.PrologError;
import alice.tuprolog.Server_Socket;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.Var;
import alice.tuprologx.pj.model.List;

import alice.tuprolog.lib.SocketLib;
import alice.tuprolog.Client_Socket;
import alice.tuprolog.PrologError;
import alice.tuprolog.Server_Socket;
import alice.tuprolog.Term;


public class SocketLibTest {



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
	public Thread thread=new Thread();

	@Test
	public void testTcp_socket_server_open_3() throws PrologError, InvalidTheoryException, MalformedGoalException {

		String theory="server(X,Y,Z):- tcp_socket_server_open('127.0.0.1:4444', Sock,[]).";

		engine.setTheory(new Theory(theory));
		//Struct Options=new Struct("[]");

		//SocketLib lib= (SocketLib) engine.getLibrary("alice.tuprolog.lib.SocketLib");
		//Struct Address=new Struct("127.0.0.1:4444");
		//Term Socket= new Var();
		
		//boolean t=lib.tcp_socket_server_open_3(Address, Socket, Options);

		SolveInfo goal=	engine.solve("server(X,Y,Z).");
		assertTrue(goal.isSuccess());

	}
	@Test
	public void testTcp_socket_client_open_2() throws PrologException, PrologError {
		String theory="client(X,Y,Z):-tcp_socket_client_open('127.0.0.1:4444',Sock).";
		engine.setTheory(new Theory(theory));
		
		SocketLib lib=(SocketLib) engine.loadLibrary("alice.tuprolog.lib.SocketLib");
		
		
		Struct Address=new Struct("127.0.0.1:4444");
		Term Socket= new Var();
		Struct Options=new Struct("[]");
		
		lib.tcp_socket_server_open_3(Address, Socket, Options);
		
		SolveInfo goal=engine.solve("client(X,Y,Z).");
		
		//boolean t=lib.tcp_socket_client_open_2(Address, Socket);
		assertTrue(goal.isSuccess());
	}




	@Test
	public void testTcp_socket_server_accept_3() throws InvalidTheoryException, PrologError {
		String theory="server(X,Y,Z):- tcp_socket_server_open('127.0.0.1:4444', Sock,[])," +
				"tcp_socket_server_accept(Sock,ClientAddr,Slave).";
		engine.setTheory(new Theory(theory));
		SocketLib lib= (SocketLib) engine.getLibrary("alice.tuprolog.lib.SocketLib");
//
//		Term ServerSock=Var.FALSE;
//		Term Client_Addr=new Var();
//		Term Client_Slave_Socket=new Var();
//
//		boolean t=lib.tcp_socket_server_accept_3(ServerSock, Client_Addr, Client_Slave_Socket);

		//assertTrue(t);

	}

	@Test
	public void testTcp_socket_server_close_1() throws PrologError, InvalidTheoryException {
		String theory="tcp_socket_server_close(Sock).";
		engine.setTheory(new Theory(theory));
		SocketLib lib=(SocketLib) engine.getLibrary("alice.tuprolog.lib.SocketLib");
		
		Term Sock= new Var();

		boolean t=lib.tcp_socket_server_close_1(Sock);
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
