module com.grupo6.projetoptda {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;


    opens com.grupo6.projetoptda to javafx.fxml;
    exports com.grupo6.projetoptda;
}