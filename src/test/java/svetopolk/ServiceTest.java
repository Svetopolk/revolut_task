package svetopolk;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ServiceTest {

    @Test
    void test() {
        Service service = new Service();
        assertThat(service.transfer(), is("ok"));
    }

    @Test
    void testThrow() {
        Service service = new Service();
        assertThat(service.transfer(), is("ok"));
    }
}