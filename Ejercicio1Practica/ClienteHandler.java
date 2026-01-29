package Ejercicio1Practica;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClienteHandler implements Runnable{

    private Socket socket;
    private static ArrayList<ClienteHandler> clienteHandlers = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreCliente;

    public ClienteHandler(Socket socket){
        try{
            this.socket=socket;
            this.bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            this.nombreCliente = bufferedReader.readLine();
            clienteHandlers.add(this);
            mandarMensaje("SERVER: Se ha unido el cliente " + nombreCliente);
        } catch (IOException e){
            e.printStackTrace();
            cerrarComunicacion(socket, bufferedWriter, bufferedReader);
        }

    }

    public void quitarClienteHandler(){
        clienteHandlers.remove(this);
        mandarMensaje("SERVER: Se ha ido el cliente " + nombreCliente);
    }

    @Override
    public void run() {
        String mensajeDesdeCliente;
        while (socket.isConnected()){
            try {
                mensajeDesdeCliente = bufferedReader.readLine();
                if (mensajeDesdeCliente == null) {
                    cerrarComunicacion(socket, bufferedWriter, bufferedReader);
                    break;
                }
                mandarMensaje(mensajeDesdeCliente);
            }catch (IOException e){
                e.printStackTrace();
                cerrarComunicacion(socket, bufferedWriter, bufferedReader);
            }
        }

    }

    public void mandarMensaje(String mensaje){
        for (ClienteHandler clienteHandler : clienteHandlers){
            try{
                clienteHandler.bufferedWriter.write(mensaje);
                clienteHandler.bufferedWriter.newLine();
                clienteHandler.bufferedWriter.flush();
            }catch (IOException e){
                e.printStackTrace();
                cerrarComunicacion(socket, bufferedWriter, bufferedReader);
            }
        }
    }

    public void cerrarComunicacion(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        quitarClienteHandler();
        try{
            if (socket != null){
                socket.close();
            }
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


}
