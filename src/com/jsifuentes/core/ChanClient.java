package com.jsifuentes.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Jacob on 11/18/2014.
 */
public class ChanClient {
    private Object UniqueID;
    private Socket Socket;
    private PrintWriter Writer;
    private BufferedReader Reader;
    private boolean Admin = false;
    private String IP;

    public void setUniqueID(Object uniqueID) {
        this.UniqueID = uniqueID;
    }

    public void setSocket(Socket clientSocket) {
        this.Socket = clientSocket;
    }

    public void setWriter(PrintWriter clientWriter) {
        this.Writer = clientWriter;
    }

    public void setReader(BufferedReader clientReader) {
        this.Reader = clientReader;
    }

    public void setAdmin(boolean admin) {
        this.Admin = admin;
    }

    public void setIP(String ip) {
        this.IP = ip;
    }

    public boolean isAdmin() {
        return this.Admin;
    }

    public Socket getSocket() {
        return this.Socket;
    }

    public Object getUniqueID() {
        return this.UniqueID;
    }

    public Socket getClientSocket() {
        return this.Socket;
    }

    public PrintWriter getWriter() {
        return this.Writer;
    }

    public BufferedReader getReader() {
        return this.Reader;
    }

    public String getIP() {
        return this.IP;
    }

    public boolean isClosed() {
        return this.Socket.isClosed();
    }

    public String readInput() {
        try {
            return this.Reader.readLine();
        } catch(IOException ioExc) {
            Output.error("Failed to read input! " + ioExc.getMessage());
            return null;
        }
    }

    public boolean writeToClient(String msg) {
        this.Writer.println(msg);
        Output.debug("Wrote to client(" + this.getUniqueID() + "): " + msg);
        return false;
    }

    public void close() {
        try {
            this.Socket.close();
        } catch(IOException ioExc) {
            Output.error("Failed to close client socket! " + ioExc.getMessage());
        }
    }
}
