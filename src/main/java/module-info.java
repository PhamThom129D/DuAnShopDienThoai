module com.example.duanshopdienthoai {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens com.example.duanshopdienthoai to javafx.fxml;
    exports com.example.duanshopdienthoai;
    exports com.example.duanshopdienthoai.Login;
    opens com.example.duanshopdienthoai.Login to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer.Additional;
    opens com.example.duanshopdienthoai.Customer.Additional to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer.Cart;
    opens com.example.duanshopdienthoai.Customer.Cart to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer.Account;
    opens com.example.duanshopdienthoai.Customer.Account to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer.Order;
    opens com.example.duanshopdienthoai.Customer.Order to javafx.fxml;
    exports com.example.duanshopdienthoai.Customer.Invoice;
    opens com.example.duanshopdienthoai.Customer.Invoice to javafx.fxml;

    exports com.example.duanshopdienthoai.Admin;
    opens com.example.duanshopdienthoai.Admin to javafx.fxml, javafx.base;
    exports com.example.duanshopdienthoai.Admin.Customer;
    opens com.example.duanshopdienthoai.Admin.Customer to javafx.base, javafx.fxml;
    exports com.example.duanshopdienthoai.Admin.Products;
    opens com.example.duanshopdienthoai.Admin.Products to javafx.base, javafx.fxml;
    exports com.example.duanshopdienthoai.Customer;
    opens com.example.duanshopdienthoai.Customer to javafx.fxml;
    exports com.example.duanshopdienthoai.Admin.Invoices;
    opens com.example.duanshopdienthoai.Admin.Invoices to javafx.base, javafx.fxml;
    exports com.example.duanshopdienthoai.Admin.Statistics;
    opens com.example.duanshopdienthoai.Admin.Statistics to javafx.base, javafx.fxml;

}
