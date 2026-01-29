import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreCliente;

    public Cliente(Socket socket, String nombreCliente){
        try{
            this.socket=socket;
            this.nombreCliente=nombreCliente;
            this.bufferedReader= new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e){
            e.printStackTrace();
            cerrarComunicacion(socket, bufferedWriter, bufferedReader);
        }
    }

    public void cerrarComunicacion(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
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

    public void enviarMensaje(){
        try{
            bufferedWriter.write(nombreCliente);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                System.out.println("Escribe el mensaje: ");
                String mensaje = scanner.next();
                bufferedWriter.write(nombreCliente +": " + mensaje);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            e.printStackTrace();
            cerrarComunicacion(socket, bufferedWriter, bufferedReader);
        }
    }

    public void escucharMensaje(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mensajeDesdeChat;
                while (socket.isConnected()){
                    try {
                        mensajeDesdeChat=bufferedReader.readLine();
                        System.out.println(mensajeDesdeChat);
                    } catch (IOException e) {
                        e.printStackTrace();
                        cerrarComunicacion(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce tu nombre");
        String nombreCliente = scanner.next();

        Socket socket = new Socket("localhost", 5000);
        Cliente cliente = new Cliente(socket,nombreCliente);
        cliente.escucharMensaje();
        cliente.enviarMensaje();
    }


}
