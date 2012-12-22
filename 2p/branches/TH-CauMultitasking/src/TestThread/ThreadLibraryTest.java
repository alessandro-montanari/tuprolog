package TestThread;
import static org.junit.Assert.*;

import org.junit.Test;

import alice.tuprolog.Int;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;

/**
 * 
 */

/**
 * @author Eleonora Cau
 *
 */
public class ThreadLibraryTest {
	
	Prolog engine = new Prolog();
	String theory;
	

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_id_1(alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 * @throws NoSolutionException 
	 */
	@Test
	public void testThread_id_1() throws InvalidTheoryException, MalformedGoalException, NoSolutionException {
		SolveInfo sinfo = engine.solve("thread_id(ID).");	//unifica ad ID l'identificativo del thread corrente (Root)
		assertTrue(sinfo.isSuccess());
		Term id = sinfo.getVarValue("ID");
		assertEquals(id, new Int(1));
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_create_2(alice.tuprolog.Term, alice.tuprolog.Term)}.
	 * @throws MalformedGoalException 
	 * @throws InvalidTheoryException 
	 */
	@Test
	public void testThread_create_2() throws MalformedGoalException, InvalidTheoryException {
		theory = "genitore(bob,a).\n" +
		"genitore(bob,b).\n" +
		"genitore(bob,c).\n" +
		"genitore(bob,d).";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("thread_create(genitore(bob,X), ID).");
		assertTrue(sinfo.isSuccess());
		
		sinfo = engine.solve("thread_create(genitore(bob,X), ID), thread_create(genitore(b,Y), ID2).");
		assertTrue(sinfo.isSuccess());
	}
	
	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_next_sol_1(alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 * @throws NoSolutionException 
	 */
	@Test
	public void testThread_next_sol_1() throws InvalidTheoryException, MalformedGoalException, NoSolutionException {
		theory = "start(X,Y) :- thread_create(genitore(bob,X), ID), thread_create(genitore(b,Y), ID2), loop(1,5,1,ID), thread_read(ID, X), loop(1,3,1,ID2), thread_read(ID2, Y).\n"+
		"loop(I, To, Inc, ThreadId) :- Inc >= 0, I > To, !.\n"+
		"loop(I, To, Inc, ThreadId) :- Inc < 0,  I < To, !.\n"+
		"loop(I, To, Inc, ThreadId) :- (thread_has_next(ThreadId) -> thread_next_sol(ThreadId), Next is I+Inc, loop(Next, To, Inc, ThreadId); !).\n"+
		"genitore(b,b).\n" +
		"genitore(bob,c).\n" +
		"genitore(b,d).\n" +
		"genitore(bob,gdh).\n"+
		"genitore(b,e).\n" +
		"genitore(b,f).";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X,Y).");
		assertTrue(sinfo.isSuccess());
		
		Term X = sinfo.getVarValue("X");
		assertEquals(X, Term.createTerm("genitore(bob,gdh)"));
		
		Term Y = sinfo.getVarValue("Y");
		assertEquals(Y, Term.createTerm("genitore(b,f)"));
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_join_2(alice.tuprolog.Term, alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 * @throws NoSolutionException 
	 */
	@Test
	public void testThread_join_2() throws InvalidTheoryException, MalformedGoalException, NoSolutionException {
		theory = "genitore(bob,a).\n" +
		"genitore(b,b).";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("thread_create(genitore(bob,X), ID), thread_create(genitore(b,Y), ID2), thread_join(ID2,Y), thread_join(ID,X).");
		assertTrue(sinfo.isSuccess());
		
		Term X = sinfo.getVarValue("X");
		assertEquals(X, Term.createTerm("genitore(bob,a)"));
		
		Term Y = sinfo.getVarValue("Y");
		assertEquals(Y, Term.createTerm("genitore(b,b)"));
		
		sinfo = engine.solve("thread_create(genitore(bob,X), ID), thread_join(ID,X), thread_next_sol(ID).");	//il thread  stato rimosso
		assertFalse(sinfo.isSuccess());
	}
/*
	*//**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#new_log_file_1(alice.tuprolog.Term)}.
	 *//*
	@Test
	public void testNew_log_file_1() {
		
	}

	*//**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#write_log_2(alice.tuprolog.Term, alice.tuprolog.Term)}.
	 *//*
	@Test
	public void testWrite_log_2() {
		fail("Not yet implemented");
	}*/

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_read_2(alice.tuprolog.Term, alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 * @throws NoSolutionException 
	 */
	@Test
	public void testThread_read_2() throws InvalidTheoryException, MalformedGoalException, NoSolutionException {
		theory = "genitore(bob,a).\n" +
		"genitore(b,b).";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("thread_create(genitore(bob,X), ID), thread_create(genitore(b,Y), ID2), thread_read(ID2,Y), thread_read(ID,X).");
		assertTrue(sinfo.isSuccess());
		
		Term X = sinfo.getVarValue("X");
		assertEquals(X, Term.createTerm("genitore(bob,a)"));
		
		Term Y = sinfo.getVarValue("Y");
		assertEquals(Y, Term.createTerm("genitore(b,b)"));
		
		sinfo = engine.solve("thread_create(genitore(bob,X), ID), thread_read(ID,X), thread_next_sol(ID).");	//Il thread non  stato rimosso
		assertTrue(sinfo.isSuccess());
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_has_next_1(alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 * @throws NoSolutionException 
	 */
	@Test
	public void testThread_has_next_1() throws InvalidTheoryException, MalformedGoalException, NoSolutionException {
		theory = "start(X) :- thread_execute(X, ID), thread_sleep(1), mutex_lock('mutex'), lettura(ID,X).\n" +
		"lettura(ID, X):- thread_read(ID, X).\n" +
		"thread_execute(X, ID):- mutex_lock('mutex'), thread_create(X, ID), thread_execute2(ID), mutex_unlock('mutex'). \n" +
		"thread_execute2(ID) :- (thread_has_next(ID) -> thread_next_sol(ID), thread_execute2(ID); !). \n" +
		"genitore(bob,a).\n" +
		"genitore(bob,b).\n" +
		"genitore(bob,d).";
		engine.setTheory(new Theory(theory));
	
		SolveInfo sinfo = engine.solve("start(genitore(bob,X)).");
		assertTrue(sinfo.isSuccess());
		
		Term X = sinfo.getVarValue("X");
		assertEquals(X, Term.createTerm("d"));
	}

	

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_detach_1(alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 */
	@Test
	public void testThread_detach_1() throws InvalidTheoryException, MalformedGoalException {
		theory = "genitore(bob,a).\n" +
				"genitore(bob,b).";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("thread_create(genitore(bob,X), ID), thread_detach(ID), thread_next_sol(ID).");
		assertFalse(sinfo.isSuccess());
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_sleep_1(alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 */
	@Test
	public void testThread_sleep_1() throws InvalidTheoryException, MalformedGoalException {
		theory = "genitore(bob,a).\n" +
		"genitore(bob,b).";
		engine.setTheory(new Theory(theory));

		SolveInfo sinfo = engine.solve("thread_create(genitore(bob,X), ID), thread_sleep(500).");
		assertTrue(sinfo.isSuccess());
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_send_msg_2(alice.tuprolog.Term, alice.tuprolog.Term)}.
	 * @throws MalformedGoalException 
	 * @throws NoSolutionException 
	 * @throws InvalidTheoryException 
	 */
	@Test
	public void testThread_send_msg_2() throws MalformedGoalException, NoSolutionException, InvalidTheoryException {
		theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), invio('CODA', 'messaggio molto importante'), lettura(ID,X).\n" +
		"thread1(X) :- thread_wait_msg(a(X),'CODA'). \n " +
		"invio(ID, M):- thread_send_msg(a(M),ID). \n" +		//Versione con 'CODA'
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X).");
		assertTrue(sinfo.isSuccess());
		
		Term X = sinfo.getVarValue("X");
		assertEquals(X, new Struct("messaggio molto importante"));
		
		theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), invio(ID, 'messaggio molto importante'), lettura(ID,X), thread_get_msg(a(X),'CODA').\n" +	//Posso nuovamente prelevare, in quanto il msg non  stato eliminato
		"thread1(X) :- thread_wait_msg(a(X),'CODA'). \n " +
		"invio(ID, M):- thread_send_msg(a(M),ID). \n" +		//Versione con ID
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		sinfo = engine.solve("start(X).");
		assertTrue(sinfo.isSuccess());
		
		Term X1 = sinfo.getVarValue("X");
		assertEquals(X1, new Struct("messaggio molto importante"));
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_get_msg_2(alice.tuprolog.Term, alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 * @throws NoSolutionException 
	 */
	@Test
	public void testThread_get_msg_2() throws InvalidTheoryException, MalformedGoalException, NoSolutionException {
		theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), thread_sleep(500), invio('CODA', 'messaggio molto importante'), lettura(ID,X).\n" +
		"thread1(X) :- thread_get_msg(a(X),'CODA'). \n " +
		"invio(ID, M):- thread_send_msg(a(M),ID). \n" +		//Versione con 'CODA'
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X).");
		assertTrue(sinfo.isSuccess());
		
		Term X = sinfo.getVarValue("X");
		assertEquals(X, new Struct("messaggio molto importante"));
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_peek_msg_2(alice.tuprolog.Term, alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 * @throws NoSolutionException 
	 */
	@Test
	public void testThread_peek_msg_2() throws InvalidTheoryException, MalformedGoalException, NoSolutionException {
		theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), thread_sleep(500), invio('CODA', 'messaggio molto importante'), lettura(ID,X).\n" +
		"thread1(X) :- thread_peek_msg(a(X),'CODA'). \n " +
		"invio(ID, M):- thread_send_msg(a(M),ID). \n" +		//Versione con 'CODA'
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X).");
		assertFalse(sinfo.isSuccess());
		
		theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), invio(ID, 'messaggio molto importante'), lettura(ID,X), thread_get_msg(a(X),'CODA').\n" +	//Posso nuovamente prelevare, in quanto il msg non  stato rimosso
		"thread1(X) :- thread_peek_msg(a(X),'CODA'). \n " +
		"invio(ID, M):- thread_send_msg(a(M),ID). \n" +		//Versione con ID
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		sinfo = engine.solve("start(X).");
		assertTrue(sinfo.isSuccess());
		
		Term X = sinfo.getVarValue("X");
		assertEquals(X, new Struct("messaggio molto importante"));
		
		theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), lettura(ID,X).\n" +	
		"thread1(X) :- thread_peek_msg(a(X),'CODA'). \n " +
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		sinfo = engine.solve("start(X).");
		assertFalse(sinfo.isSuccess());
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_wait_msg_2(alice.tuprolog.Term, alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 * @throws NoSolutionException 
	 */
	@Test
	public void testThread_wait_msg_2() throws InvalidTheoryException, MalformedGoalException, NoSolutionException {
		theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), thread_sleep(500), invio('CODA', 'messaggio molto importante'), lettura(ID,X).\n" +
		"thread1(X) :- thread_wait_msg(a(X),'CODA'). \n " +
		"invio(ID, M):- thread_send_msg(a(M),ID). \n" +		//Versione con 'CODA'
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X).");
		assertTrue(sinfo.isSuccess());
		
		theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), invio(ID, 'messaggio molto importante'), lettura(ID,X), thread_get_msg(a(X),'CODA').\n" +	//Posso nuovamente prelevare, in quanto il msg non  stato rimosso
		"thread1(X) :- thread_wait_msg(a(X),'CODA'). \n " +
		"invio(ID, M):- thread_send_msg(a(M),ID). \n" +		//Versione con ID
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		sinfo = engine.solve("start(X).");
		assertTrue(sinfo.isSuccess());
		
		Term X = sinfo.getVarValue("X");
		assertEquals(X, new Struct("messaggio molto importante"));
		
		//SI BLOCCA IN ATTESA DEL MESSAGGIO
		/*theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), lettura(ID,X).\n" +	
		"thread1(X) :- thread_wait_msg(a(X),'CODA'). \n " +
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		sinfo = engine.solve("start(X).");
		assertFalse(sinfo.isSuccess());*/
	}

	/**
	 * Il metodo peek non riesce a prelevare la soluzione perch  stato rimosso il messaggio dalla coda
	 * 
	 * Test method for {@link alice.tuprolog.ThreadLibrary#thread_remove_msg_2(alice.tuprolog.Term, alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 */
	@Test
	public void testThread_remove_msg_2() throws InvalidTheoryException, MalformedGoalException {
		theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), invio('CODA', 'messaggio molto importante'), remove('CODA', 'messaggio molto importante'), lettura(ID,X).\n" +
		"thread1(X) :- thread_peek_msg(a(X),'CODA'), thread_sleep(500). \n " +
		"invio(ID, M):- thread_send_msg(a(M),ID). \n" +		//Versione con 'CODA'
		"remove(ID,M):- thread_remove_msg(a(M), ID).\n" +
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X).");
		assertFalse(sinfo.isSuccess());
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#message_queue_create_1(alice.tuprolog.Term)}.
	 *//*
	@Test
	public void testMessage_queue_create_1() {
		
	}*/

	/**
	 * start(X) -> prelevo la soluzione, poi distruggo la coda.
	 * start2(X) -> distruggo la coda, poi prelevo la soluzione.
	 * Test method for {@link alice.tuprolog.ThreadLibrary#message_queue_destroy_1(alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 */
	@Test
	public void testMessage_queue_destroy_1() throws InvalidTheoryException, MalformedGoalException {
		theory = "start(X) :- message_queue_create('CODA'), thread_create(thread1(X), ID), invio('CODA', 'messaggio molto importante'), lettura(ID,X), message_queue_destroy('CODA').\n" +
		"start2(X) :- message_queue_create('CODA'), invio('CODA', 'messaggio molto importante'), message_queue_destroy('CODA'), thread_create(thread1(X), ID),  lettura(ID,X).\n" +
		"thread1(X) :- thread_wait_msg(a(X),'CODA'). \n " +
		"invio(ID, M):- thread_send_msg(a(M),ID). \n" +		//Versione con 'CODA'
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X).");
		assertTrue(sinfo.isSuccess());
		
		sinfo = engine.solve("start2(X).");
		assertFalse(sinfo.isSuccess());
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#msg_queue_size_2(alice.tuprolog.Term, alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 * @throws NoSolutionException 
	 */
	@Test
	public void testMsg_queue_size_2() throws InvalidTheoryException, MalformedGoalException, NoSolutionException {
		theory = "start(X, S) :- message_queue_create('CODA'), thread_create(thread1(X), ID), loop(1,5,1,invio('CODA', 'messaggio molto importante')), lettura(ID,X), msg_queue_size(S, 'CODA').\n" +
		"loop(I, To, Inc, Action) :- Inc >= 0, I > To, !.\n"+
		"loop(I, To, Inc, Action) :- Inc < 0,  I < To, !.\n"+
		"loop(I, To, Inc, Action) :- Action, Next is I+Inc, loop(Next, To, Inc, Action).\n"+
		"thread1(X) :- thread_wait_msg(a(X),'CODA'). \n " +
		"invio(ID, M):- thread_send_msg(a(M),ID). \n" +		//Versione con 'CODA'
		"lettura(ID, X):- thread_join(ID, thread1(X)). ";
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X, S).");
		assertTrue(sinfo.isSuccess());
		
		Term X = sinfo.getVarValue("S");
		assertEquals(X, new Int(5));
	}

/*	*//**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#mutex_create_1(alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 *//*
	@Test
	public void testMutex_create_1() throws InvalidTheoryException, MalformedGoalException {
		
	}*/

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#mutex_destroy_1(alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 */
	@Test
	public void testMutex_destroy_1() throws InvalidTheoryException, MalformedGoalException {
		theory = "start(X, M) :- mutex_create(M), mutex_lock(M), thread_create(thread1(M), ID), thread_sleep(500), message_queue_create('CODA'), invio('CODA', 'messaggio molto importante'), lettura(ID,X). \n" +
		"thread1(M) :- mutex_destroy(M). \n" +
		"invio(Q, M):- thread_send_msg(a(M),Q), mutex_unlock('mutex'). \n" +
		"lettura(ID, X):- thread_read(ID, thread1(X))."	;
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X, 'mutex').");
		assertFalse(sinfo.isSuccess());
	}

