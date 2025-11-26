package com.algolab.util;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

public class LeftPanelTreeCell extends TreeCell<LeftPannelNodeData> {

    private TextField textField;

    // 🔥 Context menu for folders
    private final ContextMenu folderMenu = new ContextMenu();

    // Instance initializer (runs once per cell)
    {
        MenuItem deleteItem = new MenuItem("Delete");

        deleteItem.setOnAction(event -> {
            TreeItem<LeftPannelNodeData> item = getTreeItem();
            if (item != null && item.getParent() != null) {
                item.getParent().getChildren().remove(item);
            }
        });

        folderMenu.getItems().addAll(deleteItem);
    }


    @Override
    public void startEdit() {
        super.startEdit();

        if (textField == null) {
            textField = createTextField();
        }

        setText(null);
        setGraphic(textField);
        textField.requestFocus();
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        // 🗑 Delete empty item on cancel
        if (getItem() != null && getItem().getName().trim().isEmpty()) {
            deleteSelf();
            return;
        }

        setText(getItem() != null ? getItem().getName() : "");
        setGraphic(getTreeItem().getGraphic());
    }

    @Override
    protected void updateItem(LeftPannelNodeData item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            setContextMenu(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(item.getName());
                }
                setText(null);
                setGraphic(textField);
                setContextMenu(null);
            } else {
                setText(item.getName());
                setGraphic(getTreeItem().getGraphic());

                // 🔥 Only folders get context menu
                if (item.getType() == LeftPannelNodeData.NodeType.FOLDER) {
                    setContextMenu(folderMenu);
                } else {
                    setContextMenu(null);
                }
            }
        }
    }

    private TextField createTextField() {
        TextField tf = new TextField(getItem().getName());

        // 🎨 Custom styling to match VS Code
        tf.setStyle(
            "-fx-background-color: #2d2d2d;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 4;" +
            "-fx-border-radius: 4;" +
            "-fx-border-color: #4c4c4c;" +
            "-fx-padding: 2 6 2 6;" +
            "-fx-font-size: 13px;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;"
        );

        // ENTER → commit or delete
        tf.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String name = tf.getText().trim();
                if (name.isEmpty()) {
                    deleteSelf();
                } else {
                    commitEdit(new LeftPannelNodeData(name, getItem().getType()));
                }
            }

            // ESC → cancel & delete
            else if (event.getCode() == KeyCode.ESCAPE) {
                deleteSelf();
            }
        });

        // Click away → delete or commit
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // lost focus
                String name = tf.getText().trim();
                if (name.isEmpty()) {
                    deleteSelf();
                } else {
                    commitEdit(new LeftPannelNodeData(name, getItem().getType()));
                }
            }
        });

        return tf;
    }

    private void deleteSelf() {
        TreeItem<LeftPannelNodeData> item = getTreeItem();
        if (item != null && item.getParent() != null) {
            item.getParent().getChildren().remove(item);
        }
    }
}
