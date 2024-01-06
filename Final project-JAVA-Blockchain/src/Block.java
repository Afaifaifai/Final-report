import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
    private int index;
    private String previous_hash;
    private String data;
    private long timestamp;
    private int nonce;
    private String hash;
    public List<Integer> L = new ArrayList<>();

    public Block(int index, String previousHash, String data, long timestamp, int nonce) {
        this.index = index;
        this.previous_hash = previousHash;
        this.data = data;
        this.timestamp = timestamp;
        this.nonce = nonce;
        calculate_hash();
    }

    // calculate hash with SHA256 algorithm
    public void calculate_hash() {
        String dataToHash = index + previous_hash + data + timestamp + nonce;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dataToHash.getBytes());
            StringBuilder hashStringBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hashStringBuilder.append(String.format("%02x", b));
            }
            hash = hashStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            hash = null;
        }
    }

    // mining data
    public void mine_block(int difficulty) {
        String targetPrefix = "0".repeat(difficulty);
        while (!hash.startsWith(targetPrefix)) {
            nonce++;
            timestamp = System.currentTimeMillis();
            calculate_hash();
        }
    }

    public void nonce_pp() {
        nonce++;
    }

    public void update_timestamp() {
        timestamp = System.currentTimeMillis();
    }

    public int get_index() {
        return index;
    }

    public String get_hash() {
        return hash;
    }

    public String get_previous_hash() {
        return previous_hash;
    }

    public String get_data() {
        return data;
    }

    public Long get_timestamp() {
        return timestamp;
    }
    public int get_nonce() {
        return nonce;
    }

    public void show_info() {
        System.out.println(index + " " + previous_hash + " " + data + "  " + hash);
    }

    public String info() {
        return index + " " + previous_hash + " " + data + "  " + hash;
    }

    public void add_to_L(int x) {
        L.add(x);
    }

    public void show_L() {
        for (int i : L) System.out.print(i + " ");
        System.out.println();
    }

    public List<String> get_info() {
        return new ArrayList<String>(Arrays.asList(""+index, previous_hash, data, ""+timestamp, ""+nonce, hash));
    }
}