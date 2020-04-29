/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafx_settingbar;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author MASOUD
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private ImageView btn_setting, btn_user, btn_exit, btn_sorti;

    @FXML
    private AnchorPane h_user, h_setting, topbar;

    @FXML
    void handleButtonAction(MouseEvent event) {
        if(event.getTarget() == btn_setting){
            h_setting.setVisible(true);
            h_user.setVisible(false);
        }else
            if(event.getTarget() == btn_user)
            {
                h_user.setVisible(true);
                h_setting.setVisible(false);
            }else
            if(event.getTarget() == btn_exit)
            {
                h_user.setVisible(false);
                h_setting.setVisible(false);
            }else
            if(event.getTarget() == btn_sorti)
            {
                Platform.exit();
            }

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
