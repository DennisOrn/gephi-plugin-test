/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.testmodule2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.DecimalFormat;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.gephi.plugins.testmodule2//MainWindow//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "MainWindowTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.gephi.plugins.testmodule2.MainWindowTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MainWindowAction",
        preferredID = "MainWindowTopComponent"
)
@Messages({
    "CTL_MainWindowAction=MainWindow",
    "CTL_MainWindowTopComponent=MainWindow Window",
    "HINT_MainWindowTopComponent=This is a MainWindow window"
})
public final class MainWindowTopComponent extends TopComponent {

    private static JFXPanel fxPanel;
    private static final int NODE_RADIUS = 50;
    private double lastX, lastY, startScale, startRotate;
    
    public MainWindowTopComponent() {
        initComponents();
        setName(Bundle.CTL_MainWindowTopComponent());
        setToolTipText(Bundle.HINT_MainWindowTopComponent());
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

        Circle node1 = new Circle(300, 300, NODE_RADIUS, javafx.scene.paint.Color.LIGHTBLUE);
        Circle node2 = new Circle(500, 300, NODE_RADIUS, javafx.scene.paint.Color.LIGHTBLUE);
        Circle node3 = new Circle(400, 200, NODE_RADIUS, javafx.scene.paint.Color.LIGHTBLUE);
        node1.setStroke(javafx.scene.paint.Color.BLACK);
        node2.setStroke(javafx.scene.paint.Color.BLACK);
        node3.setStroke(javafx.scene.paint.Color.BLACK);

        Text text1 = new Text("node1");
        Text text2 = new Text("node2");
        Text text3 = new Text("node3");

        double W = text1.getBoundsInLocal().getWidth();
        double H = text1.getBoundsInLocal().getHeight(); // Is this necessary?
        text1.relocate(300 - W / 2, 300 - H / 2);

        W = text2.getBoundsInLocal().getWidth();
        H = text2.getBoundsInLocal().getHeight();
        text2.relocate(500 - W / 2, 300 - H / 2);

        W = text3.getBoundsInLocal().getWidth();
        H = text3.getBoundsInLocal().getHeight();
        text3.relocate(400 - W / 2, 200 - H / 2);

        Line line1 = new Line(300, 300, 500, 300);
        Line line2 = new Line(300, 300, 400, 200);

        group.getChildren().add(new Group(
            line1, line2,
            node1, node2, node3,
            text1, text2, text3));
        
        group.setOnMousePressed(new EventHandler<MouseEvent>() {             
            @Override public void handle(MouseEvent event) {
                lastX = event.getX();
                lastY = event.getY();
            }
        });
        
        group.setOnMouseDragged(new EventHandler<MouseEvent>() {                
            @Override public void handle(MouseEvent event) {
                double layoutX = group.getLayoutX() + (event.getX() - lastX);
                double layoutY = group.getLayoutY() + (event.getY() - lastY);
                if ((layoutX >= 0) && (layoutX <= (group.getLayoutBounds().getWidth()))) {
                    group.setLayoutX(layoutX);
                }
                if ((layoutY >= 0) && (layoutY <= (group.getLayoutBounds().getHeight()))) {
                    group.setLayoutY(layoutY);
                }
                if ((group.getLayoutX() + (event.getX() - lastX) <= 0)) {
                    group.setLayoutX(0);
                }
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
