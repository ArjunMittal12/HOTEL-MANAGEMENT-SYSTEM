module com.oswin.miniproject {
    requires transitive javafx.controls;
    requires javafx.graphics;

    exports com.oswin.miniproject;
    exports com.oswin.miniproject.service;
    exports com.oswin.miniproject.models;
    exports com.oswin.miniproject.controller;
    exports com.oswin.miniproject.util;
}
