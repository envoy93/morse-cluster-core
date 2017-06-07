import com.shashov.cluster.math.model.Bits;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by envoy on 07.06.2017.
 */

public class TestBits {

    @Test
    public void testBits() {
        int SIZE = 5;

        //new Bits(SIZE)
        Bits bits = new Bits(SIZE);
        assertEquals("00000", bits.getBites().toString());
        assertEquals(0, bits.getNumber().intValue());

        bits = new Bits(new StringBuilder("10011"));
        assertEquals(5, bits.getSize());
        assertEquals(19, bits.getNumber().intValue());

        bits = new Bits(5, new BigInteger("19"));
        assertEquals("10011", bits.getBites().toString());
    }

}
