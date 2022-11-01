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
    protected Translate myTranslation;
    protected Rotate myRotation;
    protected Scale myScale;

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
        add(chopperBody);
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
        add(chopperTail);
    }
}

class HelicopterBlade extends GameObject{
    private static final double chopperBladeWidth = 2;
    private static final double chopperBladeLength = 60;
    public HelicopterBlade(){
        Rectangle chopperBlade = new Rectangle(chopperBladeWidth,
                                                chopperBladeLength);
        chopperBlade.setFill(Color.BLACK);
        add(chopperBlade);
    }
}

class Helicopter extends GameObject{
    private double speed = 0;
    private double velocityX = 0;
    private double velocityY = 0;

    public Helicopter(){
        HelicopterBody body = new HelicopterBody();
        add(makeTail(10, -59, 0));
        add(body);

        add(makeBlade(14, 25, 1, 0));
        add(makeBlade(14, 25, 1, 90));
        add(makeBlade(14, 25, 1, 180));
        add(makeBlade(14, 25, 1, 270));

        add(makeBlade(14, -60, .25, 0));
        add(makeBlade(14, -60, .25, 120));
        add(makeBlade(14, -60, .25, 240));
    }
    private HelicopterTail makeTail(double tx, double ty, int degrees){
        HelicopterTail tail = new HelicopterTail();
        tail.rotate(degrees);
        tail.translate(tx, ty);
        return tail;
    }
    private HelicopterBlade makeBlade(double tx, double ty, double sy,
                                      int degrees){
        HelicopterBlade blade = new HelicopterBlade();
        blade.rotate(degrees);
        blade.scale(1, sy);
        blade.translate(tx, ty);
        return blade;
    }
    public void accelerate(){
        if(speed < 2){
            speed += .2;
        }
    }
    public void decelerate(){
        if(speed > -2){
            speed -= .2;
        }
    }
    public void setPivot(){
        this.myRotation.setPivotX(this.myTranslation.getX() + 15);
        this.myRotation.setPivotY(this.myTranslation.getY() + 25);
    }
    public void clockwiseTurn(){
        this.rotate(this.getMyRotation() - 3);
    }
    public void counterClockwiseTurn(){
        this.rotate(this.getMyRotation() + 3);
    }
    public double getVelocityX(){
        velocityX = speed * cos(Math.toRadians(90 + getMyRotation()));
        return velocityX;
    }
    public double getVelocityY(){
        velocityY = speed * sin(Math.toRadians(90 - getMyRotation()));
        return velocityY;
    }

    public void update(double velocityX, double velocityY){
        this.setTranslateX(this.getTranslateX() + velocityX);
        this.setTranslateY(this.getTranslateY() + velocityY);
        this.setPivot();
    }

}

class Helipad extends Pane{
    private static final int helipadWidth = 200;
    private static final int helipadHeight = 200;
    private static final int helipadRadius = 80;
    private static final int helipadStartX = -85;
    private static final int helipadStartY = -90;
    public Helipad(){
        Rectangle helipadRect = new Rectangle(helipadWidth, helipadHeight);
        helipadRect.setFill(Color.SANDYBROWN);
        helipadRect.setStroke(Color.BLACK);
        helipadRect.setStrokeWidth(2);
        helipadRect.setTranslateX(helipadStartX);
        helipadRect.setTranslateY(helipadStartY);

        Ellipse helipadCircle = new Ellipse(helipadRadius, helipadRadius);
        helipadCircle.setFill(Color.SADDLEBROWN);
        helipadCircle.setStroke(Color.WHITE);
        helipadCircle.setStrokeWidth(2);
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

            choppah.update(choppah.getVelocityX(), choppah.getVelocityY());
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
                                rainmakerApp.getY(), Color.SANDYBROWN);
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
                    rainmaker.choppah.counterClockwiseTurn();
                }
                if(event.getCode() == KeyCode.RIGHT){
                    rainmaker.choppah.clockwiseTurn();
                }
            }

        });

        root.setScaleY(-1);
        root.setTranslateX(438);
        root.setTranslateY(-100);
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
