package org.example.GESTORIVANTORRANO;

import PaqueteTrabajador.Trabajador;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InsertarController {
    @FXML
    private TextField txtFieldNombre;
    @FXML
    private ComboBox<String> cmbBoxPuesto;
    @FXML
    private TextField txtFieldSalario;
    @FXML
    private ListView lstVwNombres;
    @FXML
    private Label lblTrabajador;


    @FXML
    protected void insertar() {
        if(txtFieldNombre.getText().isEmpty() || txtFieldSalario.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Campos incompletos");
            alert.showAndWait();
        }
        else if(txtFieldSalario.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Campos incompletos");
            alert.showAndWait();
        }
        else{
            String nombre = txtFieldNombre.getText();
            String puesto = cmbBoxPuesto.getValue();
            Integer salario = Integer.valueOf(txtFieldSalario.getText());
            Trabajador trabajador = new Trabajador(nombre, puesto, salario);
            trabajador.insertarTrabajador(trabajador);
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("HECHO");
            alerta.setHeaderText("Mensaje");
            alerta.setContentText("Empleado " + nombre + " introducido en la base de datos satisfactoriamente.");
            alerta.showAndWait();
            verTrabajadores();
        }

    }

    @FXML
    protected void meterDatos() {
        Scanner sc = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            sc = new Scanner(new File(classLoader.getResource("trabajadores.txt").getFile()));
            while(sc.hasNextLine()){
                String linea = sc.nextLine();
                Trabajador trabajador = parsearLinea(linea);
                trabajador.insertarTrabajador(trabajador);
            }
            verTrabajadores();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally{
            if(sc != null){
                sc.close();
            }
        }
    }

    @FXML
    protected static Trabajador parsearLinea(String linea) {
        String[] tokens = linea.split(";");
        int sueldo = Integer.parseInt(tokens[2]);
        Trabajador trabajador = new Trabajador(tokens[0], tokens[1], sueldo);
        return trabajador;
    }

    @FXML
    protected void verTrabajadores(){
        String url = "jdbc:mysql://localhost:3306/GestorEmpleados";
        String user = "root";
        String pass = "root";

        Connection conexion = null;

        try {
            conexion = DriverManager.getConnection(url, user, pass);
            PreparedStatement pst = conexion.prepareStatement("SELECT NOMBRE FROM TRABAJADOR");
            ResultSet rs = pst.executeQuery();
            List<String> lista = new ArrayList<>();
            while(rs.next()){
                lista.add(rs.getString("NOMBRE"));
            }
            ObservableList<String> nombres = FXCollections.observableArrayList(lista);
            lstVwNombres.setItems(nombres);
            verDetalles();
            conexion.close();
        }
        catch(SQLException e){
            throw new IllegalStateException("Error al conectar la BD");
        }
    }

    @FXML
    protected void verDetalles(){
        String url = "jdbc:mysql://localhost:3306/GestorEmpleados";
        String user = "root";
        String pass = "root";

        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(url, user, pass);

            String nom = (String) lstVwNombres.getSelectionModel().getSelectedItem();
            PreparedStatement pst = conexion.prepareStatement("SELECT * FROM TRABAJADOR WHERE NOMBRE = '"+nom+"'" );
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                lblTrabajador.setText(rs.getString("ID") + "\n\n" +
                                            rs.getString("NOMBRE") + "\n\n" +
                                            rs.getString("PUESTO") + "\n\n" +
                                            rs.getString("SALARIO") + "\n\n" +
                                            rs.getString("FECHA"));
            }
            conexion.close();
        }
        catch(SQLException e){
            throw new IllegalStateException("Error al conectar la BD");
        }
    }

    @FXML
    protected void onEliminar(){
        String url = "jdbc:mysql://localhost:3306/GestorEmpleados";
        String user = "root";
        String pass = "root";

        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(url, user, pass);

            String nom = (String) lstVwNombres.getSelectionModel().getSelectedItem();
            PreparedStatement pst = conexion.prepareStatement("SET sql_safe_updates=0;");
            pst.executeUpdate();
            PreparedStatement pst1 = conexion.prepareStatement("DELETE FROM TRABAJADOR WHERE NOMBRE = ?");
            pst1.setString(1, nom);
            pst1.executeUpdate();
            PreparedStatement pst2 = conexion.prepareStatement("SET sql_safe_updates=1;");
            pst2.executeUpdate();
            conexion.close();
        }
        catch(SQLException e){
            throw new IllegalStateException("Error al eliminar un trabajador");
        }
    }

    @FXML
    protected void onEditar() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("edicion-view.fxml"));
        fxmlLoader.load();
        String nom = (String) lstVwNombres.getSelectionModel().getSelectedItem();
        EdicionController document = fxmlLoader.getController();
        document.mostrar(nom);
        Parent p = fxmlLoader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(p));
        stage.setTitle("Modificar empleado");
        stage.show();

    }
}