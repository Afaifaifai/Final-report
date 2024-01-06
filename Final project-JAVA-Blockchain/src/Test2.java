import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Test2 extends Application {
    // private Canvas left_canvas = new Canvas();
    // private Canvas mid_canvas = new Canvas();
    // private Canvas right_canvas = new Canvas();
    private Canvas left_canvas;
    private Canvas mid_canvas;
    private Canvas right_canvas;
    private ScrollPane left_sp; // scroll pane
    private ScrollPane right_sp;
    private List<Integer> dataList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    private List<Block> dataList2 = new ArrayList<>(Arrays.asList(new Block(0, "0", "0", (long) 0, 0),
                                                                   new Block(1, "1", "1", (long) 1, 1),
                                                                   new Block(2, "2", "2", (long) 2, 2),
                                                                   new Block(3, "3", "3", (long) 3, 3)  ));
    private Settings s = new Settings();
    private int left_index = 0;
    private int right_index = 0;

    private int left_edge_per_block = 2;
    private int right_edge_per_block = 2;

    private Pane left_contentPane = new Pane();
    private Pane right_contentPane = new Pane();
    private Block test_block = new Block(0, "7042ac7d09c7bc41c8cfa5749e41858f6980643bc0db1a83cc793d3e24d3f77", "0", (long) 0, 0);
        // scrollPane.setContent(contentPane);


    // this.index = index;
    // this.previousHash = previousHash;
    // this.data = data;
    // this.timestamp = timestamp;
    // this.nonce = nonce;
    // this.has

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Control Panel");

        left_canvas = new Canvas(s.LEFT_PANE__WIDTH, s.LEFT_PANE__HEIGHT);
        right_canvas = new Canvas(s.RIGHT_PANE__WIDTH, s.RIGHT_PANE__HEIGHT);
        mid_canvas = new Canvas(s.MID_PANE__WIDTH, s.MID_PANE__HEIGHT);

        //add scroll to left and right canvas
        left_sp = create_scroll_pane(left_canvas, true, false);
        left_sp.setPrefSize(s.LEFT_PANE__WIDTH, s.LEFT_PANE__HEIGHT);
        right_sp = create_scroll_pane(right_canvas, true, false);
        right_sp.setPrefSize(s.RIGHT_PANE__WIDTH, s.RIGHT_PANE__HEIGHT);

        left_sp.setContent(left_contentPane);
        right_sp.setContent(right_contentPane);

        Button button1 = new Button("Button 1"), button2 = new Button("Button 2");
        HBox buttonBox = new HBox(button1, button2);
        StackPane middle_pane = new StackPane(mid_canvas, buttonBox);
        // Pane middle_pane = new Pane(buttonBox);

        HBox hbox = new HBox(left_sp, middle_pane, right_sp);

        // 將 HBox 放入 Scene 中
        Scene scene = new Scene(hbox, s.WIDTH, s.HEIGHT);

        // 設置 Scene 到主 Stage
        primaryStage.setScene(scene);
        primaryStage.show();

        button1.setOnAction(e -> left_action());
        button2.setOnAction(e -> right_action());
    }

    private ScrollPane create_scroll_pane(Canvas canvas, boolean horizontal, boolean vertical) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(canvas);
        scrollPane.setHbarPolicy(horizontal ? ScrollPane.ScrollBarPolicy.ALWAYS : ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(vertical ? ScrollPane.ScrollBarPolicy.ALWAYS : ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private void expand_and_move_canvas(Canvas canvas, ScrollPane scroll_pane, double size) {
        // 在這裡可以根據需要更改 Canvas 的大小
        double newWidth = canvas.getWidth() + size;
        canvas.setWidth(newWidth);
        scroll_pane.setHvalue(1.0);
    }

    private void drawLinkedList(javafx.scene.canvas.GraphicsContext gc, List<Integer> dataList, int index) {
        double x = 50; // 初始X座標
        double y = 50; // Y座標
        index = dataList.size();
        
        for (int i = 0; i < index+1; i++) {
            Integer data = dataList.get(i);

            // 計算節點的大小
            double nodeWidth = s.NODE_WIDTH;

            // 畫節點
            gc.strokeRect(x, y, s.RECT_WIDTH, s.RECT_HEIGHT);
            // gc.fillText(data.toString(), x + 0.5 * nodeWidth, y + 20);
            Label label = new Label("這是一些文字");
            label.setLayoutX(x+5); // X座標
            label.setLayoutY(y + 5);  // Y座標
            // left_sp.getChildren().add(label);

            // 移到下一個X座標
            x += s.EXPAND_SIZE;
        }
    }
    private void drawLinkedList2(GraphicsContext gc, List<Block> dataList, int index) {
        double x = s.RECT_X_COORD; // 初始X座標
        double y = s.RECT_Y_COORD; // Y座標
        // index = dataList.size() - 1;
        // int error = index + 1 - left_edge_per_block;
        // if (error > 0) {
        //     for (int i = 0; i < error; i++)
        //         expand_and_move_canvas(left_canvas, left_sp, s.EXPAND_SIZE);
        //     left_edge_per_block += error;
        // }

        for (int i = 0; i < index + 1; i++) {
        //     System.out.println(i);
        //     List<String> nodeData = dataList.get(i).get_info();

        //     // 計算節點的大小

        //     // 畫節點
        //     gc.strokeRect(x, y, s.RECT_WIDTH, s.RECT_HEIGHT);

        //     // 在長方形內顯示資料
        //     double textY = y + 20;

        //     for (String data : nodeData) {
        //         gc.fillText(data+"\n", x + 0.5 * nodeWidth, textY);
        //         // System.out.println(data.length());
        //         textY += 20; // 設定行距，可以根據需要調整
        //     }
            Rectangle rect = new Rectangle(x, y, s.RECT_WIDTH, s.RECT_HEIGHT); // x, y, 寬, 高
            rect.setFill(Color.LIGHTGRAY);
            rect.setStroke(Color.BLACK);

            // 創建 Label 並指定位置
            String text = generate_block_mes(test_block);
            Label label = new Label(text);
            label.setLayoutX(x + 0.5 * s.NODE_WIDTH); // Label 的 X 座標
            label.setLayoutY(y + 20); // Label 的 Y 座標
            label.setMaxSize(s.RECT_WIDTH - 2 * 0.5 * s.NODE_WIDTH, s.RECT_HEIGHT - 5);
            label.setWrapText(true); // 啟用自動換行

            // 將方形和 Label 添加到 Pane
            left_contentPane.getChildren().addAll(rect, label);

            // 移到下一個X座標
            x += s.EXPAND_SIZE;
            left_sp.setHvalue(left_sp.getHmax());
        }
        left_sp.setHvalue(100.0);
    }

    private String generate_block_mes(Block block) {
        String text = "";
        List<String> block_info = block.get_info();
        for (int j = 0; j < block_info.size(); j++) {
            text += s.LABEL_NAME[j] + block_info.get(j) + '\n' + '\n';
        }
        return text;
    }


    private void left_action() {
        if (left_index == s.MAX_BLOCK_EDGE-1) 
            expand_and_move_canvas(left_canvas, left_sp, 0);
        else if (left_index > s.MAX_BLOCK_EDGE-1) 
            expand_and_move_canvas(left_canvas, left_sp, s.EXPAND_SIZE);
        drawLinkedList2(left_canvas.getGraphicsContext2D(), dataList2, left_index);
        left_index++;
        // left_sp.setH;
        left_sp.setHvalue(left_sp.getHmax());
    }

    private void right_action() {
        if (right_index == s.MAX_BLOCK_EDGE-1) 
            expand_and_move_canvas(right_canvas, right_sp, 0);
        else if (right_index > s.MAX_BLOCK_EDGE-1) 
            expand_and_move_canvas(right_canvas, right_sp, s.EXPAND_SIZE);
        drawLinkedList(right_canvas.getGraphicsContext2D(), dataList, right_index);
        right_index++;
    }
}

class Block {
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