	/**
	 * 
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 */
	@Test
	public void testMutex_lock_unlock_1() throws InvalidTheoryException, MalformedGoalException {
		theory = "start(X) :- mutex_lock('mutex'), thread_create(thread1(X), ID), message_queue_create('CODA'), invio('CODA', 'messaggio molto importante'), lettura(ID,X). \n" +
		"thread1(X) :- mutex_lock('mutex'), thread_peek_msg(a(X),'CODA'), mutex_unlock('mutex'). \n" +
		"invio(Q, M):- thread_send_msg(a(M),Q), mutex_unlock('mutex'). \n" +
		"lettura(ID, X):- thread_read(ID, thread1(X))."	;
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X).");
		assertTrue(sinfo.isSuccess());
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#mutex_trylock_1(alice.tuprolog.Term)}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 */
	@Test
	public void testMutex_trylock_1() throws InvalidTheoryException, MalformedGoalException {
		theory = "start(X) :- mutex_lock('mutex'), thread_create(thread1(X), ID), message_queue_create('CODA'), invio('CODA', 'messaggio molto importante'), lettura(ID,X). \n" +
		"thread1(X) :- mutex_trylock('mutex'), thread_peek_msg(a(X),'CODA'), mutex_unlock('mutex'). \n" +
		"invio(Q, M):- thread_send_msg(a(M),Q). \n" +
		"lettura(ID, X):- thread_read(ID, thread1(X))."	;
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X).");
		assertFalse(sinfo.isSuccess());
	}

	/**
	 * Test method for {@link alice.tuprolog.ThreadLibrary#mutex_unlock_all_0()}.
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 */
	
	@Test
	public void testMutex_unlock_all_0() throws InvalidTheoryException, MalformedGoalException {
		theory = "start(X) :- thread_create(thread1(X), ID), mutex_lock('mutex1'). \n" +
		"thread1(X, M1, M2) :- mutex_lock('mutex1'), mutex_lock('mutex2'), mutex_unlock_all." ;
		engine.setTheory(new Theory(theory));
		
		SolveInfo sinfo = engine.solve("start(X).");
		assertTrue(sinfo.isSuccess());
	}
}