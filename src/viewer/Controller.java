package viewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    // View.
    @FXML
    private Canvas canvas;
    @FXML
    private Slider rightSlider;
    @FXML
    private Slider bottomSlider;
    @FXML
    private CheckBox checkBoxShowCordRender;
    @FXML
    private CheckBox checkBoxUseCentralProjection;
    @FXML
    private CheckBox checkBoxDisplayTextures;
    @FXML
    private CheckBox checkBoxTurnOnShading;
    @FXML
    private CheckBox checkBoxTurnOnLight;
    @FXML
    private Slider zoomSlider;
    @FXML
    private AnchorPane anchorPaneSourceOfLight;
    @FXML
    private TextField textFieldSourceOfLightX;
    @FXML
    private TextField textFieldSourceOfLightY;
    @FXML
    private TextField textFieldSourceOfLightZ;
    @FXML
    private TextField textFieldPointOfEyeX;
    @FXML
    private TextField textFieldPointOfEyeY;
    @FXML
    private TextField textFieldPointOfEyeZ;
    @FXML
    private TextField textFieldPointOfAimX;
    @FXML
    private TextField textFieldPointOfAimY;
    @FXML
    private TextField textFieldPointOfAimZ;
    @FXML
    private Button buttonLoadModel;
    @FXML
    private Button buttonLoadTexture;
    // Model.
    private GraphicsContext graphicsContext;
    private ObjHandler objHandler;
    // Parameters.
    private boolean cordRender = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rightSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateScene();
            }
        });
        bottomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateScene();
            }
        });
        checkBoxShowCordRender.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                cordRender = newValue;
                checkBoxDisplayTextures.setDisable(newValue);
                checkBoxTurnOnShading.setDisable(newValue);
                checkBoxTurnOnLight.setDisable(newValue);
                anchorPaneSourceOfLight.setDisable(newValue);
                updateScene();
            }
        });
        checkBoxUseCentralProjection.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                objHandler.setUseCentralProjection(newValue);
                updateScene();
            }
        });
        checkBoxDisplayTextures.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                objHandler.setUseTexture(newValue);
                updateScene();
            }
        });
        checkBoxTurnOnShading.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                objHandler.setUseShading(newValue);
                updateScene();
            }
        });
        checkBoxTurnOnLight.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                checkBoxTurnOnShading.setDisable(!newValue);
                anchorPaneSourceOfLight.setDisable(!newValue);
                objHandler.setUseLight(newValue);
                updateScene();
            }
        });
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                objHandler.setZoom((float) zoomSlider.getValue());
                updateScene();
            }
        });
        ChangeListener<String> changeSourceOfLight = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    objHandler.sourceOfLight.setX(Float.parseFloat(textFieldSourceOfLightX.getText()));
                    objHandler.sourceOfLight.setY(Float.parseFloat(textFieldSourceOfLightY.getText()));
                    objHandler.sourceOfLight.setZ(Float.parseFloat(textFieldSourceOfLightZ.getText()));
                    updateScene();
                }
                catch (NumberFormatException e) {
                }
            }
        };
        textFieldSourceOfLightX.textProperty().addListener(changeSourceOfLight);
        textFieldSourceOfLightY.textProperty().addListener(changeSourceOfLight);
        textFieldSourceOfLightZ.textProperty().addListener(changeSourceOfLight);
        ChangeListener<String> changePointOfEye = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    objHandler.eye.setX(Float.parseFloat(textFieldPointOfEyeX.getText()));
                    objHandler.eye.setY(Float.parseFloat(textFieldPointOfEyeY.getText()));
                    objHandler.eye.setZ(Float.parseFloat(textFieldPointOfEyeZ.getText()));
                    updateScene();
                }
                catch (NumberFormatException e) {
                }
            }
        };
        textFieldPointOfEyeX.textProperty().addListener(changePointOfEye);
        textFieldPointOfEyeY.textProperty().addListener(changePointOfEye);
        textFieldPointOfEyeZ.textProperty().addListener(changePointOfEye);
        ChangeListener<String> changePointOfAim = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    objHandler.centerOfModel.setX(Float.parseFloat(textFieldPointOfAimX.getText()));
                    objHandler.centerOfModel.setY(Float.parseFloat(textFieldPointOfAimY.getText()));
                    objHandler.centerOfModel.setZ(Float.parseFloat(textFieldPointOfAimZ.getText()));
                    updateScene();
                }
                catch (NumberFormatException e) {
                }
            }
        };
        textFieldPointOfAimX.textProperty().addListener(changePointOfAim);
        textFieldPointOfAimY.textProperty().addListener(changePointOfAim);
        textFieldPointOfAimZ.textProperty().addListener(changePointOfAim);
        buttonLoadModel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Load model");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Wavefront (.obj)", "*.obj"));
                fileChooser.setInitialDirectory(new File("."));
                File file = fileChooser.showOpenDialog(buttonLoadModel.getScene().getWindow());
                if (file != null) {
                    ObjModel objModel = new ObjParser(file.getAbsolutePath()).getObjModel();
                    objHandler = new ObjHandler((float)canvas.getWidth(), (float)canvas.getHeight(), objModel);
                    checkParameters();
                    updateScene();
                }
            }
        });
        buttonLoadTexture.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Load texture");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Portable network graphics (.png)", "*.png"));
                fileChooser.setInitialDirectory(new File("."));
                File file = fileChooser.showOpenDialog(buttonLoadTexture.getScene().getWindow());
                if (file != null) {
                    try {
                        objHandler.getObjModel().setTextureMap(file.getAbsolutePath());
                    }
                    catch (IOException e) {
                        System.err.println("Error occured while texture file loading!");
                    }
                    checkParameters();
                    updateScene();
                }
            }
        });
        graphicsContext = canvas.getGraphicsContext2D();
        ObjModel objModel = new ObjParser("drums.obj").getObjModel();
        objHandler = new ObjHandler((float)canvas.getWidth(), (float)canvas.getHeight(), objModel);
        checkParameters();
        updateScene();
    }

    private void checkParameters() {
        zoomSlider.setValue(objHandler.getZoom());
        textFieldSourceOfLightX.setText(String.valueOf(objHandler.sourceOfLight.getX()));
        textFieldSourceOfLightY.setText(String.valueOf(objHandler.sourceOfLight.getY()));
        textFieldSourceOfLightZ.setText(String.valueOf(objHandler.sourceOfLight.getZ()));
        textFieldPointOfEyeX.setText(String.valueOf(objHandler.eye.getX()));
        textFieldPointOfEyeY.setText(String.valueOf(objHandler.eye.getY()));
        textFieldPointOfEyeZ.setText(String.valueOf(objHandler.eye.getZ()));
        textFieldPointOfAimX.setText(String.valueOf(objHandler.centerOfModel.getX()));
        textFieldPointOfAimY.setText(String.valueOf(objHandler.centerOfModel.getY()));
        textFieldPointOfAimZ.setText(String.valueOf(objHandler.centerOfModel.getZ()));
    }

    private void updateScene() {
        if (cordRender) {
            objHandler.cordRender(bottomSlider.getValue(), rightSlider.getValue());
        }
        else {
            objHandler.render(bottomSlider.getValue(), rightSlider.getValue());
        }
        graphicsContext.drawImage(objHandler.getImage(), 0, 0);
    }
}