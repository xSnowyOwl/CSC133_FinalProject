import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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

class HelicopterBody extends GameObject{
    private static final double chopperBodyWidth = 30;
    private static final double chopperBodyHeight = 50;
    public HelicopterBody(){
        super();
        Rectangle chopperBody = new Rectangle();
        chopperBody.setWidth(chopperBodyWidth);
        chopperBody.setHeight(chopperBodyHeight);
        chopperBody.setFill(Color.LEMONCHIFFON);
    }
}

class HelicopterTail extends GameObject{
    private static final double chopperTailWidth = 10;
    private static final double chopperTailHeight = 60;
    public HelicopterTail(){
        Rectangle chopperTail = new Rectangle();
        chopperTail.setWidth(chopperTailWidth);
        chopperTail.setHeight(chopperTailHeight);
        chopperTail.setFill(Color.LEMONCHIFFON);
    }
}

class Helicopter extends GameObject{
    private static final double chopperBodyWidth = 30;
    private static final double chopperBodyHeight = 50;
    private static final double chopperTailWidth = 10;
    private static final double chopperTailHeight = 60;
    private double speed = 0;
    private double velocityX = 0;
    private double velocityY = 0;

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
    public void accelerate(){
        if(speed <= 3){
            speed += .5;
        }
    }
    public void decelerate(){
        if(speed >= -3){
            speed -= .5;
        }
    }
    public void clockwiseTurn(){

    }
    public void counterClockwiseTurn(){

    }
    public double getVelocityX(double speed){
        velocityX = speed * cos(Math.toDegrees(getRotate()));
        return velocityX;
    }
    public double getVelocityY(){
        velocityY = speed * sin(Math.toDegrees(getRotate()));
        return velocityY;
    }

    public void update(double velocityX, double velocityY, double rotate){

    }

}

class Helipad extends Pane{
    private static final int helipadWidth = 200;
    private static final int helipadHeight = 200;
    private static final int helipadRadius = 80;
    private static final int helipadStartX = 350;
    private static final int helipadStartY = 775;
    public Helipad(){
        Rectangle helipadRect = new Rectangle(helipadWidth, helipadHeight);
        helipadRect.setOpacity(100);
        helipadRect.setStroke(Color.GRAY);
        helipadRect.setTranslateX(helipadStartX);
        helipadRect.setTranslateY(helipadStartY);

        Ellipse helipadCircle = new Ellipse(helipadRadius, helipadRadius);
        helipadCircle.setOpacity(100);
        helipadCircle.setStroke(Color.GRAY);
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
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.UP) {
                    rainmaker.choppah.accelerate();
                }
                if(event.getCode() == KeyCode.DOWN){
                    rainmaker.choppah.decelerate();
                }
                if(event.getCode() == KeyCode.LEFT){

                }
                if(event.getCode() == KeyCode.RIGHT){

                }
            }

        });

        rainmaker.game.start();
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Rainmaker");
        primaryStage.show();
    }

    public static void main(String[] args){
        Application.launch(args);
    }


}
