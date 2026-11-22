package models;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.*;

import java.io.File;
import java.time.LocalDate;

/**
 * Main JavaFX Application for Campsite Management System.
 * Connects the JavaFX front-end to the custom backend models without
 * storing actual system data inside GUI widgets directly.
 */
public class App extends Application {

    private CampsiteManager manager;
    private TreeView<String> drillDownTree;
    private TextArea displayArea;

    @Override
    public void start(Stage primaryStage) {
        manager = new CampsiteManager();
        loadDemoData(); // Pre-populate sample data so the app isn't empty upon start

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top Toolbar: Save, Load, and Reset facilities
        ToolBar toolBar = createToolBar(primaryStage);
        root.setTop(toolBar);

        // Center Content: TabPane organizing core CA requirements
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createDrillDownTab(),
                createBookingAndManagementTab(),
                createReportsAndGuestTab(),
                createSmartFindTab()
        );
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 950, 650);
        primaryStage.setTitle("Campsite Management System - Data Structures CA1");
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshTree();
    }

    // =========================================================================
    // Toolbar: Save / Load / Reset
    // =========================================================================

    private ToolBar createToolBar(Stage stage) {
        Button btnSave = new Button("Save System");
        btnSave.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Campsite Data");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    manager.saveToFile(file.getAbsolutePath());
                    showAlert(Alert.AlertType.INFORMATION, "Success", "System state saved successfully!");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to save: " + ex.getMessage());
                }
            }
        });

        Button btnLoad = new Button("Load System");
        btnLoad.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Campsite Data");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    manager = CampsiteManager.loadFromFile(file.getAbsolutePath());
                    refreshTree();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "System data loaded successfully!");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load: " + ex.getMessage());
                }
            }
        });

        Button btnReset = new Button("Reset System");
        btnReset.setStyle("-fx-text-fill: red;");
        btnReset.setOnAction(e -> {
            manager.reset();
            refreshTree();
            displayArea.setText("All system data has been cleared.");
            showAlert(Alert.AlertType.WARNING, "System Reset", "All campsites and guests cleared.");
        });

        return new ToolBar(btnSave, btnLoad, new Separator(), btnReset);
    }

    // =========================================================================
    // Tab 1: Interactive Drill-Down (TreeView)
    // =========================================================================

    private Tab createDrillDownTab() {
        Tab tab = new Tab("Interactive Drill-Down");
        tab.setClosable(false);

        SplitPane splitPane = new SplitPane();
        drillDownTree = new TreeView<>();
        drillDownTree.setShowRoot(false);

        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPromptText("Select an entity on the left to inspect its details...");

        drillDownTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayArea.setText(newVal.getValue());
            }
        });

        splitPane.getItems().addAll(drillDownTree, displayArea);
        splitPane.setDividerPositions(0.4);

        tab.setContent(splitPane);
        return tab;
    }

    /**
     * Rebuilds the TreeView from custom MyList structures.
     * Demonstrates interactive drill-down: Campsite -> Area -> Plot -> Bookings.
     */
    private void refreshTree() {
        TreeItem<String> rootItem = new TreeItem<>("Root");

        MyList<Campsite> siteList = manager.getCampsites();
        for (int i = 0; i < siteList.size(); i++) {
            Campsite site = siteList.get(i);
            TreeItem<String> siteItem = new TreeItem<>(site.getName() + " (" + site.getStars() + " Stars)");

            MyList<CampingArea> areaList = site.getAreas();
            for (int j = 0; j < areaList.size(); j++) {
                CampingArea area = areaList.get(j);
                TreeItem<String> areaItem = new TreeItem<>(area.getAreaId() + ": " + area.getDescription());

                MyList<Plot> plotList = area.getPlots();
                for (int k = 0; k < plotList.size(); k++) {
                    Plot plot = plotList.get(k);
                    TreeItem<String> plotItem = new TreeItem<>(plot.toString());

                    MyList<Booking> bookingList = plot.getBookings();
                    for (int b = 0; b < bookingList.size(); b++) {
                        Booking booking = bookingList.get(b);
                        plotItem.getChildren().add(new TreeItem<>(booking.toString()));
                    }

                    areaItem.getChildren().add(plotItem);
                }
                siteItem.getChildren().add(areaItem);
            }
            rootItem.getChildren().add(siteItem);
        }

        drillDownTree.setRoot(rootItem);
    }

    // =========================================================================
    // Tab 2: Create & Add Facilities (Campsite, Area, Plot, Booking)
    // =========================================================================

