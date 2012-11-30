package alice.tuprolog;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import javax.print.attribute.standard.Severity;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cli.System.IO.File;

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

	private static Prolog engine = null;
	private static  Prolog engine2=null;

	@BeforeClass
	public static void before() throws InterruptedException {
		engine = new Prolog();
		engine2=new Prolog();
		
		System.out.println("Server configuration"); 
		Thread myThread = new Thread() { 
			public void run() { 
				System.out.println("Running myThread"); 
				SocketLib lib;

				Struct Address=new Struct("127.0.0.1:4444");
				Term Socket= new Var();
				Struct Options=new Struct("[]");
				Term ClientSocketSalve=new Var();
				
					try {			
				
					engine.loadLibrary("alice.tuprolog.lib.SocketLib");
					engine2.loadLibrary("alice.tuprolog.lib.SocketLib");
						
					lib = (SocketLib) engine.getLibrary("alice.tuprolog.lib.SocketLib");
									
					boolean ts;
					ts=lib.tcp_socket_server_open_3(Address, Socket, Options);
					if (ts)	
					{
						System.out.println("Server connection established"); 
						
					}
					boolean ta;
					ta=lib.tcp_socket_server_accept_3(Socket, Address, ClientSocketSalve);
					if(ta)
					{
						System.out.println("Connection accepted");
					}				
						
				
				} catch (InvalidLibraryException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				 catch (PrologError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}; 
		myThread.start(); 
		Thread.sleep(1000);
	} 

	@Test
	public void testTcp_socket_client_open_2() throws PrologException, PrologError {
			
		String theory="client(X,Y,Z):-tcp_socket_client_open('127.0.0.1:4444',Sock).";
		engine2.setTheory(new Theory(theory));
		SolveInfo goal=engine2.solve("client(X,Y,Z).");
		assertTrue(goal.isSuccess());
	}


	@Test
	public void testTcp_socket_server_close_1() throws PrologError, InvalidTheoryException, MalformedGoalException {
		
//		String theory="server(X,Y,Z):-tcp_socket_server_close(Sock).";
//		
//		engine.setTheory(new Theory(theory));
//		SolveInfo goal=engine.solve("server(X,Y,Z).");
//		assertTrue(goal.isSuccess());
		

	}

	@Test
	public void testWrite_to_socket_2() throws InvalidTheoryException, MalformedGoalException {
		String theory="client(X,Y,Z):-tcp_socket_client_open('127.0.0.1:4444',Sock),write_to_socket(Sock,test1).";
		engine2.setTheory(new Theory(theory));
		SolveInfo goal=engine2.solve("client(X,Y,Z).");
		assertTrue(goal.isSuccess());	
	}

	@Test
	public void testRead_from_socket_3() throws InvalidTheoryException, MalformedGoalException {

//		String theory="client(X,Y,Z):-tcp_socket_client_open('127.0.0.1:4444',Sock),read_from_socket(Sock,X,[]).";
//		engine2.setTheory(new Theory(theory));
//		SolveInfo goal=engine2.solve("client(X,Y,Z).");
//		assertTrue(goal.isSuccess());	
		
	}

	@Test
	public void testAread_from_socket_2() {
		fail("Not yet implemented");
	}


}
