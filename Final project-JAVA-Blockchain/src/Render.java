import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Render extends Application {
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
    // private int left_index = 0;
    // private int right_index = 0;\
    double dragDeltaX;
    double dragDeltaY;
    private Stage stage;

    private Pane left_contentPane = new Pane();
    private Pane right_contentPane = new Pane();
    private Block test_block = new Block(0, "7042ac7d09c7bc41c8cfa5749e41858f6980643bc0db1a83cc793d3e24d3f77", "0", (long) 0, 0);

    private Transmitter transmitter = new Transmitter();
    private TextField text_field;
    private String input_data = "000";
    private int target_node = 2;

    public void initialize(Stage stage) {
        this.stage = stage;
        stage.setTitle("Control Panel");

        left_canvas = new Canvas(s.LEFT_PANE__WIDTH, s.LEFT_PANE__HEIGHT);
        right_canvas = new Canvas(s.RIGHT_PANE__WIDTH, s.RIGHT_PANE__HEIGHT);
        mid_canvas = new Canvas(s.MID_PANE__WIDTH, s.MID_PANE__HEIGHT);

        //add scroll to left and right canvas
        left_sp = create_scroll_pane(left_canvas, true, true);
        left_sp.setPrefSize(s.LEFT_PANE__WIDTH, s.LEFT_PANE__HEIGHT);
        right_sp = create_scroll_pane(right_canvas, true, true);
        right_sp.setPrefSize(s.RIGHT_PANE__WIDTH, s.RIGHT_PANE__HEIGHT);

        left_sp.setContent(left_contentPane);
        right_sp.setContent(right_contentPane);
        left_contentPane.setOnMousePressed(event -> {
            dragDeltaX = left_sp.getHvalue() - event.getSceneX();
            dragDeltaY = left_sp.getVvalue() - event.getSceneY();
        });

        left_contentPane.setOnMouseDragged(event -> {
            left_sp.setHvalue(dragDeltaX + event.getSceneX());
            left_sp.setVvalue(dragDeltaY + event.getSceneY());
        });
        right_contentPane.setOnMousePressed(event -> {
            dragDeltaX = right_sp.getHvalue() - event.getSceneX();
            dragDeltaY = right_sp.getVvalue() - event.getSceneY();
        });

        right_contentPane.setOnMouseDragged(event -> {
            right_sp.setHvalue(dragDeltaX + event.getSceneX());
            right_sp.setVvalue(dragDeltaY + event.getSceneY());
        });
        
        Button button1 = new Button("Node 1"), button2 = new Button("Node 2"), button3 = new Button("Both"), button4 = new Button("Send"), button5 = new Button("END");
        text_field = new TextField();
        text_field.setLayoutX(10); // 設置 TextField 的 X 座標
        text_field.setLayoutY(100); // 設置 TextField 的 Y 座標
        text_field.setPrefWidth(190); // 設置 TextField 的寬度
        text_field.setPrefHeight(30); // 設置 TextField 的高度

        button1.setLayoutX(10); // 設置 Button 的 X 座標
        button1.setLayoutY(50);  // 設置 Button 的 Y 座標
        button1.setPrefWidth(80); // 設置 Button 的寬度
        button1.setPrefHeight(30); // 設置 Button 的高度
        button1.setCursor(Cursor.HAND);

        button2.setLayoutX(110); // 設置 Button 的 X 座標
        button2.setLayoutY(50);  // 設置 Button 的 Y 座標
        button2.setPrefWidth(80); // 設置 Button 的寬度
        button2.setPrefHeight(30); // 設置 Button 的高度
        button2.setCursor(Cursor.HAND);

        button3.setLayoutX(210); // 設置 Button 的 X 座標
        button3.setLayoutY(50);  // 設置 Button 的 Y 座標
        button3.setPrefWidth(80); // 設置 Button 的寬度
        button3.setPrefHeight(30); // 設置 Button 的高度
        button3.setCursor(Cursor.HAND);

        button4.setLayoutX(210); // 設置 Button 的 X 座標
        button4.setLayoutY(100);  // 設置 Button 的 Y 座標
        button4.setPrefWidth(80); // 設置 Button 的寬度
        button4.setPrefHeight(30); // 設置 Button 的高度
        button4.setCursor(Cursor.HAND);

        button5.setLayoutX(210); // 設置 Button 的 X 座標
        button5.setLayoutY(950);  // 設置 Button 的 Y 座標
        button5.setPrefWidth(80); // 設置 Button 的寬度
        button5.setPrefHeight(30); // 設置 Button 的高度
        button5.setCursor(Cursor.HAND);
        // HBox buttonBox = new HBox(button1, button2);
        // StackPane middle_pane = new StackPane(mid_canvas, buttonBox);
        Pane middle_pane = new Pane();

        middle_pane.getChildren().addAll(button1, button2, button3, button4, text_field, button5);

        VBox vbox = new VBox(left_sp, right_sp);
        HBox hbox = new HBox(vbox, middle_pane);

        // 將 HBox 放入 Scene 中
        Scene scene = new Scene(hbox, s.WIDTH, s.HEIGHT);

        // 設置 Scene 到主 Stage
        stage.setScene(scene);
        // primaryStage.show();

        button1.setOnAction(e -> node1_action());
        button2.setOnAction(e -> node2_action());
        button3.setOnAction(e -> both_nodes_action());
        button4.setOnAction(e -> send_action());
        button5.setOnAction(e -> end_action());
    }

    public void main(String[] args) {
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

        button1.setOnAction(e -> node1_action());
        button2.setOnAction(e -> node2_action());
    }

    private ScrollPane create_scroll_pane(Canvas canvas, boolean horizontal, boolean vertical) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(canvas);
        scrollPane.setHbarPolicy(horizontal ? ScrollPane.ScrollBarPolicy.ALWAYS : ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(vertical ? ScrollPane.ScrollBarPolicy.ALWAYS : ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefSize(300, 200); // 設置 ScrollPane 的大小
        scrollPane.setCursor(Cursor.HAND); // 設置手形光標
        scrollPane.setPannable(true); // 啟用內容拖動

        // 處理拖動事件
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

        for (int i = 0; i < index + 1; i++) {
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

    public void render(String ID, List<Block> chain, List<Block> fork_chain, int fork_index) {
        ScrollPane sp = left_sp;
        Pane pane = left_contentPane;
        if (ID.equals("1")) {
            sp = left_sp;
            pane = left_contentPane;
        }
        else {
            sp = right_sp;
            pane = right_contentPane;
        }
        pane.getChildren().clear();

        if (fork_index == s.RESET_FORK_INDEX) {
            double x = s.RECT_X_COORD; // 初始X座標
            double y = s.RECT_Y_COORD; // Y座標
            for (int i = 0; i < chain.size(); i++) {
                Rectangle rect = new Rectangle(x, y, s.RECT_WIDTH, s.RECT_HEIGHT); // x, y, 寬, 高
                rect.setFill(Color.LIGHTGRAY);
                rect.setStroke(Color.BLACK);

                // 創建 Label 並指定位置
                Block block = chain.get(i);
                String text = generate_block_mes(block);
                Label label = new Label(text);
                label.setLayoutX(x + 0.5 * s.NODE_WIDTH); // Label 的 X 座標
                label.setLayoutY(y + 5); // Label 的 Y 座標
                label.setMaxSize(s.RECT_WIDTH - 2 * 0.5 * s.NODE_WIDTH, s.RECT_HEIGHT - 5);
                label.setWrapText(true); // 啟用自動換行
                label.setFont(new Font(s.FONT_SIZE));

                // 將方形和 Label 添加到 Pane
                pane.getChildren().addAll(rect, label);

                // 移到下一個X座標
                x += s.EXPAND_SIZE;
                sp.setHvalue(sp.getHmax());
            }
        }

        else {
            double x = s.RECT_X_COORD; // 初始X座標
            double y = s.RECT_Y_COORD; // Y座標
            double down_x = x;

            for (int i = 0; i < chain.size(); i++) {
                if (i == fork_index) {
                    down_x = x;
                }
                else if (i == fork_index + 1) {
                    y= s.FORK_UP_Y_COORD;
                }
                Rectangle rect = new Rectangle(x, y, s.RECT_WIDTH, s.RECT_HEIGHT); // x, y, 寬, 高
                rect.setFill(Color.LIGHTGRAY);
                rect.setStroke(Color.BLACK);

                // 創建 Label 並指定位置
                Block block = chain.get(i);
                String text = generate_block_mes(block);
                Label label = new Label(text);
                label.setLayoutX(x + 0.5 * s.NODE_WIDTH); // Label 的 X 座標
                label.setLayoutY(y + 5); // Label 的 Y 座標
                label.setMaxSize(s.RECT_WIDTH - 2 * 0.5 * s.NODE_WIDTH, s.RECT_HEIGHT - 5);
                label.setWrapText(true); // 啟用自動換行
                label.setFont(new Font(s.FONT_SIZE));

                // 將方形和 Label 添加到 Pane
                pane.getChildren().addAll(rect, label);

                // 移到下一個X座標
                x += s.EXPAND_SIZE;
                sp.setHvalue(sp.getHmax());
            }

            x = down_x + s.EXPAND_SIZE;
            y = s.FORK_DOWN_Y_COORD;
            for (int j = 0; j < fork_chain.size(); j++) {
                Rectangle rect = new Rectangle(x, y, s.RECT_WIDTH, s.RECT_HEIGHT); // x, y, 寬, 高
                rect.setFill(Color.LIGHTGRAY);
                rect.setStroke(Color.BLACK);

                // 創建 Label 並指定位置
                Block block = fork_chain.get(j);
                String text = generate_block_mes(block);
                Label label = new Label(text);
                label.setLayoutX(x + 0.5 * s.NODE_WIDTH); // Label 的 X 座標
                label.setLayoutY(y + 5); // Label 的 Y 座標
                label.setMaxSize(s.RECT_WIDTH - 2 * 0.5 * s.NODE_WIDTH, s.RECT_HEIGHT - 5);
                label.setWrapText(true); // 啟用自動換行
                label.setFont(new Font(s.FONT_SIZE));

                // 將方形和 Label 添加到 Pane
                pane.getChildren().addAll(rect, label);

                // 移到下一個X座標
                x += s.EXPAND_SIZE;
                sp.setHvalue(sp.getHmax());
            }
        }
    }

    private String generate_block_mes(Block block) {
        String text = "";
        List<String> block_info = block.get_info();
        for (int j = 0; j < block_info.size(); j++) {
            text += s.LABEL_NAME[j] + block_info.get(j) + '\n' + '\n';
        }
        return text;
    }

    private void node2_action() {
        // if (left_index == s.MAX_BLOCK_EDGE-1) 
        //     expand_and_move_canvas(left_canvas, left_sp, 0);
        // else if (left_index > s.MAX_BLOCK_EDGE-1) 
        //     expand_and_move_canvas(left_canvas, left_sp, s.EXPAND_SIZE);
        // drawLinkedList2(left_canvas.getGraphicsContext2D(), dataList2, left_index);
        // left_index++;
        // // left_sp.setH;
        // left_sp.setHvalue(left_sp.getHmax());
        target_node = 1;
    }

    private void node1_action() {
        // if (right_index == s.MAX_BLOCK_EDGE-1) 
        //     expand_and_move_canvas(right_canvas, right_sp, 0);
        // else if (right_index > s.MAX_BLOCK_EDGE-1) 
        //     expand_and_move_canvas(right_canvas, right_sp, s.EXPAND_SIZE);
        // drawLinkedList(right_canvas.getGraphicsContext2D(), dataList, right_index);
        // right_index++;
        target_node = 0;
    }

    private void both_nodes_action() {
        target_node = 2;
    }

    private void send_action() {
        input_data = text_field.getText();
        text_field.setText("");
        Package pack = new Package(target_node, input_data);
        transmitter.broadcast(pack);
    }

    private void end_action() {
        target_node = 9;
        send_action();
        stage.close();
    }

    public void add_peer(Reciever peer) {
        List<Reciever> peers = new ArrayList<Reciever>();
        peers.add(peer);
        transmitter.update_peers(peers);
    }
}