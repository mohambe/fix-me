package za.co.wethinkcode.model;

import java.io.IOException;

import java.net.Socket;

import java.util.Scanner;

/**
 * Market App
 *
 */
public class Market {
    public Market() throws IOException {
        Socket socket =  new Socket("127.0.0.1", 5000);
        Scanner in = new Scanner(socket.getInputStream());
        System.out.println("Server response " + in.nextLine());
    }
}