module com.company.applianceManager.appliances {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;

    opens com.company.applianceManager to javafx.fxml, javafx.base;
    exports com.company.applianceManager;

    opens com.company.applianceManager.Appliances to javafx.fxml, javafx.base;
    exports com.company.applianceManager.Appliances;

    opens com.company.applianceManager.Conditional to javafx.fxml, javafx.base;
    exports com.company.applianceManager.Conditional;
}