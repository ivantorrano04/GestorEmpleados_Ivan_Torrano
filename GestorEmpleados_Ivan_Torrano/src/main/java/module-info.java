module org.example.gestorempleadosolgaalfaro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jdi;
    requires jdk.jconsole;


    opens org.example.GESTORIVANTORRANO to javafx.fxml;
    exports org.example.GESTORIVANTORRANO;
}