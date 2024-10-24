module com.example.duanshopdienthoai {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.duanshopdienthoai to javafx.fxml;
    exports com.example.duanshopdienthoai;
    exports com.example.duanshopdienthoai.Login;
    opens com.example.duanshopdienthoai.Login to javafx.fxml;
    opens com.example.duanshopdienthoai.Customer to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer;
//    exports com.example.duanshopdienthoai.Customer.Cart;
//    opens com.example.duanshopdienthoai.Customer.Cart to javafx.fxml;
}