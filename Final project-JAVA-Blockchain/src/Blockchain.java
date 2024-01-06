import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.application.Platform;

public class Blockchain {
    private List<Block> chain;
    private List<Block> fork_chain;
    private int fork_index;
    private int difficulty;
    private String ID;
    private Settings s;
    private Render render;
    int length = 1;

    public Blockchain(int difficulty, String id, Settings s, Render render) {
        this.chain = new ArrayList<>();
        this.fork_chain = new ArrayList<>();
        this.difficulty = difficulty;
        this.ID = id;
        this.s = s;
        this.fork_index = s.RESET_FORK_INDEX;
        this.render = render;
        create_initial_block();
    }

    //  add initial block to the chain
    private void create_initial_block() {
        chain.add(s.INITIAL_BLOCK);
    }

    // create and add a new block to the chain
    public void add_block(Block new_block, Boolean fork) {
        if (fork) {
            if (validation_fork(new_block)) {
                if (fork_chain.size() == 0)
                    fork_index = new_block.get_index() - 1;
                    
                fork_chain.add(new_block);
            } 
            else {
                System.out.println("Block rejected: Invalid fork block.");
            }
        }
        else {
            if (validation(new_block, new_block.get_index())) {
                chain.add(new_block);
            }
            else
                System.out.println("Block rejected: Invalid block.");
        }
    }

    private boolean validation(Block new_block, int index) {
        if (index > chain.size())
            return false;
        else {
            Block previous_block =  chain.get(index-1);
            // new_block.show_info();
            if (! (previous_block.get_hash().equals(new_block.get_previous_hash()))) {
                // System.out.println("111");
                return false;
            }
            else if (! (calculate_hash(new_block).equals(new_block.get_hash()))) {
                // System.out.println("222");
                // System.out.println(calculate_hash(new_block));
                // System.out.println(new_block.get_hash());
                return false;
            }
            else
                return true;
        }
    }

    private boolean validation_fork(Block new_block) {
        // later check whether the data is unique
        if (fork_chain.size() == 0)
            return validation(new_block, new_block.get_index());
        else {  
            Block previous_block = fork_chain.get(fork_chain.size() - 1);
            if (new_block.get_index() <= previous_block.get_index()) 
                return false;
            else {
                if (! (previous_block.get_hash().equals(new_block.get_previous_hash())))
                    return false;
                else if (! (calculate_hash(new_block).equals(new_block.get_hash())))
                    return false;
                else
                    return true;
            }
        }
    }

    private String calculate_hash(Block block) {
        int index = block.get_index(), nonce = block.get_nonce();
        long timestamp= block.get_timestamp();
        String previous_hash = block.get_previous_hash(), data = block.get_data();

        String dataToHash = index + previous_hash + data + timestamp + nonce; 
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dataToHash.getBytes());
            StringBuilder hashStringBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hashStringBuilder.append(String.format("%02x", b));
            }
            return hashStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void merge(AtomicBoolean mining_available){
        int chain_length = chain.size() - 1 - fork_index;
        int fork_length = fork_chain.size();

        if (chain_length - fork_length >= s.MERGE_LIMIT) { // main chain merges fork chain
            fork_chain = new ArrayList<>();
            fork_index = s.RESET_FORK_INDEX;
        }

        else if (fork_length - chain_length >= s.MERGE_LIMIT) { // fork chain merges main chain
            int range = chain.size();
            for (int i = 0; i < range; i++) {
                if (i > fork_index) {
                    chain.remove(fork_index + 1);
                    // System.out.println(i);
                }
            }

            for (Block block : fork_chain) {
                chain.add(block);
            }

            fork_chain = new ArrayList<>();
            fork_index = s.RESET_FORK_INDEX;
            // drop the current mining block
            // becasue the current mining block calculated on main chain got merged
            mining_available.set(false);
        }
        length = chain.size();
        display_chain();
    }

    public List<Block> get_chain() {
        return new ArrayList<>(chain);
    }

    // display the whole chain
    public void display_chain() {
        Platform.runLater(() -> {
            render.render(ID, chain, fork_chain, fork_index);
        });
        String info = "";
        info += "BlockChain " + ID + '\n';
        // System.out.println("BlockChain " + ID);
        for (Block block : chain) 
            info += block.info() + '\n';
        System.out.println(info + '\n' + "Chain length : " + chain.size() + "\t Fork index : " + fork_index + '\n');
    }

    // get the last block in the list 
    public Block get_latest_block() {
        return chain.get(chain.size() - 1);
    }
}