package com.example.duanshopdienthoai.Customer;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.LoggedInUser;
import com.example.duanshopdienthoai.Main;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartController {
    @FXML
    private TableView<CartItem> cartTable;
    @FXML
    private TableColumn<CartItem,Integer> idColumn;
    @FXML
    private  TableColumn<CartItem, Boolean> checkBoxColumn;
    @FXML
    private TableColumn<CartItem, String> nameColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> priceColumn;
    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> amountColumn;
    @FXML
    private TableColumn<CartItem, Void> actionColumn;
    @FXML
    private Label totalLabel;
    @FXML
    public void initialize() throws SQLException {
        showCart();
        cartTable.setEditable(true);
    }

    public void showCart() throws SQLException {
        String query = "SELECT p.productName, p.price, c.quantity, c.cartID " +
                "FROM products p JOIN cart c ON p.productId = c.productId WHERE c.userID = ?";
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, LoggedInUser.getInstance().getUserID());

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String productName = resultSet.getString("productName");
                BigDecimal price = resultSet.getBigDecimal("price");
                int quantity = resultSet.getInt("quantity");
                int cartID = resultSet.getInt("cartID");

                CartItem cartItem = new CartItem(productName, price, quantity, false,cartID);
                cartTable.getItems().add(cartItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTableColumn();
    }
    private void setTableColumn() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cartTable.getItems().indexOf(cellData.getValue()) + 1).asObject());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        actionColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(null));
        actionColumn.setCellFactory(col -> new TableCell<CartItem, Void>() {
            private final Button buttonDelete = new Button("Xóa khỏi giỏ hàng");

            {
                buttonDelete.setOnAction(e -> {
                    CartItem cartItem = getTableView().getItems().get(getIndex());
                    if(showConfirmation("Bạn có muốn xóa sản phẩm khỏi giỏ hàng ?")) {
                        try {
                            handleDelete(cartItem.getCartID());
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    cartTable.refresh();
                });
                HBox buttonBox = new HBox(buttonDelete);
                buttonBox.setPadding(new Insets(10, 10, 10, 10));
                setGraphic(buttonBox);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(buttonDelete));
                }
            }
        });
        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkboxProperty());
        checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));
        checkBoxColumn.setEditable(true);

        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkboxProperty());
        checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));

        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkboxProperty());
        checkBoxColumn.setCellFactory(column -> new CheckBoxTableCell<CartItem, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    CartItem cartItem = getTableRow().getItem();
                    if (cartItem != null) {
                        cartItem.setCheckbox(checkBox.isSelected());
                        System.out.println("Checkbox đã được cập nhật cho sản phẩm: " + cartItem.getName());
                        updateTotal();
                        try {
                            updateCheckBox(cartItem.getCartID(), checkBox.isSelected());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });


        quantityColumn.setCellFactory(col -> new TableCell<CartItem, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>();
            {spinner.setEditable(true);}
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CartItem cartItem = getTableView().getItems().get(getIndex());
                    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, cartItem.getQuantity());
                    spinner.setValueFactory(valueFactory);

                    spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                        cartItem.setQuantity(newValue);
                        try {
                            updateQuantityCart(cartItem.getCartID(), newValue);
                            updateTotal();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    setGraphic(spinner);
                }
            }
        });

        amountColumn.setCellValueFactory(cellData -> {
            CartItem cartItem = cellData.getValue();
            BigDecimal amount = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            return new ReadOnlyObjectWrapper<>(amount);
        });
    }

    private void handleDelete(int cartID) throws SQLException {
        String query = "DELETE FROM cart WHERE cartID = ?";
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, cartID);
        ps.executeUpdate();
        ps.close();
        cartTable.getItems().removeIf(cartItem -> cartItem.getCartID() == cartID);
        updateTotal();
        System.out.println("Xóa thành công " + cartID);
    }


    public void updateQuantityCart(int cartID, int quantity) throws SQLException {
        String query = "UPDATE cart SET quantity=? WHERE cartID=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, cartID);
            preparedStatement.executeUpdate();
            cartTable.refresh();
            System.out.println("Cập nhật số lượng thành công" + cartID);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTotal() {
        BigDecimal total = cartTable.getItems().stream()
                .filter(CartItem::isCheckbox)
                .map(cartItem -> cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalLabel.setText(total.toString());
    }

    public void updateCheckBox(int cartID, boolean isChecked) throws SQLException {
        String query = "UPDATE cart SET checkbox = ? WHERE cartID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setBoolean(1, isChecked);
            preparedStatement.setInt(2, cartID);
            preparedStatement.executeUpdate();
            cartTable.refresh();
            System.out.println("Cập nhật checkbox thành công cho cartID: " + cartID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public void goBack(ActionEvent event) throws IOException {
        Main.changeScene("HomeCustomer.fxml");
    }

    public void handlePayment(ActionEvent event) {
    }
    private boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get() == ButtonType.OK;
    }
}
