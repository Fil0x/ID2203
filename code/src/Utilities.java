import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class Utilities {
    public static int hash(int key, int numNodes) {
        HashFunction hf = Hashing.murmur3_128();
        HashCode hc = hf.newHasher().putInt(key).hash();

        return hc.asInt() % numNodes;
    }
}
