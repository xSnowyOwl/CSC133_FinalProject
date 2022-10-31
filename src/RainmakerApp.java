import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;

interface Updatable{
    void update();
}

abstract class GameObject extends Group implements Updatable{
    private Translate myTranslation;
    private Rotate myRotation;
    private Scale myScale;

    public GameObject(){
        myTranslation = new Translate();
        myRotation = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll(myTranslation, myRotation, myScale);
    }

    public void rotate(double degrees) {
        myRotation.setAngle(degrees);
        myRotation.setPivotX(0);
        myRotation.setPivotY(0);
    }

    public void scale(double scaleX, double scaleY) {
        myScale.setX(scaleX);
        myScale.setY(scaleY);
    }

    public void translate(double translateX, double translateY) {
        myTranslation.setX(translateX);
        myTranslation.setY(translateY);
    }

    public double getMyRotation(){
        return myRotation.getAngle();
    }

    public void update(){
        for(Node n : getChildren()) {
            if (n instanceof Updatable)
                ((Updatable) n).update();
        }
    }

    void add(Node node) {
        this.getChildren().add(node);
    }
}

class Chopper extends GameObject{
    private static double chopperBodyWidth = 30;
    private static double chopperBodyHeight = 50;
    private static double chopperTailWidth = 10;
    private static double chopperTailHeight = 60;

    public Chopper(){
        super();

        Rectangle chopperBody = new Rectangle();
        chopperBody.setWidth(chopperBodyWidth);
        chopperBody.setHeight(chopperBodyHeight);
        chopperBody.setFill(Color.LEMONCHIFFON);
        chopperBody.setTranslateX(438);
        chopperBody.setTranslateY(800);

        Rectangle chopperTail = new Rectangle();
        chopperTail.setWidth(chopperTailWidth);
        chopperTail.setHeight(chopperTailHeight);
        chopperTail.setFill(Color.LEMONCHIFFON);
        chopperTail.setTranslateX(chopperBody.getTranslateX()
                                        + 10);
        chopperTail.setTranslateY(chopperBody.getTranslateY()
                                        + chopperBodyHeight);

        add(chopperBody);
        add(chopperTail);
    }
}

class Rainmaker extends Group{
    private final static double APP_WIDTH = 900;
    private final static double APP_HEIGHT = 1000;
    Chopper chopper = new Chopper();

    AnimationTimer game = new AnimationTimer() {
        double oldFrame = -1;
        double elapsedTime = 0;
        int frameCount = 0;
        @Override
        public void handle(long currentFrame) {
            if(oldFrame < 0) oldFrame = currentFrame;
            double frameTime = (currentFrame - oldFrame) / 1e9;
            oldFrame = currentFrame;
            elapsedTime += frameTime;
        }
    };
    public Rainmaker(){
        this.getChildren().addAll(chopper);
    }
}


public class RainmakerApp extends Application {
    Pane root = new Pane();
    Point2D rainmakerApp = new Point2D(900, 1000);
    Rainmaker rainmaker = new Rainmaker();
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root, rainmakerApp.getX(),
                                rainmakerApp.getY(), Color.BLACK);
        root.getChildren().add(rainmaker);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Rainmaker");
        primaryStage.show();
    }

    public static void main(String[] args){
        Application.launch(args);
    }


}
