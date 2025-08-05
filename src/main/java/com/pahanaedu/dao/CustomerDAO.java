package com.pahanaedu.dao;

import com.pahanaedu.model.Customer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private static final String FILE_PATH = "Customer.txt";

    public void addCustomer(Customer customer) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(customer.toString());
            writer.newLine();
        }
    }

    public List<Customer> getAllCustomers() throws IOException {
        List<Customer> customers = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return customers;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    Customer customer = new Customer(data[0], data[1], data[2], data[3], Double.parseDouble(data[4]));
                    customers.add(customer);
                }
            }
        }
        return customers;
    }

    public Customer getCustomerByAccountNumber(String accountNumber) throws IOException {
        for (Customer customer : getAllCustomers()) {
            if (customer.getAccountNumber().equals(accountNumber)) {
                return customer;
            }
        }
        return null;
    }

    public void updateCustomer(Customer updatedCustomer) throws IOException {
        List<Customer> customers = getAllCustomers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Customer customer : customers) {
                if (customer.getAccountNumber().equals(updatedCustomer.getAccountNumber())) {
                    writer.write(updatedCustomer.toString());
                } else {
                    writer.write(customer.toString());
                }
                writer.newLine();
            }
        }
    }
}