/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.testmodule2;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.gephi.plugins.testmodule2//MainWindow2//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "MainWindow2TopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.gephi.plugins.testmodule2.MainWindow2TopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MainWindow2Action",
        preferredID = "MainWindow2TopComponent"
)
@Messages({
    "CTL_MainWindow2Action=MainWindow2",
    "CTL_MainWindow2TopComponent=MainWindow2 Window",
    "HINT_MainWindow2TopComponent=This is a MainWindow2 window"
})
public final class MainWindow2TopComponent extends TopComponent {
    
    public class TestNode {
        
        private Circle circle;
        private Text text;
        private ArrayList<TestNode> connections = new ArrayList<TestNode>();
        private ArrayList<Edge> lines = new ArrayList<Edge>();

        public TestNode(double centerX, double centerY, String name) {
            circle = new Circle(centerX, centerY, NODE_RADIUS, NODE_COLOR);
            circle.setStroke(Color.BLACK);
            text = new Text(name);
            relocateText(text, centerX, centerY);
        }

        public void relocateText(Text text, double centerX, double centerY) {
            double textWidth = text.getBoundsInLocal().getWidth();
            double textHeight = text.getBoundsInLocal().getHeight();
            text.relocate(circle.getCenterX() - textWidth / 2, circle.getCenterY() - textHeight / 2);
        }
    }

    public class Edge {

        private Line line;
        private TestNode firstNode, secondNode;

        public Edge(TestNode node1, TestNode node2) {
            line = new Line(
                node1.circle.getCenterX(),
                node1.circle.getCenterY(),
                node2.circle.getCenterX(),
                node2.circle.getCenterY());
            firstNode = node1;
            secondNode = node2;
        }
    }

    private static JFXPanel fxPanel;
    private static final int NODE_RADIUS = 50;
    private static final Color NODE_COLOR = Color.LIGHTBLUE;
    private double lastX, lastY, startScale, startRotate;

    private int ITERATION = 0; // TEMPORARY; FOR TESTING

    private void connectNodes(TestNode node1, TestNode node2) {
        node1.connections.add(node2);
        node2.connections.add(node1);
        Edge edge = new Edge(node1, node2);
        node1.lines.add(edge);
        node2.lines.add(edge);
    }

    private void changePosition(TestNode node, double centerX, double centerY) {
        node.circle.setCenterX(centerX);
        node.circle.setCenterY(centerY);
        node.relocateText(node.text, centerX, centerY);
        for(Edge n : node.lines) {
            n.line.setStartX(centerX);
            n.line.setStartY(centerY);
            TestNode other = n.firstNode != node ? n.firstNode : n.secondNode;
            n.line.setEndX(other.circle.getCenterX());
            n.line.setEndY(other.circle.getCenterY());
        }
    }
    
    public MainWindow2TopComponent() {
        initComponents();
        setName(Bundle.CTL_MainWindow2TopComponent());
        setToolTipText(Bundle.HINT_MainWindow2TopComponent());
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);

        setLayout(new BorderLayout());
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                init(fxPanel);
            }
        });
    }
    
    private void init(JFXPanel fxPanel) {
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }
    
    private Scene createScene() {
        final Group group = new Group();
        Scene scene = new Scene(group);

        final TestNode node1 = new TestNode(300, 300, "node1");
        final TestNode node2 = new TestNode(500, 200, "node2");
        final TestNode node3 = new TestNode(80, 100, "node3");
        final TestNode node4 = new TestNode(650, 500, "node4");
        connectNodes(node1, node2);
        connectNodes(node1, node3);
        connectNodes(node2, node3);
        connectNodes(node1, node4);

        ArrayList<Node> array = new ArrayList<Node>();
        for(Edge n : node1.lines) array.add(n.line);
        for(Edge n : node2.lines) array.add(n.line);
        for(Edge n : node3.lines) array.add(n.line);
        for(Edge n : node4.lines) array.add(n.line);
        LinkedHashSet<Node> lhs = new LinkedHashSet<Node>();
        lhs.addAll(array); // Remove duplicates
        array.clear();
        array.addAll(lhs);

        array.add(node1.circle);
        array.add(node1.text);
        array.add(node2.circle);
        array.add(node2.text);
        array.add(node3.circle);
        array.add(node3.text);
        array.add(node4.circle);
        array.add(node4.text);

        Collection<Node> c = array;
        
        // Won't work with JavaFX 2.2 ->
        //group.getChildren().add(new Group(c));
        
        group.getChildren().add(new Group(
            //edge1.line, edge2.line, edge3.line, edge4.line,
            node1.circle, node1.text,
            node2.circle, node2.text,
            node3.circle, node3.text,
            node4.circle, node4.text));
        
        group.setOnMousePressed(new EventHandler<MouseEvent>() {             
            @Override public void handle(MouseEvent event) {
                lastX = event.getX();
                lastY = event.getY();
                if(ITERATION == 0) changePosition(node1, 100, 500); // TEMPORARY; FOR TESTING
                if(ITERATION == 1) changePosition(node2, 300, 200); // TEMPORARY; FOR TESTING
                if(ITERATION == 2) changePosition(node3, 600, 300); // TEMPORARY; FOR TESTING
                if(ITERATION == 3) changePosition(node4, 300, 530); // TEMPORARY; FOR TESTING
                if(ITERATION == 4) changePosition(node1, 500, 100); // TEMPORARY; FOR TESTING
                if(ITERATION == 5) changePosition(node1, 400, 350); // TEMPORARY; FOR TESTING
                if(ITERATION == 6) changePosition(node2, 200, 150); // TEMPORARY; FOR TESTING
                if(ITERATION == 6) changePosition(node3, 700, 200); // TEMPORARY; FOR TESTING
                ++ITERATION; // TEMPORARY; FOR TESTING
            }
        });
        
        group.addEventHandler(ZoomEvent.ZOOM_STARTED, new EventHandler<ZoomEvent>() {               
            @Override public void handle(ZoomEvent event) {
                startScale = group.getScaleX();
                group.setScaleX(startScale);
                //group.setScaleY(startScale); // Is this necessary? Apparently not.
            }
        });
        group.addEventHandler(ZoomEvent.ZOOM, new EventHandler<ZoomEvent>() {               
            @Override public void handle(ZoomEvent event) {
                group.setScaleX(startScale * event.getTotalZoomFactor());
                group.setScaleY(startScale * event.getTotalZoomFactor());
            }
        });
        
        // TODO: Adjust the text after rotation?

        group.addEventHandler(RotateEvent.ROTATION_STARTED, new EventHandler<RotateEvent>() {               
            @Override public void handle(RotateEvent event) {
                startRotate = group.getRotate();
                group.setRotate(startRotate);
            }
        });
        group.addEventHandler(RotateEvent.ROTATE, new EventHandler<RotateEvent>() {               
            @Override public void handle(RotateEvent event) {
                group.setRotate(startRotate + event.getTotalAngle());
            }
        });
        
        return scene;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
