import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
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

class Helicopter extends GameObject{
    private static final double chopperBodyWidth = 30;
    private static final double chopperBodyHeight = 50;
    private static final double chopperTailWidth = 10;
    private static final double chopperTailHeight = 60;

    public Helicopter(){
        super();

        Rectangle chopperBody = new Rectangle();
        chopperBody.setWidth(chopperBodyWidth);
        chopperBody.setHeight(chopperBodyHeight);
        chopperBody.setFill(Color.LEMONCHIFFON);
        chopperBody.setTranslateX(438);
        chopperBody.setTranslateY(850);

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

class Helipad extends Pane{
    private static final int helipadWidth = 200;
    private static final int helipadRadius = 80;
    private static final int helipadStartX = 350;
    private static final int helipadStartY = 775;
    public Helipad(){
        Rectangle helipadRect = new Rectangle();
        helipadRect.setWidth(helipadWidth);
        helipadRect.setHeight(helipadWidth);
        helipadRect.setOpacity(100);
        helipadRect.setStroke(Color.WHITE);
        helipadRect.setTranslateX(helipadStartX);
        helipadRect.setTranslateY(helipadStartY);

        Ellipse helipadCircle = new Ellipse();
        helipadCircle.setRadiusX(helipadRadius);
        helipadCircle.setRadiusY(helipadRadius);
        helipadCircle.setOpacity(100);
        helipadCircle.setStroke(Color.WHITE);
        helipadCircle.setTranslateX(helipadRect.getTranslateX() + 100);
        helipadCircle.setTranslateY(helipadRect.getTranslateY()
                                    + helipadRadius + 20);

        this.getChildren().addAll(helipadRect, helipadCircle);
    }
}

class Cloud{

}

class Pond{

}


class Game extends Pane{
    private final static double APP_WIDTH = 900;
    private final static double APP_HEIGHT = 1000;
    Helicopter choppah = new Helicopter();
    Helipad helipad = new Helipad();

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
    public Game(){
        this.getChildren().addAll(helipad, choppah);
    }
}


public class GameApp extends Application {
    Pane root = new Pane();
    Point2D rainmakerApp = new Point2D(900, 1000);
    Game rainmaker = new Game();
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root, rainmakerApp.getX(),
                                rainmakerApp.getY(), Color.BLACK);
        root.getChildren().add(rainmaker);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Rainmaker");
        primaryStage.show();
    }

    public static void main(String[] args){
        Application.launch(args);
    }


}
