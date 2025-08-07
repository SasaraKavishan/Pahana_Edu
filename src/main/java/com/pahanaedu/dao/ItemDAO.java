package com.pahanaedu.dao;

import com.pahanaedu.model.Item;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private static final String FILE_PATH = "Item.txt";

    public void addItem(Item item) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(item.toString());
            writer.newLine();
        }
    }

    public List<Item> getAllItems() throws IOException {
        List<Item> items = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return items;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    Item item = new Item(data[0], data[1], Double.parseDouble(data[2]));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public Item getItemById(String itemId) throws IOException {
        for (Item item : getAllItems()) {
            if (item.getItemId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }

    public void updateItem(Item updatedItem) throws IOException {
        List<Item> items = getAllItems();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Item item : items) {
                if (item.getItemId().equals(updatedItem.getItemId())) {
                    writer.write(updatedItem.toString());
                } else {
                    writer.write(item.toString());
                }
                writer.newLine();
            }
        }
    }

    public void deleteItem(String itemId) throws IOException {
        List<Item> items = getAllItems();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Item item : items) {
                if (!item.getItemId().equals(itemId)) {
                    writer.write(item.toString());
                    writer.newLine();
                }
            }
        }
    }
}