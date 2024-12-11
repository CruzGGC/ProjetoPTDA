module com.grupo6.projetoptda {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;
    requires com.google.gson;
    requires java.sql;


    opens com.grupo6.projetoptda to javafx.fxml;
    opens com.grupo6.projetoptda.Controller to javafx.fxml;
    opens com.grupo6.projetoptda.Getter to javafx.fxml;
    exports com.grupo6.projetoptda;
    exports com.grupo6.projetoptda.Controller;
    exports com.grupo6.projetoptda.Getter;
}