import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Miner {
    private Settings s;
    private Blockchain blockchain;
    private AtomicBoolean mining_available = new AtomicBoolean(true);
    private Reciever reciever = new Reciever();
    private List<Reciever> peers = new ArrayList<Reciever>();
    private Transmitter transmitter = new Transmitter();

    public Miner(String index, Settings settings, Render render) {
        this.s = settings;
        this.blockchain = new Blockchain(2, index, s, render);
    }
    public Reciever get_peer() {
        return reciever;
    }

    public void add_peers(Reciever peer) {
        peers.add(peer);
        transmitter.update_peers(peers);
    }

    public Boolean mine_block(Block block) {
        String targetPrefix = "0".repeat(s.difficulty);
        while (!block.get_hash().startsWith(targetPrefix) && mining_available.get()) {
            block.nonce_pp();
            block.update_timestamp();
            block.calculate_hash();
        }
        if (block.get_hash().startsWith(targetPrefix))
            return true;

        else
            return false;
    }

    public void activate() {
        blockchain.display_chain();

        Runnable mining = () -> {
            while (s.running.get()) {
                if (reciever.is_waiting_data()) {
                    String data = reciever.get_data();
                    // System.out.println(blockchain.length);
                    Block block = new Block(blockchain.length, blockchain.get_latest_block().get_hash(), data, System.currentTimeMillis(), 0);

                    mining_available.set(true);
                    Boolean is_mined = mine_block(block);
                    if (is_mined) {
                        blockchain.add_block(block, false);
                        transmitter.broadcast(block); // important
                        blockchain.merge(mining_available);
                    }
                }
            }
        };

        Runnable listening = () -> {
            while (s.running.get()) {
                if (reciever.is_waiting_blocks()) {
                    Block block = reciever.get_block();
                    blockchain.add_block(block, true);
                    blockchain.merge(mining_available);
                }
            }
        };

        Thread mining_thread = new Thread(mining);
        Thread listening_thread = new Thread(listening);

        mining_thread.start();
        listening_thread.start();
    }
}

class Transmitter {
    protected List<Reciever> peers;

    public void update_peers(List<Reciever> new_peers) {
        this.peers = new_peers;
    }

    public void broadcast(Block block) {
        for (Reciever peer: peers) {
            peer.listen_block(block);
        }
    }

    public void broadcast(Reciever peer, String data) {
        peer.listen_data(data);
    }

    public void broadcast(Package pack) {
        for (Reciever peer : peers)
            peer.listen_package(pack);
    }
}

class Reciever {
    private Queue<String> waiting_data =  new LinkedList<String>();
    private Queue<Block> waiting_blocks = new LinkedList<Block>();
    private Queue<Package> waiting_packages = new LinkedList<Package>();
    private int len_blocks = 0, len_data = 0, len_package = 0;

    public Boolean is_waiting_data() {
        if (len_data != 0) 
            return true;
        else
            return false;
    }

    public String get_data() {
        len_data--;
        return waiting_data.poll();
    }

    public void listen_data(String data) {
        waiting_data.add(data);
        len_data++;
    }

    public Boolean is_waiting_blocks() {
        if (len_blocks != 0) 
            return true;
        else
            return false;
    }

    public Block get_block() {
        len_blocks--;
        return waiting_blocks.poll();
    }

    public void listen_block(Block block) {
        waiting_blocks.add(block);
        len_blocks++;
    }

    public Boolean is_waiting_packages() {
        if (len_package != 0)
            return true;
        else
            return false; 
    }

    public Package get_package() {
        len_package--;
        return waiting_packages.poll();
    }

    public void listen_package(Package pack) {
        waiting_packages.add(pack);
        len_package++;
    }
}

class Package {
    private int node;
    private String data;

    Package(int node, String data) {
        this.node = node;
        this.data = data;
    }

    public int get_node() {
        return node;
    }

    public String get_data() {
        return data;
    }
}