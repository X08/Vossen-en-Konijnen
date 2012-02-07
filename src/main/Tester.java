package src.main;
import static org.junit.Assert.*;

import org.junit.Test;


public class Tester {

	@Test
	public void test() {
		if (src.logic.FieldStats.rabbitCount == 0) {
		fail("Counter did not collect data!");
		}
	}
	
	@Test
	public void test2() {
		fail("No second test has been entered");
	}

}
