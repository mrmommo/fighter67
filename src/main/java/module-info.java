open module com.nhom67.platformfighter {
    requires com.almasb.fxgl.all;

    // Yêu cầu (requires) các module của JavaFX phòng trường hợp bạn dùng trực tiếp
    // UI của JavaFX
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.fxml;
    requires com.almasb.fxgl.entity;
}
