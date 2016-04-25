package Testing;

/**
 * Created by Evdal on 09.03.2016.
 */
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;

//JUnit Suite Test
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestJUnitDB.class,
        TestJUnitEncryption.class,
        TestJUnitDelivery.class,
})
public class JUnitTestSuite {

}
