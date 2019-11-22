package auth;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyTest {

    private static final Logger logger = LoggerFactory.getLogger(ProxyTest.class);

    @Test
    public void testProxy(){
        ProxyHandler.getNewProxy();
    }
}
