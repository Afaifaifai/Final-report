import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DataGenerator {
    private List<Reciever> peers = new ArrayList<Reciever>();
    private DataTransmitter transmitter = new DataTransmitter();
    Settings s;
    private Reciever reciever = new Reciever();

    public DataGenerator(Settings settings) {
        s = settings;
    }
    
    public void add_peers(Reciever peer) {
        peers.add(peer);
        transmitter.update_peers(peers);
    }

    public void activate(int mode) throws InterruptedException {
        if (mode == 0) 
            manual();
        else if (mode == 1)
            automatic();
        else if (mode == 2)
            manual2();
    }

    private void manual() {
        Scanner sc = new Scanner(System.in);
        String data = "";
        int chain_tunnel = 0;
        while (chain_tunnel != 9) {
            System.out.println("Enter data: ");
            data = sc.next();
            System.out.println("Enter chain: ");
            chain_tunnel = sc.nextInt();
            transmit(chain_tunnel, data);
        }
        sc.close();
        s.running.set(false);
    }

    private void automatic() throws InterruptedException {
        Random random = new Random();
        String data = "";
        int chain_tunnel = 0;
        for (int i = 0; i < 100; i++) {
            Thread.sleep(s.TIME_SLEEP);
            data = "" + random.nextInt();
            chain_tunnel = random.nextInt(3);
            transmit(chain_tunnel, data);
        }
        manual2();
    }

    private void manual2() {
        while (s.running.get()) {
            if (reciever.is_waiting_packages()) {
                Package pack = reciever.get_package();
                int node = pack.get_node();
                String data = pack.get_data();
                if (node == 9) {
                    s.running.set(false);
                    break;
                }
                else {
                    transmit(node, data);
                }
            }
        }
    }

    private void transmit(int chain_tunnel, String data) {
        switch (chain_tunnel) {
            case 0: 
                transmitter.broadcast(chain_tunnel, data);
                break;
            case 1: 
                transmitter.broadcast(chain_tunnel, data);
                break;
            case 2: 
                transmitter.broadcast(data);
                break;
            case 9: break;
            default: System.out.println("Unvalid chain tunnel.");
        }
    }

    public Reciever get_peer() {
        return reciever;
    }

    public Transmitter getTransmitter() {
        return transmitter;
    }

    public Settings getS() {
        return s;
    }
}

class DataTransmitter extends Transmitter {
    public void broadcast(int chain_tunnel, String data) {
        Reciever peer = peers.get(chain_tunnel);
        peer.listen_data(data);
    }

    public void broadcast(String data) {
        for (Reciever peer : peers) 
            peer.listen_data(data);
    }
}