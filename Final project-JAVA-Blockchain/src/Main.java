import java.util.*;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Settings s = new Settings();
        Render render = new Render();
        Miner miner1 = new Miner("1", s, render), miner2 = new Miner("2", s, render);
        DataGenerator generator = new DataGenerator(s);
        Reciever peer1 = miner1.get_peer(), peer2 = miner2.get_peer(), data_peer = generator.get_peer();
        
        generator.add_peers(peer1);
        generator.add_peers(peer2);
        
        miner1.add_peers(peer2);
        miner2.add_peers(peer1);
        render.add_peer(data_peer);

        Platform.startup(() -> {
            Stage stage = new Stage();
            render.initialize(stage);
            stage.show();
        });

        // use for blockchain1
        Runnable program1 = () -> {
            miner1.activate();
        };

        // use for blockchain2
        Runnable program2 = () -> {
            miner2.activate();
        };

        // use for controlling 
        Runnable program3 = () -> {   
            try {
                generator.activate(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread1 = new Thread(program1);
        Thread thread2 = new Thread(program2);
        Thread thread3 = new Thread(program3);
        
        thread1.start();
        thread2.start();
        thread3.start();
        try {
            thread3.join();
        } 
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        s.running.set(false);
    }
    
    public static void show_queue(Queue<String> q) {
        Iterator<String> iterator = q.iterator();
        while (iterator.hasNext()) System.out.print(iterator.next() + " ");
        System.out.println();
    }
}