private Tab createBookingAndManagementTab() {
        Tab tab = new Tab("Add & Book");
        tab.setClosable(false);

        Accordion accordion = new Accordion();

        // --- 1. Add Campsite ---
        GridPane siteGrid = new GridPane();
        siteGrid.setHgap(10); siteGrid.setVgap(8); siteGrid.setPadding(new Insets(10));
        TextField tfNewSiteName = new TextField();
        TextField tfNewSiteAddr = new TextField();
        TextField tfNewSiteStars = new TextField("4");
        TextField tfNewSiteContact = new TextField();
        TextField tfNewSiteFac = new TextField("WiFi");
        Button btnAddSite = new Button("Add Campsite");

        siteGrid.add(new Label("Name:"), 0, 0);        siteGrid.add(tfNewSiteName, 1, 0);
        siteGrid.add(new Label("Address:"), 0, 1);     siteGrid.add(tfNewSiteAddr, 1, 1);
        siteGrid.add(new Label("Stars (1-5):"), 0, 2); siteGrid.add(tfNewSiteStars, 1, 2);
        siteGrid.add(new Label("Contact:"), 0, 3);     siteGrid.add(tfNewSiteContact, 1, 3);
        siteGrid.add(new Label("Facility:"), 0, 4);    siteGrid.add(tfNewSiteFac, 1, 4);
        siteGrid.add(btnAddSite, 1, 5);

        btnAddSite.setOnAction(e -> {
            try {
                Campsite c = new Campsite(
                        tfNewSiteName.getText().trim(), tfNewSiteAddr.getText().trim(),
                        Integer.parseInt(tfNewSiteStars.getText().trim()), tfNewSiteContact.getText().trim()
                );
                if (!tfNewSiteFac.getText().trim().isEmpty()) {
                    c.addFacility(tfNewSiteFac.getText().trim());
                }
                if (manager.addCampsite(c)) {
                    refreshTree();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Campsite added!");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Duplicate", "Campsite name already exists.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Check inputs: " + ex.getMessage());
            }
        });

        // --- 2. Add Camping Area ---
        GridPane areaGrid = new GridPane();
        areaGrid.setHgap(10); areaGrid.setVgap(8); areaGrid.setPadding(new Insets(10));
        TextField tfAreaSite = new TextField("Sunny Valley");
        TextField tfNewAreaId = new TextField();
        TextField tfNewAreaDesc = new TextField();
        Button btnAddArea = new Button("Add Area");

        areaGrid.add(new Label("Campsite Name:"), 0, 0); areaGrid.add(tfAreaSite, 1, 0);
        areaGrid.add(new Label("Area ID:"), 0, 1);       areaGrid.add(tfNewAreaId, 1, 1);
        areaGrid.add(new Label("Description:"), 0, 2);   areaGrid.add(tfNewAreaDesc, 1, 2);
        areaGrid.add(btnAddArea, 1, 3);

        btnAddArea.setOnAction(e -> {
            CampingArea area = new CampingArea(tfNewAreaId.getText().trim(), tfNewAreaDesc.getText().trim());
            if (manager.addCampingArea(tfAreaSite.getText().trim(), area)) {
                refreshTree();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Camping Area added!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Failed", "Campsite not found or Area ID already exists.");
            }
        });

        // --- 3. Add Plot / Pitch ---
        GridPane plotGrid = new GridPane();
        plotGrid.setHgap(10); plotGrid.setVgap(8); plotGrid.setPadding(new Insets(10));
        TextField tfPlotSite = new TextField("Sunny Valley");
        TextField tfPlotArea = new TextField("Area-A");
        TextField tfPlotId = new TextField();
        ComboBox<String> cbPlotType = new ComboBox<>();
        cbPlotType.getItems().addAll("bungalow", "empty pitch", "static mobile home", "apartment");
        cbPlotType.setValue("bungalow");
        CheckBox chkPlotPower = new CheckBox("Has Power");
        chkPlotPower.setSelected(true);
        TextField tfBeds = new TextField("2");
        TextField tfSize = new TextField("40");
        TextField tfCost = new TextField("70");
        Button btnAddPlot = new Button("Add Plot");

        plotGrid.add(new Label("Campsite:"), 0, 0);      plotGrid.add(tfPlotSite, 1, 0);
        plotGrid.add(new Label("Area ID:"), 0, 1);       plotGrid.add(tfPlotArea, 1, 1);
        plotGrid.add(new Label("Plot ID:"), 0, 2);       plotGrid.add(tfPlotId, 1, 2);
        plotGrid.add(new Label("Type:"), 0, 3);          plotGrid.add(cbPlotType, 1, 3);
        plotGrid.add(chkPlotPower, 1, 4);
        plotGrid.add(new Label("Bedrooms:"), 0, 5);      plotGrid.add(tfBeds, 1, 5);
        plotGrid.add(new Label("Size (m2):"), 0, 6);     plotGrid.add(tfSize, 1, 6);
        plotGrid.add(new Label("Cost/Night (€):"), 0, 7); plotGrid.add(tfCost, 1, 7);
        plotGrid.add(btnAddPlot, 1, 8);

        btnAddPlot.setOnAction(e -> {
            try {
                int beds = Integer.parseInt(tfBeds.getText().trim());
                Plot p = new Plot(
                        tfPlotId.getText().trim(), cbPlotType.getValue(), chkPlotPower.isSelected(),
                        beds, beds * 2, Double.parseDouble(tfSize.getText().trim()),
                        Double.parseDouble(tfCost.getText().trim()), "https://example.com/plot.jpg"
                );
                if (manager.addPlot(tfPlotSite.getText().trim(), tfPlotArea.getText().trim(), p)) {
                    refreshTree();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Plot added!");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Failed", "Check Campsite/Area names or duplicate Plot ID.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Check numeric fields: " + ex.getMessage());
            }
        });

        // --- 4. Add Guest ---
        GridPane guestGrid = new GridPane();
        guestGrid.setHgap(10); guestGrid.setVgap(8); guestGrid.setPadding(new Insets(10));
        TextField tfNewGuestId = new TextField("102");
        TextField tfNewGuestName = new TextField();
        TextField tfNewGuestDob = new TextField("2000-01-01");
        TextField tfNewGuestEmail = new TextField();
        Button btnAddGuest = new Button("Add Guest");

        guestGrid.add(new Label("Guest ID:"), 0, 0); guestGrid.add(tfNewGuestId, 1, 0);
        guestGrid.add(new Label("Name:"), 0, 1);     guestGrid.add(tfNewGuestName, 1, 1);
        guestGrid.add(new Label("DOB:"), 0, 2);      guestGrid.add(tfNewGuestDob, 1, 2);
        guestGrid.add(new Label("Email:"), 0, 3);    guestGrid.add(tfNewGuestEmail, 1, 3);
        guestGrid.add(btnAddGuest, 1, 4);

        btnAddGuest.setOnAction(e -> {
            try {
                Guest g = new Guest(
                        Integer.parseInt(tfNewGuestId.getText().trim()), tfNewGuestName.getText().trim(),
                        tfNewGuestDob.getText().trim(), "Ireland", "0870000000",
                        tfNewGuestEmail.getText().trim(), "Registered via GUI"
                );
                if (manager.addGuest(g)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Guest added!");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Duplicate", "Guest ID already exists.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Guest ID: " + ex.getMessage());
            }
        });

        // --- 5. Add Booking ---
        GridPane bookGrid = new GridPane();
        bookGrid.setHgap(10); bookGrid.setVgap(8); bookGrid.setPadding(new Insets(10));
        TextField tfSiteName = new TextField("Sunny Valley");
        TextField tfAreaId = new TextField("Area-A");
        TextField tfGuestId = new TextField("101");
        DatePicker dpStart = new DatePicker(LocalDate.now().plusDays(2));
        DatePicker dpEnd = new DatePicker(LocalDate.now().plusDays(7));
        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("bungalow", "empty pitch", "static mobile home", "apartment");
        cbType.setValue("bungalow");
        CheckBox chkPower = new CheckBox("Require Power");
        chkPower.setSelected(true);
        TextField tfBudget = new TextField("600");
        Button btnMakeBooking = new Button("Reserve Plot");

        bookGrid.add(new Label("Campsite Name:"), 0, 0); bookGrid.add(tfSiteName, 1, 0);
        bookGrid.add(new Label("Area ID:"), 0, 1);       bookGrid.add(tfAreaId, 1, 1);
        bookGrid.add(new Label("Guest ID:"), 0, 2);      bookGrid.add(tfGuestId, 1, 2);
        bookGrid.add(new Label("Start Date:"), 0, 3);    bookGrid.add(dpStart, 1, 3);
        bookGrid.add(new Label("End Date:"), 0, 4);      bookGrid.add(dpEnd, 1, 4);
        bookGrid.add(new Label("Dwelling Type:"), 0, 5); bookGrid.add(cbType, 1, 5);
        bookGrid.add(chkPower, 1, 6);
        bookGrid.add(new Label("Max Budget (€):"), 0, 7); bookGrid.add(tfBudget, 1, 7);
        bookGrid.add(btnMakeBooking, 1, 8);

        btnMakeBooking.setOnAction(e -> {
            try {
                Booking result = manager.addBooking(
                        tfSiteName.getText().trim(), tfAreaId.getText().trim(),
                        Integer.parseInt(tfGuestId.getText().trim()),
                        dpStart.getValue(), dpEnd.getValue(), cbType.getValue(),
                        chkPower.isSelected(), 0, 0, Double.parseDouble(tfBudget.getText().trim())
                );
                if (result != null) {
                    refreshTree();
                    showAlert(Alert.AlertType.INFORMATION, "Booking Confirmed",
                            "Assigned Plot: " + result.getPlotId() + "\nTotal Cost: €" + result.getTotalCost());
                } else {
                    showAlert(Alert.AlertType.WARNING, "No Plot Available",
                            "No available plot matches all criteria in this area.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Verify fields: " + ex.getMessage());
            }
        });

        TitledPane tpBook = new TitledPane("5. Add Booking", bookGrid);
        accordion.getPanes().addAll(
                new TitledPane("1. Add Campsite", siteGrid),
                new TitledPane("2. Add Camping Area", areaGrid),
                new TitledPane("3. Add Plot / Pitch", plotGrid),
                new TitledPane("4. Add Guest", guestGrid),
                tpBook
        );
        accordion.setExpandedPane(tpBook);

        tab.setContent(new ScrollPane(accordion));
        return tab;
    }

    // =========================================================================
    // Tab 3: Pending Bookings & Guest Records
    // =========================================================================

private Tab createReportsAndGuestTab() {
        Tab tab = new Tab("Bookings & Guests");
        tab.setClosable(false);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Row 1: Overall & Campsite-specific bookings
        HBox viewBar = new HBox(10);
        Button btnViewAllBookings = new Button("All Pending Bookings");
        TextField tfFilterSite = new TextField("Sunny Valley");
        tfFilterSite.setPromptText("Campsite Name");
        Button btnViewBySite = new Button("View by Campsite");
        viewBar.getChildren().addAll(btnViewAllBookings, new Separator(), tfFilterSite, btnViewBySite);

        // Row 2: Cancel booking & Guest lookup
        HBox actionBar = new HBox(10);
        TextField tfCancelId = new TextField();
        tfCancelId.setPromptText("Booking ID (e.g. BK-1001)");
        Button btnCancelBooking = new Button("Cancel Booking");

        TextField tfGuestLookup = new TextField("101");
        tfGuestLookup.setPromptText("Guest ID");
        Button btnLookup = new Button("View Guest Record");

        actionBar.getChildren().addAll(tfCancelId, btnCancelBooking, new Separator(), tfGuestLookup, btnLookup);

        TextArea reportArea = new TextArea();
        reportArea.setPrefRowCount(18);
        reportArea.setEditable(false);

        // Overall system bookings
        btnViewAllBookings.setOnAction(e -> {
            StringBuilder sb = new StringBuilder("=== OVERALL SYSTEM PENDING BOOKINGS ===\n\n");
            MyList<Campsite> sites = manager.getCampsites();
            int total = 0;

            for (int i = 0; i < sites.size(); i++) {
                Campsite c = sites.get(i);
                sb.append("Campsite: ").append(c.getName()).append("\n");
                for (int j = 0; j < c.getAreas().size(); j++) {
                    CampingArea a = c.getAreas().get(j);
                    sb.append("  Area: ").append(a.getAreaId()).append("\n");
                    for (int k = 0; k < a.getPlots().size(); k++) {
                        Plot p = a.getPlots().get(k);
                        for (int b = 0; b < p.getBookings().size(); b++) {
                            Booking bk = p.getBookings().get(b);
                            sb.append("    -> ").append(bk.getBookingId())
                                    .append(" | Plot: ").append(bk.getPlotId())
                                    .append(" | Guest ID: ").append(bk.getGuestId())
                                    .append(" | ").append(bk.getStartDate()).append(" to ").append(bk.getEndDate())
                                    .append(" | €").append(bk.getTotalCost()).append("\n");
                            total++;
                        }
                    }
                }
            }
            sb.append("\nTotal Pending Bookings: ").append(total);
            reportArea.setText(sb.toString());
        });

        // Campsite-specific bookings (organised by Area -> Plot)
        btnViewBySite.setOnAction(e -> {
            Campsite c = manager.findCampsite(tfFilterSite.getText().trim());
            if (c == null) {
                showAlert(Alert.AlertType.WARNING, "Not Found", "Campsite not found.");
                return;
            }
            StringBuilder sb = new StringBuilder("=== PENDING BOOKINGS FOR: " + c.getName() + " ===\n\n");
            int total = 0;
            for (int j = 0; j < c.getAreas().size(); j++) {
                CampingArea a = c.getAreas().get(j);
                sb.append("Area: ").append(a.getAreaId()).append("\n");
                for (int k = 0; k < a.getPlots().size(); k++) {
                    Plot p = a.getPlots().get(k);
                    for (int b = 0; b < p.getBookings().size(); b++) {
                        Booking bk = p.getBookings().get(b);
                        sb.append("  -> ").append(bk.getBookingId())
                                .append(" | Plot: ").append(bk.getPlotId())
                                .append(" | Guest ID: ").append(bk.getGuestId())
                                .append(" | ").append(bk.getStartDate()).append(" to ").append(bk.getEndDate())
                                .append(" | €").append(bk.getTotalCost()).append("\n");
                        total++;
                    }
                }
            }
            sb.append("\nTotal Bookings in ").append(c.getName()).append(": ").append(total);
            reportArea.setText(sb.toString());
        });

        btnCancelBooking.setOnAction(e -> {
            String bid = tfCancelId.getText().trim();
            if (manager.cancelBooking(bid)) {
                refreshTree();
                showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Booking " + bid + " was cancelled.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Booking ID not found.");
            }
        });

        btnLookup.setOnAction(e -> {
            try {
                int gid = Integer.parseInt(tfGuestLookup.getText().trim());
                Guest g = manager.findGuest(gid);
                if (g != null) {
                    reportArea.setText("=== GUEST RECORD ===\n" +
                            "ID: " + g.getGuestId() + "\nName: " + g.getName() +
                            "\nDOB: " + g.getDateOfBirth() + "\nAddress: " + g.getAddress() +
                            "\nEmail: " + g.getEmail() + "\nPhone: " + g.getPhone() +
                            "\nNotes: " + g.getExtraInfo());
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "Guest ID does not exist.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Guest ID format.");
            }
        });

        layout.getChildren().addAll(viewBar, actionBar, reportArea);
        tab.setContent(layout);
        return tab;
    }

    // =========================================================================
    // Tab 4: Smart Find & Remove Plot
    // =========================================================================

    private Tab createSmartFindTab() {
        Tab tab = new Tab("Smart Find & Plot Removal");
        tab.setClosable(false);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));

        // Smart Find Section
        Label lblSmart = new Label("Smart Find Facility for Camping Holidays");
        lblSmart.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);

        TextField tfStars = new TextField("3");
        TextField tfMaxBudget = new TextField("500");
        DatePicker dpStart = new DatePicker(LocalDate.now().plusDays(10));
        DatePicker dpEnd = new DatePicker(LocalDate.now().plusDays(15));
        TextField tfFacility = new TextField("Swimming Pool");
        TextField tfType = new TextField("bungalow");

        grid.add(new Label("Min Stars:"), 0, 0); grid.add(tfStars, 1, 0);
        grid.add(new Label("Max Budget (€):"), 0, 1); grid.add(tfMaxBudget, 1, 1);
        grid.add(new Label("Start Date:"), 0, 2); grid.add(dpStart, 1, 2);
        grid.add(new Label("End Date:"), 0, 3); grid.add(dpEnd, 1, 3);
        grid.add(new Label("Required Facility:"), 0, 4); grid.add(tfFacility, 1, 4);
        grid.add(new Label("Dwelling Type:"), 0, 5); grid.add(tfType, 1, 5);

        Button btnFind = new Button("Run Smart Find");
        TextArea resultArea = new TextArea();
        resultArea.setPrefRowCount(4);
        resultArea.setEditable(false);

        btnFind.setOnAction(e -> {
            try {
                int stars = Integer.parseInt(tfStars.getText().trim());
                double bgt = Double.parseDouble(tfMaxBudget.getText().trim());
                String res = manager.smartFind(stars, bgt, dpStart.getValue(), dpEnd.getValue(),
                        tfFacility.getText().trim(), tfType.getText().trim());

                if (res != null) {
                    resultArea.setText(res);
                } else {
                    resultArea.setText("No holidays matched your criteria. Try adjusting dates or budget.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Input error: " + ex.getMessage());
            }
        });

        // Remove Plot Section
        Separator sep = new Separator();
        Label lblRemove = new Label("Remove Plot (Auto-Reassigns Pending Bookings)");
        lblRemove.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox removeBox = new HBox(10);
        TextField tfRemSite = new TextField("Sunny Valley");
        TextField tfRemArea = new TextField("Area-A");
        TextField tfRemPlot = new TextField("P-1");
        Button btnRemove = new Button("Remove Plot");

        removeBox.getChildren().addAll(
                new Label("Site:"), tfRemSite,
                new Label("Area:"), tfRemArea,
                new Label("Plot:"), tfRemPlot,
                btnRemove
        );

        btnRemove.setOnAction(e -> {
            boolean removed = manager.removePlotWithReassignment(
                    tfRemSite.getText().trim(), tfRemArea.getText().trim(), tfRemPlot.getText().trim()
            );

            if (removed) {
                refreshTree();
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Plot removed. All pending bookings relocated to alternative plots.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure",
                        "Could not remove plot. Either plot not found or bookings could not be reassigned.");
            }
        });

        layout.getChildren().addAll(lblSmart, grid, btnFind, resultArea, sep, lblRemove, removeBox);
        tab.setContent(new ScrollPane(layout));
        return tab;
    }

    // =========================================================================
    // Helpers & Demo Data
    // =========================================================================

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Seeds initial demo data into the system for demonstration/interview.
     */
    private void loadDemoData() {
        Campsite site1 = new Campsite("Sunny Valley", "Waterford Coastline", 4, "+353 51 123456");
        site1.addFacility("Swimming Pool");
        site1.addFacility("WiFi");
        site1.addFacility("Restaurant");
        manager.addCampsite(site1);

        CampingArea areaA = new CampingArea("Area-A", "Lakefront pitches");
        site1.addCampingArea(areaA);

        Plot p1 = new Plot("P-1", "bungalow", true, 2, 4, 45.0, 75.0, "https://example.com/p1.jpg");
        Plot p2 = new Plot("P-2", "bungalow", true, 2, 4, 48.0, 75.0, "https://example.com/p2.jpg");
        Plot p3 = new Plot("P-3", "empty pitch", false, 0, 0, 30.0, 25.0, "https://example.com/p3.jpg");

        areaA.addPlot(p1);
        areaA.addPlot(p2);
        areaA.addPlot(p3);

        Guest g1 = new Guest(101, "Alice Smith", "1998-04-12", "Waterford City", "0891234567", "alice@mail.com", "Frequent visitor");
        manager.addGuest(g1);

        // Pre-create a sample booking
        manager.addBooking("Sunny Valley", "Area-A", 101,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(6),
                "bungalow", true, 30.0, 1, 500.0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}