package jerry.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jerry.stack.I_NewStack;
import jerry.stack.NewStack;
import junit.framework.Assert;

class NewStackTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testAddNewElement() {
		I_NewStack ns = new NewStack();
		
		ns.AddNew((java.lang.Integer) Integer(3));
		
		assertEquals(1, ns.Length());
	}

	private Object Integer(int i) {
		// TODO Auto-generated method stub
		return null;
	}

}
