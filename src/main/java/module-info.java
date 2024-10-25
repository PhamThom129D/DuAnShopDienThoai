module com.example.duanshopdienthoai {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.duanshopdienthoai to javafx.fxml;
    exports com.example.duanshopdienthoai;
    exports com.example.duanshopdienthoai.Login;
    opens com.example.duanshopdienthoai.Login to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer.Product;
    opens com.example.duanshopdienthoai.Customer.Product to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer.Cart;
    opens com.example.duanshopdienthoai.Customer.Cart to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer.Account;
    opens com.example.duanshopdienthoai.Customer.Account to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer.Order;
    opens com.example.duanshopdienthoai.Customer.Order to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer.Invoice;
    opens com.example.duanshopdienthoai.Customer.Invoice to javafx.fxml;
    exports com.example.duanshopdienthoai.Admin to javafx.fxml;
    opens com.example.duanshopdienthoai.Admin to javafx.fxml;

}