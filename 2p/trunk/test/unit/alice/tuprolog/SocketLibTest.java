package alice.tuprolog;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import alice.tuprolog.lib.SocketLibrary;


public class SocketLibTest {

	private static Prolog engine1 = null;
	private static Prolog engine2 = null;
	private static Term socket  = new Var(); // ADDED BY ED 2013-05-23
	
	/*
	@AfterClass 
	public static void after() throws PrologError { // ED 2013
		SocketLibrary lib1 = (SocketLibrary) engine1.getLibrary("alice.tuprolog.lib.SocketLibrary");
        boolean closed = lib1.tcp_socket_server_close_1(socket);
		System.out.println("[SocketLibTest] Server socket " + socket + " closed ? " + closed); 
	}
	*/
	
	@BeforeClass
	public static void before() throws InterruptedException, InvalidTheoryException, MalformedGoalException {
		engine1 = new Prolog();
		engine2 = new Prolog();
		
		System.out.println("[SocketLibTest] Starting server configuration"); 
		
		Thread myThread = new Thread() { 
			public void run() { 
				System.out.println("[SocketLibTest] Starting Server Thread"); 
				SocketLibrary lib;

				Struct Address = new Struct("127.0.0.1:4444");
				//Term   socket  = new Var(); // COMMENTED OUT BY ED 2013-05-23
				Struct Options = new Struct("[]");
				Term   ClientSocket = new Var();
				Term   Msg = new Var();
				
					try {			
				
					engine1.loadLibrary("alice.tuprolog.lib.SocketLibrary");
					engine2.loadLibrary("alice.tuprolog.lib.SocketLibrary");
						
					lib = (SocketLibrary) engine1.getLibrary("alice.tuprolog.lib.SocketLibrary");
									
					if (!((Var)socket).isBound()){ 	// External If ADDED BY ED 2013-05-23
						if (lib.tcp_socket_server_open_3(Address, socket, Options))	// raises bindException if address already in use
						{
							// System.out.println("[SocketLibTest] Server listening at " + socket); 
						}
					}
					System.out.println("[SocketLibTest] Server listening at " + socket);
					boolean ta;
					ta=lib.tcp_socket_server_accept_3(socket, Address, ClientSocket);
					if(ta)
					{
						System.out.println("[SocketLibTest] Connection accepted from client " + ClientSocket);
					}				
					
					boolean rd;
					rd=lib.read_from_socket_3(ClientSocket, Msg, Options);
					if(rd)
					{
						System.out.println("[SocketLibTest] Client reading at " + ClientSocket + ", message read: " + Msg );
					}
				
					
				} catch (InvalidLibraryException e1) {
					System.out.println("[SocketLibTest] InvalidLibraryException");
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				 catch (PrologError e) {
					System.out.println("[SocketLibTest] PrologError");
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			
		}; 
		myThread.start(); 
		Thread.sleep(500); //ED: was 1000
	} 
	
	@Test
	public void testWrite_to_socket_2() throws InvalidTheoryException, MalformedGoalException, PrologError, NoSolutionException, UnknownVarException {
		String theory = "client(Sock) :- tcp_socket_client_open('127.0.0.1:4444',Sock), write_to_socket(Sock,test1).";
		engine2.setTheory(new Theory(theory));
		System.out.println("[SocketLibTest] Client1 connecting...");
		SolveInfo result = engine2.solve("client(X).");
		assertTrue(result.isSuccess());
		Var sock = (Var)result.getTerm("X");
		System.out.println("[SocketLibTest] socket is bound? "+ sock.isBound());	
		System.out.println("[SocketLibTest] Client1 connected successfully from socket "+ sock);	
	}

	@Test
	public void testTcp_socket_client_open_2() throws PrologException, PrologError {
		String theory = "client(Sock):-tcp_socket_client_open('127.0.0.1:4444',Sock).";
		engine2.setTheory(new Theory(theory));
		System.out.println("[SocketLibTest] Client2 connecting...");
		SolveInfo result = engine2.solve("client(X).");
		assertTrue(result.isSuccess());
		Var sock = (Var)result.getTerm("X");
		System.out.println("[SocketLibTest] socket is bound? "+ sock.isBound());	
		System.out.println("[SocketLibTest] Client2 connected successfully from socket "+ sock);	
	}
	
	@Test
	public void testTcp_socket_server_close_1() throws PrologError, InvalidTheoryException, MalformedGoalException {
		Struct Address=new Struct("127.0.0.1:4441");
        Term Socket= new Var();
        Struct Options=new Struct("[]");
                
        SocketLibrary lib = (SocketLibrary) engine1.getLibrary("alice.tuprolog.lib.SocketLibrary");

        System.out.println("[SocketLibTest] Testing socket server close");	
        System.out.println("[SocketLibTest] Opening server socket");	
        lib.tcp_socket_server_open_3(Address, Socket, Options);
        System.out.println("[SocketLibTest] Server socket opened successfully at " + Socket);	

		System.out.println("[SocketLibTest] Closing server socket");	
        boolean close=lib.tcp_socket_server_close_1(Socket);
        assertTrue(close);
		System.out.println("[SocketLibTest] Server socket closed successfully");	
	}

}
