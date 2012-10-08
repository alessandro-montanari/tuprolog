package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.Test;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({	BasicLibraryExceptionsTestCase.class, 
				BuiltInExceptionsTestCase.class, 
				DCGLibraryExceptionsTestCase.class,
				IOLibraryExceptionsTestCase.class, 
				ISOLibraryExceptionsTestCase.class, 
				JavaLibraryExceptionsTestCase.class, 
				JavaThrowCatchTestCase.class,
				ThrowCatchTestCase.class
})
public class ExceptionsTestSuite {}
