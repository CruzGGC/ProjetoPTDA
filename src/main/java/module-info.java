module com.grupo6.projetoptda {
    requires javafx.fxml;
    requires jbcrypt;
    requires javafx.controls;
    requires com.google.gson;
    requires java.sql;

    exports com.grupo6.projetoptda;
    exports com.grupo6.projetoptda.Controller;
    exports com.grupo6.projetoptda.Getter;
    exports com.grupo6.projetoptda.Utilidades;

    opens com.grupo6.projetoptda to javafx.fxml;
    opens com.grupo6.projetoptda.Controller to javafx.fxml;
    opens com.grupo6.projetoptda.Getter to javafx.fxml;
    opens com.grupo6.projetoptda.Utilidades to javafx.fxml;
}