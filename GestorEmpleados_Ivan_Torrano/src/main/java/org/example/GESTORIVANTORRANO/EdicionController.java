package org.example.GESTORIVANTORRANO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.*;
import java.util.Objects;

public class EdicionController {

    @FXML
    private Label lblIdTrabajador;
    @FXML
    private TextField txtFldNombre;
    @FXML
    private TextField txtFldPuesto;
    @FXML
    private TextField txtFldSal;
    private String nombre1;

    protected void mostrar(String nom){
        String url = "jdbc:mysql://localhost:3306/GestorEmpleados";
        String user = "root";
        String pass = "root";

        nombre1 = nom;

        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(url, user, pass);
            PreparedStatement pst = conexion.prepareStatement("select * from TRABAJADOR where nombre = ?");
            pst.setString(1, nom);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                lblIdTrabajador.setText(rs.getString("ID"));
                txtFldNombre.setText(rs.getString("Nombre"));
                txtFldPuesto.setText(rs.getString("Puesto"));
                txtFldSal.setText(rs.getString("Salario"));
            }
            conexion.close();
        }
        catch(SQLException e){
            throw new IllegalStateException("Error al conectar con la BD");
        }
    }
    @FXML
    protected void onMod(){
        String url = "jdbc:mysql://localhost:3306/GestorEmpleados";
        String user = "root";
        String pass = "root";

        if(!Objects.equals(txtFldPuesto.getText(), "Scada Manager") && !Objects.equals(txtFldPuesto.getText(), "Sales Manager") && !Objects.equals(txtFldPuesto.getText(), "Product Owner") && !Objects.equals(txtFldPuesto.getText(), "Product Manager") && !Objects.equals(txtFldPuesto.getText(), "Analyst Programmer") && !Objects.equals(txtFldPuesto.getText(), "Junior Programmer")){
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setHeaderText("Introduzca un valor valido en puesto:");
            alerta.setContentText("Scada Manager, Sales Manager, Product Owner, Product Manager, Analyst Programmer, o Junior Programmer");
            alerta.showAndWait();
        }
        else{
            Connection conexion = null;
            try {
                conexion = DriverManager.getConnection(url, user, pass);

                PreparedStatement st = conexion.prepareStatement("SELECT ID FROM TRABAJADOR WHERE NOMBRE = ?");
                st.setString(1, nombre1);
                ResultSet rs = st.executeQuery();
                int id = 0;
                while(rs.next()){
                    id = rs.getInt("ID");
                }

                PreparedStatement pst = conexion.prepareStatement("SET sql_safe_updates=0;");
                pst.executeUpdate();

                PreparedStatement pst1 = conexion.prepareStatement("UPDATE TRABAJADOR SET NOMBRE = ?, PUESTO = ?, SALARIO = ? WHERE ID = ?");
                pst1.setString(1, txtFldNombre.getText());
                pst1.setString(2, txtFldPuesto.getText());
                pst1.setInt(3, Integer.parseInt(txtFldSal.getText()));
                pst1.setInt(4, id);
                pst1.executeUpdate();

                PreparedStatement pst2 = conexion.prepareStatement("SET sql_safe_updates=1;");
                pst2.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Trabajador actualizado");
                alert.showAndWait();

            }
            catch(SQLException e){
                throw new IllegalStateException("Error al modificar trabajador");
            }
        }

    }

    @FXML
    protected void onSalir(ActionEvent event){
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void onModificar(ActionEvent actionEvent) {
    }

    public void onCancelar(ActionEvent actionEvent) {
    }
}
